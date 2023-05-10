package BGU.Group13B.backend.Repositories.Implementations.BasketReposistoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IBasketRepository;
import BGU.Group13B.backend.User.Basket;


import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BasketRepositoryAsHashMap implements IBasketRepository {
    private final ConcurrentHashMap<Integer/*userId*/, Set<Basket>> baskets;

    public BasketRepositoryAsHashMap() {
        this.baskets = new ConcurrentHashMap<>();
    }

    public Set<Basket> getUserBaskets(int userId) {
        return baskets.getOrDefault(userId, ConcurrentHashMap.newKeySet());
    }

    public void removeUserBasket(int userId, int storeId) {
        if (!baskets.containsKey(userId))
            throw new NoSuchElementException("User does not have the basket associated with the storeId: " + storeId);
        baskets.get(userId).removeIf(basket -> basket.getStoreId() == storeId);
    }

    public Basket addUserBasket(int userId, int storeId) {
        if (baskets.putIfAbsent(userId, ConcurrentHashMap.newKeySet()) == null) {
            Basket basket = new Basket(userId, storeId);
            baskets.get(userId).add(basket);
            return basket;
        }
        throw new IllegalArgumentException("User already has a basket associated with the storeId: " + storeId);
    }

    @Override
    public void removeAllUserBaskets(int userId) {
        baskets.remove(userId);
    }

}
