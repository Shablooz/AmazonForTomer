package BGU.Group13B.backend.Repositories.Interfaces;

public interface IStoreScore {
    void addStoreScore(int userId, int score);

    void removeStoreScore(int userId);

    void modifyStoreScore(int userId, int score);

    int getStoreScore();
}
