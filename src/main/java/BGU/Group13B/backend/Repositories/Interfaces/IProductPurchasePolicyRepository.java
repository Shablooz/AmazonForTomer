package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.storePackage.PurchasePolicy;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseBounder;

import java.util.Set;

public interface IProductPurchasePolicyRepository {
    void insertPurchasePolicy(int storeId, PurchasePolicy purchasePolicy);

    void deletePurchasePolicy(int storeId, int productId);

    PurchasePolicy getPurchasePolicy(int storeId, int productId);

    Set<PurchasePolicy> getStoreProductsPurchasePolicies(int storeId);

    //will return a NEW purchase policy that is the merged purchase policy of all the products in the store
    PurchasePolicy getStoreProductsMergedPurchasePolicies(int storeId);

    //will return a NEW purchase bounder that is the merged quantity purchase bounder of all the products in the store
    PurchaseBounder getStoreProductsMergedPurchaseQuantityBounders(int storeId);

    //will return a NEW purchase bounder that is the merged price purchase bounder of all the products in the store
    PurchaseBounder getStoreProductsMergedPurchasePriceBounders(int storeId);

}
