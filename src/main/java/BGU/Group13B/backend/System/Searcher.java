package BGU.Group13B.backend.System;

import BGU.Group13B.backend.Repositories.Interfaces.IProductRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IStoreRepository;
import BGU.Group13B.backend.storePackage.Product;
import BGU.Group13B.service.Response;
import BGU.Group13B.service.SingletonCollection;
import BGU.Group13B.service.info.ProductInfo;

import java.util.Collection;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

//a class that searches product by its name, category and keywords
public class Searcher {
    private IProductRepository productRepository;
    private IStoreRepository storeRepository;

    private List<Product> products;


    public IProductRepository getProductRepository() {
        this.productRepository = SingletonCollection.getProductRepository();
        return productRepository;
    }

    public IStoreRepository getStoreRepository() {
        this.storeRepository = SingletonCollection.getStoreRepository();
        return storeRepository;
    }

    public Searcher(IProductRepository productRepository, IStoreRepository storeRepository) {
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
        products = new LinkedList<>();
    }
    public List<ProductInfo> searchByName(String name) {
        products = getProductRepository().getProductByName(name);
        filterHiddenProducts();
        return wrapAsProductInfo(products);

    }
    public List<ProductInfo> searchByCategory(String category) {
        products = getProductRepository().getProductByCategory(category);
        filterHiddenProducts();
        return wrapAsProductInfo(products);
    }

    public List<ProductInfo> searchByKeywords(String Keywords) {
        String[] keywords = Keywords.split(" ");
        products = getProductRepository().getProductByKeywords(Arrays.asList(keywords));
        filterHiddenProducts();
        return wrapAsProductInfo(products);
    }

    private List<ProductInfo> wrapAsProductInfo(List<Product> products) {
        List<ProductInfo> productsInfo = new LinkedList<>();
        for (Product product : products) {
            productsInfo.add(new ProductInfo(product));
        }
        return productsInfo;
    }

    public List<ProductInfo> filterByPriceRange(double minPrice, double maxPrice) {
        filterHiddenProducts();
        List<Product> newProducts = new LinkedList<>();
        for (Product product : products) {
            if (product.getPrice() >= minPrice && product.getPrice() <= maxPrice) {
                newProducts.add(product);
            }
        }
        products = newProducts;
        return wrapAsProductInfo(products);
    }
    public List<ProductInfo> filterByProductRank(double minRating, double maxRating) {
        filterHiddenProducts();
        List<Product> newProducts = new LinkedList<>();
        for (Product product : products) {
            if (product.getProductScore() >= minRating && product.getProductScore() <= maxRating) {
                newProducts.add(product);
            }
        }
        products = newProducts;
        return wrapAsProductInfo(products);
    }
    public List<ProductInfo> filterByCategory(String category) {
        filterHiddenProducts();
        List<Product> newProducts = new LinkedList<>();
        for (Product product : products) {
            if (product.getCategory().toLowerCase().equals(category.toLowerCase())) {
                newProducts.add(product);
            }
        }
        products = newProducts;
        return wrapAsProductInfo(products);
    }

    public List<ProductInfo> filterByStoreRank(double minRating, double maxRating) {
        filterHiddenProducts();
        List<Product> newProducts = new LinkedList<>();
        for (Product product : products) {
            if (getStoreRepository().getStore(product.getStoreId()).getStoreScore() >= minRating &&
                    getStoreRepository().getStore(product.getStoreId()).getStoreScore() <= maxRating) {
                newProducts.add(product);
            }
        }

        products = newProducts;
        return wrapAsProductInfo(products);
    }


    private void checkProducts() {
        if(products.isEmpty()) {
            throw new IllegalArgumentException("No products to filter");
        }
       }

    private void filterHiddenProducts() {
        List<Product> filteredProducts = new LinkedList<>();
        for (Product product : products) {
            if (!product.isHidden()) {
                filteredProducts.add(product);
            }
        }
        products = filteredProducts;
    }

}


