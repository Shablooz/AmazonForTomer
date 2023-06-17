package BGU.Group13B.frontEnd.components.views.viewEntity;

import java.util.Objects;

public class Coupon {
    private final String coupon;
    public Coupon(String coupon){
        this.coupon = coupon;
    }

    public String getCoupon() {
        return coupon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coupon coupon1 = (Coupon) o;
        return Objects.equals(coupon, coupon1.coupon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coupon);
    }
}
