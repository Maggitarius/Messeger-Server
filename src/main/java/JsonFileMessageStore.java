import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class JsonFileMessageStore implements MessageStore {

    private static final String HISTORY_DIR = "histories";
    private static final String GLOBAL_HISTORY_FILE = "history_global.json";
    private static final int MAX_HISTORY_SIZE = 300;
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void setCurrentUserMac(String mac){
        ensureHistoryFileExist();
    }

    @Override
    public void saveMessage(Message message) throws IOException {
        List<Message> messages = loadMessages();
        messages.add(message);

        if (messages.size() > MAX_HISTORY_SIZE) {
            messages = messages.subList(messages.size() - MAX_HISTORY_SIZE, messages.size());
        }

        mapper.writeValue(getHistoryFile(), messages);
    }

    @Override
    public List<Message> getLastMessages(int limit) throws IOException {
        List<Message> allMessages = loadMessages();
        int fromIndex = Math.max(0, allMessages.size() - limit);
        return allMessages.subList(fromIndex, allMessages.size());
    }

    private void ensureHistoryFileExist() {
        File dir = new File(HISTORY_DIR);
        if(!dir.exists()) {
            dir.mkdirs();
        }

        File file = getHistoryFile();
        if(!file.exists()) {
            try {
                Files.createFile(file.toPath());
                Files.write(file.toPath(), "[]".getBytes());
            } catch (IOException e) {
                System.err.println("Could not create history file: " + e.getMessage());
            }
        }
    }

    private File getHistoryFile() {
        return Paths.get(HISTORY_DIR, GLOBAL_HISTORY_FILE).toFile();
    }

    private List<Message> loadMessages() throws IOException {
        File file = getHistoryFile();
        if (!file.exists() || file.length() == 0) {
            return new LinkedList<>();
        }

        return mapper.readValue(file, new TypeReference<>() {});
    }
}
