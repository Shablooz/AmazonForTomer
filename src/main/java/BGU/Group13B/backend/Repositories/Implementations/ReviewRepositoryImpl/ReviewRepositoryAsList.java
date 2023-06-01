package BGU.Group13B.backend.Repositories.Implementations.ReviewRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IRepositoryReview;
import BGU.Group13B.backend.storePackage.Review;
import jakarta.persistence.*;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
@Entity
public class ReviewRepositoryAsList implements IRepositoryReview {

    @Id
    private int id;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "ReviewRepositoryAsList_review",
            joinColumns = {@JoinColumn(name = "reviewrepositoryaslist_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "review_userid", referencedColumnName = "storeId"),
                    @JoinColumn(name = "review_storeid", referencedColumnName = "productId"),
                    @JoinColumn(name = "review_productid", referencedColumnName = "userId")})
    @MapKey(name = "userId")
    ConcurrentHashMap<Integer, Review> reviews;

    @ElementCollection
    @CollectionTable(name = "ReviewRepositoryAsList_scores",
            joinColumns = {@JoinColumn(name = "reviewrepositoryaslist_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "user_id")
    @Column(name = "score")
    ConcurrentHashMap<Integer, Integer> scores;


    public ReviewRepositoryAsList() {
        reviews = new ConcurrentHashMap<>();
        scores = new ConcurrentHashMap<>();
    }

    @Override
    public void addReview(String review, int storeId, int productId, int userId) {
        if (reviews.putIfAbsent(userId, new Review(review, storeId, productId, userId))!=null)
            throw new IllegalArgumentException("User already reviewed this product");
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
    public List<Review> getAllReviews(int storeId, int productId) {
        return reviews.values().stream().toList();
    }

    @Override
    public float getProductScore(int storeId, int productId) {
        Integer sum = scores.reduceValues(0, Integer::sum);
        if(sum!=null)
            return (float) sum /scores.size();
        return 0;
    }
    @Override
    public float getProductScoreUser(int storeId, int productId, int userId) {
        if (!scores.containsKey(userId))
            throw new IllegalArgumentException("User didn't score this product");
        return scores.get(userId);
    }

    @Override
    public void addAndSetProductScore(int storeId, int productId, int userId, int score) {
        if (scores.containsKey(userId))
            throw new IllegalArgumentException("User already scored this product");
        if (score < 0 || score > 5)
            throw new IllegalArgumentException("Score must be between 0 to 5");
        scores.put(userId, score);

    }

    @Override
    public void removeProductScore(int storeId, int productId, int userId) {
        if (!scores.containsKey(userId))
            throw new IllegalArgumentException("User didn't score this product");
        scores.remove(userId);
    }



    @Override
    public void removeProductData(int storeId, int productId) {
        reviews.clear();
        scores.clear();
    }
}
