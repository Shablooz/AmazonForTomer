package BGU.Group13B.service.callbacks;

import BGU.Group13B.backend.User.BasketProduct;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@FunctionalInterface
public interface CalculatePriceOfBasket {
    double apply(double totalAmount, ConcurrentLinkedQueue<BasketProduct> successfulProducts,
                 int storeId,
                 String storeCoupons);
}
