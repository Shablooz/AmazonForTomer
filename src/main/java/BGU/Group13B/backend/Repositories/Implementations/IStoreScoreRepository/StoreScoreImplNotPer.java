package BGU.Group13B.backend.Repositories.Implementations.IStoreScoreRepository;

import BGU.Group13B.backend.Repositories.Interfaces.IStoreScore;
import jakarta.persistence.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Entity
public class StoreScoreImplNotPer implements IStoreScore {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "StoreScoreImplNotPer_scores",
            joinColumns = {@JoinColumn(name = "StoreScoreImplNotPer_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "userId")
    @Column(name = "score")
    Map<Integer, Integer> storeScore;

    public StoreScoreImplNotPer() {
        storeScore = new ConcurrentHashMap<>();
    }

    @Override
    public void addStoreScore(int userId, int storeId, int score) {
        if(storeScore.containsKey(userId))
            throw new IllegalArgumentException("User already scored this store");
        if(score<0 || score>5)
            throw new IllegalArgumentException("Score must be between 0 to 5");
        storeScore.put(userId, score);
    }

    @Override
    public void removeStoreScore(int userId, int storeId) {
        if(!storeScore.containsKey(userId))
            throw new IllegalArgumentException("User didn't score this store");
        storeScore.remove(userId);
    }

    @Override
    public void modifyStoreScore(int userId, int storeId, int score) {
        if(!storeScore.containsKey(userId))
            throw new IllegalArgumentException("User didn't score this store");
        if(score<0 || score>5)
            throw new IllegalArgumentException("Score must be between 0 to 5");
        storeScore.replace(userId, score);
    }

    @Override
    public float getStoreScore(int storeId) {
        Integer sum = ((ConcurrentHashMap<Integer, Integer>) storeScore).reduceValues(0, Integer::sum);
        if(sum!=null)
            return (float) sum /storeScore.size();

        return 0;
    }
    public void setSaveMode(boolean saveMode) {

    }

    @Override
    public int getNumberOfScores(int storeId) {
        return storeScore.size();
    }

    @Override
    public void clearStoreScore(int storeId) {
        storeScore.clear();
    }

    //getters and setters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<Integer, Integer> getStoreScore() {
        return storeScore;
    }

    public void setStoreScore(Map<Integer, Integer> storeScore) {
        this.storeScore = storeScore;
    }
}
