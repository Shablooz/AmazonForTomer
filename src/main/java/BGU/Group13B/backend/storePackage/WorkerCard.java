package BGU.Group13B.backend.storePackage;

import BGU.Group13B.backend.User.UserPermissions;

import java.util.Set;

public record WorkerCard (int userId, UserPermissions.StoreRole storeRole, Set<UserPermissions.IndividualPermission> userPermissions){
}
