package BGU.Group13B.backend.System;

import BGU.Group13B.backend.Repositories.Interfaces.IProductRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IStoreRepository;
import BGU.Group13B.backend.storePackage.Product;

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
        return products;
    }
    public List<Product> searchByCategory(String category) {
        products = productRepository.getProductByCategory(category);
        return products;
    }
    public List<Product> searchByKeywords(List<String> Keywords) {
        products = productRepository.getProductByKeywords(Keywords);
        return products;
    }
    public List<Product> filterByPriceRange(int minPrice, int maxPrice) {
        checkProducts();
        for (Product product : products) {
            if (product.getPrice() < minPrice || product.getPrice() > maxPrice) {
                products.remove(product);
            }
        }
        return products;
    }
    public List<Product> filterByProductRank(int minRating, int maxRating) {
        checkProducts();
        for (Product product : products) {
            if (product.getRank() < minRating || product.getRank() > maxRating) {
                products.remove(product);
            }
        }
        return products;
    }
    public List<Product> filterByCategory(String category) {
        checkProducts();
        for (Product product : products) {
            if (!product.getCategory().toLowerCase().equals(category.toLowerCase())) {
                products.remove(product);
            }
        }
        return products;
    }
    //filter by store rating
    public List<Product> filterByStoreRank(int minRating, int maxRating) {
        checkProducts();
        for (Product product : products) {
            if (storeRepository.getStore(product.getStoreId()).getRank() < minRating ||
                    storeRepository.getStore(product.getStoreId()).getRank() > maxRating) {
                products.remove(product);
            }
        }
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


