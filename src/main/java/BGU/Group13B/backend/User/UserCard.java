package BGU.Group13B.backend.User;

import BGU.Group13B.backend.Pair;

import java.util.List;

public record UserCard(int userId, String userName, String email, String userPermissions) {
    public UserCard(int userId, String userName, String email, String userPermissions){
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.userPermissions = userPermissions;
    }
}
