package BGU.Group13B.backend.User;

public class Message {
    private final int senderId;
    private final int massageId;
    private final String message;

    public Message(int fromUserId, int massageId, String message) {
        this.senderId = fromUserId;
        this.massageId = massageId;
        this.message = message;
    }

    public int getFromUserId() {
        return senderId;
    }

    public int getMassageId() {
        return massageId;
    }

    public String getMessage() {
        return message;
    }
}
