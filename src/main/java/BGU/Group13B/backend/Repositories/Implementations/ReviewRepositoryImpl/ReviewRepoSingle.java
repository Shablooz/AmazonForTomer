package BGU.Group13B.backend.Repositories.Implementations.ReviewRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IRepositoryReview;
import BGU.Group13B.backend.storePackage.Review;
import BGU.Group13B.service.SingletonCollection;
import jakarta.persistence.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
@Entity
public class ReviewRepoSingle implements IRepositoryReview {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    private boolean saveMode;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    @JoinTable(name = "SingleReviewRepo_ReviewRepositoryAsList",
            joinColumns = {@JoinColumn(name = "ReviewRepoSingle_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "ReviewRepositoryAsList_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "pair_hashcode")
    Map<Integer,ReviewRepositoryAsList> implementations;


    public ReviewRepoSingle() {
        implementations = new ConcurrentHashMap<>();
        saveMode = true;
    }

    public ReviewRepoSingle(boolean saveMode) {
        implementations = new ConcurrentHashMap<>();
        this.saveMode = saveMode;
    }


    @Override
    public void addReview(String review, int storeId, int productId, int userId) {
        implementations.putIfAbsent(Objects.hash(storeId,productId),new ReviewRepositoryAsList());
        implementations.get(Objects.hash(storeId,productId)).addReview(review,storeId,productId,userId);
        save();

    }

    @Override
    public void removeReview(int storeId, int productId, int userId) {
        implementations.putIfAbsent(Objects.hash(storeId,productId) ,new ReviewRepositoryAsList());
        implementations.get(Objects.hash(storeId,productId)).removeReview(storeId,productId,userId);
        save();
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
        save();

    }

    @Override
    public void removeProductScore(int storeId, int productId, int userId) {
        implementations.putIfAbsent(Objects.hash(storeId,productId),new ReviewRepositoryAsList());
        implementations.get(Objects.hash(storeId,productId)).removeProductScore(storeId,productId,userId);
        save();

    }

    @Override
    public void removeProductData(int storeId, int productId) {
        //won't throw an error if the product doesn't exist / has no reviews
        implementations.remove(Objects.hash(storeId,productId));
    }

    public void save(){
        if(saveMode)
            SingletonCollection.getContext().getBean(ReviewRepoSingleService.class).save(this);
    }

}
