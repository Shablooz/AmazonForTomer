package BGU.Group13B.backend.storePackage;


import BGU.Group13B.backend.Repositories.Interfaces.IProductRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IStoreMessagesRepository;
import BGU.Group13B.backend.User.Message;
import BGU.Group13B.backend.storePackage.delivery.DeliveryAdapter;
import BGU.Group13B.backend.storePackage.payment.PaymentAdapter;

import java.util.List;

public class Store {
    private final int storeId;
    private final IProductRepository productRepository;
    private final PurchasePolicy purchasePolicy;
    private final DiscountPolicy discountPolicy;
    private final DeliveryAdapter deliveryAdapter;
    private final PaymentAdapter paymentAdapter;
    private final AlertManger alertManger;
    private final StorePermission storePermission;
    private final IStoreMessagesRepository storeMessagesRepository;

    public Store(int storeId, IProductRepository productRepository, PurchasePolicy purchasePolicy, DiscountPolicy discountPolicy, DeliveryAdapter deliveryAdapter, PaymentAdapter paymentAdapter, AlertManger alertManger, StorePermission storePermission, IStoreMessagesRepository storeMessagesRepository) {
        this.storeId = storeId;
        this.productRepository = productRepository;
        this.purchasePolicy = purchasePolicy;
        this.discountPolicy = discountPolicy;
        this.deliveryAdapter = deliveryAdapter;
        this.paymentAdapter = paymentAdapter;
        this.alertManger = alertManger;
        this.storePermission = storePermission;
        this.storeMessagesRepository = storeMessagesRepository;
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

    public List<Message> getUnreadMessages(int numOfMessages) {
        //TODO: need to check permission
        return storeMessagesRepository.unreadMassages(this.storeId,numOfMessages);
    }
    public void markAsCompleted(Message message) {
        //TODO: need to check permission
        storeMessagesRepository.markAsCompleted(message);
    }
    public void addMessage(Message message) {
        storeMessagesRepository.addMessage(this.storeId,message);
    }
    public void removeMassage(String senderId,int massageId) {
        storeMessagesRepository.removeMassage(this.storeId,senderId,massageId);
    }

}
