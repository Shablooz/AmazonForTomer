package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.storePackage.Review;

public interface IRepositoryReview {
    void addReview(String review, int storeId, int productId, int userId);
    void removeReview(int storeId, int productId, int userId);
    Review getReview(int storeId, int productId, int userId);

    int getProductScore(int storeId,int productId);

    int addAndSetScore(int storeId,int productId,int userId);
    int removeScore(int storeId,int productId,int userId);
}
