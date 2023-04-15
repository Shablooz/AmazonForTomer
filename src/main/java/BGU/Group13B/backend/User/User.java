package BGU.Group13B.backend.User;

import BGU.Group13B.backend.Repositories.Interfaces.ICartRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IMessageRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IPurchaseHistoryRepository;
import BGU.Group13B.backend.storePackage.Market;
import jdk.jshell.spi.ExecutionControl;
import java.io.NotActiveException;
//eyal import
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;

public class User {
    private final IPurchaseHistoryRepository purchaseHistoryRepository;
    private final ICartRepository cartRepository;
    private final IMessageRepository messageRepository;
    private final UserPermissions userPermissions;
    private final Market market;

    //eyal addition
    private boolean isLoggedIn;


    public User(IPurchaseHistoryRepository purchaseHistoryRepository, ICartRepository cartRepository, IMessageRepository messageRepository, UserPermissions userPermissions, Market market) {
        this.purchaseHistoryRepository = purchaseHistoryRepository;
        this.cartRepository = cartRepository;
        this.messageRepository = messageRepository;
        this.userPermissions = userPermissions;
        this.market = market;
    }


    //#15
    //returns User on success (for future functionalities)
    public User register(String userName, String password, String email) {
        String usernameRegex = "^[a-zA-Z0-9_-]{4,16}$"; // 4-16 characters, letters/numbers/underscore/hyphen
        String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)[A-Za-z\\d]{8,}$"; // need at least 8 characters, 1 uppercase, 1 lowercase, 1 number)
        String emailRegex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\\\.[A-Za-z0-9-]+)*(\\\\.[A-Za-z]{2,})$"; // checks email validation
        if (!Pattern.matches(usernameRegex, userName)) {
            throw new IllegalArgumentException("Invalid username. Username must be 4-16 characters long and can only contain letters, numbers, underscores, or hyphens.");
        }
        if (!Pattern.matches(passwordRegex, password)) {
            throw new IllegalArgumentException("Invalid password. at least 8 characters, 1 uppercase, 1 lowercase, 1 number");
        }
        if (!Pattern.matches(emailRegex, email)) {
            throw new IllegalArgumentException("Invalid email.");
        }
        this.userPermissions.register();
        return this;
    }


}




