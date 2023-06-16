package BGU.Group13B.backend.Repositories.Implementations.BasketProductRepositoryImpl;

import BGU.Group13B.backend.Pair;
import BGU.Group13B.backend.Repositories.Interfaces.IBasketProductRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IProductRepository;
import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.storePackage.Product;
import BGU.Group13B.service.SingletonCollection;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class BasketProductRepositoryAsHashMap implements IBasketProductRepository {
    private final ConcurrentHashMap<Pair<Integer/*storeId*/, Integer/*userId*/>, Optional<List<BasketProduct>>> basketProducts;
    private final IProductRepository productRepository;

    public BasketProductRepositoryAsHashMap() {
        this.basketProducts = new ConcurrentHashMap<>();
        this.productRepository = SingletonCollection.getProductRepository();
    }

    @Override
    public Optional<List<BasketProduct>>
    getBasketProducts(int storeId, int userId) {
        return basketProducts.getOrDefault(Pair.of(storeId, userId), Optional.empty());
    }

    @Override
    public void removeBasketProducts(int storeId, int userId) {
       getBasketProducts(storeId, userId).ifPresent(List::clear);
        /* basketProducts.remove(Pair.of(storeId, userId)).orElseThrow(
                () -> new IllegalArgumentException("There is not basket for store with id: " + storeId + " and user with id: " + userId));
    */}



    //1 - get the products of the store
    //2 - put the products in the basket
    @Override
    public void addNewProductToBasket(int productId, int storeId, int userId) {
        Product product = productRepository.getStoreProductById(productId, storeId);

        if(!basketProducts.containsKey(Pair.of(storeId, userId))) {
            List<BasketProduct> newBasketProducts = new LinkedList<>();
            newBasketProducts.add(new BasketProduct(product));
            basketProducts.put(Pair.of(storeId, userId), Optional.of(newBasketProducts));
        }else{
            List<BasketProduct> basketProductsList = basketProducts.get(Pair.of(storeId, userId)).get();
            var basketProductOptional = basketProductsList.stream().filter(basketProduct -> basketProduct.getProductId() == productId).findFirst();
            if(basketProductOptional.isPresent()) {
               throw new IllegalArgumentException("Product with id: " + productId + " already exists in the basket");
            }
            basketProductsList.add(new BasketProduct(product));
        }
    }


    @Override
    public void changeProductQuantity(int productId, int storeId, int userId, int newQuantity) {

        var basketProducts = getBasketProducts(storeId, userId).orElseThrow(
                () -> new IllegalArgumentException("Products of store with id: " + storeId + " does not exist"));

        basketProducts.stream().filter(basketProduct -> basketProduct.getProductId() == productId).findFirst().
                orElseThrow(
                        () -> new IllegalArgumentException("Product with id: " + productId + " does not exist in this basket")
                ).setQuantity(newQuantity);
    }

    @Override
    public BasketProduct getBasketProduct(int productId, int storeId, int userId) throws IllegalArgumentException{
        for (BasketProduct basketProduct : getBasketProducts(storeId, userId).orElseGet(LinkedList::new)) {
            if (basketProduct.getProductId() == productId) {
                return basketProduct;
            }
        }
        return null;
    }
    @Override
    public void removeBasketProduct(int productId, int userId, int storeId) {
        BasketProduct basketProduct = getBasketProduct(productId, storeId, userId);
        if(basketProduct!=null)
            getBasketProducts(storeId, userId).get().remove(basketProduct);
    }

    @Override
    public boolean isUserBasketsExists(int userId) {
        //if basketProducts contain pair keys with userId return true else return false
        return basketProducts.keySet().stream().anyMatch(pair -> pair.getSecond() == userId);
    }

    @Override
    public void dropBasket(int storeId, int userId) {
        basketProducts.remove(Pair.of(storeId, userId));
    }

}
