package BGU.Group13B.service.callbacks;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseExceedsPolicyException;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@FunctionalInterface
public interface CalculatePriceOfBasket {
    double apply(int storeId, BasketInfo basketInfo, UserInfo userInfo,List<String> coupons) throws PurchaseExceedsPolicyException;// add date of birth, add product discounts => UserInfo, List<String>
}
