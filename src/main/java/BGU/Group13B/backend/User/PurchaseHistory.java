package BGU.Group13B.backend.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PurchaseHistory {
    private int userId;
    private int storeId;
    private HashMap<Integer /*product id*/,Integer /*amount*/> products;
    private double price;

    public PurchaseHistory(int userId, int storeId, HashMap<Integer /*product id*/,Integer /*amount*/> products, double price) {
        this.userId = userId;
        this.storeId = storeId;
        this.products = products;
        this.price = price;
    }

    public int getUserId() {
        return userId;
    }

    public int getStoreId() {
        return storeId;
    }

    public double getPrice() {
        return price;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Store id: ").append(storeId).append("\n");
        sb.append("Products: ").append("\n");
        for (Integer productId : products.keySet()) {
            sb.append("Product id: ").append(productId).append(" Amount: ").append(products.get(productId)).append("\n");
        }
        sb.append("Price: ").append(price).append("\n");
        return sb.toString();
    }

    public List<Integer> getProductsId() {
        return List.copyOf(products.keySet());
    }
}
