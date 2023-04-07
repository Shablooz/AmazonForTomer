package BGU.Group13B.backend.storePackage;

public class Review {
    private final String review;
    private final int storeId;
    private final int productId;
    private final int userId;

    public Review(String review, int storeId, int productId, int userId) {
        this.review = review;
        this.storeId = storeId;
        this.productId = productId;
        this.userId = userId;
    }

    public String getReview() {
        return review;
    }

    public int getStoreId() {
        return storeId;
    }

    public int getProductId() {
        return productId;
    }

    public int getUserId() {
        return userId;
    }
}
