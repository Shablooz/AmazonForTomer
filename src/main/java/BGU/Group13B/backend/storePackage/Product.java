package BGU.Group13B.backend.storePackage;

import java.io.Serializable;
import java.util.List;

import BGU.Group13B.backend.Repositories.Implementations.ReviewRepositoryImpl.ReviewRepoSingle;
import BGU.Group13B.backend.Repositories.Interfaces.IRepositoryReview;
import BGU.Group13B.service.SingletonCollection;
import BGU.Group13B.service.info.ProductInfo;
import com.vaadin.flow.function.SerializableEventListener;
import jakarta.persistence.*;

@Entity
public class Product implements Serializable {

    private String name;
    @Id
    private int productId;

    private int storeId;

    private double price;

    private String category;

    private String description;

    private int stockQuantity;

    @Transient
    private IRepositoryReview repositoryReview;

    public boolean deleted = false;
    public boolean hidden = false;


    public Product(int productId, int storeId, String name, String category, double price, int stockQuantity, String description) {
        this.productId = productId;
        this.storeId = storeId;
        this.name = name;
        this.category = category;
        this.setPrice(price);
        this.setStockQuantity(stockQuantity);
        this.description = description;
        repositoryReview = SingletonCollection.getReviewRepository();
    }

    public Product(int productId, int storeId, String name, String category, double price, int stockQuantity, String description, boolean hidden){
        this(productId, storeId, name, category, price, stockQuantity, description);
        this.hidden = hidden;
    }
    //shouldnt acualy have those values
    public Product() {
        this.productId = 50;
        this.storeId = 50;
        this.name = "gk";
        this.category = "gk";
        this.setPrice(50);
        this.setStockQuantity(50);
        this.description = "gk";
        repositoryReview = SingletonCollection.getReviewRepository();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public boolean tryDecreaseQuantity(int quantity) {
        if (this.stockQuantity < quantity)
            return false;
        this.stockQuantity -= quantity;
        return true;
    }

    public void increaseQuantity(int quantity) {
        this.stockQuantity += quantity;
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

    public List<Review> getAllReviews(){
        return repositoryReview.getAllReviews(storeId,productId);
    }
    public float getProductScore(){
        return repositoryReview.getProductScore(storeId,productId);
    }
    public float getProductScoreUser(int userId){
        return repositoryReview.getProductScoreUser(storeId,productId,userId);
    }
    public void addAndSetScore(int userId,int score){
        repositoryReview.addAndSetProductScore(storeId,productId,userId,score);
    }
    public void removeProductScore(int userId){
        repositoryReview.removeProductScore(storeId,productId,userId);
    }
    public String getDescription() {
        return description;
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

    public ProductInfo getProductInfo() {
        return new ProductInfo(this);
    }

    public synchronized void delete() {
        repositoryReview.removeProductData(storeId,productId);
        this.deleted = true;
    }

    public boolean isDeleted() {
        return this.deleted;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSeller(){
        return SingletonCollection.getStoreRepository().getStore(storeId).getStoreName();
    }
  
    public void hide() {
        this.hidden = true;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public void unhide() {
        this.hidden = false;
    }

    public IRepositoryReview getRepositoryReview() {
        return repositoryReview;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }


}
