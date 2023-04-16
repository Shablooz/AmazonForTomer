package BGU.Group13B.service;

import BGU.Group13B.backend.User.BasketProduct;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@FunctionalInterface
public interface CalculatePriceOfBasket {
    double apply(double totalAmount, ConcurrentLinkedQueue<BasketProduct> successfulProducts,
                 int storeId,
                 String storeCoupons);
}
