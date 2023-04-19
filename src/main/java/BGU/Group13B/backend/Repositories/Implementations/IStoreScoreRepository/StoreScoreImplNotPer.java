package BGU.Group13B.backend.Repositories.Implementations.IStoreScoreRepository;

import BGU.Group13B.backend.Repositories.Interfaces.IStoreScore;

import java.util.concurrent.ConcurrentHashMap;

public class StoreScoreImplNotPer implements IStoreScore {

    ConcurrentHashMap<Integer, Integer> storeScore;

    @Override
    public void addStoreScore(int userId, int storeId, int score) {
        if(storeScore.containsKey(storeId))
            throw new IllegalArgumentException("User already scored this store");
        storeScore.put(storeId, score);
    }

    @Override
    public void removeStoreScore(int userId, int storeId) {
        if(!storeScore.containsKey(storeId))
            throw new IllegalArgumentException("User didn't score this store");
        storeScore.remove(storeId);
    }

    @Override
    public void modifyStoreScore(int userId, int storeId, int score) {
        if(!storeScore.containsKey(storeId))
            throw new IllegalArgumentException("User didn't score this store");
        storeScore.replace(storeId, score);
    }

    @Override
    public float getStoreScore(int storeId) {
        return (float) storeScore.reduceValues(0, Integer::sum) /storeScore.size();
    }

}
