package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.StoreDiscount;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.productDisountTree.DiscountAccumulationNode;

public interface IDiscountAccumulationRepository {
    DiscountAccumulationNode addDiscountAsRoot(StoreDiscount discount);


    DiscountAccumulationNode addDiscountToMAXRoot(int rootId, StoreDiscount discount);
    DiscountAccumulationNode addDiscountToXORRoot(int rootId, StoreDiscount discount);
    DiscountAccumulationNode addDiscountToADDRoot(int rootId, StoreDiscount discount);

    DiscountAccumulationNode getDiscountAccumulationNode(int nodeId);
    void removeDiscountAccumulationNode(int nodeId);

    void reset();

    void setSaveMode(boolean saveMode);
}
