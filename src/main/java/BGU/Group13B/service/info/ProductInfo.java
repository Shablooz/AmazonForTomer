package BGU.Group13B.service.info;

import BGU.Group13B.backend.storePackage.Product;

/*
 * this class stores the information of a product
 * it will be used to send the information to the UI
 * it can also be used for tests
 */

public record ProductInfo(int productId, int storeId, String name, String category, double price,
                          int stockQuantity, float score) {
    public ProductInfo(Product product) {
        this(product.getProductId(), product.getStoreId(), product.getName(),
                product.getCategory(), product.getPrice(), product.getStockQuantity(),
                product.getProductScore());
    }
}