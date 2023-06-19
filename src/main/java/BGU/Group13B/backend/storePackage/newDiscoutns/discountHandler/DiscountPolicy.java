
package BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler;

import BGU.Group13B.backend.Repositories.Interfaces.IConditionRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IDiscountAccumulationRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IDiscountRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IStoreDiscountRootsRepository;
import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.UserInfo;

import BGU.Group13B.backend.storePackage.newDiscoutns.DiscountInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.productDisountTree.*;
import BGU.Group13B.frontEnd.components.views.ManageDiscountsView;
import BGU.Group13B.service.SingletonCollection;
import BGU.Group13B.service.info.DiscountAccumulationTreeInfo;


import java.time.LocalDate;
import java.util.*;

//NO NEED TO PERSIST UWU
public class DiscountPolicy {


    private int id;

    private int storeId;

    private DiscountAccumulationNode discountAccumulationTree;
    private IStoreDiscountRootsRepository discountRootsRepository;
    private IDiscountAccumulationRepository discountAccumulationRepository;
    private IDiscountRepository discountRepository;
    private IConditionRepository conditionRepository;

    public DiscountPolicy(int storeId) {
        this.storeId = storeId;

        discountRepository = SingletonCollection.getDiscountRepository();
        discountAccumulationRepository = SingletonCollection.getDiscountAccumulationRepository();
        conditionRepository = SingletonCollection.getConditionRepository();
        discountRootsRepository = SingletonCollection.getStoreDiscountRootsRepository();
        int discountRoot = discountRootsRepository.getStoreDiscountRoot(storeId);
        if (discountRoot != -1) {
            discountAccumulationTree = discountAccumulationRepository.getDiscountAccumulationNode(discountRoot);
        }


    }


    public IDiscountRepository getDiscountRepository() {
        return SingletonCollection.getDiscountRepository();
    }

    /**
     * <h1>Discount crud</h1>
     */
    public int addStoreDiscount(int conditionId, double discountPercentage, LocalDate expirationDate, String coupon) {
        Condition condition = SingletonCollection.getConditionRepository().getCondition(conditionId);
        return getDiscountRepository().addStoreDiscount(storeId, condition, discountPercentage, expirationDate, coupon);

    }

    public int addStoreDiscount(double discountPercentage, LocalDate expirationDate, String coupon) {
        var v = getDiscountRepository().addStoreDiscount(storeId, discountPercentage, expirationDate, coupon);
        return v;
    }

    public int addStoreDiscount(int conditionId, double discountPercentage, LocalDate expirationDate) {
        Condition condition = SingletonCollection.getConditionRepository().
                getCondition(conditionId);
        var v = getDiscountRepository().addStoreDiscount(storeId, condition, discountPercentage, expirationDate);
        return v;
    }

    public int addStoreDiscount(double discountPercentage, LocalDate expirationDate) {
        var v = getDiscountRepository().addStoreDiscount(storeId, discountPercentage, expirationDate);
        return v;
    }

    public int addCategoryDiscount(int conditionId, double discountPercentage, LocalDate expirationDate, String category, String coupon) {
        Condition condition = SingletonCollection.getConditionRepository().getCondition(conditionId);
        var v = getDiscountRepository().addCategoryDiscount(storeId, condition, discountPercentage, expirationDate, category, coupon);
        return v;
    }

    public int addCategoryDiscount(double discountPercentage, LocalDate expirationDate, String category, String coupon) {
        var v = getDiscountRepository().addCategoryDiscount(storeId, discountPercentage, expirationDate, category, coupon);
        return v;
    }


    public int addCategoryDiscount(int conditionId, double discountPercentage, LocalDate expirationDate, String category) {
        Condition condition = SingletonCollection.getConditionRepository().getCondition(conditionId);
        var v = getDiscountRepository().addCategoryDiscount(storeId, condition, discountPercentage, expirationDate, category);
        return v;
    }

    public int addCategoryDiscount(double discountPercentage, LocalDate expirationDate, String category) {
        var v = getDiscountRepository().addCategoryDiscount(storeId, discountPercentage, expirationDate, category);
        return v;
    }

    public int addProductDiscount(int conditionId, double discountPercentage, LocalDate expirationDate, int productId, String coupon) {
        Condition condition = SingletonCollection.getConditionRepository().getCondition(conditionId);
        var v = getDiscountRepository().addProductDiscount(storeId, condition, discountPercentage, expirationDate, productId, coupon);
        return v;
    }

    public int addProductDiscount(double discountPercentage, LocalDate expirationDate, int productId, String coupon) {
        var v = getDiscountRepository().addProductDiscount(storeId, discountPercentage, expirationDate, productId, coupon);
        return v;
    }

    public int addProductDiscount(int conditionId, double discountPercentage, LocalDate expirationDate, int productId) {
        Condition condition = SingletonCollection.getConditionRepository().getCondition(conditionId);
        var v = getDiscountRepository().addProductDiscount(storeId, condition, discountPercentage, expirationDate, productId);
        return v;
    }

