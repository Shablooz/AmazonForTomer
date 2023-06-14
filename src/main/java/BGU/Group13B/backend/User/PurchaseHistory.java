package BGU.Group13B.backend.User;

import BGU.Group13B.service.SingletonCollection;
import org.springframework.scheduling.support.SimpleTriggerContext;

import javax.xml.crypto.Data;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PurchaseHistory {
    private int userId;
    private int storeId;
    private HashMap<Integer /*product id*/,Integer /*quantity*/> products;
    private double price;
    private Date date;

    public PurchaseHistory(int userId, int storeId, HashMap<Integer /*product id*/,Integer /*quantity*/> products, double price) {
        this.userId = userId;
        this.storeId = storeId;
        this.products = products;
        this.price = price;
        this.date = new Date();
    }

    public int getUserId() {
        return userId;
    }
    public String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = sdf.format(date);
        return dateString;
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

    public String getStoreName(){
        return SingletonCollection.getStoreRepository().getStore(storeId).getStoreName();
    }

    public String getProductName(int productId){
        return SingletonCollection.getProductRepository().getProductById(productId).getProductInfo().name();
    }

    public String productsToString(){
        StringBuilder sb = new StringBuilder();
        for (Integer productId : products.keySet()) {
            sb.append("Product id: ").append(productId)
                    .append("\nProduct name: ").append(getProductName(productId))
                    .append("\nAmount: ").append(products.get(productId)).append("\n");
        }
       // sb.append("Price: ").append(price).append("\n");
        return sb.toString();
    }

    public LocalDate getLocalDate(){
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
