package BGU.Group13B.backend.storePackage.newDiscoutns;

import BGU.Group13B.backend.Repositories.Interfaces.IConditionRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IPurchasePolicyRootsRepository;
import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseExceedsPolicyException;
import BGU.Group13B.service.SingletonCollection;
import jakarta.persistence.*;

@Entity
public class PurchasePolicy {
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Condition condition;
    @Id
    private int storeId;
    @Transient
    private IConditionRepository conditionRepository;
    @Transient
    private IPurchasePolicyRootsRepository purchasePolicyRootsRepository;

    public PurchasePolicy(int storeId) {
        this.storeId = storeId;
        this.conditionRepository = SingletonCollection.getConditionRepository();
        this.purchasePolicyRootsRepository = SingletonCollection.getPurchasePolicyRootsRepository();

        int rootId = purchasePolicyRootsRepository.getPurchasePolicyRoot(storeId);
        if (rootId != -1) {
            condition = conditionRepository.getCondition(rootId);
        }
    }

    public PurchasePolicy() {
        this.storeId = 0;
        this.conditionRepository = SingletonCollection.getConditionRepository();
        this.purchasePolicyRootsRepository = SingletonCollection.getPurchasePolicyRootsRepository();

    }

    public synchronized void setCondition(int conditionRootId) {
        this.condition = SingletonCollection.getConditionRepository().getCondition(conditionRootId);
        SingletonCollection.getPurchasePolicyRootsRepository().addPurchasePolicyRoot(storeId, conditionRootId);
    }

    public void removeRoot() {
        SingletonCollection.getPurchasePolicyRootsRepository().removePurchasePolicyRoot(storeId);
    }

    public void satisfied(BasketInfo basketInfo, UserInfo userInfo) throws PurchaseExceedsPolicyException {
        if (condition != null)
            condition.satisfied(basketInfo, userInfo);
    }

    public Condition getRootCondition() {
        return condition;
    }

    public void resetPurchasePolicy() {
        this.condition = null;
        SingletonCollection.getPurchasePolicyRootsRepository().removePurchasePolicyRoot(storeId);
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public IConditionRepository getConditionRepository() {
        return conditionRepository;
    }

    public void setConditionRepository(IConditionRepository conditionRepository) {
        this.conditionRepository = conditionRepository;
    }

    public IPurchasePolicyRootsRepository getPurchasePolicyRootsRepository() {
        return purchasePolicyRootsRepository;
    }

    public void setPurchasePolicyRootsRepository(IPurchasePolicyRootsRepository purchasePolicyRootsRepository) {
        this.purchasePolicyRootsRepository = purchasePolicyRootsRepository;
    }
}
