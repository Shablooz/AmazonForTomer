package BGU.Group13B.service;

public abstract class ProjectTest {
    private ISession session;
    protected final String[] products = {"PC", "Laptop", "Phone", "Tablet", "TV", "Headphones", "Mouse", "Keyboard", "Printer", "Monitor"};
    protected final String[] stores = {"Apple", "Samsung", "LG", "Sony", "Microsoft", "Dell", "HP", "Lenovo", "Asus", "Acer"};
    protected final String[] users = {"admin", "storeOwner", "guest"};

    public void setUp() {
        this.session = Driver.getSession();
    }
    protected void addProduct(int userId, String productName, int quantity, double price, int storeId){
        session.addProduct(userId, productName, quantity, price, storeId);
    }
    private void setUpProducts(){

    }
    private void setUpStoreOwnersUsers(){

    }
    private void setUpGuestsUsers(){

    }
    private void setUpAdminsUsers(){

    }
    private void setUpStores(){

    }



}
