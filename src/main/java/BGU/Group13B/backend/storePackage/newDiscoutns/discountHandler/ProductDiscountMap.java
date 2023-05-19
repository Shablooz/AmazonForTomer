package BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.BasketProduct;


import java.util.HashMap;

public class ProductDiscountMap {
    private HashMap<Integer/*productId*/, Double/*discount percentage*/> productDiscountMap;


    public ProductDiscountMap(BasketInfo basketInfo){
        productDiscountMap = new HashMap<>();

        for(BasketProduct product : basketInfo.basketProducts()){
            productDiscountMap.put(product.getProductId(), 0.0);
        }
    }

    public void computeMaxDiscount(ProductDiscountMap other){
        //assumes the other has the same key set
        for(int productId : productDiscountMap.keySet()){
            productDiscountMap.put(productId, Math.max(productDiscountMap.get(productId), other.productDiscountMap.get(productId)));
        }
    }

    public void computeAddDiscount(ProductDiscountMap other){
        //assumes the other has the same key set
        for(int productId : productDiscountMap.keySet()){
            productDiscountMap.put(productId, Math.max(productDiscountMap.get(productId) + other.productDiscountMap.get(productId), 1.0));
        }
    }

    public void computeXorDiscount(ProductDiscountMap other){
        if(this.hasDiscounts() && other.hasDiscounts()){
            this.reset();
            return;
        }
        computeAddDiscount(other);
    }

    public void setProductDiscount(int productId, double discount){
        productDiscountMap.put(productId, discount);
    }

    public void reset(){
        productDiscountMap.replaceAll((i, v) -> 0.0);
    }

    public boolean hasDiscounts(){
        return productDiscountMap.entrySet().stream().anyMatch(e -> e.getValue() > 0);
    }

    public double getProductDiscount(int productId){
        return productDiscountMap.get(productId);
    }
}
