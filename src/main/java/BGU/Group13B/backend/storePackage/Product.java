package BGU.Group13B.backend.storePackage;

public class Product {

    private String name;
    private final int productId;

    private final int storeId;
    private double price;
    private int amount;

    private final PurchasePolicy purchasePolicy;
    private final DiscountPolicy discountPolicy;

    public Product(String name, int productId, int storeId, double price, int amount) {
        this.name = name;
        this.productId = productId;
        this.storeId = storeId;
        this.price = price;
        this.amount = amount;

        purchasePolicy = null;
        discountPolicy = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProductId() {
        return productId;
    }


    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public PurchasePolicy getPurchasePolicy() {
        if (purchasePolicy == null)
            throw new NullPointerException("Purchase policy is null");
        return purchasePolicy;
    }

    public DiscountPolicy getDiscountPolicy() {
        if (discountPolicy == null)
            throw new NullPointerException("Discount policy is null");
        return discountPolicy;
    }

    public boolean tryDecreaseQuantity(int quantity) {
        if (amount < quantity)
            return false;
        amount -= quantity;
        return true;
    }
}
