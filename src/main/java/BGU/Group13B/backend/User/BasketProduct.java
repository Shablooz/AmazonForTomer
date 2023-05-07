package BGU.Group13B.backend.User;

import BGU.Group13B.backend.storePackage.Product;

public class BasketProduct  {
    private final Product product;
    private int quantity;

    public BasketProduct(Product product) {
        this.product = product;
        this.quantity = 1;
    }

    public Product getProduct() {
        return product;
    }

    //gets product id
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public int getProductId(){
        return product.getProductId();
    }

    public void addQuantity(int addedQuantity) {
        this.quantity += addedQuantity;
    }

    public String toString(){
        return "Product: " + product.getName() + " Quantity: " + quantity +"\n";
    }
}
