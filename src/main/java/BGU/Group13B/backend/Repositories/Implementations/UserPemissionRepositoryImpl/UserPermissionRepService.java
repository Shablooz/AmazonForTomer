package BGU.Group13B.backend.Repositories.Implementations.UserPemissionRepositoryImpl;

import BGU.Group13B.backend.Repositories.Implementations.UserRepositoryImpl.UserRepoJPA;
import BGU.Group13B.backend.Repositories.Implementations.UserRepositoryImpl.UserRepositoryAsHashmap;
import BGU.Group13B.service.SingletonCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserPermissionRepService {

    private UserPermissionRepJPA userPermissionRepJPA;

    @Autowired
    public UserPermissionRepService(UserPermissionRepJPA userPermissionRepJPA) {
        this.userPermissionRepJPA = userPermissionRepJPA;
    }

    public UserPermissionRepService(){

    }

    public void save(UserPermissionRepositoryAsHashmap userPermissionRepositoryAsHashmap){
        SingletonCollection.setUserPermissionRepository(userPermissionRepJPA.save(userPermissionRepositoryAsHashmap));

    }

    public void delete(UserPermissionRepositoryAsHashmap userPermissionRepositoryAsHashmap){
        userPermissionRepJPA.delete(userPermissionRepositoryAsHashmap);
    }

    public UserPermissionRepositoryAsHashmap getUserPermissionRepositoryAsHashmap() {
        return userPermissionRepJPA.findById(1).orElse(null);
    }
}
