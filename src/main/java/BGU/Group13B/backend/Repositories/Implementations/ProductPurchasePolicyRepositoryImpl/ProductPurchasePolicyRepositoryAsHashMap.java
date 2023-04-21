package BGU.Group13B.backend.Repositories.Implementations.ProductPurchasePolicyRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IProductPurchasePolicyRepository;
import BGU.Group13B.backend.storePackage.PurchasePolicy;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseBounder;

import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class ProductPurchasePolicyRepositoryAsHashMap implements IProductPurchasePolicyRepository {

    private final ConcurrentHashMap<Integer /*storeId*/, ConcurrentSkipListSet<PurchasePolicy> /*product policies*/> storeProductPolicies;

    public ProductPurchasePolicyRepositoryAsHashMap() {
        this.storeProductPolicies = new ConcurrentHashMap<>();
    }

    @Override
    public void insertPurchasePolicy(int storeId, PurchasePolicy purchasePolicy) {
        if(!storeProductPolicies.containsKey(storeId))
            storeProductPolicies.put(storeId, new ConcurrentSkipListSet<>(Comparator.comparingInt(PurchasePolicy::getParentId)));

        //throw error if found
        storeProductPolicies.get(storeId).stream().filter(p -> p.getParentId() == purchasePolicy.getParentId())
                .findFirst().ifPresent(p -> {
                    throw new IllegalArgumentException("Purchase policy already exists for product" + purchasePolicy.getParentId());});

        storeProductPolicies.get(storeId).add(purchasePolicy);
    }

    @Override
    public void deletePurchasePolicy(int storeId, int productId) {
        //throw error if not found
        storeProductPolicies.get(storeId).stream().filter(p -> p.getParentId() == productId)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Purchase policy not found for product" + productId));

        storeProductPolicies.get(storeId).removeIf(p -> p.getParentId() == productId);
    }

    @Override
    public PurchasePolicy getPurchasePolicy(int storeId, int productId) {
        return storeProductPolicies.get(storeId).stream().filter(p -> p.getParentId() == productId)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Purchase policy not found for product" + productId));
    }

    @Override
    public Set<PurchasePolicy> getStoreProductsPurchasePolicies(int storeId) {
        //throw error if not found
        if(!storeProductPolicies.containsKey(storeId))
            throw new IllegalArgumentException("product purchase policies not found for store" + storeId);

        return storeProductPolicies.get(storeId);
    }

    @Override
    public PurchasePolicy getStoreProductsMergedPurchasePolicies(int storeId) {
        int maxQuantityLowerBound = 0;
        int minQuantityUpperBound = -1;
        int maxPriceLowerBound = 0;
        int minPriceUpperBound = -1;

        for(PurchasePolicy policy : storeProductPolicies.get(storeId)){
            if(policy.getQuantityLowerBound() > maxQuantityLowerBound)
                maxQuantityLowerBound = policy.getQuantityLowerBound();
            if(minQuantityUpperBound == -1 || (policy.getQuantityUpperBound() != -1 && policy.getQuantityUpperBound() < minQuantityUpperBound))
                minQuantityUpperBound = policy.getQuantityUpperBound();
            if(policy.getPriceLowerBound() > maxPriceLowerBound)
                maxPriceLowerBound = policy.getPriceLowerBound();
            if(minPriceUpperBound == -1 || (policy.getPriceUpperBound() != -1 && policy.getPriceUpperBound() < minPriceUpperBound))
                minPriceUpperBound = policy.getPriceUpperBound();
        }

        return new PurchasePolicy(maxQuantityLowerBound, minQuantityUpperBound, maxPriceLowerBound, minPriceUpperBound);

    }

    @Override
    public PurchaseBounder getStoreProductsMergedPurchaseQuantityBounders(int storeId) {
        int maxQuantityLowerBound = 0;
        int minQuantityUpperBound = -1;

        for(PurchasePolicy policy : storeProductPolicies.get(storeId)){
            if(policy.getQuantityLowerBound() > maxQuantityLowerBound)
                maxQuantityLowerBound = policy.getQuantityLowerBound();
            if(minQuantityUpperBound == -1 || (policy.getQuantityUpperBound() != -1 && policy.getQuantityUpperBound() < minQuantityUpperBound))
                minQuantityUpperBound = policy.getQuantityUpperBound();
        }
        return new PurchaseBounder(maxQuantityLowerBound, minQuantityUpperBound);
    }

    @Override
    public PurchaseBounder getStoreProductsMergedPurchasePriceBounders(int storeId) {
        int maxPriceLowerBound = 0;
        int minPriceUpperBound = -1;

        for(PurchasePolicy policy : storeProductPolicies.get(storeId)){
            if(policy.getPriceLowerBound() > maxPriceLowerBound)
                maxPriceLowerBound = policy.getPriceLowerBound();
            if(minPriceUpperBound == -1 || (policy.getPriceUpperBound() != -1 && policy.getPriceUpperBound() < minPriceUpperBound))
                minPriceUpperBound = policy.getPriceUpperBound();
        }
        return new PurchaseBounder(maxPriceLowerBound, minPriceUpperBound);
    }


}
