package BGU.Group13B.backend.User;

public class Message {
    private final String senderId; //primary key
    private final int massageId; //primary key start from 1
    private final String Header;
    private final String message;
    private final String receiverId;

    private Message(String senderId, int massageId, String header, String message, String receiverId) {
        this.senderId = senderId;
        this.massageId = massageId;
        Header = header;
        this.message = message;
        this.receiverId = receiverId;
    }
    public static Message constractMessage(String senderId, int massageId, String header, String message, String receiverId){
        if(senderId==null||massageId==0||header==null||message==null||receiverId==null)
            throw new IllegalArgumentException("one of the arguments is null");
        return new Message(senderId,massageId,header,message,receiverId);
    }

    public String getSenderId() {
        return senderId;
    }

    public int getMessageId() {
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

    public String toString(){
        return "Sender User Name:\t"+senderId+"\n\nHeader:\t"+Header+"\n\nBody:\n"+message;
    }
}
