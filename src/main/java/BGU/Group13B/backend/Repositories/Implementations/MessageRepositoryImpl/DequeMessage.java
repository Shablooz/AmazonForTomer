package BGU.Group13B.backend.Repositories.Implementations.MessageRepositoryImpl;

import BGU.Group13B.backend.User.Message;
import jakarta.persistence.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Entity
public class DequeMessage {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;


    @OneToMany(cascade = jakarta.persistence.CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    @JoinTable(name = "DequeMessage_Message",
            joinColumns = {@JoinColumn(name = "DequeMessage_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "sender_id", referencedColumnName = "senderId"),
                    @JoinColumn(name = "massage_Id", referencedColumnName = "massageId")})
    private List<Message> list;

    public DequeMessage(List<Message> list) {
        this.list = list;
    }

    public DequeMessage() {
        this.list=new LinkedList<>();
    }

    public synchronized Iterator<Message> iterator(){
        return list.iterator();
    }
    public synchronized boolean isEmpty(){
        return list.isEmpty();
    }
    public synchronized void add(Message message){
        list.add(message);
    }
    public synchronized Message peek(){
        if (list.isEmpty())
            return null;
        return list.get(0);
    }
    public synchronized Message poll(){
        return list.remove(0);
    }
    public synchronized boolean remove(Message message){
        return list.remove(message);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Message> getList() {
        return list;
    }

    public void setList(List<Message> list) {
        this.list = list;
    }
}
