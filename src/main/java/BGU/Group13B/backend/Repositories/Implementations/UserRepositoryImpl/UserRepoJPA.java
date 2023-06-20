package BGU.Group13B.backend.Repositories.Implementations.UserRepositoryImpl;

import BGU.Group13B.backend.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepoJPA extends JpaRepository<UserRepositoryAsHashmap, Integer> {
}
