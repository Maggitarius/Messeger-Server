import java.io.IOException;
import java.util.List;

public interface MessageStore {
    void saveMessage(Message message) throws Exception;
    List<Message> getLastMessages(int limit) throws Exception;
    void setCurrentUserMac(String mac) throws Exception;
}
