package BGU.Group13B.backend.storePackage;

import BGU.Group13B.backend.Repositories.Interfaces.IUserRepository;

public class AlertManger {
    private final IUserRepository userRepository;

    public AlertManger(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
