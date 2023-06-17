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
        if (baskets.putIfAbsent(userId, ConcurrentHashMap.newKeySet()) == null
                || noStoreBasket(userId, storeId)) {
            Basket basket = new Basket(userId, storeId);
            baskets.get(userId).add(basket);
            return basket;
        }
        throw new IllegalArgumentException("User already has a basket associated with the storeId: " + storeId);
    }

    //for tests
    @Override
    public void addUserBasket(Basket basket) {
        //removeUserBasket(basket.getUserId(), basket.getStoreId());
        if (baskets.putIfAbsent(basket.getUserId(), ConcurrentHashMap.newKeySet()) == null)
            baskets.get(basket.getUserId()).add(basket);
    }

    private boolean noStoreBasket(int userId, int storeId) {
        return baskets.get(userId).stream().noneMatch(basket -> basket.getStoreId() == storeId);
    }

    @Override
    public void clearUserBaskets(int userId) {
        Set<Basket> userBaskets= getUserBaskets(userId);
        for(Basket b: userBaskets){
            b.clearBasket();
            removeUserBasket(userId,b.getStoreId());
        }
    }

}
