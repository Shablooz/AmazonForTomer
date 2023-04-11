package BGU.Group13B.backend.User;

import BGU.Group13B.backend.Repositories.Interfaces.IBasketProductRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IProductHistoryRepository;
import BGU.Group13B.backend.storePackage.payment.PaymentAdapter;
import BGU.Group13B.service.CalculatePriceOfBasket;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Basket {
    private final int userId;
    private final int storeId;
    private final IBasketProductRepository productRepository;
    private final PaymentAdapter paymentAdapter;
    private final ConcurrentLinkedQueue<BasketProduct> successfulProducts;
    private final ConcurrentLinkedQueue<BasketProduct> failedProducts;
    private final IProductHistoryRepository productHistoryRepository;
    private final CalculatePriceOfBasket calculatePriceOfBasket;

    public Basket(int userId, int storeId, IBasketProductRepository productRepository,
                  PaymentAdapter paymentAdapter, IProductHistoryRepository productHistoryRepository,
                  CalculatePriceOfBasket calculatePriceOfBasket) {
        this.userId = userId;
        this.storeId = storeId;
        this.productRepository = productRepository;
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

    public void purchaseBasket(String address, String creditCardNumber,
                               String creditCardMonth, String creditCardYear,
                               String creditCardHolderFirstName, String creditCardHolderLastName,
                               String creditCardCVV, String id, String creditCardType,
                               HashMap<Integer/*productId*/, String/*productDiscountCode*/> productsDiscountCodes
                               ) throws Exception {
        getSuccessfulProducts();
        double totalAmount = getTotalAmount();
        //calculate the total price of the products by the store discount policy
        totalAmount = calculateStoreDiscount(totalAmount);
        if (paymentAdapter.
                pay(address, creditCardNumber, creditCardMonth, creditCardYear,
                        creditCardHolderFirstName, creditCardHolderLastName,
                        creditCardCVV, id, creditCardType, successfulProducts, failedProducts)) {
            //if succeeded, add the successful products to the purchase history
            for (BasketProduct basketProduct : successfulProducts) {
                productHistoryRepository.addProductToHistory(basketProduct, userId);
            }
        } else {
            throw new PurchaseFailedException("Payment failed");
        }

        //try to pay for the product using PaymentAdapter
        //if succeeded, add the product to the purchase history, todo: and send message with the failed products ids
        //if the user wants to cancel the purchase
        //then restore the quantity of the product in the store
        //if failed throw an appropriate exception

    }

    private double calculateStoreDiscount(double totalAmountAfterProductDiscounts) {
        return calculatePriceOfBasket.apply(totalAmountAfterProductDiscounts, successfulProducts, storeId);
    }


    private void getSuccessfulProducts() {

        //for every product in the basket

        for (BasketProduct basketProduct : productRepository.getBasketProducts(storeId, userId).get()) {
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

    private double getTotalAmount() {
        double totalAmount = 0.0;//store.calculatePrice(successfulProducts);
        for (BasketProduct basketProduct : successfulProducts) {//Hidden assumption we are first calculating the discount of the products
            //calculate price remembering the discount policies
            totalAmount += basketProduct.getProduct().calculatePrice(basketProduct.getQuantity());
        }
        return totalAmount;
    }
}
