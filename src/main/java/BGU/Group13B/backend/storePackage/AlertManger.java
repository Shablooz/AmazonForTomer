package BGU.Group13B.backend.storePackage;

import BGU.Group13B.backend.Repositories.Interfaces.IUserRepository;

/*
* If I want to send a message from the Store to the user.
* Real time response is needed
* */
public class AlertManger {
    private final IUserRepository userRepository;

    public AlertManger(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
