package BGU.Group13B.backend.User;

import BGU.Group13B.backend.storePackage.Product;

public class BasketProduct {
    private final Product product;
    private int quantity;
    private double price;

    public BasketProduct(Product product) {
        this.product = product;
        this.quantity = 1;
        this.price = product.getPrice();
    }


    //gets product id
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getProductId() {
        return product.getProductId();
    }

    public void addQuantity(int addedQuantity) {
        this.quantity += addedQuantity;
    }

    public String getName() {
        return product.getName();
    }

    public double getPrice() {
        return price;
    }

    public String getCategory() {
        return product.getCategory();
    }

    public int getStoreId() {
        return product.getStoreId();
    }


    public String toString() {
        return "Product: " + product.getName() + " Quantity: " + quantity + "\n";
    }

    public Product getProduct() {
        return product;
    }
    public double getSubtotal() {
        return getPrice() * quantity;
    }

    public void setPrice(double price) {
    	this.price = price;
    }
}
