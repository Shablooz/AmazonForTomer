package BGU.Group13B.backend.User;

import BGU.Group13B.backend.Pair;
import BGU.Group13B.backend.Repositories.Interfaces.IBasketRepository;
import BGU.Group13B.backend.storePackage.Product;
import BGU.Group13B.service.callbacks.CalculatePriceOfBasket;
import BGU.Group13B.service.SingletonCollection;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.*;


public class Cart {

    private  IBasketRepository basketRepository;
    private final int userId;
    private final CalculatePriceOfBasket calculatePriceOfBasket;

    public Cart(int userId) {
        this.basketRepository = SingletonCollection.getBasketRepository();
        this.userId = userId;
        this.calculatePriceOfBasket = SingletonCollection.getCalculatePriceOfBasket();
    }

    public double purchaseCart(String creditCardNumber,
                               String creditCardMonth, String creditCardYear,
                               String creditCardHolderFirstName,
                               String creditCardCcv, String id,
                               HashMap<Integer/*productId*/, String/*productDiscountCode*/> productsCoupons,
                               String/*store coupons*/ storeCoupon) throws PurchaseFailedException {
        var userBaskets = getBasketRepository().getUserBaskets(userId);
        if (userBaskets.isEmpty()) {
            throw new NoSuchElementException("No baskets in cart");
        }
        double totalPrice = 0;
        for (var basket : userBaskets) {
            totalPrice += basket.purchaseBasket(creditCardNumber, creditCardMonth, creditCardYear, creditCardHolderFirstName, creditCardCcv, id,
                    productsCoupons, storeCoupon);

        }
        return totalPrice;
    }

    /*returns price after discounts*/
    public Pair<Double, List<BasketProduct>> startPurchaseBasketTransaction(UserInfo userInfo, List<String> coupons) throws PurchaseFailedException {
        var userBaskets = getBasketRepository().getUserBaskets(userId);
        List<BasketProduct> successfulProducts = new LinkedList<>();
        if (userBaskets.isEmpty()) {
            throw new NoSuchElementException("No baskets in cart");
        }
        double totalPrice = 0;
        for (var basket : userBaskets) {
            var transaction = basket.startPurchaseBasketTransactionWithSuccessful(userInfo, coupons);
            totalPrice += transaction.getFirst();
            successfulProducts.addAll(transaction.getSecond());
        }
        return Pair.of(totalPrice, successfulProducts);
    }

    public void purchaseCart(String creditCardNumber,
                             String creditCardMonth, String creditCardYear,
                             String creditCardHolderFirstName,
                             String creditCardCVV, String id,
                             String address, String city, String country,
                             String zip
    ) throws PurchaseFailedException {
        var userBaskets = getBasketRepository().getUserBaskets(userId);
        if (userBaskets.isEmpty()) {
            throw new NoSuchElementException("No baskets in cart");
        }
        for (var basket : userBaskets) {
            basket.purchaseBasket(creditCardNumber, creditCardMonth, creditCardYear,
                    creditCardHolderFirstName, creditCardCVV, id,
                    address, city, country, zip);
        }
    }

    private boolean isContainsBasket(int storeId, Set<Basket> userBaskets) {
        for (var basket : userBaskets) {
            if (basket.getStoreId() == storeId) {
                return true;
            }
        }
        return false;
    }

    public void addProductToCart(int productId, int storeId, int amount, double price) {
        //if user has basket with storeID, add product to it. else, create new basket and add product to it.
        boolean added = false;
        Set<Basket> userBaskets = getBasketRepository().getUserBaskets(userId);
        for (var basket : userBaskets) {
            if (basket.getStoreId() == storeId) {
                basket.addProduct(productId, amount, price);
                added = true;
            }
        }
        if (!added) {
            getBasketRepository().addUserBasket(userId, storeId).addProduct(productId, amount, price);
        }
    }
    public void addProductToCart(int productId, int storeId){
        addProductToCart(productId, storeId, 1, -1);
    }

    public String getCartDescription() {
        String cartContent = "";
        var userBaskets = getBasketRepository().getUserBaskets(userId);
        for (var basket : userBaskets) {
            cartContent += "Store id " + basket.getStoreId() + " : " + basket.getBasketDescription();
        }
        return cartContent;
    }

    public List<Product> getCartContent() {
        List<Product> cartContent = new LinkedList<>();
        var userBaskets = getBasketRepository().getUserBaskets(userId);
        for (var basket : userBaskets) {
            cartContent.addAll(basket.getBasketContent());
        }
        return cartContent;
    }

    public List<BasketProduct> getCartBasketProducts() {
        List<BasketProduct> cartContent = new LinkedList<>();
        var userBaskets = getBasketRepository().getUserBaskets(userId);
        for (var basket : userBaskets) {
            cartContent.addAll(basket.getBasketProducts());
        }
        return cartContent;
    }


    public void removeProduct(int storeId, int productId) throws Exception {
        var userBaskets = getBasketRepository().getUserBaskets(userId);
        for (var basket : userBaskets) {
            if (basket.getStoreId() == storeId) {
                basket.removeProduct(productId);
            }
        }
    }

    public void changeProductQuantity(int storeId, int productId, int quantity) throws Exception {
        var userBaskets = getBasketRepository().getUserBaskets(userId);
        for (var basket : userBaskets) {
            if (basket.getStoreId() == storeId) {
                basket.changeProductQuantity(productId, quantity);
            }
        }
    }

    public void removeBasket(int userId, int storeId) {
        getBasketRepository().removeUserBasket(userId, storeId);
    }

    public List<Integer> getFailedProducts(int storeId, int userId) {
        return getBasketRepository().getUserBaskets(userId).stream().
                filter(basket -> basket.getStoreId() == storeId).findFirst().get().
                getFailedProducts().stream().map(BasketProduct::getProductId).toList();
    }

    public List<Product> getAllFailedProductsAfterPayment() {
        List<Product> failedProducts = new LinkedList<>();
        for (var basket : getBasketRepository().getUserBaskets(userId)) {
            failedProducts.addAll(basket.getFailedProducts().stream().map(BasketProduct::getProduct).toList());
        }
        return failedProducts;
    }

    public double getTotalPriceOfCartBeforeDiscount() {
        double totalPrice = 0;
        for (var basket : getBasketRepository().getUserBaskets(userId)) {
            totalPrice += basket.getTotalPriceOfBasketBeforeDiscount();
        }
        return totalPrice;
    }

    public void cancelPurchase() {
        for (var basket : getBasketRepository().getUserBaskets(userId)) {
            basket.cancelPurchase();
        }
    }
    public IBasketRepository getBasketRepository() {
        basketRepository= SingletonCollection.getBasketRepository();
        return basketRepository;
    }
}
