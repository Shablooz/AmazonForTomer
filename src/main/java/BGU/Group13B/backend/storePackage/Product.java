package BGU.Group13B.backend.storePackage;

public class Product {

    private String name;
    private int productId;
    private int price;
    private int amount;

    private final PurchasePolicy purchasePolicy;
    private final DiscountPolicy discountPolicy;

    public Product(String name, int productId, int price, int amount, PurchasePolicy purchasePolicy, DiscountPolicy discountPolicy) {
        this.name = name;
        this.productId = productId;
        this.price = price;
        this.amount = amount;
        this.purchasePolicy = purchasePolicy;
        this.discountPolicy = discountPolicy;
    }
}
