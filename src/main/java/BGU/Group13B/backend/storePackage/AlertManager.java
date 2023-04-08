package BGU.Group13B.backend.storePackage;

import BGU.Group13B.backend.Repositories.Interfaces.IUserRepository;

/*
* If I want to send a message from the Store to the user.
* Real time response is needed
* */
public class AlertManager {
    private final IUserRepository userRepository;

    public AlertManager(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void sendAlert(Integer id, String msg) {
        //stub for tests
    }
}
