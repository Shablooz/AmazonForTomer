package BGU.Group13B.frontEnd.components.views.Searcher;

public class PriceRange {
    private double minPrice;
    private double maxPrice;

    public PriceRange(double minPrice, double maxPrice) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public double getMaxPrice() {
        return maxPrice;
    }
}
