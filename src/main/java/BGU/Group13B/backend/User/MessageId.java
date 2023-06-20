package BGU.Group13B.backend.User;

import java.io.Serializable;
import java.util.Objects;

public class MessageId implements Serializable {

    private  String senderId;
    private  int massageId;

    public MessageId(String senderId, int massageId) {
        this.senderId = senderId;
        this.massageId = massageId;
    }

    public MessageId() {
    }

    public String getSenderId() {
        return senderId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageId messageId = (MessageId) o;
        return massageId == messageId.massageId && Objects.equals(senderId, messageId.senderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(senderId, massageId);
    }
}
