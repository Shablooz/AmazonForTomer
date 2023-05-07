package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.storePackage.PurchasePolicy;

public interface IStorePurchasePolicyRepository {
    void insertPurchasePolicy(PurchasePolicy purchasePolicy);

    void deletePurchasePolicy(int storeId);

    PurchasePolicy getPurchasePolicy(int storeId);

    void reset();
}
