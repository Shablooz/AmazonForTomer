package BGU.Group13B.backend.User;

import java.io.Serializable;
import java.util.Objects;

public class BasketId implements Serializable {

    private int userId;
    private int storeId;

    public BasketId() {
    }

    public BasketId(int userId, int storeId) {
        this.userId = userId;
        this.storeId = storeId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasketId basketId = (BasketId) o;
        return userId == basketId.userId && storeId == basketId.storeId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, storeId);
    }
}
