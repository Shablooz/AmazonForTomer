package BGU.Group13B.backend.Repositories.Implementations.ReviewRepositoryImpl;

import BGU.Group13B.backend.Pair;
import BGU.Group13B.backend.Repositories.Interfaces.IRepositoryReview;
import BGU.Group13B.backend.storePackage.Review;

import java.util.concurrent.ConcurrentHashMap;

public class ReviewRepoSingle implements IRepositoryReview {



    ConcurrentHashMap<Pair<Integer /*storeId*/, Integer /*productId*/>,ReviewRepositoryAsList> implementations;


    public ReviewRepoSingle() {
        implementations = new ConcurrentHashMap<>();
    }



    @Override
    public void addReview(String review, int storeId, int productId, int userId) {
        implementations.putIfAbsent(Pair.of(storeId, productId),new ReviewRepositoryAsList());
        implementations.get(Pair.of(storeId, productId)).addReview(review,storeId,productId,userId);
    }

    @Override
    public void removeReview(int storeId, int productId, int userId) {
        implementations.putIfAbsent(Pair.of(storeId, productId) ,new ReviewRepositoryAsList());
        implementations.get(Pair.of(storeId, productId)).removeReview(storeId,productId,userId);
    }

    @Override
    public Review getReview(int storeId, int productId, int userId) {
        implementations.putIfAbsent(Pair.of(storeId, productId),new ReviewRepositoryAsList());
        return implementations.get(Pair.of(storeId, productId)).getReview(storeId,productId,userId);
    }

    @Override
    public float getProductScore(int storeId, int productId) {
        implementations.putIfAbsent(Pair.of(storeId, productId),new ReviewRepositoryAsList());
        return implementations.get(Pair.of(storeId, productId)).getProductScore(storeId,productId);
    }

    @Override
    public void addAndSetProductScore(int storeId, int productId, int userId, int score) {
        implementations.putIfAbsent(Pair.of(storeId, productId),new ReviewRepositoryAsList());
        implementations.get(Pair.of(storeId, productId)).addAndSetProductScore(storeId,productId,userId,score);
    }

    @Override
    public void removeProductScore(int storeId, int productId, int userId) {
        implementations.putIfAbsent(Pair.of(storeId, productId),new ReviewRepositoryAsList());
        implementations.get(Pair.of(storeId, productId)).removeProductScore(storeId,productId,userId);
    }

    @Override
    public void removeProductData(int storeId, int productId) {
        //won't throw an error if the product doesn't exist / has no reviews
        implementations.remove(Pair.of(storeId, productId));
    }

}
