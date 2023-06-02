package BGU.Group13B.backend.Repositories.Implementations.ReviewRepositoryImpl;

import BGU.Group13B.backend.Pair;
import BGU.Group13B.backend.Repositories.Implementations.ReviewRepositoryImpl.Keys.ReviewRepoSinglePair;
import BGU.Group13B.backend.Repositories.Implementations.ReviewRepositoryImpl.Keys.ReviewRepoSinglePairId;
import BGU.Group13B.backend.Repositories.Interfaces.IRepositoryReview;
import BGU.Group13B.backend.storePackage.Review;
import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
@Entity
public class ReviewRepoSingle implements IRepositoryReview {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    @JoinTable(name = "SingleReviewRepo_ReviewRepositoryAsList",
            joinColumns = {@JoinColumn(name = "ReviewRepoSingle_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "ReviewRepositoryAsList_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "pair_hashcode")
    ConcurrentHashMap<Integer,ReviewRepositoryAsList> implementations;


    public ReviewRepoSingle() {
        implementations = new ConcurrentHashMap<>();
    }



    @Override
    public void addReview(String review, int storeId, int productId, int userId) {
        implementations.putIfAbsent(Objects.hash(storeId,productId),new ReviewRepositoryAsList());
        implementations.get(Objects.hash(storeId,productId)).addReview(review,storeId,productId,userId);
    }

    @Override
    public void removeReview(int storeId, int productId, int userId) {
        implementations.putIfAbsent(Objects.hash(storeId,productId) ,new ReviewRepositoryAsList());
        implementations.get(Objects.hash(storeId,productId)).removeReview(storeId,productId,userId);
    }

    @Override
    public Review getReview(int storeId, int productId, int userId) {
        implementations.putIfAbsent(Objects.hash(storeId,productId),new ReviewRepositoryAsList());
        return implementations.get(Objects.hash(storeId,productId)).getReview(storeId,productId,userId);
    }
    @Override
    public List<Review> getAllReviews(int storeId, int productId) {
        implementations.putIfAbsent(Objects.hash(storeId,productId),new ReviewRepositoryAsList());
        return implementations.get(Objects.hash(storeId,productId)).getAllReviews(storeId,productId);
    }

    @Override
    public float getProductScore(int storeId, int productId) {
        implementations.putIfAbsent(Objects.hash(storeId,productId),new ReviewRepositoryAsList());
        return implementations.get(Objects.hash(storeId,productId)).getProductScore(storeId,productId);
    }

    @Override
    public float getProductScoreUser(int storeId, int productId, int userId) {
        implementations.putIfAbsent(Objects.hash(storeId,productId),new ReviewRepositoryAsList());
        return implementations.get(Objects.hash(storeId,productId)).getProductScoreUser(storeId,productId,userId);
    }

    @Override
    public void addAndSetProductScore(int storeId, int productId, int userId, int score) {
        implementations.putIfAbsent(Objects.hash(storeId,productId),new ReviewRepositoryAsList());
        implementations.get(Objects.hash(storeId,productId)).addAndSetProductScore(storeId,productId,userId,score);
    }

    @Override
    public void removeProductScore(int storeId, int productId, int userId) {
        implementations.putIfAbsent(Objects.hash(storeId,productId),new ReviewRepositoryAsList());
        implementations.get(Objects.hash(storeId,productId)).removeProductScore(storeId,productId,userId);
    }

    @Override
    public void removeProductData(int storeId, int productId) {
        //won't throw an error if the product doesn't exist / has no reviews
        implementations.remove(Objects.hash(storeId,productId));
    }

}
