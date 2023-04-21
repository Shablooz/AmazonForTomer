package BGU.Group13B.service;

import BGU.Group13B.backend.System.SystemInfo;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;

public abstract class ProjectTest {
    private ISession session;
    protected final String[] products = {"PC", "Laptop", "Phone", "Tablet", "TV", "Headphones", "Mouse", "Keyboard", "Printer", "Monitor"};
    protected final String[] stores = {"Apple", "Samsung", "LG", "Sony", "Microsoft", "Dell", "HP", "Lenovo", "Asus", "Acer"};
    protected final String[] users = {"admin", "storeOwner", "guest"};

    public void setUp() {
        this.session = Driver.getSession();
        setUpProducts();
        setUpStores();
        setUpGuestsUsers();
        setUpStoreOwnersUsers();
        setUpAdminsUsers();
        setUpProducts();
    }

    protected void addProduct(int userId, int storeId, String productName, String category, double price, int stockQuantity){
        session.addProduct(userId, storeId, productName, category, price, stockQuantity);
    }

    protected void purchaseProductCart(int userId, String address, String creditCardNumber, String creditCardMonth, String creditCardYear, String creditCardHolderFirstName, String creditCardHolderLastName, String creditCardCcv, String id, String creditCardType) {
        session.purchaseProductCart(userId, address, creditCardNumber, creditCardMonth, creditCardYear, creditCardHolderFirstName, creditCardHolderLastName, creditCardCcv, id, creditCardType);
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

    private void setUpStoreOwnersUsers() {

    }

    private void setUpGuestsUsers() {

    }

    private void setUpAdminsUsers() {

    }

    private void setUpStores() {

    }


}
