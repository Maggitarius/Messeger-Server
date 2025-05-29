import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {
    private String senderMac;
    private String senderName;
    private long timestamp;
    private String text;
    private String type;

    @JsonCreator
    public Message(
            @JsonProperty("senderMac") String senderMac,
            @JsonProperty("senderName") String senderName,
            @JsonProperty("timestamp") long timestamp,
            @JsonProperty("text") String text,
            @JsonProperty("type") String type
    ) {
        this.senderMac = senderMac;
        this.senderName = senderName;
        this.timestamp = timestamp;
        this.text = text;
        this.type = type;
    }

    public Message() {

    }

    public String getSenderMac() {
        return senderMac;
    }

    public void setSenderMac(String senderMac) {
        this.senderMac = senderMac;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Message{" +
                "senderMac='" + senderMac + '\'' +
                ", timestamp=" + timestamp +
                ", text='" + text + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
