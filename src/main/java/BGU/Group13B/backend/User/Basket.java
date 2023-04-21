package BGU.Group13B.backend.User;

import BGU.Group13B.backend.Repositories.Interfaces.IBasketProductRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IProductHistoryRepository;
import BGU.Group13B.backend.storePackage.Product;
import BGU.Group13B.backend.storePackage.payment.PaymentAdapter;
import BGU.Group13B.service.callbacks.CalculatePriceOfBasket;
import BGU.Group13B.service.SingletonCollection;

import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class Basket {
    private final int userId;
    private final int storeId;
    private final IBasketProductRepository basketProductRepository;
    private final PaymentAdapter paymentAdapter;
    private final ConcurrentLinkedQueue<BasketProduct> successfulProducts;
    private final ConcurrentLinkedQueue<BasketProduct> failedProducts;
    private final IProductHistoryRepository productHistoryRepository;
    private final CalculatePriceOfBasket calculatePriceOfBasket;

    public Basket(int userId, int storeId) {
        this.userId = userId;
        this.storeId = storeId;
        this.basketProductRepository = SingletonCollection.getBasketProductRepository();
        this.paymentAdapter = SingletonCollection.getPaymentAdapter();
        this.productHistoryRepository = SingletonCollection.getProductHistoryRepository();
        this.calculatePriceOfBasket = SingletonCollection.getCalculatePriceOfBasket();
        this.successfulProducts = new ConcurrentLinkedQueue<>();
        this.failedProducts = new ConcurrentLinkedQueue<>();
    }

    //used for testing
    public Basket(int userId, int storeId, IBasketProductRepository productRepository,
                  PaymentAdapter paymentAdapter, IProductHistoryRepository productHistoryRepository,
                  CalculatePriceOfBasket calculatePriceOfBasket) {
        this.userId = userId;
        this.storeId = storeId;
        this.basketProductRepository = productRepository;
        this.paymentAdapter = paymentAdapter;
        this.productHistoryRepository = productHistoryRepository;
        this.calculatePriceOfBasket = calculatePriceOfBasket;
        this.successfulProducts = new ConcurrentLinkedQueue<>();
        this.failedProducts = new ConcurrentLinkedQueue<>();
    }

    public int getUserId() {
        return userId;
    }

    public int getStoreId() {
        return storeId;
    }

    public double purchaseBasket(String address, String creditCardNumber,
                               String creditCardMonth, String creditCardYear,
                               String creditCardHolderFirstName, String creditCardHolderLastName,
                               String creditCardCVV, String id, String creditCardType,
                               HashMap<Integer/*productId*/, String/*productDiscountCode*/> productsCoupons,
                               String/*store coupons*/ storeCoupon
    ) throws PurchaseFailedException {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        var scheduledFuture = scheduler.schedule(this::restoreProductsStock, 5, java.util.concurrent.TimeUnit.MINUTES);
        getSuccessfulProducts();


        double totalAmount = getTotalAmount(productsCoupons);
        //calculate the total price of the products by the store discount policy
        totalAmount = calculateStoreDiscount(totalAmount, storeCoupon);
        if (paymentAdapter.
                pay(address, creditCardNumber, creditCardMonth, creditCardYear,
                        creditCardHolderFirstName, creditCardHolderLastName,
                        creditCardCVV, id, creditCardType, successfulProducts, failedProducts, totalAmount)) {
            scheduledFuture.cancel(true);
            scheduler.shutdown();
            //if succeeded, add the successful products to the purchase history
            for (BasketProduct basketProduct : successfulProducts) {
                productHistoryRepository.addProductToHistory(basketProduct, userId);
            }
            basketProductRepository.removeBasketProducts(storeId, userId);
            successfulProducts.clear();
            /*//todo: send message with the failed products ids!
            failedProducts.clear();*/
            return totalAmount;
        } else {
            throw new PurchaseFailedException("Payment failed");
        }

        //try to pay for the product using PaymentAdapter
        //if succeeded, add the product to the purchase history, todo: and send message with the failed products ids
        //if the user wants to cancel the purchase, added the function cancel purchase.
        //then restore the quantity of the product in the store, yap
        //if failed throw an appropriate exception

    }

    /*In case the user pressed on exit in the middle of the purchase or something like that*/
    public void cancelPurchase() {
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

    private double calculateStoreDiscount(double totalAmountAfterProductDiscounts, String storeCoupon) {
        return calculatePriceOfBasket.apply(totalAmountAfterProductDiscounts, successfulProducts, storeId, storeCoupon);
    }


    private void getSuccessfulProducts() {

        //for every product in the basket

        for (BasketProduct basketProduct : basketProductRepository.getBasketProducts(storeId, userId).orElseGet(LinkedList::new)) {
            //synchronize product
            synchronized (basketProduct.getProduct()) {
                //try to decrease the quantity of the product in the store
                //if succeeded, add the product to the successful products list
                //if failed, add the product to the failed products list
                if (basketProduct.getProduct().tryDecreaseQuantity(basketProduct.getQuantity())) {
                    successfulProducts.add(basketProduct);
                } else {
                    failedProducts.add(basketProduct);
                }
            }
        }
    }

    private double getTotalAmount(HashMap<Integer/*productId*/, String/*productDiscountCode*/> productsCoupons) {
        double totalAmount = 0.0;//store.calculatePrice(successfulProducts);
        for (BasketProduct basketProduct : successfulProducts) {//Hidden assumption we are first calculating the discount of the products
            //calculate price remembering the discount policies
            String productDiscountCode = productsCoupons.getOrDefault(basketProduct.getProductId(), null);
            Product currentProduct = basketProduct.getProduct();
            totalAmount += currentProduct.calculatePrice(basketProduct.getQuantity(), productDiscountCode);
        }
        return totalAmount;
    }

    public void addProduct(int productId) throws IllegalArgumentException {
        BasketProduct basketProduct = basketProductRepository.getBasketProduct(storeId, userId, productId);
        if (basketProduct != null) {
            basketProductRepository.changeProductQuantity(productId, userId, storeId, 1);
        } else
            basketProductRepository.addNewProductToBasket(productId, userId, storeId);
    }

    public String getBasketContent() {
        String basketContent = "";
        basketProductRepository.getBasketProducts(storeId, userId);
        for (BasketProduct basketProduct : basketProductRepository.getBasketProducts(storeId, userId).get()) {
            basketContent += basketProduct.toString();
    }
        return basketContent;
    }

    public void removeProduct(int productId) throws Exception {
        try {
            BasketProduct basketProduct = basketProductRepository.getBasketProduct(storeId, userId, productId);
            if(basketProduct != null){
                basketProductRepository.removeBasketProduct(productId, userId, storeId);
            }
            else
                throw new Exception("Product not in basket");
        }catch (Exception e){
            throw e;
        }
    }

    public void changeProductQuantity(int productId, int quantity) throws Exception {
        BasketProduct basketProduct = basketProductRepository.getBasketProduct(storeId, userId, productId);
        if(basketProduct != null){
            basketProductRepository.changeProductQuantity(productId, userId, storeId, quantity);
        }
        else
            throw new Exception("Product not in basket");
    }

    public ConcurrentLinkedQueue<BasketProduct> getFailedProducts() {
        return failedProducts;
    }
    public ConcurrentLinkedQueue<BasketProduct> getSuccessfulProductsList() {
        return successfulProducts;
    }


}
