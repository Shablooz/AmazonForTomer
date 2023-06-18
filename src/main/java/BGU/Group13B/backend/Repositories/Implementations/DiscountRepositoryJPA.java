package BGU.Group13B.backend.Repositories.Implementations;

import BGU.Group13B.backend.Repositories.Implementations.DiscountRepositoryAsHashMap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountRepositoryJPA extends JpaRepository<DiscountRepositoryAsHashMap,Integer> {
}
