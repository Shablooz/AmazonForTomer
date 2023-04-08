package BGU.Group13B.backend.Repositories.Implementations.CartRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.ICartRepository;
import BGU.Group13B.backend.User.Basket;
import BGU.Group13B.backend.User.Cart;

import java.util.List;

public class CartRepositoryAsList implements ICartRepository {
    private final List<Cart> carts;

    public CartRepositoryAsList(List<Cart> carts) {
        this.carts = carts;
    }
}
