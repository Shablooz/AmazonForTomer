package BGU.Group13B.backend.User;

import BGU.Group13B.service.SingletonCollection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PurchaseHistory {
    private int userId;
    private int storeId;
    private HashMap<Integer /*product id*/,Integer /*quantity*/> products;
    private double price;
    private String storeName;

    public PurchaseHistory(int userId, int storeId, HashMap<Integer /*product id*/,Integer /*quantity*/> products, double price) {
        this.userId = userId;
        this.storeId = storeId;
        this.products = products;
        this.price = price;
        this.storeName=getStoreName();
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

    public String productsToString(){
        StringBuilder sb = new StringBuilder();
        for (Integer productId : products.keySet()) {
            sb.append("Product id: ").append(productId).append(" Amount: ").append(products.get(productId)).append("\n");
        }
        sb.append("Price: ").append(price).append("\n");
        return sb.toString();
    }
    public List<Integer> getProductsId() {
        return List.copyOf(products.keySet());
    }

    public String getStoreName(){
       return SingletonCollection.getStoreRepository().getStore(storeId).getStoreName();
    }
}
