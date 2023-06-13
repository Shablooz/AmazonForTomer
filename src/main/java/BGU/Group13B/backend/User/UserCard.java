package BGU.Group13B.backend.User;


public record UserCard(int userId, String userName, String email, String userPermissions) {
    public UserCard(int userId, String userName, String email, String userPermissions){
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.userPermissions = userPermissions;
    }
}
