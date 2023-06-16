package BGU.Group13B.backend.Repositories.Implementations.UserRepositoryImpl;

import BGU.Group13B.backend.Repositories.Implementations.StoreRepositoryImpl.StoreRepoJPA;
import BGU.Group13B.backend.Repositories.Implementations.StoreRepositoryImpl.StoreRepositoryAsList;
import BGU.Group13B.service.SingletonCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRepoService {

    private UserRepoJPA userRepoJPA;

    @Autowired
    public UserRepoService(UserRepoJPA userRepoJPA) {
        this.userRepoJPA = userRepoJPA;
    }

    public UserRepoService(){

    }

    public void save(UserRepositoryAsHashmap userRepositoryAsHashmap){
        SingletonCollection.setUserRepository(userRepoJPA.save(userRepositoryAsHashmap));

    }

    public void delete(UserRepositoryAsHashmap userRepositoryAsHashmap){
        userRepoJPA.delete(userRepositoryAsHashmap);
    }

    public UserRepositoryAsHashmap getUserRepositoryAsHashmap() {
        return userRepoJPA.findById(1).orElse(null);
    }
}
