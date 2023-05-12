package BGU.Group13B.service;

import BGU.Group13B.backend.Pair;
import BGU.Group13B.backend.System.SystemInfo;
import BGU.Group13B.service.info.ProductInfo;
import BGU.Group13B.service.info.StoreInfo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

public abstract class ProjectTest {

    private final List<Pair<String, String>> users = List.of(
            Pair.of("adminUser", "Qwerty12345"),
            Pair.of("storeOwnerUser1", "KingBIBI69420"),
            Pair.of("storeOwnerUser2", "KingBIBI69420"),
            //Pair.of("guestUser", "SuperSecuredPassword123"),
            Pair.of("badUser", "BadPas"));
    protected final int[] userIds = new int[users.size()];
    protected ISession session; //TODO: change to private ask shaun

/*    protected final Object[][] products = {
            {0*//*userId*//*, 0*//*storeId*//*, "product1", "The best category1", 100.0, 10, "Least beast in the east description1"},
            {0*//*userId*//*, 0*//*storeId*//*, "product2", "Mediocre category2", 200.0, 1, "Best in the west description2"}
    };*/

    protected final String[] categories = {"Electronics", "Computers", "Phones", "Tablets", "TVs", "Audio", "Peripherals"};

    enum ProductIndexes {
        PRODUCT_ID, STORE_ID, STORE_FOUNDER_ID, NAME, CATEGORY, PRICE, QUANTITY, DESCRIPTION
    }


    //String productName, String category, double price, int stockQuantity, String description
    protected final Object[][] products = {
            {-1 /*productId*/, -1 /*storeId*/, -1 /*store founderId*/, "PC" /*name*/, categories[0], 100.0 /*price*/, 10 /*stock quantity*/, "PC description"},
            {-1 , -1, -1, "Laptop", categories[1], 100.0, 10, "Laptop description"},
            {-1 , -1, -1, "Phone", categories[2], 100.0, 10, "Phone description"},
            {-1 , -1, -1, "Tablet", categories[3], 100.0, 10, "Tablet description"},
            {-1 , -1, -1, "TV", categories[4], 100.0, 10, "TV description"},
            {-1 , -1, -1, "Headphones", categories[5], 100.0, 10, "Headphones description"},
            {-1 , -1, -1, "Mouse", categories[6], 100.0, 10, "Mouse description"},
            {-1 , -1, -1, "Keyboard", categories[6], 100.0, 10, "Keyboard description"},
            {-1 , -1, -1, "Printer", categories[6], 100.0, 10, "Printer description"},
            {-1 , -1, -1, "Monitor", categories[6], 100.0, 10, "Monitor description"}
    };

    protected final Object[][] stores = {
            {-1 /*storeId*/, -1 /*founderId*/, "store1", "category1"},
            {-1, -1, "store2", "category2"}
    };

    protected final int[] storeIds = new int[stores.length];

    protected enum UsersIndex {
        ADMIN, STORE_OWNER_1, STORE_OWNER_2, GUEST, BAD
        // STORE_MANAGER_1, STORE_MANAGER_2

    }

    protected enum StoresIndex {
        STORE_1, STORE_2
    }

    protected enum ProductsIndex {
        PRODUCT_1, PRODUCT_2
    }

    // README:
    // Example: int adminId = userIds[UsersIndex.ADMIN.ordinal()]
    private final String[] userEmails = {
            "adminUser@gmail.com", "storeOwner1@gmail.com",
            "storeOwner2@gmail.com", "bad@gmail.scam.com"};
    protected final int ADMIN_CREATOR_MASTER_ID = 1;

    /*the products belong to STORE_OWNER_1*/
    protected final int[] productIds = new int[products.length];


    @BeforeEach
    public void setUp() {
        SingletonCollection.reset_system();
        this.session = SingletonCollection.getSession();
        setUpUsers();
        setUpAdmin();
        setUpStores();
        setUpStoresManagers();
        setUpProducts();
    }

