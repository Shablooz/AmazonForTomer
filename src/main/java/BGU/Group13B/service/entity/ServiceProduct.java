package BGU.Group13B.service.entity;

import BGU.Group13B.backend.storePackage.Product;

public class ServiceProduct extends AbstractEntity{
    private String name;
    private double originalPrice;

    public ServiceProduct(Product product) {
        this.name = product.getName();
        this.originalPrice = product.getPrice();
    }
    public ServiceProduct(String name, double originalPrice) {
        this.name = name;
        this.originalPrice = originalPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }
}
