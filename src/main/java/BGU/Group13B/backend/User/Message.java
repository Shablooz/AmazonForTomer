package BGU.Group13B.backend.User;

public class Message {
    private final int senderId;
    private final int massageId;
    private final String Header;
    private final String message;
    private final int receiverId;

    public Message(int fromUserId, int massageId, String header, String message, int receiverId) {
        this.senderId = fromUserId;
        this.massageId = massageId;
        Header = header;
        this.message = message;
        this.receiverId = receiverId;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getMassageId() {
        return massageId;
    }

    public String getMessage() {
        return message;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public String getHeader() {
        return Header;
    }
}
