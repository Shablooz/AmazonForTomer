package BGU.Group13B.frontEnd;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoderJPA extends JpaRepository<NoderSon, Integer> {
}
