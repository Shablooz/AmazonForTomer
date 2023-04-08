package BGU.Group13B.backend.User;

import BGU.Group13B.backend.Repositories.Interfaces.IBasketRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IMessageRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IPurchaseHistoryRepository;
import BGU.Group13B.backend.storePackage.Market;

public class User {
    private final IPurchaseHistoryRepository purchaseHistoryRepository;

    private final int userId;
    private final IMessageRepository messageRepository;
    private final UserPermissions userPermissions;
    private final Cart cart;
    private final Market market;


    public User(IPurchaseHistoryRepository purchaseHistoryRepository, int userId, IMessageRepository messageRepository, UserPermissions userPermissions, Market market, IBasketRepository basketRepository) {
        this.purchaseHistoryRepository = purchaseHistoryRepository;
        this.userId = userId;
        this.messageRepository = messageRepository;
        this.userPermissions = userPermissions;
        this.cart = new Cart(userId, basketRepository);
        this.market = market;
    }
    void purchaseCart(String address, String creditCardNumber, String creditCardMonth, String creditCardYear, String creditCardHolderFirstName, String creditCardHolderLastName, String creditCardCcv, String id, String creditCardType){
        basketRepository.purchaseBasket(userId);
    }

}
