package BGU.Group13B.backend.Repositories.Interfaces;

public interface IPurchasePolicyRootsRepository {
    int addPurchasePolicyRoot(int storeId, int conditionRootId);
    int getPurchasePolicyRoot(int storeId);
    void removePurchasePolicyRoot(int storeId);
    void setSaveMode(boolean saveMode);
}
