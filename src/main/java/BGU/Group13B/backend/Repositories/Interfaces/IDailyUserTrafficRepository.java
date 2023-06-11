package BGU.Group13B.backend.Repositories.Interfaces;

public interface IDailyUserTrafficRepository {
    void addGuest();
    void addRegularMember();
    void addStoreManagerThatIsNotOwner();
    void addStoreOwner();
    void addAdmin();

    void reset();
}
