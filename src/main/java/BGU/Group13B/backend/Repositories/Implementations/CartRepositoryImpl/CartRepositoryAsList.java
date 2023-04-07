package BGU.Group13B.backend.Repositories.Implementations.CartRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.ICartRepository;
import BGU.Group13B.backend.User.Basket;

import java.util.List;

public class CartRepositoryAsList implements ICartRepository {
    private final List<Basket> baskets;

    public CartRepositoryAsList(List<Basket> baskets) {
        this.baskets = baskets;
    }
}
