package BGU.Group13B.backend.Repositories.Implementations.UserRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IUserRepository;
import BGU.Group13B.backend.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

public class UserRepositoryAsJpa implements IUserRepository {
    private final JpaRepository<User, Integer> repo;

    public UserRepositoryAsJpa(JpaRepository<User, Integer> repo) {
        this.repo = repo;
    }
}
