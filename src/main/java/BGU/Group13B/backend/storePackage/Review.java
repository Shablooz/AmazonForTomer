package BGU.Group13B.backend.storePackage;

import BGU.Group13B.service.SingletonCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Review {

    private final String review;
    @Id
    private final int storeId;
    @Id
    private final int productId;
    @Id
    private final int userId;
    private final String userName;

    public Review(String review, int storeId, int productId, int userId) {
        this.review = review;
        this.storeId = storeId;
        this.productId = productId;
        this.userId = userId;
        this.userName = "default";
    }

    public Review() {
        this.review = "";
        this.storeId = 0;
        this.productId = 0;
        this.userId = 0;
        this.userName = "default";
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

    public String getUserName() {
        return userName;
    }
}