    @AfterAll
    public static void teardown() {
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
            if (!user.getFirst().equals("guestUser") && !user.getFirst().equals("badUser"))
                session.register(userIds[i], user.getFirst(), user.getSecond(), userEmails[i],
                        "BLACK LIVES MATTER " + i, "because i never go back " + i, "YEAH " + i);
            i++;
        }
        for (int j = 0; j < users.size(); j++) {
            if (j != UsersIndex.BAD.ordinal() && j != UsersIndex.GUEST.ordinal())
                session.login(userIds[j], users.get(j).getFirst(), users.get(j).getSecond(),
                        "BLACK LIVES MATTER " + j, "because i never go back " + j, "YEAH " + j);
        }
    }

    public void setUpAdmin() {
        session.setUserStatus(ADMIN_CREATOR_MASTER_ID, userIds[UsersIndex.ADMIN.ordinal()], 1);
    }

    protected int addProduct(int userId, int storeId, String productName, String category, double price, int stockQuantity, String description) {
        Response<Integer> addProductResponse = session.addProduct(userId, storeId, productName, category, price, stockQuantity, description);
        if(addProductResponse.didntSucceed()) {
            fail();
        }
        return addProductResponse.getData();
    }

    protected int addStore(int userId, String storeName, String category) {
        Response<Integer> addStoreResponse = session.addStore(userId, storeName, category);
        if(addStoreResponse.didntSucceed()) {
            fail();
        }
        return addStoreResponse.getData();
    }

    protected StoreInfo getStoreInfo(int userId, int storeId) {
        Response<StoreInfo> storeInfoResponse = session.getStoreInfo(userId, storeId);
        if(storeInfoResponse.didntSucceed()) {
            fail();
        }
        return storeInfoResponse.getData();
    }

    protected ProductInfo getStoreProductInfo(int userId, int storeId, int productId) {
        Response<ProductInfo> productInfoResponse = session.getStoreProductInfo(userId, storeId, productId);
        if(productInfoResponse.didntSucceed()) {
            fail();
        }
        return productInfoResponse.getData();
    }

    protected void removeProduct(int userId, int storeId, int productId) {
        Response<VoidResponse> productInfoResponse = session.removeProduct(userId, storeId, productId);
        if(productInfoResponse.didntSucceed()) {
            fail();
        }
    }

    protected void setProductStockQuantity(int userId, int storeId, int productId, int quantity) {
        Response<VoidResponse> productInfoResponse = session.setProductStockQuantity(userId, storeId, productId, quantity);
        if(productInfoResponse.didntSucceed()) {
            fail();
        }
    }

    protected void setProductPrice(int userId, int storeId, int productId, double price) {
        Response<VoidResponse> productInfoResponse = session.setProductPrice(userId, storeId, productId, price);
        if(productInfoResponse.didntSucceed()) {
            fail();
        }
    }

    protected void setProductName(int userId, int storeId, int productId, String name) {
        Response<VoidResponse> productInfoResponse = session.setProductName(userId, storeId, productId, name);
        if(productInfoResponse.didntSucceed()) {
            fail();
        }
    }

    protected void setProductDescription(int userId, int storeId, int productId, String description) {
        Response<VoidResponse> productInfoResponse = session.setProductDescription(userId, storeId, productId, description);
        if(productInfoResponse.didntSucceed()) {
            fail();
        }
    }

    protected void setProductCategory(int userId, int storeId, int productId, String category) {
        Response<VoidResponse> productInfoResponse = session.setProductCategory(userId, storeId, productId, category);
        if(productInfoResponse.didntSucceed()) {
            fail();
        }
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
        for(int i = 0; i < products.length; i++) {
            products[i][1] = stores[i % stores.length][0]; //storeId
            products[i][2] = stores[i % stores.length][1]; //founder
            products[i][0] = addProduct((int) stores[i % stores.length][1],
                    (int) products[i][1], (String) products[i][3], (String) products[i][4],
                    (double) products[i][5], (int) products[i][6], (String) products[i][7]);
        }
    }

    private void setUpStores() {
        for (int i = 0; i < stores.length; i++) {
            stores[i][1] = userIds[UsersIndex.STORE_OWNER_1.ordinal() + i];
            stores[i][0] = addStore(userIds[UsersIndex.STORE_OWNER_1.ordinal() + i], (String) stores[i][2], (String) stores[i][3]);
        }

    }


}
