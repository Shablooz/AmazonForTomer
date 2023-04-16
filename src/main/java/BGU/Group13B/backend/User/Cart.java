package BGU.Group13B.backend.User;

import BGU.Group13B.backend.Repositories.Interfaces.IBasketRepository;
import BGU.Group13B.service.CalculatePriceOfBasket;
import BGU.Group13B.service.SingletonCollection;

import java.util.NoSuchElementException;


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
        var userBaskets = basketRepository.getUserBaskets(userId).orElseThrow(() ->
                new NoSuchElementException("No baskets for user " + userId)
        );
        for (var basket : userBaskets) {
            //basket.purchaseBasket(address, creditCardNumber, creditCardMonth, creditCardYear, creditCardHolderFirstName, creditCardHolderLastName, creditCardCcv, id, creditCardType, calculatePriceOfBasket);
            throw new RuntimeException("Not implemented");
            //fixme
        }

    }
}
