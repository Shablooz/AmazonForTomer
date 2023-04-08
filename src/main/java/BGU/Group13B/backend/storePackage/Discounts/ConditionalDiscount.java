package BGU.Group13B.backend.storePackage.Discounts;

public class ConditionalDiscount extends Discount{


    public ConditionalDiscount(Integer priority) {
        super(priority);
    }

    @Override
    double apply(double currentPrice) {
        return 0;
    }
}
