package BGU.Group13B.backend.Repositories.Implementations.IStoreScoreRepository;

import BGU.Group13B.backend.Repositories.Interfaces.IStoreScore;

import java.util.concurrent.ConcurrentHashMap;

public class StoreScoreImplNotPer implements IStoreScore {

    ConcurrentHashMap<Integer, Integer> storeScore;

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
        Integer sum = storeScore.reduceValues(0, Integer::sum);
        if(sum!=null)
            return (float) sum /storeScore.size();

        return 0;
    }

    @Override
    public int getNumberOfScores(int storeId) {
        return storeScore.size();
    }

}
