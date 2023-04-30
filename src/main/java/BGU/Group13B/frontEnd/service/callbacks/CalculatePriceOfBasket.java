package BGU.Group13B.frontEnd.service.callbacks;

import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseExceedsPolicyException;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@FunctionalInterface
public interface CalculatePriceOfBasket {
    double apply(double totalAmount, ConcurrentLinkedQueue<BasketProduct> successfulProducts,
                 int storeId,
                 String storeCoupons) throws PurchaseExceedsPolicyException;
}
