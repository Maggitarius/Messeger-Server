import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.*;
import java.io.IOException;

public class ChatServerWebSocket extends WebSocketServer {

    private final ObjectMapper mapper = new ObjectMapper();
    private final JsonFileMessageStore messageStore = new JsonFileMessageStore();
    private final Map<String, String> macToNick = new HashMap<>();
    private final Set<WebSocket> connections = Collections.synchronizedSet(new HashSet<>());

    public ChatServerWebSocket(int port) {
        super(new InetSocketAddress("0.0.0.0",port));
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        connections.add(webSocket);
        System.out.println("Connected to " + webSocket.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        connections.remove(webSocket);
        System.out.println("Disconnected from " + webSocket.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket webSocket, String messageJson) {
        System.out.println("[RECEIVED] " + messageJson);

        try {
            Message message = mapper.readValue(messageJson, Message.class);

            if ("init".equals(message.getType())) {
                macToNick.put(message.getSenderMac(), message.getSenderName());
                System.out.println("User initiated: " + message.getSenderName());
                List<Message> history = messageStore.getLastMessages(100);
                for (Message msg : history) {
                    String json = mapper.writeValueAsString(msg);
                    webSocket.send(json);
                    }
                return;
            }

            // подставляем имя из map по MAC
            message.setSenderName(macToNick.getOrDefault(message.getSenderMac(), "Unknown"));
            message.setTimestamp(System.currentTimeMillis());

            if ("text".equals(message.getType())) {
                String sender = message.getSenderName();
                System.out.println("[" + Instant.ofEpochMilli(message.getTimestamp()) + "] " + sender + " -> " + message.getText());
            }

            // сохраняем
            messageStore.saveMessage(message);
            String broadcast = mapper.writeValueAsString(message);
            broadcastMessage(broadcast);

        } catch (IOException e) {
            System.err.println("The message was not parsed " + e.getMessage());
        }

    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        System.err.println("Error: " + e.getMessage());
    }

    @Override
    public void onStart() {
        System.out.println("Server started on port " + getPort());
    }

    private void broadcastMessage(String message) {
        synchronized (connections) {
            for (WebSocket connection : connections) {
                if (connection.isOpen()) {
                    connection.send(message);
                }
            }
        }
    }
}