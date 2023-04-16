package BGU.Group13B.backend.storePackage;

public class Product {

    private String name;
    private int productId;
    private int storeId;
    private double price;
    private int amount;
    private String category;
    private int rank;
    private final PurchasePolicy purchasePolicy;
    private final DiscountPolicy discountPolicy;

    public Product(String name, int productId, double price, int amount) {
        this.name = name;
        this.productId = productId;
        this.price = price;
        this.amount = amount;
        this.rank=0;
        purchasePolicy = null;
        discountPolicy = null;
    }

    public String getName() {
        return name;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public int getRank() {
        return this.rank;
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

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public synchronized double getPrice() {
        return price;
    }

    public synchronized void setPrice(double price) {
        this.price = price;
    }

    public synchronized int getAmount() {
        return amount;
    }

    public synchronized void setAmount(int amount) {
        this.amount = amount;
    }

    public synchronized PurchasePolicy getPurchasePolicy() {
        if(purchasePolicy == null)
            throw new NullPointerException("Purchase policy is null");
        return purchasePolicy;
    }

    public synchronized DiscountPolicy getDiscountPolicy() {
        if(discountPolicy == null)
            throw new NullPointerException("Discount policy is null");
        return discountPolicy;
    }


}
