package BGU.Group13B.backend.User;

import BGU.Group13B.backend.Pair;
import BGU.Group13B.backend.Repositories.Interfaces.IBasketProductRepository;

import BGU.Group13B.backend.Repositories.Interfaces.IPurchaseHistoryRepository;
import BGU.Group13B.backend.storePackage.Product;
import BGU.Group13B.backend.storePackage.delivery.DeliveryAdapter;
import BGU.Group13B.backend.storePackage.payment.PaymentAdapter;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseExceedsPolicyException;
import BGU.Group13B.service.BroadCaster;
import BGU.Group13B.service.callbacks.CalculatePriceOfBasket;
import BGU.Group13B.service.SingletonCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Transient;

import java.time.LocalDate;
import java.util.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Entity
@IdClass(BasketId.class)
public class Basket {
    @Id
    private  int userId;
    @Id
    private  int storeId;

    @Transient
    private  IBasketProductRepository basketProductRepository;
    @Transient
    private  IPurchaseHistoryRepository purchaseHistoryRepository;
    @Transient
    private  PaymentAdapter paymentAdapter;
    @Transient
    private  DeliveryAdapter deliveryAdapter;
    @Transient
    private  ConcurrentLinkedQueue<BasketProduct> successfulProducts;
    @Transient
    private  ConcurrentLinkedQueue<BasketProduct> failedProducts;

    @Transient
    private  CalculatePriceOfBasket calculatePriceOfBasket;
    @Transient
    private ScheduledFuture<?> scheduledFuture;
    private int idealTime = 5;
    @Transient
    private TimeUnit unitsToRestore = TimeUnit.MINUTES;
    @Transient
    private ScheduledExecutorService scheduler;
    private double finalPrice;

    public Basket(int userId, int storeId) {
        this.userId = userId;
        this.storeId = storeId;
        this.basketProductRepository = SingletonCollection.getBasketProductRepository();
        this.paymentAdapter = SingletonCollection.getPaymentAdapter();
        this.calculatePriceOfBasket = SingletonCollection.getCalculatePriceOfBasket();
        this.successfulProducts = new ConcurrentLinkedQueue<>();
        this.failedProducts = new ConcurrentLinkedQueue<>();
        this.purchaseHistoryRepository = SingletonCollection.getPurchaseHistoryRepository();
        deliveryAdapter = SingletonCollection.getDeliveryAdapter();
    }
    public Basket() {
        this.userId = 0;
        this.storeId = 0;
        this.basketProductRepository = SingletonCollection.getBasketProductRepository();
        this.paymentAdapter = SingletonCollection.getPaymentAdapter();
        this.calculatePriceOfBasket = SingletonCollection.getCalculatePriceOfBasket();
        this.successfulProducts = new ConcurrentLinkedQueue<>();
        this.failedProducts = new ConcurrentLinkedQueue<>();
        this.purchaseHistoryRepository = SingletonCollection.getPurchaseHistoryRepository();
        deliveryAdapter = SingletonCollection.getDeliveryAdapter();
    }

