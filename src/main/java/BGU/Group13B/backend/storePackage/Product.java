package BGU.Group13B.backend.storePackage;

import BGU.Group13B.backend.Repositories.Interfaces.IProductDiscountsRepository;
import BGU.Group13B.backend.storePackage.Discounts.Discount;

import java.util.LinkedList;

public class Product {

    private String name;
    private final int productId;

    private final int storeId;
    private double price;
    private String category;
    private int rank;
    private int maxAmount;

    private final PurchasePolicy purchasePolicy;
    private final IProductDiscountsRepository productDiscounts;


    public Product(String name, int productId, int storeId, double price, int maxAmount, IProductDiscountsRepository productDiscounts) {
        this.name = name;
        this.productId = productId;
        this.storeId = storeId;
        this.price = price;
        this.maxAmount = maxAmount;
        this.productDiscounts = productDiscounts;
        this.rank=0;
        purchasePolicy = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
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

    public void setPrice(double price) {
        this.price = price;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    public PurchasePolicy getPurchasePolicy() {
        if (purchasePolicy == null)
            throw new NullPointerException("Purchase policy is null");
        return purchasePolicy;
    }


    public boolean tryDecreaseQuantity(int quantity) {
        if (maxAmount < quantity)
            return false;
        maxAmount -= quantity;
        return true;
    }

    public synchronized double calculatePrice(int productQuantity, String couponCodes) {
        purchasePolicy.checkPolicy(this, productQuantity);
        double finalPrice = price;
        for (Discount discount :
                productDiscounts.getProductDiscounts(productId).orElseGet(LinkedList::new))
            if(discount.isExpired())
                productDiscounts.removeProductDiscount(productId, discount);
            else
                finalPrice = discount.applyProductDiscount(finalPrice, productQuantity, couponCodes);
        return finalPrice;
    }

    public double getPrice() {
        return price;
    }
}
