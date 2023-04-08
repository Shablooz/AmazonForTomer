package BGU.Group13B.backend.User;

public class Message {
    private final String senderId;
    private final int massageId;
    private final String Header;
    private final String message;
    private final String receiverId;

    public Message(String senderId, int massageId, String header, String message, String receiverId) {
        this.senderId = senderId;
        this.massageId = massageId;
        Header = header;
        this.message = message;
        this.receiverId = receiverId;
    }

    public String getSenderId() {
        return senderId;
    }

    public int getMassageId() {
        return massageId;
    }

    public String getMessage() {
        return message;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getHeader() {
        return Header;
    }
}
