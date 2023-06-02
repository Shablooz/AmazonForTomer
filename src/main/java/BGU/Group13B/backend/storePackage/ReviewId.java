package BGU.Group13B.backend.storePackage;

import BGU.Group13B.frontEnd.GradeId;
import jakarta.persistence.Id;

import java.io.Serializable;
import java.util.Objects;

public class ReviewId implements Serializable {

    private int storeId;

    private int productId;

    private int userId;

    public ReviewId(int storeId, int productId, int userId) {
        this.storeId = storeId;
        this.productId = productId;
        this.userId = userId;
    }

    public ReviewId() {
        this.storeId = 0;
        this.productId = 0;
        this.userId = 0;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Constructors, getters, and setters
    // Make sure to override equals() and hashCode() methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReviewId reviewId = (ReviewId) o;
        return storeId == reviewId.storeId && userId == reviewId.userId && productId == reviewId.productId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, productId, storeId);
    }
}
