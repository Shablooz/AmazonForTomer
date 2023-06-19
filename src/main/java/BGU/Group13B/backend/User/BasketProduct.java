package BGU.Group13B.backend.User;

import BGU.Group13B.backend.storePackage.Product;
import jakarta.persistence.*;

@Entity
public class BasketProduct {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", referencedColumnName = "productId")
    private Product product;
    private int quantity;
    private double price;

    public BasketProduct(Product product) {
        this.product = product;
        this.quantity = 1;
        this.price = product.getPrice();
    }

    public BasketProduct() {
        this.product = null;
        this.quantity = 1;
        this.price = 0;
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
    //getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
