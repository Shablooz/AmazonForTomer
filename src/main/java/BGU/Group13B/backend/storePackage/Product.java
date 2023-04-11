package BGU.Group13B.backend.storePackage;

import BGU.Group13B.backend.Repositories.Interfaces.IProductDiscountsRepository;
import BGU.Group13B.backend.storePackage.Discounts.Discount;

import java.util.LinkedList;

public class Product {

    private String name;
    private final int productId;

    private final int storeId;
    private double price;
    private int amount;

    private final PurchasePolicy purchasePolicy;
    private final IProductDiscountsRepository productDiscounts;


    public Product(String name, int productId, int storeId, double price, int amount, IProductDiscountsRepository productDiscounts) {
        this.name = name;
        this.productId = productId;
        this.storeId = storeId;
        this.price = price;
        this.amount = amount;
        this.productDiscounts = productDiscounts;

        purchasePolicy = null;
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


    public double calculatePrice() {
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


    public boolean tryDecreaseQuantity(int quantity) {
        if (amount < quantity)
            return false;
        amount -= quantity;
        return true;
    }

    public double calculatePrice(int productQuantity) {
        purchasePolicy.checkPolicy(this, productQuantity);
        double finalPrice = price;
        for (Discount discount :
                productDiscounts.getProductDiscounts(productId).orElseGet(LinkedList::new))
            finalPrice = discount.apply(finalPrice, productQuantity);
        return finalPrice;
    }
}
