package BGU.Group13B.backend.storePackage;

import BGU.Group13B.backend.Repositories.Interfaces.IProductDiscountsRepository;
import BGU.Group13B.backend.storePackage.Discounts.Discount;

import java.util.LinkedList;

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
    private int maxAmount;

    private final PurchasePolicy purchasePolicy;
    private final DiscountPolicy discountPolicy;
    private final IRepositoryReview repositoryReview;
    private final IProductDiscountsRepository productDiscounts;


    public Product(String name, int productId, int storeId, double price, int maxAmount, IProductDiscountsRepository productDiscounts) {
        this.name = name;
        this.productId = productId;
        this.storeId = storeId;
        this.price = price;
        this.maxAmount = maxAmount;
        this.productDiscounts = productDiscounts;
        this.rank=0;
        purchasePolicy = null;
        discountPolicy = null;
        repositoryReview=new ReviewRepositoryAsList();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
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

    public void setPrice(double price) {
        this.price = price;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    public PurchasePolicy getPurchasePolicy() {
        if (purchasePolicy == null)
            throw new NullPointerException("Purchase policy is null");
        return purchasePolicy;
    }


    public boolean tryDecreaseQuantity(int quantity) {
        if (maxAmount < quantity)
            return false;
        maxAmount -= quantity;
        return true;
    }

    public synchronized double calculatePrice(int productQuantity, String couponCodes) {
        purchasePolicy.checkPolicy(this, productQuantity);
        double finalPrice = price;
        for (Discount discount :
                productDiscounts.getProductDiscounts(productId).orElseGet(LinkedList::new))
            if(discount.isExpired())
                productDiscounts.removeProductDiscount(productId, discount);
            else
                finalPrice = discount.applyProductDiscount(finalPrice, productQuantity, couponCodes);
        return finalPrice;
    }

    public double getPrice() {
        return price;
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
