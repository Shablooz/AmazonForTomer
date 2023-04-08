package BGU.Group13B.backend.User;

import BGU.Group13B.backend.storePackage.Product;

public class BasketProduct {
    private final Product product;
    private int amount;

    public BasketProduct(Product product) {
        this.product = product;
        this.amount = 1;
    }

    public Product getProduct() {
        return product;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
    public int getProductId(){
        return product.getProductId();
    }

}
