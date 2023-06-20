package BGU.Group13B.backend.Repositories.Implementations.ProductRepositoryImpl;

import BGU.Group13B.service.SingletonCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductRepositoryAsHashMapService {

        private ProductRepositoryAsHashMapJPA productRepositoryAsHashMapJPA;

        @Autowired
        public ProductRepositoryAsHashMapService(ProductRepositoryAsHashMapJPA productRepositoryAsHashMapJPA) {
            this.productRepositoryAsHashMapJPA = productRepositoryAsHashMapJPA;
        }

        public ProductRepositoryAsHashMapService(){

        }

        public void save(ProductRepositoryAsHashMap productRepositoryAsHashMap){
             SingletonCollection.setProductRepository(productRepositoryAsHashMapJPA.save(productRepositoryAsHashMap));
        }

        public void delete(ProductRepositoryAsHashMap productRepositoryAsHashMap){
            productRepositoryAsHashMapJPA.delete(productRepositoryAsHashMap);
        }

        public ProductRepositoryAsHashMap getProductRepositoryAsHashMapJPA() {
            return productRepositoryAsHashMapJPA.findById(1).orElse(null);
        }
}
