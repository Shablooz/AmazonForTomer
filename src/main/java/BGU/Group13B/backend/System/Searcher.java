package BGU.Group13B.backend.System;

import BGU.Group13B.backend.Repositories.Interfaces.IProductRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IStoreRepository;
import BGU.Group13B.backend.storePackage.Product;

import java.util.Collection;
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
    public List<Product> searchByName(String name) {
        products = productRepository.getProductByName(name);
        filterHiddenProducts();
        return products;
    }
    public List<Product> searchByCategory(String category) {
        products = productRepository.getProductByCategory(category);
        filterHiddenProducts();
        return products;
    }
    public List<Product> searchByKeywords(List<String> Keywords) {
        products = productRepository.getProductByKeywords(Keywords);
        filterHiddenProducts();
        return products;
    }
    public List<Product> filterByPriceRange(int minPrice, int maxPrice) {
        filterHiddenProducts();
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
        filterHiddenProducts();
        List<Product> newProducts = new LinkedList<>();
        for (Product product : products) {
            if (product.getProductScore() >= minRating && product.getProductScore() <= maxRating) {
                newProducts.add(product);
            }
        }
        products = newProducts;
        return products;
    }
    public List<Product> filterByCategory(String category) {
        filterHiddenProducts();
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
        filterHiddenProducts();
        List<Product> newProducts = new LinkedList<>();
        for (Product product : products) {
            if (storeRepository.getStore(product.getStoreId()).getStoreScore() >= minRating &&
                    storeRepository.getStore(product.getStoreId()).getStoreScore() <= maxRating) {
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


