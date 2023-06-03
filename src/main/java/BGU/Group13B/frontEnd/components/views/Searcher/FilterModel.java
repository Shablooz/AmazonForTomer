package BGU.Group13B.frontEnd.components.views.Searcher;

public class FilterModel {
    public Double minPrice;
    public Double maxPrice;
    public String category;
    public Double minProductRank;
    public Double maxProductRank;
    public Double minStoreRank;
    public Double maxStoreRank;

    public FilterModel(Double minPrice, Double maxPrice, String category, Double minProductRank, Double maxProductRank, Double minStoreRank, Double maxStoreRank) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.category = category;
        this.minProductRank = minProductRank;
        this.maxProductRank = maxProductRank;
        this.minStoreRank = minStoreRank;
        this.maxStoreRank = maxStoreRank;
    }

}
