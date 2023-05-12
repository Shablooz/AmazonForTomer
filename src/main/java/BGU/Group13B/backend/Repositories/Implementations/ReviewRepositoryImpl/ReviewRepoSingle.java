package BGU.Group13B.backend.Repositories.Implementations.ReviewRepositoryImpl;

import BGU.Group13B.backend.Pair;
import BGU.Group13B.backend.Repositories.Interfaces.IRepositoryReview;
import BGU.Group13B.backend.storePackage.Review;

import java.util.concurrent.ConcurrentHashMap;

public class ReviewRepoSingle implements IRepositoryReview {



    ConcurrentHashMap<Pair<Integer,Integer> /*store Id */,ReviewRepositoryAsList> implementations;


    public ReviewRepoSingle() {
        implementations = new ConcurrentHashMap<>();
    }



    @Override
    public void addReview(String review, int storeId, int productId, int userId) {
        var pair= new Pair<>(storeId,productId);
        implementations.putIfAbsent(pair,new ReviewRepositoryAsList());
        implementations.get(pair).addReview(review,storeId,productId,userId);
    }

    @Override
    public void removeReview(int storeId, int productId, int userId) {
        var pair= new Pair<>(storeId,productId);
        implementations.putIfAbsent(pair,new ReviewRepositoryAsList());
        implementations.get(pair).removeReview(storeId,productId,userId);
    }

    @Override
    public Review getReview(int storeId, int productId, int userId) {
        var pair= new Pair<>(storeId,productId);
        implementations.putIfAbsent(pair,new ReviewRepositoryAsList());
        return implementations.get(pair).getReview(storeId,productId,userId);
    }

    @Override
    public float getProductScore(int storeId, int productId) {
        var pair= new Pair<>(storeId,productId);
        implementations.putIfAbsent(pair,new ReviewRepositoryAsList());
        return implementations.get(pair).getProductScore(storeId,productId);
    }

    @Override
    public void addAndSetProductScore(int storeId, int productId, int userId, int score) {
        var pair= new Pair<>(storeId,productId);
        implementations.putIfAbsent(pair,new ReviewRepositoryAsList());
        implementations.get(pair).addAndSetProductScore(storeId,productId,userId,score);
    }

    @Override
    public void removeProductScore(int storeId, int productId, int userId) {
        var pair= new Pair<>(storeId,productId);
        implementations.putIfAbsent(pair,new ReviewRepositoryAsList());
        implementations.get(pair).removeProductScore(storeId,productId,userId);
    }

}
