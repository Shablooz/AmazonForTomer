package BGU.Group13B.backend.storePackage;


import BGU.Group13B.backend.Repositories.Interfaces.IStoreRepository;
import BGU.Group13B.backend.System.Searcher;
import BGU.Group13B.backend.User.Message;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
import BGU.Group13B.service.callbacks.AddToUserCart;

import java.util.List;

public class Market {
    private final IStoreRepository storeRepository;

    private final AddToUserCart addToUserCart;

    private Searcher searcher; //inject in the loading of the system

    public Market(IStoreRepository storeRepository, AddToUserCart addToUserCart, Searcher searcher) {
        this.searcher = searcher;
        this.storeRepository = storeRepository;
        this.addToUserCart = addToUserCart;
    }

    //Tomer
    //#27
    public void sendMassage(Message message, String userName,int storeId) { //need to check how to send message back to the user
        storeRepository.getStore(storeId).sendMassage(message,userName,storeId);
    }
    //#42
    public Message getUnreadMessages(String userName,int storeId)throws NoPermissionException {

        return storeRepository.getStore(storeId).getUnreadMessages(userName,storeId);
    }
    //#42
    public Message getReadMessages(String userName,int storeId)throws NoPermissionException {

        return storeRepository.getStore(storeId).getReadMessages(userName,storeId);
    }
    //#42
    public void markAsCompleted(String senderId,int messageId,String userName,int storeId) throws NoPermissionException {

        storeRepository.getStore(storeId).markAsCompleted(senderId,messageId,userName,storeId);
    }
    //#42
    public void refreshMessages(String userName,int storeId) throws NoPermissionException {
        storeRepository.getStore(storeId).refreshMessages(userName,storeId);
    }

    public void addReview(String review, int storeId, int productId, int userId){ //TODO:check get store impl
        storeRepository.getStore(storeId).addReview(review,productId,userId);
    }
    public void removeReview(int storeId, int productId, int userId){
        storeRepository.getStore(storeId).removeReview(productId,userId);
    }
    public Review getReview(int storeId, int productId, int userId){
        return storeRepository.getStore(storeId).getReview(productId,userId);
    }

    public float getProductScore(int storeId,int productId){
        return storeRepository.getStore(storeId).getProductScore(productId);
    }

    public void addAndSetProductScore(int storeId,int productId,int userId,int score){
        storeRepository.getStore(storeId).addAndSetProductScore(productId,userId,score);
    }
    public void removeProductScore(int storeId,int productId,int userId){
        storeRepository.getStore(storeId).removeProductScore(productId,userId);
    }


    public void addProduct(int userId, String productName, int quantity, double price, int storeId) throws NoPermissionException {
        storeRepository.getStore(storeId).addProduct(userId, productName, quantity, price);
    }


    public void purchaseProposalSubmit(int userId, int storeId, int productId, double proposedPrice, int amount) throws NoPermissionException {
        storeRepository.getStore(storeId).purchaseProposalSubmit(userId, productId, proposedPrice, amount);
    }

    public void purchaseProposalApprove(int managerId, int storeId, int productId) throws NoPermissionException {
        storeRepository.getStore(storeId).purchaseProposalApprove(managerId, productId);
    }

    public void searchProductByName(String productName) {
        searcher.searchByName(productName);
    }

    public void searchProductByCategory(String category) {
        searcher.searchByCategory(category);
    }

    public void searchProductByKeywords(List<String> keywords) {
        searcher.searchByKeywords(keywords);
    }

    public void filterByPriceRange(int minPrice, int maxPrice) {
        searcher.filterByPriceRange(minPrice, maxPrice);
    }

    public void filterByProductRank(int minRating, int maxRating) {
        searcher.filterByProductRank(minRating, maxRating);
    }

    public void filterByCategory(String category) {
        searcher.filterByCategory(category);
    }

    public void filterByStoreRank(int minRating, int maxRating) {
        searcher.filterByStoreRank(minRating, maxRating);
    }




}
