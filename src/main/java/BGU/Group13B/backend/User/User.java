package BGU.Group13B.backend.User;

import BGU.Group13B.backend.Repositories.Interfaces.IMessageRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IPurchaseHistoryRepository;

public class User {
    private final IPurchaseHistoryRepository purchaseHistoryRepository;
    private final IMessageRepository messageRepository;
    private final UserPermissions userPermissions;

    public User(IPurchaseHistoryRepository purchaseHistoryRepository, IMessageRepository messageRepository, UserPermissions userPermissions) {
        this.purchaseHistoryRepository = purchaseHistoryRepository;
        this.messageRepository = messageRepository;
        this.userPermissions = userPermissions;
    }
}