    public int addProductDiscount(double discountPercentage, LocalDate expirationDate, int productId) {
        var v = getDiscountRepository().addProductDiscount(storeId, discountPercentage, expirationDate, productId);
        return v;
    }

    public List<StoreDiscount> getStoreDiscounts() {
        return getDiscountRepository().getStoreDiscounts(storeId);
    }

    public StoreDiscount getDiscount(int discountId) {
        return getDiscountRepository().getDiscount(discountId, storeId);
    }

    public void removeDiscount(int discountId) {
        getDiscountRepository().removeDiscount(discountId, storeId);
    }


    /**
     * <h1>Discount Accumulation Tree</h1>
     */

    public void addDiscountAsRoot(int discountId) {
        StoreDiscount discount = getDiscount(discountId);
        discountAccumulationTree = SingletonCollection.getDiscountAccumulationRepository().addDiscountAsRoot(discount);
        updateStoreDiscountRoot();
    }

    public void addDiscountToXORRoot(int discountId) {
        StoreDiscount discount = getDiscount(discountId);
        if (discountAccumulationTree == null) {
            throw new IllegalArgumentException("root is empty");
        }


        discountAccumulationTree = SingletonCollection.getDiscountAccumulationRepository().
                addDiscountToXORRoot(discountAccumulationTree.getDiscountNodeId(), discount);
        updateStoreDiscountRoot();
    }

    public void addDiscountToMAXRoot(int discountId) {
        StoreDiscount discount = getDiscount(discountId);
        if (discountAccumulationTree == null) {
            throw new IllegalArgumentException("root is empty");
        }

        discountAccumulationTree = SingletonCollection.getDiscountAccumulationRepository().
                addDiscountToMAXRoot(discountAccumulationTree.getDiscountNodeId(), discount);
        updateStoreDiscountRoot();

    }

    public void addDiscountToADDRoot(int discountId) {
        StoreDiscount discount = getDiscount(discountId);
        if (discountAccumulationTree == null) {
            throw new IllegalArgumentException("root is empty");
        }

        discountAccumulationTree = SingletonCollection.getDiscountAccumulationRepository().
                addDiscountToADDRoot(discountAccumulationTree.getDiscountNodeId(), discount);
        updateStoreDiscountRoot();

    }

    /**
     * <h1>Store Discount Root</h1>
     */

    private void updateStoreDiscountRoot() {
        SingletonCollection.getStoreDiscountRootsRepository().setStoreDiscountRoot(storeId, discountAccumulationTree.getDiscountNodeId());
    }

    /**
     * <h1>Calculate Discount</h1>
     */

    public double calculatePriceOfBasket(BasketInfo basketInfo, UserInfo userInfo, List<String> coupons) {
        if (discountAccumulationTree == null) {
            return basketInfo.basketProducts().
                    stream().mapToDouble(
                            product -> product.getQuantity() * product.getPrice()).
                    sum();
        }
        ProductDiscountMap finalProductDiscountMap = discountAccumulationTree.computeProductDiscountMap(basketInfo, userInfo, coupons);
        return basketInfo.basketProducts().
                stream().mapToDouble(
                        product -> {
                            double discount = finalProductDiscountMap.getProductDiscount(product.getProductId());
                            return product.getQuantity() * product.getPrice() * (1 - discount);
                        }).
                sum();
    }

    public void removeAllDiscounts() {
        getDiscountRepository().removeAllStoreDiscounts(storeId);
    }

    public void removeProductDiscount(int productId) {
        getDiscountRepository().removeStoreProductDiscounts(storeId, productId);
    }

    public DiscountAccumulationTreeInfo getDiscountAccumulationTree() {
        if (discountAccumulationTree == null)
            return new DiscountAccumulationTreeInfo(List.of(), List.of());
        return discountAccumulationTree.getInfo();
    }

    public void deleteStoreAccumulationTree() {
        if (discountAccumulationTree == null)
            return;

        SingletonCollection.getStoreDiscountRootsRepository().removeStoreDiscountRoot(storeId);
        discountAccumulationTree = null;
    }

    public int getId() {
        return id;

    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public void setDiscountAccumulationTree(DiscountAccumulationNode discountAccumulationTree) {
        this.discountAccumulationTree = discountAccumulationTree;
    }

    public IStoreDiscountRootsRepository getDiscountRootsRepository() {
        return discountRootsRepository;
    }

    public void setDiscountRootsRepository(IStoreDiscountRootsRepository discountRootsRepository) {
        this.discountRootsRepository = discountRootsRepository;
    }

    public IDiscountAccumulationRepository getDiscountAccumulationRepository() {
        return discountAccumulationRepository;
    }

    public void setDiscountAccumulationRepository(IDiscountAccumulationRepository discountAccumulationRepository) {
        this.discountAccumulationRepository = discountAccumulationRepository;
    }

    public void setDiscountRepository(IDiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    public IConditionRepository getConditionRepository() {
        return conditionRepository;
    }

    public void setConditionRepository(IConditionRepository conditionRepository) {
        this.conditionRepository = conditionRepository;
    }
}
