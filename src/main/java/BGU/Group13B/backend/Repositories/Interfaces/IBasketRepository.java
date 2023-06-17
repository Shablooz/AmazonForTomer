package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.User.Basket;

import java.util.Set;

public interface IBasketRepository {
    public Set<Basket> getUserBaskets(int userId);

    public void removeUserBasket(int userId, int storeId);

    Basket addUserBasket(int userId, int storeId);

    void addUserBasket(Basket basket);

    public void setSaveMode(boolean saved);

    void clearUserBaskets(int userId);
}
