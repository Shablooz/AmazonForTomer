package BGU.Group13B.backend.Repositories.Implementations.IStoreScoreRepository;

import BGU.Group13B.backend.Repositories.Implementations.ReviewRepositoryImpl.ReviewRepoSingleService;
import BGU.Group13B.backend.Repositories.Interfaces.IStoreScore;
import BGU.Group13B.service.SingletonCollection;
import jakarta.persistence.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Entity
public class StoreScoreSingle implements IStoreScore {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @Transient
    private boolean saveMode;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    @JoinTable(name = "StoreScoreSingle_StoreScoreImplNotPer",
            joinColumns = {@JoinColumn(name = "StoreScoreSingle_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "StoreScoreImplNotPer_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "storeId")
    Map<Integer/*store Id */,StoreScoreImplNotPer> implementations;


    public StoreScoreSingle() {
        implementations = new ConcurrentHashMap<>();
        this.saveMode=true;
    }
    //for testing
    public StoreScoreSingle(boolean saveMode) {
        implementations = new ConcurrentHashMap<>();
        this.saveMode=saveMode;
    }
    @Override
    public void addStoreScore(int userId, int storeId, int score) {
        implementations.putIfAbsent(storeId,new StoreScoreImplNotPer());
        implementations.get(storeId).addStoreScore(userId,storeId,score);
        save();
    }

    @Override
    public void removeStoreScore(int userId, int storeId) {
        implementations.putIfAbsent(storeId,new StoreScoreImplNotPer());
        implementations.get(storeId).removeStoreScore(userId,storeId);
        save();
    }

    @Override
    public void modifyStoreScore(int userId, int storeId, int score) {
        implementations.putIfAbsent(storeId,new StoreScoreImplNotPer());
        implementations.get(storeId).modifyStoreScore(userId,storeId,score);
        save();
    }

    @Override
    public float getStoreScore(int storeId) {
        implementations.putIfAbsent(storeId,new StoreScoreImplNotPer());
        return implementations.get(storeId).getStoreScore(storeId);
    }

    @Override
    public int getNumberOfScores(int storeId) {
        implementations.putIfAbsent(storeId,new StoreScoreImplNotPer());
        return implementations.get(storeId).getNumberOfScores(storeId);
    }

    @Override
    public void clearStoreScore(int storeId) {
        implementations.remove(storeId);
        save();
    }

    public void save(){
        if(saveMode)
            SingletonCollection.getContext().getBean(StoreScoreRepoService.class).save(this);
    }
    public boolean getSaveMode(){
        return saveMode;
    }

    //getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSaveMode() {
        return saveMode;
    }

    public void setSaveMode(boolean saveMode) {
        this.saveMode = saveMode;
    }

    public Map<Integer, StoreScoreImplNotPer> getImplementations() {
        return implementations;
    }

    public void setImplementations(Map<Integer, StoreScoreImplNotPer> implementations) {
        this.implementations = implementations;
    }
}
