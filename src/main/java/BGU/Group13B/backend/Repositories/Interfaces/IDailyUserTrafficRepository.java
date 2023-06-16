package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.System.UserTrafficRecord;

import java.time.LocalDate;

public interface IDailyUserTrafficRepository {
    void addGuest();
    void addRegularMember();
    void addStoreManagerThatIsNotOwner();
    void addStoreOwner();
    void addAdmin();
    UserTrafficRecord getUserTrafficOfRage(LocalDate start, LocalDate end);
    void reset();
}
