
package BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler;

import BGU.Group13B.backend.Repositories.Interfaces.IConditionRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IDiscountAccumulationRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IDiscountRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IStoreDiscountRootsRepository;
import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.UserInfo;

import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.productDisountTree.*;
import BGU.Group13B.service.SingletonCollection;

import java.time.LocalDate;
import java.util.*;


public class DiscountPolicy {

    private final int storeId;
    private DiscountAccumulationNode discountAccumulationTree;
    private final IStoreDiscountRootsRepository discountRootsRepository;
    private final IDiscountAccumulationRepository discountAccumulationRepository;
    private final IDiscountRepository discountRepository;
    private final IConditionRepository conditionRepository;

    public DiscountPolicy(int storeId) {
        this.storeId = storeId;

        discountRepository = SingletonCollection.getDiscountRepository();
        discountAccumulationRepository = SingletonCollection.getDiscountAccumulationRepository();
        conditionRepository = SingletonCollection.getConditionRepository();
        discountRootsRepository = SingletonCollection.getStoreDiscountRootsRepository();
        int discountRoot = discountRootsRepository.getStoreDiscountRoot(storeId);
        if(discountRoot != -1){
            discountAccumulationTree = discountAccumulationRepository.getDiscountAccumulationNode(discountRoot);
        }


    }


    /**
     * <h1>Discount crud</h1>
     */
    public int addStoreDiscount(int conditionId, double discountPercentage, LocalDate expirationDate, String coupon) {
        Condition condition = conditionRepository.getCondition(conditionId);
        return discountRepository.addStoreDiscount(storeId, condition, discountPercentage, expirationDate, coupon);
    }

    public int addStoreDiscount(double discountPercentage, LocalDate expirationDate, String coupon) {
        return discountRepository.addStoreDiscount(storeId, discountPercentage, expirationDate, coupon);
    }

    public int addStoreDiscount(int conditionId, double discountPercentage, LocalDate expirationDate) {
        Condition condition = conditionRepository.getCondition(conditionId);
        return discountRepository.addStoreDiscount(storeId, condition, discountPercentage, expirationDate);
    }

    public int addStoreDiscount(double discountPercentage, LocalDate expirationDate) {
        return discountRepository.addStoreDiscount(storeId, discountPercentage, expirationDate);
    }

    public int addCategoryDiscount(int conditionId, double discountPercentage, LocalDate expirationDate, String category, String coupon) {
        Condition condition = conditionRepository.getCondition(conditionId);
        return discountRepository.addCategoryDiscount(storeId, condition, discountPercentage, expirationDate, category, coupon);
    }

    public int addCategoryDiscount(double discountPercentage, LocalDate expirationDate, String category, String coupon) {
        return discountRepository.addCategoryDiscount(storeId, discountPercentage, expirationDate, category, coupon);
    }

    public int addCategoryDiscount(int conditionId, double discountPercentage, LocalDate expirationDate, String category) {
        Condition condition = conditionRepository.getCondition(conditionId);
        return discountRepository.addCategoryDiscount(storeId, condition, discountPercentage, expirationDate, category);
    }

    public int addCategoryDiscount(double discountPercentage, LocalDate expirationDate, String category) {
        return discountRepository.addCategoryDiscount(storeId, discountPercentage, expirationDate, category);
    }

    public int addProductDiscount(int conditionId, double discountPercentage, LocalDate expirationDate, int productId, String coupon) {
        Condition condition = conditionRepository.getCondition(conditionId);
        return discountRepository.addProductDiscount(storeId, condition, discountPercentage, expirationDate, productId, coupon);
    }

    public int addProductDiscount(double discountPercentage, LocalDate expirationDate, int productId, String coupon) {
        return discountRepository.addProductDiscount(storeId, discountPercentage, expirationDate, productId, coupon);
    }

    public int addProductDiscount(int conditionId, double discountPercentage, LocalDate expirationDate, int productId) {
        Condition condition = conditionRepository.getCondition(conditionId);
        return discountRepository.addProductDiscount(storeId, condition, discountPercentage, expirationDate, productId);
    }

    public int addProductDiscount(double discountPercentage, LocalDate expirationDate, int productId) {
        return discountRepository.addProductDiscount(storeId, discountPercentage, expirationDate, productId);
    }

    public List<StoreDiscount> getStoreDiscounts() {
        return discountRepository.getStoreDiscounts(storeId);
    }

    public StoreDiscount getDiscount(int discountId) {
        return discountRepository.getDiscount(discountId, storeId);
    }

    public void removeDiscount(int discountId) {
        discountRepository.removeDiscount(discountId, storeId);
    }


    /**
     * <h1>Discount Accumulation Tree</h1>
     */

    public void addDiscountAsRoot(int discountId) {
        StoreDiscount discount = getDiscount(discountId);
        discountAccumulationTree = discountAccumulationRepository.addDiscountAsRoot(discount);
        updateStoreDiscountRoot();
    }

    public void addDiscountToXORRoot(int discountId) {
        StoreDiscount discount = getDiscount(discountId);
        if (discountAccumulationTree == null) {
            throw new IllegalArgumentException("root is empty");
        }

        discountAccumulationTree = discountAccumulationRepository.
                addDiscountToXORRoot(discountAccumulationTree.getDiscountNodeId(), discount);
        updateStoreDiscountRoot();

    }

    public void addDiscountToMAXRoot(int discountId) {
        StoreDiscount discount = getDiscount(discountId);
        if (discountAccumulationTree == null) {
            throw new IllegalArgumentException("root is empty");
        }

        discountAccumulationTree = discountAccumulationRepository.
                addDiscountToMAXRoot(discountAccumulationTree.getDiscountNodeId(), discount);
        updateStoreDiscountRoot();

    }

    public void addDiscountToADDRoot(int discountId) {
        StoreDiscount discount = getDiscount(discountId);
        if (discountAccumulationTree == null) {
            throw new IllegalArgumentException("root is empty");
        }

        discountAccumulationTree = discountAccumulationRepository.
                addDiscountToADDRoot(discountAccumulationTree.getDiscountNodeId(), discount);
        updateStoreDiscountRoot();

    }

    /**
     * <h1>Store Discount Root</h1>
     */

    private void updateStoreDiscountRoot(){
        discountRootsRepository.setStoreDiscountRoot(storeId, discountAccumulationTree.getDiscountNodeId());
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


    public static void main(String[] args) {
    }

    public void removeAllDiscounts() {
        discountRepository.removeAllStoreDiscounts(storeId);
    }

    public void removeProductDiscount(int productId){
        discountRepository.removeStoreProductDiscounts(storeId, productId);
    }
}
