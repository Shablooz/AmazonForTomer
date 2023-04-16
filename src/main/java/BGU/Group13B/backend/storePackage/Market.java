package BGU.Group13B.backend.storePackage;


import BGU.Group13B.backend.Repositories.Interfaces.IStoreRepository;
import BGU.Group13B.backend.User.Message;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
import BGU.Group13B.service.callbacks.AddToUserCart;

public class Market {
    private final IStoreRepository storeRepository;

    private final AddToUserCart addToUserCart;

    public Market(IStoreRepository storeRepository, AddToUserCart addToUserCart) {
        this.storeRepository = storeRepository;
        this.addToUserCart = addToUserCart;
    }

    public void addProduct(int userId, String productName, int quantity, double price, int storeId) throws NoPermissionException {
        storeRepository.getStore(storeId).addProduct(userId, productName, quantity, price);
    }

    public void sendMassage(Message message, String userName,int storeId) { //need to check how to send message back to the user
        storeRepository.getStore(storeId).sendMassage(message,userName);
    }
    public Message getUnreadMessages(String userName,int storeId) {

        return storeRepository.getStore(storeId).getUnreadMessages(userName);
    }
    public void markAsCompleted(String senderId,int messageId,String userName,int storeId) {

        storeRepository.getStore(storeId).markAsCompleted(senderId,messageId,userName);
    }

    public void refreshMessages(String userName,int storeId) {
        storeRepository.getStore(storeId).refreshMessages(userName);
    }

    public void purchaseProposalSubmit(int userId, int storeId, int productId, double proposedPrice, int amount) throws NoPermissionException {
        storeRepository.getStore(storeId).purchaseProposalSubmit(userId, productId, proposedPrice, amount);
    }

    public void purchaseProposalApprove(int managerId, int storeId, int productId) throws NoPermissionException {
        storeRepository.getStore(storeId).purchaseProposalApprove(managerId, productId);
    }

}
