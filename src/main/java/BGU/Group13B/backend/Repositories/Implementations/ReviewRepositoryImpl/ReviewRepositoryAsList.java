package BGU.Group13B.backend.Repositories.Implementations.ReviewRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IRepositoryReview;
import BGU.Group13B.backend.storePackage.Review;

import java.util.List;

public class ReviewRepositoryAsList implements IRepositoryReview {

    List<Review> reviews;
    @Override
    public void addReview(String review, int storeId, int productId, int userId) {

    }

    @Override
    public void removeReview(int storeId, int productId, int userId) {

    }

    @Override
    public Review getReview(int storeId, int productId, int userId) {
        return null;
    }
}
