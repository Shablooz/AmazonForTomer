package BGU.Group13B.backend.Repositories.Implementations.ReviewRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IRepositoryReview;
import BGU.Group13B.backend.storePackage.Review;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Entity
public class ReviewRepositoryAsList implements IRepositoryReview {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;


    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    @JoinTable(name = "ReviewRepositoryAsList_review",
            joinColumns = {@JoinColumn(name = "ReviewRepositoryAsList_id", referencedColumnName = "id")},
            inverseJoinColumns = {
                    @JoinColumn(name = "store_id", referencedColumnName = "storeId"),
                    @JoinColumn(name = "product_id", referencedColumnName = "productId"),
                    @JoinColumn(name = "user_id", referencedColumnName = "userId")})
    @MapKeyJoinColumn(name = "userId")
    Map<Integer, Review> reviews;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ReviewRepositoryAsList_scores",
            joinColumns = {@JoinColumn(name = "ReviewRepositoryAsList_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "user_Id")
    @Column(name = "score")
    Map<Integer, Integer> scores;


    public ReviewRepositoryAsList() {
        reviews = new HashMap<>();
        scores = new HashMap<>();
    }

    @Override
    public synchronized void addReview(String review, int storeId, int productId, int userId) {
        if (reviews.putIfAbsent(userId, new Review(review, storeId, productId, userId))!=null)
            throw new IllegalArgumentException("User already reviewed this product");
    }

    @Override
    public synchronized void removeReview(int storeId, int productId, int userId) {
        if (!reviews.containsKey(userId))
            throw new IllegalArgumentException("User didn't review this product");
        reviews.remove(userId);

    }

    @Override
    public synchronized Review getReview(int storeId, int productId, int userId) {
        if (!reviews.containsKey(userId))
            throw new IllegalArgumentException("User didn't review this product");
        return reviews.get(userId);
    }

    @Override
    public synchronized List<Review> getAllReviews(int storeId, int productId) {
        return reviews.values().stream().toList();
    }

    @Override
    public synchronized float getProductScore(int storeId, int productId) {
        Integer sum =  0;
        for (var score : scores.values())
            sum += score;
        if(scores.size()!=0)
            return (float) sum /scores.size();
        return 0;
    }
    @Override
    public synchronized float getProductScoreUser(int storeId, int productId, int userId) {
        if (!scores.containsKey(userId))
            throw new IllegalArgumentException("User didn't score this product");
        return scores.get(userId);
    }

    @Override
    public synchronized void addAndSetProductScore(int storeId, int productId, int userId, int score) {
        if (scores.containsKey(userId))
            throw new IllegalArgumentException("User already scored this product");
        if (score < 0 || score > 5)
            throw new IllegalArgumentException("Score must be between 0 to 5");
        scores.put(userId, score);

    }

    @Override
    public synchronized void removeProductScore(int storeId, int productId, int userId) {
        if (!scores.containsKey(userId))
            throw new IllegalArgumentException("User didn't score this product");
        scores.remove(userId);
    }



    @Override
    public synchronized void removeProductData(int storeId, int productId) {
        reviews.clear();
        scores.clear();
    }


}
