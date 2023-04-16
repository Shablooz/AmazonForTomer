package BGU.Group13B.backend.storePackage;


import BGU.Group13B.backend.Repositories.Interfaces.IStoreRepository;
import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.service.CalculatePriceOfBasket;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Market {
    private final IStoreRepository storeRepository;

    public Market(IStoreRepository storeRepository) {
        //todo: SingletonCollection.setCalculatePriceOfBasket(this::calculatePriceOfBasket);
        this.storeRepository = storeRepository;
    }

    public void addProduct(int userId, String productName, int quantity, double price, int storeId) {
        //storeRepository.getStore(storeId).addProduct(userId, productName, quantity, price);
    }

    private double calculatePriceOfBasket(double totalAmountBeforeStoreDiscountPolicy, ConcurrentLinkedQueue<BasketProduct> successfulProducts, int storeId,
                                         String storeCoupon) {
        return storeRepository.getStore(storeId).orElseThrow(
                () -> new RuntimeException("Store with id " + storeId + " does not exist")
        ).calculatePriceOfBasket(totalAmountBeforeStoreDiscountPolicy, successfulProducts, storeCoupon);
    }
}
