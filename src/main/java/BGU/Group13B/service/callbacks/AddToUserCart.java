package BGU.Group13B.service.callbacks;
@FunctionalInterface
public interface AddToUserCart {
    void apply(int userId, int storeId, int productId, int amount,  double newPrice);
}
