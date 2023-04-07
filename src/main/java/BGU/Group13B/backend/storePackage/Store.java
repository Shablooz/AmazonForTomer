package BGU.Group13B.backend.storePackage;


import BGU.Group13B.backend.Repositories.Interfaces.IProductRepository;
import BGU.Group13B.backend.storePackage.delivery.DeliveryAdapter;
import BGU.Group13B.backend.storePackage.payment.PaymentAdapter;

public class Store {
    private final IProductRepository productRepository;
    private final PurchasePolicy purchasePolicy;
    private final DiscountPolicy discountPolicy;
    private final DeliveryAdapter deliveryAdapter;
    private final PaymentAdapter paymentAdapter;
    private final AlertManger alertManger;
    private final StorePermission storePermission;

    public Store(IProductRepository productRepository, PurchasePolicy purchasePolicy, DiscountPolicy discountPolicy, DeliveryAdapter deliveryAdapter, PaymentAdapter paymentAdapter, AlertManger alertManger, StorePermission storePermission) {
        this.productRepository = productRepository;
        this.purchasePolicy = purchasePolicy;
        this.discountPolicy = discountPolicy;
        this.deliveryAdapter = deliveryAdapter;
        this.paymentAdapter = paymentAdapter;
        this.alertManger = alertManger;
        this.storePermission = storePermission;
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
        Product product = new Product(productName, -1, price, quantity);
        productRepository.add(product);
    }
}
