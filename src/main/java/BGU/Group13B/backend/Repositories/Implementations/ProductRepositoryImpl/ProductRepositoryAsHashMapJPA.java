package BGU.Group13B.backend.Repositories.Implementations.ProductRepositoryImpl;

import BGU.Group13B.backend.Repositories.Implementations.ReviewRepositoryImpl.ReviewRepoSingle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepositoryAsHashMapJPA extends JpaRepository<ProductRepositoryAsHashMap, Integer> {
}
