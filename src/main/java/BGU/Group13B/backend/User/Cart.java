package BGU.Group13B.backend.User;

import BGU.Group13B.backend.Repositories.Interfaces.IBasketRepository;
import BGU.Group13B.backend.storePackage.Product;
import BGU.Group13B.service.callbacks.CalculatePriceOfBasket;
import BGU.Group13B.service.SingletonCollection;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;


public class Cart {

    private final IBasketRepository basketRepository;
    private final int userId;
    private final CalculatePriceOfBasket calculatePriceOfBasket;

    public Cart(int userId) {
        this.basketRepository = SingletonCollection.getBasketRepository();
        this.userId = userId;
        this.calculatePriceOfBasket = SingletonCollection.getCalculatePriceOfBasket();
    }

    void purchaseCart(String address, String creditCardNumber,
                      String creditCardMonth, String creditCardYear,
                      String creditCardHolderFirstName, String creditCardHolderLastName,
                      String creditCardCcv, String id,
                      String creditCardType) {
        var userBaskets = basketRepository.getUserBaskets(userId);
        if (userBaskets.isEmpty()) {
            throw new NoSuchElementException("No baskets in cart");
        }
        for (var basket : userBaskets) {
            //basket.purchaseBasket(address, creditCardNumber, creditCardMonth, creditCardYear, creditCardHolderFirstName, creditCardHolderLastName, creditCardCcv, id, creditCardType, calculatePriceOfBasket);
            throw new RuntimeException("Not implemented");
            //fixme
        }
    }

    public void addProductToCart(int productId, int storeId) {
        //if user has basket with storeID, add product to it. else, create new basket and add product to it.
        boolean added = false;
        Set<Basket> userBaskets = basketRepository.getUserBaskets(userId);
        for (var basket : userBaskets) {
            if (basket.getStoreId() == storeId) {
                basket.addProduct(productId);
                added= true;
            }
        }
        if(!added)
        {
            basketRepository.addUserBasket(userId, storeId).addProduct(productId);
        }

    }
}