    //used for testing
    public Basket(int userId, int storeId, IBasketProductRepository productRepository, IPurchaseHistoryRepository purchaseHistoryRepository,
                  PaymentAdapter paymentAdapter, CalculatePriceOfBasket calculatePriceOfBasket, DeliveryAdapter deliveryAdapter) {
        this.userId = userId;
        this.storeId = storeId;
        this.basketProductRepository = productRepository;
        this.purchaseHistoryRepository = purchaseHistoryRepository;
        this.paymentAdapter = paymentAdapter;

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
        if(successfulProducts.isEmpty()) return Pair.of(-1.0, new LinkedList<>());
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
        getPurchaseHistoryRepository().addPurchase(userId, storeId, successfulProducts, finalPrice);
        getBasketProductRepository().removeBasketProducts(storeId, userId);
        successfulProducts.clear();

        //refresh income view
        BroadCaster.broadcastIncome();

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

        double price = startPurchaseBasketTransaction(new UserInfo(SingletonCollection.getUserRepository().getUser(userId).getDateOfBirth()), coupons);
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

        for (BasketProduct basketProduct : getBasketProductRepository().getBasketProducts(storeId, userId).orElseGet(LinkedList::new)) {
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

        //remove the failed products from the basket
        for (BasketProduct basketProduct : failedProducts) {
            getBasketProductRepository().removeBasketProduct(basketProduct.getProductId(), userId, storeId);
        }
    }

    public void addProduct(int productId, int amount, double newPrice) throws IllegalArgumentException {
        BasketProduct basketProduct = getBasketProductRepository().getBasketProduct(productId, storeId, userId);

        if (basketProduct != null) {
            getBasketProductRepository().changeProductQuantity(productId, storeId, userId, 1);
            basketProduct.setQuantity(amount);
            if (newPrice != -1)
                basketProduct.setPrice(newPrice);

        } else {
            getBasketProductRepository().addNewProductToBasket(productId, storeId, userId);
            if(newPrice != -1)
                getBasketProductRepository().getBasketProduct(productId, storeId, userId).setPrice(newPrice);
            getBasketProductRepository().getBasketProduct(productId, storeId, userId).setQuantity(amount);
        }
    }

    public String getBasketDescription() {
        StringBuilder basketContent = new StringBuilder();
        getBasketProductRepository().getBasketProducts(storeId, userId);
        for (BasketProduct basketProduct : getBasketProductRepository().getBasketProducts(storeId, userId).get()) {
            basketContent.append(basketProduct.toString());
        }
        return basketContent.toString();
    }

    public void removeProduct(int productId){
        getBasketProductRepository().removeBasketProduct(productId, userId, storeId);
    }

    public void changeProductQuantity(int productId, int quantity) throws Exception {
        BasketProduct basketProduct = getBasketProductRepository().getBasketProduct(productId, storeId, userId);
        if (basketProduct != null) {
            getBasketProductRepository().changeProductQuantity(productId, storeId, userId, quantity);
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
        return getBasketProductRepository().getBasketProduct(productId, storeId, userId);
    }

    /*used for testing*/
    public void setIdealTime(int idealTime) {
        this.idealTime = idealTime;
    }

    public void setUnitsToRestore(java.util.concurrent.TimeUnit unitsToRestore) {
        this.unitsToRestore = unitsToRestore;
    }

    public List<Product> getBasketContent() {
        return getBasketProductRepository().getBasketProducts(storeId, userId).orElseGet(LinkedList::new).stream().map(BasketProduct::getProduct).collect(Collectors.toList());
    }

    public List<BasketProduct> getBasketProducts() {
        return getBasketProductRepository().getBasketProducts(storeId, userId).orElseGet(LinkedList::new);
    }

    public double getTotalPriceOfBasketBeforeDiscount() {
        return getBasketProductRepository().getBasketProducts(storeId, userId).orElseGet(LinkedList::new).stream().mapToDouble(BasketProduct::getSubtotal).sum();
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

    public void clearBasket() {
        getBasketProductRepository().removeBasketProducts(storeId,userId);
        getBasketProductRepository().dropBasket(storeId,userId);
    }


    //getters and setters


    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public IBasketProductRepository getBasketProductRepository() {
        basketProductRepository =SingletonCollection.getBasketProductRepository();
        return basketProductRepository;
    }

    public void setBasketProductRepository(IBasketProductRepository basketProductRepository) {
        this.basketProductRepository = basketProductRepository;
    }

    public IPurchaseHistoryRepository getPurchaseHistoryRepository() {
        purchaseHistoryRepository = SingletonCollection.getPurchaseHistoryRepository();
        return purchaseHistoryRepository;
    }

    public void setPurchaseHistoryRepository(IPurchaseHistoryRepository purchaseHistoryRepository) {
        this.purchaseHistoryRepository = purchaseHistoryRepository;
    }

    public PaymentAdapter getPaymentAdapter() {
        return paymentAdapter;
    }

    public void setPaymentAdapter(PaymentAdapter paymentAdapter) {
        this.paymentAdapter = paymentAdapter;
    }

    public DeliveryAdapter getDeliveryAdapter() {
        return deliveryAdapter;
    }

    public void setDeliveryAdapter(DeliveryAdapter deliveryAdapter) {
        this.deliveryAdapter = deliveryAdapter;
    }

    public void setSuccessfulProducts(ConcurrentLinkedQueue<BasketProduct> successfulProducts) {
        this.successfulProducts = successfulProducts;
    }

    public void setFailedProducts(ConcurrentLinkedQueue<BasketProduct> failedProducts) {
        this.failedProducts = failedProducts;
    }



    public CalculatePriceOfBasket getCalculatePriceOfBasket() {
        return calculatePriceOfBasket;
    }

    public void setCalculatePriceOfBasket(CalculatePriceOfBasket calculatePriceOfBasket) {
        this.calculatePriceOfBasket = calculatePriceOfBasket;
    }

    public ScheduledFuture<?> getScheduledFuture() {
        return scheduledFuture;
    }

    public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    public int getIdealTime() {
        return idealTime;
    }

    public TimeUnit getUnitsToRestore() {
        return unitsToRestore;
    }

    public ScheduledExecutorService getScheduler() {
        return scheduler;
    }

    public void setScheduler(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }
}
