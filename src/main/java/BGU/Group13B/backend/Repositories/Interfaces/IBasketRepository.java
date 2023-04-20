package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.User.Basket;
import BGU.Group13B.backend.storePackage.payment.PaymentAdapter;
import BGU.Group13B.service.callbacks.CalculatePriceOfBasket;

import java.util.Optional;
import java.util.Set;

public interface IBasketRepository {
    public Set<Basket> getUserBaskets(int userId);

    public void removeUserBasket(int userId, int storeId);

    Basket addUserBasket(int userId, int storeId);
}
