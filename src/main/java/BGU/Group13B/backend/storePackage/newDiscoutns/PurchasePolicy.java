package BGU.Group13B.backend.storePackage.newDiscoutns;

import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;

public class PurchasePolicy {
    private final Condition condition;

    private final IConditionRepository conditionRepository;
    private final IPurchasePolicyRootsRepository purchasePolicyRootsRepository;
    public PurchasePolicy(int storeId) {
        this.storeId = storeId;
        this.conditionRepository = SingletonCollection.getConditionRepository();
        this.purchasePolicyRootsRepository = SingletonCollection.getPurchasePolicyRootsRepository();

        int rootId = purchasePolicyRootsRepository.getPurchasePolicyRoot(storeId);
        if(rootId != -1){
            condition = conditionRepository.getCondition(rootId);
        }


    }

    public synchronized void setCondition(int conditionRootId) {
        this.condition = conditionRepository.getCondition(conditionRootId);
        this.purchasePolicyRootsRepository.addPurchasePolicyRoot(storeId, conditionRootId);
    }

    public void removeRoot() {
        this.purchasePolicyRootsRepository.removePurchasePolicyRoot(storeId);
    }

    public void satisfied(BasketInfo basketInfo, UserInfo userInfo) throws PurchaseExceedsPolicyException {
        if(condition != null)
            condition.satisfied(basketInfo, userInfo);
    }
}
