package BGU.Group13B.backend.Repositories.Implementations.BasketProductRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IBasketProductRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IProductRepository;
import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.storePackage.Product;
import BGU.Group13B.service.SingletonCollection;
import org.springframework.data.util.Pair;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BasketProductRepositoryAsHashMap implements IBasketProductRepository {
    private final ConcurrentHashMap<Pair<Integer/*storeId*/, Integer/*userId*/>, Optional<Set<BasketProduct>>> basketProducts;
    private final IProductRepository productRepository;

    public BasketProductRepositoryAsHashMap() {
        this.basketProducts = new ConcurrentHashMap<>();
        this.productRepository = SingletonCollection.getProductRepository();
    }

    @Override
    public Optional<Set<BasketProduct>> getBasketProducts(int storeId, int userId) {
        return basketProducts.getOrDefault(Pair.of(storeId, userId), Optional.empty());
    }

    @Override
    public void removeStoreProduct(int productId, int storeId, int userId) {
        basketProducts.remove(Pair.of(storeId, userId)).orElseThrow(
                () -> new IllegalArgumentException("Product with id: " + productId + " does not exist in the basket"));
    }

    //1 - get the products of the store
    //2 - put the products in the basket
    @Override
    public void addNewProductToBasket(int productId, int storeId, int userId) {
        if (!basketProducts.containsKey(Pair.of(storeId, userId))) {

            var productsFromStore = productRepository.getStoreProducts(storeId).orElseThrow(
                            () -> new IllegalArgumentException("Products of store with id: " + storeId + " does not exist"))
                    .stream().map(BasketProduct::new).collect(Collectors.toSet());
            basketProducts.putIfAbsent(Pair.of(storeId, userId), Optional.of(productsFromStore));
        } else {
            throw new IllegalArgumentException("Product with id: " + productId + " already exist in store with id: " + storeId);
        }


    }

    @Override
    public void changeProductQuantity(int productId, int storeId, int userId, int addedQuantity) {

        var basketProducts = getBasketProducts(storeId, userId).orElseThrow(
                () -> new IllegalArgumentException("Products of store with id: " + storeId + " does not exist"));

        basketProducts.stream().filter(basketProduct -> basketProduct.getProductId() == productId).findFirst().
                orElseThrow(
                        () -> new IllegalArgumentException("Product with id: " + productId + " does not exist in this basket")
                ).addQuantity(addedQuantity);
    }

    @Override
    public BasketProduct getBasketProduct(int productId, int storeId, int userId) throws IllegalArgumentException{
        for (BasketProduct basketProduct : getBasketProducts(storeId, userId).orElseThrow(
                () -> new IllegalArgumentException("There is no basket of store with id: " + storeId))) {
            if (basketProduct.getProductId() == productId) {
                return basketProduct;
            }
        }
        return null;
    }
}
