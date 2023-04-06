package BGU.Group13B.backend.User;

import BGU.Group13B.backend.Repositories.Interfaces.IMessageRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IPurchaseHistoryRepository;
import BGU.Group13B.backend.storePackage.Market;

public class User {
    private final IPurchaseHistoryRepository purchaseHistoryRepository;
    private final IMessageRepository messageRepository;
    private final UserPermissions userPermissions;
    private final Market market;

    public User(IPurchaseHistoryRepository purchaseHistoryRepository, IMessageRepository messageRepository, UserPermissions userPermissions, Market market) {
        this.purchaseHistoryRepository = purchaseHistoryRepository;
        this.messageRepository = messageRepository;
        this.userPermissions = userPermissions;
        this.market = market;
    }
}
