package BGU.Group13B.backend.Repositories.Implementations.StorePurchasePolicyRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IStorePurchasePolicyRepository;
import BGU.Group13B.backend.storePackage.PurchasePolicy;

import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListSet;

public class StorePurchasePolicyRepositoryAsList implements IStorePurchasePolicyRepository {

    private final ConcurrentSkipListSet<PurchasePolicy> storePurchasePolicies;

    public StorePurchasePolicyRepositoryAsList() {
        this.storePurchasePolicies = new ConcurrentSkipListSet<>(Comparator.comparingInt(PurchasePolicy::getParentId));
    }

    @Override
    public void insertPurchasePolicy(PurchasePolicy purchasePolicy) {
        //if found throw error
        storePurchasePolicies.stream().filter(p -> p.getParentId() == purchasePolicy.getParentId())
                .findFirst().ifPresent(p -> {
                    throw new IllegalArgumentException("Purchase policy already exists for store" + purchasePolicy.getParentId());});

        storePurchasePolicies.add(purchasePolicy);
    }

    @Override
    public void deletePurchasePolicy(int storeId) {
        //throw error if not found
        storePurchasePolicies.stream().filter(p -> p.getParentId() == storeId)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Purchase policy not found for store" + storeId));
        storePurchasePolicies.removeIf(p -> p.getParentId() == storeId);
    }

    @Override
    public PurchasePolicy getPurchasePolicy(int storeId) {
        return storePurchasePolicies.stream().filter(p -> p.getParentId() == storeId)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Purchase policy not found for store" + storeId));
    }

    @Override
    public void reset() {
        storePurchasePolicies.clear();
    }
}
