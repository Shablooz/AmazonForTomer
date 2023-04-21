package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.storePackage.PurchasePolicy;

import java.util.Set;

public interface IProductPurchasePolicyRepository {
    void insertPurchasePolicy(int storeId, PurchasePolicy purchasePolicy);

    void deletePurchasePolicy(int storeId, int productId);

    PurchasePolicy getPurchasePolicy(int storeId, int productId);

    Set<PurchasePolicy> getStoreProductsPurchasePolicies(int storeId);

}
