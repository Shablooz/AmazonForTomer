package BGU.Group13B.backend.Repositories.Implementations.BasketReposistoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IBasketProductRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IBasketRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IProductHistoryRepository;
import BGU.Group13B.backend.User.Basket;
import BGU.Group13B.backend.storePackage.payment.PaymentAdapter;
import BGU.Group13B.service.CalculatePriceOfBasket;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BasketRepositoryAsHashMap implements IBasketRepository {
    private final ConcurrentHashMap<Integer/*userId*/, Set<Basket>> baskets;

    public BasketRepositoryAsHashMap() {
        this.baskets = new ConcurrentHashMap<>();
    }

    public Optional<Set<Basket>> getUserBaskets(int userId) {
        if (!baskets.containsKey(userId))
            return Optional.empty();
        return Optional.of(baskets.get(userId));
    }

    public void removeUserBasket(int userId, int storeId) {
        if (!baskets.containsKey(userId))
            throw new NoSuchElementException("User does not have the basket associated with the storeId: " + storeId);
        baskets.get(userId).removeIf(basket -> basket.getStoreId() == storeId);
    }

    public void addUserBasket(int userId, int storeId, IBasketProductRepository productRepository,
                              PaymentAdapter paymentAdapter, IProductHistoryRepository productHistoryRepository,
                              CalculatePriceOfBasket calculatePriceOfBasket) {
        if (baskets.putIfAbsent(userId, ConcurrentHashMap.newKeySet()) != null)
            baskets.get(userId).add(new Basket(userId, storeId, productRepository,
                    paymentAdapter, productHistoryRepository, calculatePriceOfBasket));
    }

}
