package BGU.Group13B.backend.Repositories.Implementations.IStoreScoreRepository;

import BGU.Group13B.backend.Repositories.Interfaces.IStoreScore;

import java.util.concurrent.ConcurrentHashMap;

public class StoreScoreSingle implements IStoreScore {


    ConcurrentHashMap<Integer/*store Id */,StoreScoreImplNotPer> implementations;

    public StoreScoreSingle() {
        implementations = new ConcurrentHashMap<>();
    }
    @Override
    public void addStoreScore(int userId, int storeId, int score) {
        implementations.putIfAbsent(storeId,new StoreScoreImplNotPer());
        implementations.get(storeId).addStoreScore(userId,storeId,score);
    }

    @Override
    public void removeStoreScore(int userId, int storeId) {
        implementations.putIfAbsent(storeId,new StoreScoreImplNotPer());
        implementations.get(storeId).removeStoreScore(userId,storeId);
    }

    @Override
    public void modifyStoreScore(int userId, int storeId, int score) {
        implementations.putIfAbsent(storeId,new StoreScoreImplNotPer());
        implementations.get(storeId).modifyStoreScore(userId,storeId,score);
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
    }
}
