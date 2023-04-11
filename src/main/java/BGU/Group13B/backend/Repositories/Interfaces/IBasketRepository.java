package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.User.Basket;
import BGU.Group13B.backend.storePackage.payment.PaymentAdapter;
import BGU.Group13B.service.CalculatePriceOfBasket;

import java.util.Optional;
import java.util.Set;

public interface IBasketRepository {
    public Optional<Set<Basket>> getUserBaskets(int userId);

    public void removeUserBasket(int userId, int storeId);

    void addUserBasket(int userId, int storeId, IBasketProductRepository productRepository,
                       PaymentAdapter paymentAdapter, IProductHistoryRepository productHistoryRepository,
                       CalculatePriceOfBasket calculatePriceOfBasket);
}
