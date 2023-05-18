package BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.Product;
import BGU.Group13B.backend.storePackage.newDiscoutns.Logical.AND;
import BGU.Group13B.backend.storePackage.newDiscoutns.Logical.IMPLY;
import BGU.Group13B.backend.storePackage.newDiscoutns.Logical.LogicalCondition;
import BGU.Group13B.backend.storePackage.newDiscoutns.Logical.OR;
import BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.baseCase.CategoryPriceCondition;
import BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.baseCase.CategoryQuantityCondition;
import BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.baseCase.StorePriceCondition;
import BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.baseCase.TimeCondition;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.*;

public class DiscountPolicy {

    public enum DiscountAccumulation {
        MAX,
        SUM
    }

    private final DiscountAccumulation discountAccumulation;
    private final String NONE_COUPON = "";
    private final HashMap<String/*coupon*/, List<StoreDiscount>> storeDiscountsCoupons;
    private final HashMap<StoreDiscount, List<StoreDiscount>> badadDiscounts;//xor discounts
    //private final HashMap<StoreDiscount, List<StoreDiscount>> maxDiscounts;

    public DiscountPolicy() {
        discountAccumulation = DiscountAccumulation.MAX;
        storeDiscountsCoupons = new HashMap<>();
        badadDiscounts = new HashMap<>();
        //maxDiscounts = new HashMap<>();
    }

    private List<StoreDiscount> getAllValidDiscounts(List<String> coupons) {
        List<StoreDiscount> validDiscounts = new LinkedList<>();
        if (storeDiscountsCoupons.containsKey(NONE_COUPON))
            validDiscounts.addAll(storeDiscountsCoupons.get(NONE_COUPON));

        for (String coupon : coupons) {
            if (storeDiscountsCoupons.containsKey(coupon))
                validDiscounts.addAll(storeDiscountsCoupons.get(coupon));
        }

        //remove because he badad (xor lo naim)
        List<StoreDiscount> discountsToRemove = new LinkedList<>();

        for (StoreDiscount d : validDiscounts) {
            if (!badadDiscounts.containsKey(d))
                continue;
            for (StoreDiscount notFriend : badadDiscounts.get(d)) {
                if (validDiscounts.contains(notFriend))
                    discountsToRemove.add(d);
                discountsToRemove.add(notFriend);
            }

        }

        validDiscounts.removeAll(discountsToRemove);


        return validDiscounts;
    }

    public double calculatePriceOfBasket(BasketInfo basketInfo, UserInfo userInfo, List<String> coupons) {
        List<StoreDiscount> allValidDiscounts = getAllValidDiscounts(coupons);

        double finalPrice = 0;
        for (BasketProduct p : basketInfo.basketProducts()) {
            double discount;
            switch (discountAccumulation) {
                case MAX -> {
                    discount = allValidDiscounts.stream()
                            .map(d -> d.getProductDiscountPercentage(basketInfo, userInfo, p.getProductId()))
                            .max(Double::compare).orElse(0.0);
                    finalPrice += (1 - discount) * p.getQuantity() * p.getPrice();
                }
                case SUM -> {
                    discount = allValidDiscounts.stream()
                            .map(d -> d.getProductDiscountPercentage(basketInfo, userInfo, p.getProductId()))
                            .reduce(0.0, Double::sum);
                    finalPrice += (1 - Math.max(1, discount)) * p.getQuantity() * p.getPrice();
                }
            }
        }
        return finalPrice;
    }

    private double maxDiscount(double... discountPercentages) {
        double max = 0;
        for (double d : discountPercentages)
            max = Math.max(d, max);

        return max;
    }
//    public Product(int productId, int storeId, String name, String category, double price, int stockQuantity, String description) {
    public static void main(String[] args) {
        Condition condition = new AND(
                new OR(
                        new CategoryPriceCondition("milk", 100, 200),
                        new CategoryQuantityCondition("milk", 2, 20))
                ,new IMPLY(
                        new TimeCondition(LocalDateTime.now().plus(2, HOURS), LocalDateTime.now().plus(3, HOURS)),
                        new StorePriceCondition(69.420)));

        var productList = Stream.of(
                new Product(1, 1, "milk", "milk", 30, 1, "milk"),
                new Product(2, 1, "milk", "milk", 30, 1, "milk")
        ).map(BasketProduct::new).toList();

        var basketInfo = new BasketInfo(productList);
        System.out.println(condition.satisfied(basketInfo, null));
    }
}
