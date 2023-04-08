package BGU.Group13B.backend.storePackage.Discounts;

public class VisibleDiscount extends Discount {


    public VisibleDiscount(Integer priority, double discountPercentage) {
        super(priority, discountPercentage);
    }


    @Override
    double apply(double currentPrice) {
        return 0;
    }
}
