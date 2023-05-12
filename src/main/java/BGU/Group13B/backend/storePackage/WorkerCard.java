package BGU.Group13B.backend.storePackage;

import BGU.Group13B.backend.User.UserPermissions;

import java.util.List;

public record WorkerCard (int userId, UserPermissions.StoreRole storeRole, List<String> userPermissions){

    public WorkerCard(int userId, UserPermissions.StoreRole storeRole, List<String> userPermissions){
        this.userId = userId;
        this.storeRole = storeRole;
        this.userPermissions = userPermissions;
    }
}
