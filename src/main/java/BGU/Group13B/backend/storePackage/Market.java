package BGU.Group13B.backend.storePackage;


import BGU.Group13B.backend.Repositories.Interfaces.IStoreRepository;
import BGU.Group13B.backend.User.Message;

public class Market {
    private final IStoreRepository storeRepository;

    public Market(IStoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    public void addProduct(int userId, String productName, int quantity, double price, int storeId) {
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
}
