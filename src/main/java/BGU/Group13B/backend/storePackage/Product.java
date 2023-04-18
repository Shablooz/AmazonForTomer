package BGU.Group13B.backend.storePackage;

import BGU.Group13B.backend.Repositories.Implementations.ReviewRepositoryImpl.ReviewRepositoryAsList;
import BGU.Group13B.backend.Repositories.Interfaces.IRepositoryReview;

public class Product {

    private String name;
    private int productId;
    private int storeId;
    private double price;
    private int amount;
    private String category;
    private int rank;
    private final PurchasePolicy purchasePolicy;
    private final DiscountPolicy discountPolicy;
    private final IRepositoryReview repositoryReview;

    public Product(String name, int productId, double price, int amount) {
        this.name = name;
        this.productId = productId;
        this.price = price;
        this.amount = amount;
        this.rank=0;
        purchasePolicy = null;
        discountPolicy = null;
        repositoryReview=new ReviewRepositoryAsList();
    }

    public String getName() {
        return name;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public int getRank() {
        return this.rank;
    }
    public String getCategory() {
        return category;
    }
    public int getStoreId() {
        return storeId;
    }
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public synchronized double getPrice() {
        return price;
    }

    public synchronized void setPrice(double price) {
        this.price = price;
    }

    public synchronized int getAmount() {
        return amount;
    }

    public synchronized void setAmount(int amount) {
        this.amount = amount;
    }

    public synchronized PurchasePolicy getPurchasePolicy() {
        if(purchasePolicy == null)
            throw new NullPointerException("Purchase policy is null");
        return purchasePolicy;
    }

    public synchronized DiscountPolicy getDiscountPolicy() {
        if(discountPolicy == null)
            throw new NullPointerException("Discount policy is null");
        return discountPolicy;
    }



    public void addReview(String review, int userId){
        repositoryReview.addReview(review,storeId,productId,userId);
    }
    public void removeReview(int userId){
        repositoryReview.removeReview(storeId,productId,userId);
    }
    public Review getReview(int userId){
        return repositoryReview.getReview(storeId,productId,userId);
    }
    public float getProductScore(){
        return repositoryReview.getProductScore(storeId,productId);
    }
    public void addAndSetScore(int userId,int score){
        repositoryReview.addAndSetProductScore(storeId,productId,userId,score);
    }
    public void removeProductScore(int userId){
        repositoryReview.removeProductScore(storeId,productId,userId);
    }


}
