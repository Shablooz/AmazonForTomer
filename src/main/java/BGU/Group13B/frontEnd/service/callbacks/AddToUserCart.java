package BGU.Group13B.frontEnd.service.callbacks;
@FunctionalInterface
public interface AddToUserCart {
    void apply(int userId, int storeId, int productId);
}
