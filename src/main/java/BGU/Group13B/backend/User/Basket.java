package BGU.Group13B.backend.User;

import BGU.Group13B.backend.Pair;
import BGU.Group13B.backend.Repositories.Interfaces.IBasketProductRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IProductHistoryRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IPurchaseHistoryRepository;
import BGU.Group13B.backend.storePackage.Product;
import BGU.Group13B.backend.storePackage.delivery.DeliveryAdapter;
import BGU.Group13B.backend.storePackage.payment.PaymentAdapter;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseExceedsPolicyException;
import BGU.Group13B.service.callbacks.CalculatePriceOfBasket;
import BGU.Group13B.service.SingletonCollection;

import java.time.LocalDate;
import java.util.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Basket {
    private final int userId;
    private final int storeId;
    private final IBasketProductRepository basketProductRepository;
    private final IPurchaseHistoryRepository purchaseHistoryRepository;
    private final PaymentAdapter paymentAdapter;
    private final DeliveryAdapter deliveryAdapter;
    private final ConcurrentLinkedQueue<BasketProduct> successfulProducts;
    private final ConcurrentLinkedQueue<BasketProduct> failedProducts;
    private final IProductHistoryRepository productHistoryRepository;
    private final CalculatePriceOfBasket calculatePriceOfBasket;
    private ScheduledFuture<?> scheduledFuture;
    private int idealTime = 5;
    private TimeUnit unitsToRestore = TimeUnit.MINUTES;
    private ScheduledExecutorService scheduler;
    private double finalPrice;

    public Basket(int userId, int storeId) {
        this.userId = userId;
        this.storeId = storeId;
        this.basketProductRepository = SingletonCollection.getBasketProductRepository();
        this.paymentAdapter = SingletonCollection.getPaymentAdapter();
        this.productHistoryRepository = SingletonCollection.getProductHistoryRepository();
        this.calculatePriceOfBasket = SingletonCollection.getCalculatePriceOfBasket();
        this.successfulProducts = new ConcurrentLinkedQueue<>();
        this.failedProducts = new ConcurrentLinkedQueue<>();
        this.purchaseHistoryRepository = SingletonCollection.getPurchaseHistoryRepository();
        deliveryAdapter = SingletonCollection.getDeliveryAdapter();
    }

    //used for testing
    public Basket(int userId, int storeId, IBasketProductRepository productRepository,
                  PaymentAdapter paymentAdapter, IProductHistoryRepository productHistoryRepository,
                  CalculatePriceOfBasket calculatePriceOfBasket, DeliveryAdapter deliveryAdapter) {
        this.userId = userId;
        this.storeId = storeId;
        this.basketProductRepository = productRepository;
        this.purchaseHistoryRepository = SingletonCollection.getPurchaseHistoryRepository();
        this.paymentAdapter = paymentAdapter;
        this.productHistoryRepository = productHistoryRepository;
        this.calculatePriceOfBasket = calculatePriceOfBasket;
        this.successfulProducts = new ConcurrentLinkedQueue<>();
        this.failedProducts = new ConcurrentLinkedQueue<>();
        this.deliveryAdapter = deliveryAdapter;
    }

    public int getUserId() {
        return userId;
    }

    public int getStoreId() {
        return storeId;
    }

    /*
     * returns total price after discounts, and updates the products stock
     * */
    public double startPurchaseBasketTransaction(UserInfo userInfo, List<String> coupons) throws PurchaseFailedException {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduledFuture = scheduler.schedule(this::restoreProductsStock, idealTime, unitsToRestore);
        getSuccessfulProducts();
        //double totalAmount = getTotalAmount(productsCoupons);
        //calculate the total price of the products by the store discount policy
        finalPrice = calculateStoreDiscount(userInfo, coupons);
        return finalPrice;
    }//[Discount1, Discount2, Discount3]
    //Discount1 /


    //For GUI
    public Pair<Double, List<BasketProduct>> startPurchaseBasketTransactionWithSuccessful(UserInfo userInfo, List<String> coupons) throws PurchaseFailedException {
        successfulProducts.clear();
        failedProducts.clear();
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduledFuture = scheduler.schedule(this::restoreProductsStock, idealTime, unitsToRestore);
        getSuccessfulProducts();
        //double totalAmount = getTotalAmount(productsCoupons);//fixme
        //calculate the total price of the products by the store discount policy
        var basketProducts= basketProductRepository.getBasketProducts(storeId, userId).orElseGet(LinkedList::new);
        finalPrice = calculateStoreDiscount(userInfo, coupons);
        return Pair.of(finalPrice, new LinkedList<>(successfulProducts));
    }

    public void purchaseBasket(String creditCardNumber,
                               String creditCardMonth, String creditCardYear,
                               String creditCardHolderFirstName,
                               String creditCardCVV, String id,
                               String address, String city, String country,
                               String zip) throws PurchaseFailedException {

        if (!paymentAdapter.pay(creditCardNumber, creditCardMonth, creditCardYear, creditCardHolderFirstName, creditCardCVV, id)) {
            throw new PurchaseFailedException("Payment failed");
        }
        if (!deliveryAdapter.supply(creditCardHolderFirstName, address, city, country, zip)) {
            throw new PurchaseFailedException("Delivery failed");
        }

        scheduledFuture.cancel(true);
        scheduledFuture = null;
        scheduler.shutdown();
        //if succeeded, add the successful products to the purchase history
        for (BasketProduct basketProduct : successfulProducts) {
            productHistoryRepository.addProductToHistory(basketProduct, userId);
        }
        purchaseHistoryRepository.addPurchase(userId, storeId, successfulProducts, finalPrice);
        basketProductRepository.removeBasketProducts(storeId, userId);
        successfulProducts.clear();

    }

    /*used for testing done both operations at once*/
    public double purchaseBasket(String creditCardNumber,
                                 String creditCardMonth, String creditCardYear,
                                 String creditCardHolderFirstName,
                                 String creditCardCVV, String id,
                                 HashMap<Integer/*productId*/, String/*productDiscountCode*/> productsCoupons,
                                 String/*store coupons*/ storeCoupon
    ) throws PurchaseFailedException {

        LinkedList<String> coupons = new LinkedList<>(productsCoupons.values());
        coupons.add(storeCoupon);

        double price = startPurchaseBasketTransaction(new UserInfo(LocalDate.now().minusYears(25)), coupons);
        purchaseBasket(creditCardNumber, creditCardMonth, creditCardYear, creditCardHolderFirstName, creditCardCVV, id,
                "address", "city", "country", "1234134");
        return price;
    }

    /*In case the user pressed on exit in the middle of the purchase or something like that*/
    public void cancelPurchase() {
        if (scheduledFuture != null && !scheduledFuture.isDone()) {
            scheduledFuture.cancel(true);
            scheduledFuture = null;
        }
        restoreProductsStock();
        successfulProducts.clear();
        failedProducts.clear();
    }

    private void restoreProductsStock() {
        for (BasketProduct basketProduct : successfulProducts) {
            synchronized (basketProduct.getProduct()) {
                basketProduct.getProduct().increaseQuantity(basketProduct.getQuantity());
            }
        }
    }

    private double calculateStoreDiscount(UserInfo userInfo, List<String> coupons) throws PurchaseExceedsPolicyException {
        return calculatePriceOfBasket.apply(storeId, new BasketInfo(getSuccessfulProductsList().stream().toList()), userInfo, coupons);
    }


    private void getSuccessfulProducts() {

        //for every product in the basket

        for (BasketProduct basketProduct : basketProductRepository.getBasketProducts(storeId, userId).orElseGet(LinkedList::new)) {
            //synchronize product
            synchronized (basketProduct.getProduct()) {
                //try to decrease the quantity of the product in the store
                //if succeeded, add the product to the successful products list
                //if failed, add the product to the failed products list
                if (basketProduct.getProduct().isDeleted() || basketProduct.getProduct().isHidden())
                    failedProducts.add(basketProduct);
                else if (basketProduct.getProduct().tryDecreaseQuantity(basketProduct.getQuantity())) {
                    successfulProducts.add(basketProduct);
                } else {
                    failedProducts.add(basketProduct);
                }
            }
        }
    }

    public void addProduct(int productId) throws IllegalArgumentException {
        BasketProduct basketProduct = basketProductRepository.getBasketProduct(productId, storeId, userId);
        if (basketProduct != null) {
            basketProductRepository.changeProductQuantity(productId, storeId, userId, 1);
        } else
            basketProductRepository.addNewProductToBasket(productId, storeId, userId);
    }

    public String getBasketDescription() {
        StringBuilder basketContent = new StringBuilder();
        basketProductRepository.getBasketProducts(storeId, userId);
        for (BasketProduct basketProduct : basketProductRepository.getBasketProducts(storeId, userId).get()) {
            basketContent.append(basketProduct.toString());
        }
        return basketContent.toString();
    }

    public void removeProduct(int productId) throws Exception {
        try {
            basketProductRepository.removeBasketProduct(productId, userId, storeId);
        } catch (Exception e) {
            throw e;
        }
    }

    public void changeProductQuantity(int productId, int quantity) throws Exception {
        BasketProduct basketProduct = basketProductRepository.getBasketProduct(productId, storeId, userId);
        if (basketProduct != null) {
            basketProductRepository.changeProductQuantity(productId, storeId, userId, quantity);
        } else
            throw new Exception("Product not in basket");
    }

    public ConcurrentLinkedQueue<BasketProduct> getFailedProducts() {
        return failedProducts;
    }

    public ConcurrentLinkedQueue<BasketProduct> getSuccessfulProductsList() {
        return successfulProducts;
    }

    public BasketProduct getBasketProduct(int productId) {
        return basketProductRepository.getBasketProduct(productId, storeId, userId);
    }

    /*used for testing*/
    public void setIdealTime(int idealTime) {
        this.idealTime = idealTime;
    }

    public void setUnitsToRestore(java.util.concurrent.TimeUnit unitsToRestore) {
        this.unitsToRestore = unitsToRestore;
    }

    public List<Product> getBasketContent() {
        return basketProductRepository.getBasketProducts(storeId, userId).orElseGet(LinkedList::new).stream().map(BasketProduct::getProduct).collect(Collectors.toList());
    }

    public List<BasketProduct> getBasketProducts() {
        return basketProductRepository.getBasketProducts(storeId, userId).orElseGet(LinkedList::new);
    }

    public double getTotalPriceOfBasketBeforeDiscount() {
        return basketProductRepository.getBasketProducts(storeId, userId).orElseGet(LinkedList::new).stream().mapToDouble(BasketProduct::getSubtotal).sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Basket basket = (Basket) o;
        return userId == basket.userId && storeId == basket.storeId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, storeId);
    }
}
