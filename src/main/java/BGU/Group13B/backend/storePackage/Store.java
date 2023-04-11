package BGU.Group13B.backend.storePackage;


import BGU.Group13B.backend.Repositories.Interfaces.IProductRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IStoreDiscountsRepository;
import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.storePackage.Discounts.Discount;
import BGU.Group13B.backend.storePackage.delivery.DeliveryAdapter;
import BGU.Group13B.backend.storePackage.payment.PaymentAdapter;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Store {
    private final int storeId;
    private final IProductRepository productRepository;
    private final PurchasePolicy purchasePolicy;
    private final DiscountPolicy discountPolicy;
    private final DeliveryAdapter deliveryAdapter;
    private final PaymentAdapter paymentAdapter;
    private final AlertManger alertManger;
    private final StorePermission storePermission;
    private final IStoreDiscountsRepository storeDiscounts;

    public Store(int storeId, IProductRepository productRepository, PurchasePolicy purchasePolicy, DiscountPolicy discountPolicy, DeliveryAdapter deliveryAdapter, PaymentAdapter paymentAdapter, AlertManger alertManger, StorePermission storePermission, IStoreDiscountsRepository storeDiscounts) {
        this.storeId = storeId;
        this.productRepository = productRepository;
        this.purchasePolicy = purchasePolicy;
        this.discountPolicy = discountPolicy;
        this.deliveryAdapter = deliveryAdapter;
        this.paymentAdapter = paymentAdapter;
        this.alertManger = alertManger;
        this.storePermission = storePermission;
        this.storeDiscounts = storeDiscounts;
    }



    //todo: complete the function
    public void addProduct(Product product, int userId) {


    }

    public void addProduct(int userId, String productName, int quantity, double price) {
        /*
         * check if the user has permission to add product
         * */
        boolean a = this.storePermission.checkAddProductPermission(userId);
        if (!a) {
            return;
        }
       /* Product product = new Product(productName, -1, storeId, price, quantity);
        productRepository.add(product);
    */}

    public double calculatePriceOfBasket(double totalAmountBeforeStoreDiscountPolicy,
                                         ConcurrentLinkedQueue<BasketProduct> successfulProducts) {
        double totalAmount = totalAmountBeforeStoreDiscountPolicy;
        for(Discount discount : storeDiscounts.getStoreDiscounts(storeId).
                orElseThrow(() -> new RuntimeException("Store with id " + storeId + " does not exist"))){
            totalAmount = discount.applyStore(totalAmount, successfulProducts);
        }
        return totalAmount;

    }
}
