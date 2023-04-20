package BGU.Group13B.backend.storePackage;

import BGU.Group13B.backend.Repositories.Interfaces.IProductDiscountsRepository;
import BGU.Group13B.backend.storePackage.Discounts.Discount;

import java.util.LinkedList;

import BGU.Group13B.backend.Repositories.Interfaces.IRepositoryReview;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseExceedsPolicyException;
import BGU.Group13B.service.SingletonCollection;

public class Product {

    private String name;
    private final int productId;
    private final int storeId;
    private double price;
    private String category;
    private int rank;
    private int stockQuantity;
    private final PurchasePolicy purchasePolicy;
    private final DiscountPolicy discountPolicy;
    private final IRepositoryReview repositoryReview;
    private final IProductDiscountsRepository productDiscounts;


    public Product(int productId, int storeId, String name, String category, double price, int stockQuantity) {
        this.productId = productId;
        this.storeId = storeId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.rank = 0;
        this.purchasePolicy = new PurchasePolicy();
        this.discountPolicy = new DiscountPolicy();
        this.repositoryReview = SingletonCollection.getReviewRepository();
        this.productDiscounts = SingletonCollection.getProductDiscountsRepository();
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
        if(price <= 0)
            throw new IllegalArgumentException("Price must be greater than 0");
        this.price = price;
    }

    public PurchasePolicy getPurchasePolicy() {
        if (purchasePolicy == null)
            throw new NullPointerException("Purchase policy is null");
        return purchasePolicy;
    }

    public boolean tryDecreaseQuantity(int quantity) {
        if (this.stockQuantity < quantity)
            return false;
        this.stockQuantity -= quantity;
        return true;
    }

    public void increaseQuantity(int quantity) {
        this.stockQuantity += quantity;
    }

    public synchronized double calculatePrice(int productQuantity, String couponCodes) throws PurchaseExceedsPolicyException {
        purchasePolicy.checkPolicy(productQuantity);
        double finalPrice = price;
        for (Discount discount :
                productDiscounts.getProductDiscounts(productId).orElseGet(LinkedList::new))
            if(discount.isExpired())
                productDiscounts.removeProductDiscount(productId, discount);
            else
                finalPrice = discount.applyProductDiscount(finalPrice, productQuantity, couponCodes);
        return finalPrice * productQuantity;//added by shaun in the night of 20/04/2023
    }

    public double getPrice() {
        return price;
    }

    public int getStockQuantity() {
        return this.stockQuantity;
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

    public void setCategory(String category) {
        this.category = category;
    }

    public void setStockQuantity(int stockQuantity) {
        if(stockQuantity < 0)
            throw new IllegalArgumentException("Stock quantity can't be negative");
        this.stockQuantity = stockQuantity;
    }

    public boolean isOutOfStock() {
        return stockQuantity <= 0; //should not be negative
    }


}
