package BGU.Group13B.backend.Repositories.Implementations.StoreMessageRepositoyImpl;

import BGU.Group13B.backend.Repositories.Implementations.MessageRepositoryImpl.MessageRepositorySingleService;
import BGU.Group13B.backend.Repositories.Interfaces.IStoreMessagesRepository;
import BGU.Group13B.backend.User.Message;
import BGU.Group13B.service.SingletonCollection;
import jakarta.persistence.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Entity
public class StoreMessageSingle implements IStoreMessagesRepository {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    @JoinTable(name = "StoreMessageSingle_implementations",
            joinColumns = {@JoinColumn(name = "StoreMessageSingle_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "StoreMessageRepositoryNonPersist_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "store_id")
    Map<Integer/*store Id */,StoreMessageRepositoryNonPersist> implementations;

    @Transient
    private boolean saveMode;

    public StoreMessageSingle() {
        implementations = new ConcurrentHashMap<>();
        this.saveMode= true;
    }
    public StoreMessageSingle(boolean saveMode) {
        implementations = new ConcurrentHashMap<>();
        this.saveMode= saveMode;
    }

    @Override
    public void sendMassage(Message message, int storeId, int userId) {
        implementations.putIfAbsent(storeId,new StoreMessageRepositoryNonPersist());
        implementations.get(storeId).sendMassage(message,storeId,userId);
        save();
    }

    @Override
    public Message readUnreadMassage(int storeId, int userId) {
        implementations.putIfAbsent(storeId,new StoreMessageRepositoryNonPersist());
        return implementations.get(storeId).readUnreadMassage(storeId,userId);
    }

    @Override
    public Message readReadMassage(int storeId, int userId) {
        implementations.putIfAbsent(storeId,new StoreMessageRepositoryNonPersist());
        return implementations.get(storeId).readReadMassage(storeId,userId);
    }

    @Override
    public void markAsRead(int storeId,String senderId, int massageId, int userId) {
        implementations.putIfAbsent(storeId,new StoreMessageRepositoryNonPersist());
        implementations.get(storeId).markAsRead(storeId,senderId,massageId,userId);
        save();
    }

    @Override
    public void refreshOldMassage(int storeId, int userId) {
        implementations.putIfAbsent(storeId,new StoreMessageRepositoryNonPersist());
        implementations.get(storeId).refreshOldMassage(storeId,userId);
        save();
    }

    @Override
    public int getUnreadMessagesSize(int storeId, int userId) {
        implementations.putIfAbsent(storeId,new StoreMessageRepositoryNonPersist());
        return implementations.get(storeId).getUnreadMessagesSize(storeId,userId);
    }

    @Override
    public int getReadMessagesSize(int storeId, int userId) {
        implementations.putIfAbsent(storeId,new StoreMessageRepositoryNonPersist());
        return implementations.get(storeId).getReadMessagesSize(storeId,userId);
    }

    public void save(){
        if(saveMode && SingletonCollection.databaseExists())
            SingletonCollection.getContext().getBean(StoreMessageSingleService.class).save(this);
    }
    public void setSaveMode(boolean saved) {
        this.saveMode = saved;
    }
    //getters and setters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<Integer, StoreMessageRepositoryNonPersist> getImplementations() {
        return implementations;
    }

    public void setImplementations(Map<Integer, StoreMessageRepositoryNonPersist> implementations) {
        this.implementations = implementations;
    }

    public boolean isSaveMode() {
        return saveMode;
    }
}
