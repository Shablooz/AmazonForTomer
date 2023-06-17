package BGU.Group13B.frontEnd.components.views.viewEntity;

public class Product {
    private String productName;
    private String storeName;
    public Product(String productName, String storeName){
        this.productName = productName;
        this.storeName = storeName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
}
