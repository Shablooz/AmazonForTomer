package BGU.Group13B.service.entity;

import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.storePackage.Product;

public class ServiceBasketProduct extends AbstractEntity {

    private String name;
    private int quantity;
    private double price;
    private double subtotal;
    private String category;
    private int StoreId;
    private int productId;

    public ServiceBasketProduct(BasketProduct product) {
        this.name = product.getName();
        this.quantity = product.getQuantity();
        this.price = product.getPrice();
        this.subtotal = product.getPrice() * product.getQuantity();
        this.category = product.getCategory();
        this.StoreId = product.getStoreId();
        this.productId = product.getProductId();
    }

    public ServiceBasketProduct(String name, int quantity, double price, double subtotal, String category) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = subtotal;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public int getStoreId() {
        return StoreId;
    }

    public int getProductId() {
        return productId;
    }

    public String getCategory() {
        return category;
    }
    public void setQuantity(String quantity) {
        this.quantity = Integer.parseInt(quantity);
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return String.format("""
                Product name: %s
                Quantity: %d
                Price: %.2f
                Subtotal: %.2f
                """, name, quantity, price, subtotal);
    }

}
