package BGU.Group13B.backend.storePackage.newDiscoutns;

import BGU.Group13B.backend.Repositories.Interfaces.IConditionRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IPurchasePolicyRootsRepository;
import BGU.Group13B.backend.User.Basket;
import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.PurchaseFailedException;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;
import BGU.Group13B.backend.storePackage.permissions.DefaultFounderFunctionality;
import BGU.Group13B.backend.storePackage.permissions.DefaultOwnerFunctionality;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseExceedsPolicyException;
import BGU.Group13B.service.SingletonCollection;


public class PurchasePolicy {
    private Condition condition;
    private final int storeId;

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

    public Condition getRootCondition() {
        return condition;
    }
}
