package BGU.Group13B.service.info;

import BGU.Group13B.backend.storePackage.Product;

/*
 * this class stores the information of a product
 * it will be used to send the information to the UI
 * it can also be used for tests
 */
public class ProductInfo {
    private final int productId;
    private final int storeId;
    private final String name;
    private final String category;
    private final double price;
    private final int stockQuantity;
    private final float score;

    public ProductInfo(Product product){
        productId = product.getProductId();
        storeId = product.getStoreId();
        name = product.getName();
        category = product.getCategory();
        price = product.getPrice();
        stockQuantity = product.getStockQuantity();
        score = product.getProductScore();
    }

    public int getProductId() {
        return productId;
    }

    public int getStoreId() {
        return storeId;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public float getScore() {
        return score;
    }
}
