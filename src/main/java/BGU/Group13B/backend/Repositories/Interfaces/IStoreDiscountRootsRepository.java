package BGU.Group13B.backend.Repositories.Interfaces;

public interface IStoreDiscountRootsRepository {
    void setStoreDiscountRoot(int storeId, int discountNodeId);
    int getStoreDiscountRoot(int storeId);
    void removeStoreDiscountRoot(int storeId);
    void reset();
    void setSaveMode(boolean saveMode);
}
