package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.backend.User.PurchaseHistory;
import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.service.Response;
import BGU.Group13B.service.Session;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@PageTitle("userHistory")
@Route(value = "userHistory", layout = MainLayout.class)
public class PurchaseHistoryView extends VerticalLayout implements HasUrlParameter<String> {

    private Session session;
    private int storeId;
    private final int userId = SessionToIdMapper.getInstance().getCurrentSessionId();

    @Autowired
    public PurchaseHistoryView(Session session) {
        this.session = session;
    }

    public void start(){
        if(storeId!=-1) {
           storeCase();
        }
        else {
          useCase();
        }
    }

    public void useCase(){
        add(new H1("My purchase history"));
        setAlignItems(Alignment.CENTER);
        Response<List<PurchaseHistory>> purchaseHistory = session.getUserPurchaseHistory(userId);
        buildGrid(purchaseHistory.getData());
    }

    public void storeCase(){
        add(new H1("Store purchase history"));
        setAlignItems(Alignment.CENTER);
        Response<List<PurchaseHistory>> purchaseHistory = session.getStorePurchaseHistory(userId,storeId);
        buildGrid(purchaseHistory.getData());
    }
    public void buildGrid(List<PurchaseHistory> purchaseHistories){
        Grid<PurchaseHistory> historyGrid = new Grid<>();
        historyGrid.setItems(purchaseHistories); // products is a List<Product> containing the products to be displayed
        historyGrid.addColumn(PurchaseHistory::getStoreName).setHeader("Store Name");
        historyGrid.addColumn(PurchaseHistory::getPrice).setHeader("Price");
        historyGrid.addColumn(PurchaseHistory::productsToString).setHeader("Products description");
        add(historyGrid);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String param) {
        if(param!="")
            storeId = Integer.parseInt(param);
        else
            storeId=-1;
        start();
    }
}
