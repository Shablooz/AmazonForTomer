package BGU.Group13B.backend.storePackage.Discounts;

import java.time.LocalDateTime;

public abstract class Discount {
    protected Integer priority;
    protected double discountPercentage;
    protected LocalDateTime discountLastDate;

    public Discount(Integer priority, double discountPercentage, LocalDateTime discountLastDate) {
        this.priority = priority;
        this.discountPercentage = discountPercentage;
        this.discountLastDate = discountLastDate;
    }
    public double apply(double currentPrice, int quantity){
        if(LocalDateTime.now().isBefore(discountLastDate))
            return currentPrice * (1 - discountPercentage);
        return currentPrice;
    }
    //todo apply for store
}
