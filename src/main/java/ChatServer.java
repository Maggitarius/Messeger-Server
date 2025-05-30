public class ChatServer {
    public static void main(String[] args) {
        int port = 8887;
        ChatServerWebSocket webServer = new ChatServerWebSocket(port);
        webServer.start();
        System.out.println("Server will listen on port " + port);

        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
