package BGU.Group13B.backend.Repositories.Implementations.ReviewRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IRepositoryReview;
import BGU.Group13B.backend.storePackage.Review;

import java.util.concurrent.ConcurrentHashMap;

public class ReviewRepoSingle implements IRepositoryReview {



    ConcurrentHashMap<Integer/*store Id */,ReviewRepositoryAsList> implementations;


    public ReviewRepoSingle() {
        implementations = new ConcurrentHashMap<>();
    }



    @Override
    public void addReview(String review, int storeId, int productId, int userId) {
        implementations.putIfAbsent(storeId,new ReviewRepositoryAsList());
        implementations.get(storeId).addReview(review,storeId,productId,userId);
    }

    @Override
    public void removeReview(int storeId, int productId, int userId) {
        implementations.putIfAbsent(storeId,new ReviewRepositoryAsList());
        implementations.get(storeId).removeReview(storeId,productId,userId);
    }

    @Override
    public Review getReview(int storeId, int productId, int userId) {
        implementations.putIfAbsent(storeId,new ReviewRepositoryAsList());
        return implementations.get(storeId).getReview(storeId,productId,userId);
    }

    @Override
    public float getProductScore(int storeId, int productId) {
        implementations.putIfAbsent(storeId,new ReviewRepositoryAsList());
        return implementations.get(storeId).getProductScore(storeId,productId);
    }

    @Override
    public void addAndSetProductScore(int storeId, int productId, int userId, int score) {
        implementations.putIfAbsent(storeId,new ReviewRepositoryAsList());
        implementations.get(storeId).addAndSetProductScore(storeId,productId,userId,score);
    }

    @Override
    public void removeProductScore(int storeId, int productId, int userId) {
        implementations.putIfAbsent(storeId,new ReviewRepositoryAsList());
        implementations.get(storeId).removeProductScore(storeId,productId,userId);
    }

    @Override
    public void removeProductData(int storeId, int productId) {
        if(!implementations.containsKey(storeId))
            return;
        implementations.get(storeId).removeProductData(storeId,productId);
        //TODO check this with tomer
    }

}
