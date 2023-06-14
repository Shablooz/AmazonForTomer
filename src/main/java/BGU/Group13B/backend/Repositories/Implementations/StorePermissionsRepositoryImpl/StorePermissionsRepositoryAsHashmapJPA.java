package BGU.Group13B.backend.Repositories.Implementations.StorePermissionsRepositoryImpl;

import BGU.Group13B.backend.storePackage.permissions.StorePermission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorePermissionsRepositoryAsHashmapJPA extends JpaRepository<StorePermissionsRepositoryAsHashmap, Integer> {
}
