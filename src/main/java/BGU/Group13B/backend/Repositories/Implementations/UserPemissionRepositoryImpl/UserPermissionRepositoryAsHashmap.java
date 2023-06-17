package BGU.Group13B.backend.Repositories.Implementations.UserPemissionRepositoryImpl;

import BGU.Group13B.backend.Repositories.Implementations.UserRepositoryImpl.UserRepoService;
import BGU.Group13B.backend.Repositories.Interfaces.IUserPermissionRepository;
import BGU.Group13B.backend.User.UserPermissions;
import BGU.Group13B.backend.storePackage.permissions.ChangePermissionException;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
import BGU.Group13B.backend.storePackage.permissions.StorePermission;
import BGU.Group13B.service.SingletonCollection;
import jakarta.persistence.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Entity
public class UserPermissionRepositoryAsHashmap implements IUserPermissionRepository {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    @JoinTable(name = "UserPermissionRepositoryAsHashmap_UserPermissions",
            joinColumns = {@JoinColumn(name = "UserPermissionRepositoryAsHashmap_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "UserPermissions_id", referencedColumnName = "userId")})
    @MapKeyJoinColumn(name = "userId")
    private Map<Integer, UserPermissions> integerUserPermissionConcurrentHashMap;

    @Transient
    private boolean saveMode;

    public UserPermissionRepositoryAsHashmap(){
        this.integerUserPermissionConcurrentHashMap = new ConcurrentHashMap<>();
        this.saveMode = true;
    }
    public UserPermissionRepositoryAsHashmap(boolean saveMode){
        this.integerUserPermissionConcurrentHashMap = new ConcurrentHashMap<>();
        this.saveMode = saveMode;
    }

    @Override
    public UserPermissions getUserPermission(int userId){
        return integerUserPermissionConcurrentHashMap.get(userId);

    }

    @Override
    public void addUserPermission(int userId, UserPermissions userPermissions){
        integerUserPermissionConcurrentHashMap.put(userId, userPermissions);
        save();
    }

    public void setSaveMode(boolean saveMode){
        this.saveMode = saveMode;
    }

    //getters and setters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<Integer, UserPermissions> getIntegerUserPermissionConcurrentHashMap() {
        return integerUserPermissionConcurrentHashMap;
    }

    public boolean isSaveMode() {
        return saveMode;
    }

    public void setIntegerUserPermissionConcurrentHashMap(Map<Integer, UserPermissions> integerUserPermissionConcurrentHashMap) {
        this.integerUserPermissionConcurrentHashMap = integerUserPermissionConcurrentHashMap;
    }

    public void save(){
        if(saveMode)
            SingletonCollection.getContext().getBean(UserPermissionRepService.class).save(this);
    }

    @Override
    public boolean isUserPermissionsExists(int userId){
        if(!integerUserPermissionConcurrentHashMap.containsKey(userId))
            return false;
        return integerUserPermissionConcurrentHashMap.get(userId).isUserPermissionsExists();
    }

    @Override
    public void deletePermissions(int adminId, int userId) throws NoPermissionException, ChangePermissionException {
        integerUserPermissionConcurrentHashMap.get(userId).deletePermissions(adminId, userId);
        integerUserPermissionConcurrentHashMap.remove(userId);
    }

}
