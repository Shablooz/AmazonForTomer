package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.User.Basket;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

public interface IBasketRepository {
    public Optional<Set<Basket>> getUserBaskets(int userId);

    public void removeUserBasket(int userId, int storeId);

    public void addUserBasket(int userId, Basket basket) ;
}
