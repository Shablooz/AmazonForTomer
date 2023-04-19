package BGU.Group13B.backend.Repositories.Implementations.ReviewRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IRepositoryReview;
import BGU.Group13B.backend.storePackage.Review;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class ReviewRepositoryAsList implements IRepositoryReview {

    ConcurrentHashMap<Integer, Review> reviews;
    ConcurrentHashMap<Integer, Integer> scores;
    float score;

    public ReviewRepositoryAsList() {
        reviews = new ConcurrentHashMap<>();
        scores = new ConcurrentHashMap<>();
        score = 5;
    }

    @Override
    public void addReview(String review, int storeId, int productId, int userId) {
        if (reviews.containsKey(userId))
            throw new IllegalArgumentException("User already reviewed this product");
        reviews.put(userId, new Review(review, storeId, productId, userId));
    }

    @Override
    public void removeReview(int storeId, int productId, int userId) {
        if (!reviews.containsKey(userId))
            throw new IllegalArgumentException("User didn't review this product");
        reviews.remove(userId);
    }

    @Override
    public Review getReview(int storeId, int productId, int userId) {
        if (!reviews.containsKey(userId))
            throw new IllegalArgumentException("User didn't review this product");
        return reviews.get(userId);
    }

    @Override
    public float getProductScore(int storeId, int productId) {
        return score;
    }

    @Override
    public synchronized void addAndSetProductScore(int storeId, int productId, int userId, int score) {
        if (scores.containsKey(userId))
            throw new IllegalArgumentException("User already scored this product");
        this.score= (this.score*reviews.size()+score)/(reviews.size()+1);
        scores.put(userId, userId);

    }

    @Override
    public synchronized void removeProductScore(int storeId, int productId, int userId) {
        if (!scores.containsKey(userId))
            throw new IllegalArgumentException("User didn't score this product");

        this.score = (this.score * reviews.size() - scores.get(userId)) / (reviews.size() - 1);
        scores.remove(userId);
    }
}
