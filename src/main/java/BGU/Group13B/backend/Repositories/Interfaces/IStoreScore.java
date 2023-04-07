package BGU.Group13B.backend.Repositories.Interfaces;

public interface IStoreScore {
    void addStoreScore(int userId,int storeId ,int score);

    void removeStoreScore(int userId,int storeId);

    void modifyStoreScore(int userId,int storeId, int score);

    int getStoreScore(int storeId);
}
