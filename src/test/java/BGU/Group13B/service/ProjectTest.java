package BGU.Group13B.service;

import BGU.Group13B.backend.System.SystemInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.data.util.Pair;

import java.util.HashMap;
import java.util.List;

public abstract class ProjectTest {
    private final List<Pair<String, String>> users = List.of(
            Pair.of("adminUser", "Qwerty12345"),
            Pair.of("storeOwnerUser1", "KingBIBI69420"),
            Pair.of("storeOwnerUser2", "KingBIBI69420"),
            Pair.of("guestUser", "SuperSecuredPassword123"),
            Pair.of("badUser", "BadPas"));
    protected final int[] userIds = new int[users.size()];
    protected ISession session; //TODO: change to private ask shaun
    protected final String[] products = {"PC", "Laptop", "Phone", "Tablet", "TV", "Headphones",
            "Mouse", "Keyboard", "Printer", "Monitor"};
    protected final String[] categories = {"Electronics", "Computers", "Phones", "Tablets", "TVs", "Audio", "Peripherals"};

    protected final Object[][] stores = {
            {0, "store1", "category1"},
            {0, "store2", "category2"}
    };


    protected enum UsersIndex {
        ADMIN_CREATOR_MASTER_ID, ADMIN, STORE_OWNER_1, STORE_OWNER_2, GUEST, BAD
        // STORE_MANAGER_1, STORE_MANAGER_2

    }

    // README:
    // Example: int adminId = userIds[UsersIndex.ADMIN.ordinal()]
    private final String[] userEmails = {
            "adminUser@gmail.com", "storeOwner1@gmail.com",
            "storeOwner2@gmail.com",
            "guestUser@gmail.com", "bad@gmail.scam.com"};
    protected final int ADMIN_CREATOR_MASTER_ID = 1;
    protected final int[] storeIds = new int[stores.length];

    @BeforeEach
    public void setUp() {
        this.session = Driver.getSession();
        setUpUsers();
        setUpAdmin();
        setUpStores();
        setUpStoresManagers();
        setUpProducts();
    }
    @AfterEach
    public void teardown() {
        SingletonCollection.reset_system();
    }

    private void setUpStoresManagers() {
        //TODO: add stores managers
        //TODO: store manager 1 to store 1
        //TODO: store manager 2 to store 2
    }

    private void setUpUsers() {
        for (int i = 0; i < users.size(); i++) {
            userIds[i] = session.enterAsGuest();
        }
        int i = 0;
        for (var user : users) {
                session.register(userIds[i], user.getFirst(), user.getSecond(), userEmails[i],
                        "BLACK LIVES MATTER " + i, "because i never go back " + i, "YEAH " + i);
            i++;
        }
        for (int j = 0; j < users.size(); j++) {
            if (i != UsersIndex.BAD.ordinal())
                session.login(userIds[j], users.get(j).getFirst(), users.get(j).getSecond(),
                        "BLACK LIVES MATTER " + i, "because i never go back " + i, "YEAH " + i);
        }
    }

    public void setUpAdmin() {
        session.setUserStatus(ADMIN_CREATOR_MASTER_ID, userIds[UsersIndex.ADMIN.ordinal()], 1);
    }

    protected void addProduct(int userId, int storeId, String productName, String category, double price, int stockQuantity, String description) {
        session.addProduct(userId, storeId, productName, category, price, stockQuantity, description);
    }

    protected void purchaseProductCart(int userId, String address, String creditCardNumber, String creditCardMonth,
                                       String creditCardYear, String creditCardHolderFirstName,
                                       String creditCardHolderLastName, String creditCardCcv,
                                       String id, String creditCardType,
                                       HashMap<Integer/*productId*/, String/*productDiscountCode*/> productsCoupons,
                                       String/*store coupons*/ storeCoupon) {
        session.purchaseProductCart(userId, address, creditCardNumber,
                creditCardMonth, creditCardYear,
                creditCardHolderFirstName, creditCardHolderLastName,
                creditCardCcv, id, creditCardType,
                productsCoupons, storeCoupon);
    }

    protected void purchaseProposalSubmit(int userId, int storeId, int productId, double proposedPrice, int amount) {
        session.purchaseProposalSubmit(userId, storeId, productId, proposedPrice, amount);
    }

    protected void immediatePurchase(int userId, int storeId, int productId, int quantity) {
        session.immediatePurchase(userId, storeId, productId, quantity);
    }

    protected void createLotteryPurchaseForProduct(int storeManagerId, int storeId, int productId) {
        session.createLotteryPurchaseForProduct(storeManagerId, storeId, productId);
    }

    protected void participateInLotteryPurchase(int userId, int storeId, int productId, double fraction) {
        session.participateInLotteryPurchase(userId, storeId, productId, fraction);
    }

    protected void auctionPurchase(int userId, int storeId, int productId, double price) {
        session.auctionPurchase(userId, storeId, productId, price);
    }

    protected SystemInfo getSystemInformation(int adminId) {
        return session.getSystemInformation(adminId);
    }

    private void setUpProducts() {

    }

    private void setUpStores() {
        stores[0][0] = userIds[UsersIndex.STORE_OWNER_1.ordinal()];
        stores[1][0] = userIds[UsersIndex.STORE_OWNER_2.ordinal()];
        for (int i = 0; i < stores.length; i++) {
            storeIds[i] = session.addStore((int) stores[i][0], (String) stores[i][1], (String) stores[i][2]);
        }

    }


}
