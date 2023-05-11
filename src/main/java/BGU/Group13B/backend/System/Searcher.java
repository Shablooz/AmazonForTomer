package BGU.Group13B.backend.System;

import BGU.Group13B.backend.Repositories.Interfaces.IProductRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IStoreRepository;
import BGU.Group13B.backend.storePackage.Product;
import BGU.Group13B.service.info.ProductInfo;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

//a class that searches product by its name, category and keywords
public class Searcher {
    private final IProductRepository productRepository;
    private IStoreRepository storeRepository;

    private List<Product> products;

    public Searcher(IProductRepository productRepository, IStoreRepository storeRepository) {
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
        products = new LinkedList<>();
    }
    public List<ProductInfo> searchByName(String name) {
        products = productRepository.getProductByName(name);
        List<ProductInfo> productsInfo = new LinkedList<>();
        for(Product product: products) {
            productsInfo.add(new ProductInfo(product));
        }
        return productsInfo;
    }
    public List<ProductInfo> searchByCategory(String category) {
        products = productRepository.getProductByCategory(category);
        List<ProductInfo> productsInfo = new LinkedList<>();
        for(Product product: products) {
            productsInfo.add(new ProductInfo(product));
        }
        return productsInfo;
    }
    public List<ProductInfo> searchByKeywords(String Keywords) {
        String[] keywords = Keywords.split(" ");
        products = productRepository.getProductByKeywords(Arrays.asList(keywords));
        List<ProductInfo> productsInfo = new LinkedList<>();
        for(Product product: products) {
            productsInfo.add(new ProductInfo(product));
        }
        return productsInfo;
    }
    public List<Product> filterByPriceRange(int minPrice, int maxPrice) {
        List<Product> newProducts = new LinkedList<>();
        for (Product product : products) {
            if (product.getPrice() >= minPrice && product.getPrice() <= maxPrice) {
                newProducts.add(product);
            }
        }
        products = newProducts;
        return products;
    }
    public List<Product> filterByProductRank(int minRating, int maxRating) {
        List<Product> newProducts = new LinkedList<>();
        for (Product product : products) {
            if (product.getRank() >= minRating && product.getRank() <= maxRating) {
                newProducts.add(product);
            }
        }
        products = newProducts;
        return products;
    }
    public List<Product> filterByCategory(String category) {
        List<Product> newProducts = new LinkedList<>();
        for (Product product : products) {
            if (product.getCategory().toLowerCase().equals(category.toLowerCase())) {
                newProducts.add(product);
            }
        }
        products = newProducts;
        return products;
    }
    //filter by store rating
    public List<Product> filterByStoreRank(int minRating, int maxRating) {
        List<Product> newProducts = new LinkedList<>();
        for (Product product : products) {
            if (storeRepository.getStore(product.getStoreId()).getRank() >= minRating &&
                    storeRepository.getStore(product.getStoreId()).getRank() <= maxRating) {
                newProducts.add(product);
            }
        }
        products = newProducts;
        return products;
    }


    private void checkProducts() {
        if(products.isEmpty()) {
            throw new IllegalArgumentException("No products to filter");
        }
       }

    public Product getProductById(int productId) {
        return productRepository.getProductById(productId);

    }

}


