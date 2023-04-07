package BGU.Group13B.backend.storePackage.Discounts;

public abstract class Discount {
    protected Integer priority;
    abstract void apply();
}
