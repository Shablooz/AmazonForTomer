package BGU.Group13B.service.info;

import BGU.Group13B.backend.storePackage.Product;

/*
 * this class stores the information of a product
 * it will be used to send the information to the UI
 * it can also be used for tests
 */

public record ProductInfo(int productId, int storeId, String seller, String name, String category, double price,
                          int stockQuantity, String description, float score) {
    public ProductInfo(Product product) {
        this(product.getProductId(), product.getStoreId(), product.getSeller(), product.getName(),
                product.getCategory(), product.getPrice(), product.getStockQuantity(),
                product.getDescription(), product.getProductScore());
    }

    public double getRoundedScore() {
        return Math.round(score * 10.0) / 10.0;
    }
}