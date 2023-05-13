package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.storePackage.Review;

public interface IRepositoryReview {
    void addReview(String review, int storeId, int productId, int userId);
    void removeReview(int storeId, int productId, int userId);
    Review getReview(int storeId, int productId, int userId);

    float getProductScore(int storeId,int productId);

    void addAndSetProductScore(int storeId, int productId, int userId, int score);
    void removeProductScore(int storeId, int productId, int userId);

    void removeProductData(int storeId, int productId);
}
