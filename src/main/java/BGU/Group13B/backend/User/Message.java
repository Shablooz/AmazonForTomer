package BGU.Group13B.backend.User;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;

@Entity
@IdClass(MessageId.class)
public class Message {

    @Id
    private  String senderId; //primary key
    @Id
    private  int massageId; //primary key start from 1
    private  String Header;
    private  String message;
    private  String receiverId;

    private Message(String senderId, int massageId, String header, String message, String receiverId) {
        this.senderId = senderId;
        this.massageId = massageId;
        Header = header;
        this.message = message;
        this.receiverId = receiverId;
    }
    public Message() {
        this.senderId = "";
        this.massageId = 0;
        Header = "";
        this.message = "";
        this.receiverId = "";
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


    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public int getMassageId() {
        return massageId;
    }

    public void setMassageId(int massageId) {
        this.massageId = massageId;
    }

    public void setHeader(String header) {
        Header = header;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
}
