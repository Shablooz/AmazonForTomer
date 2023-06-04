package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.backend.User.PurchaseHistory;
import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.service.Response;
import BGU.Group13B.service.Session;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@PageTitle("purchaseHistory")
@Route(value = "purchaseHistory", layout = MainLayout.class)
public class PurchaseHistoryView extends VerticalLayout implements HasUrlParameter<String> {

    private Session session;
    private int storeId;
    private final int userId = SessionToIdMapper.getInstance().getCurrentSessionId();

    @Autowired
    public PurchaseHistoryView(Session session) {
        this.session = session;
        //this.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        //setAlignItems(Alignment.CENTER);
        this.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        this.setAlignItems(FlexComponent.Alignment.CENTER);
    }

    public void start(){
        if(storeId!=-1) {
            storeCase();
        }
        else {
            userCase();
        }
    }

    public void userCase(){
        add(new H1("My purchase history"));
        Response<List<PurchaseHistory>> purchaseHistory = session.getUserPurchaseHistory(userId);
        buildUserGrid(purchaseHistory.getData());
    }

    public void storeCase(){
        add(new H1("Store purchase history"));
        Response<List<PurchaseHistory>> purchaseHistory = session.getStorePurchaseHistory(userId,storeId);
        buildStoreGrid(purchaseHistory.getData());
    }
    public void buildUserGrid(List<PurchaseHistory> purchaseHistories){
        Grid<PurchaseHistory> historyGrid = new Grid<>();
        setAlignItems(Alignment.CENTER);
        VerticalLayout verticalLayout = new VerticalLayout();
       this.addClassName("centered-grid");
        historyGrid.setItems(purchaseHistories); // products is a List<Product> containing the products to be displayed
        historyGrid.addColumn(PurchaseHistory::getDate).setHeader("Date");
        historyGrid.addColumn(PurchaseHistory::getStoreName).setHeader("Store Name");
        historyGrid.addColumn(PurchaseHistory::getPrice).setHeader("Price");
        historyGrid.addColumn(PurchaseHistory::productsToString).setHeader("Products description");
        historyGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        historyGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        verticalLayout.add(historyGrid);
        verticalLayout.setWidth("80%");
        add(verticalLayout);
    }

    public void buildStoreGrid(List<PurchaseHistory> purchaseHistories){
        Grid<PurchaseHistory> historyGrid = new Grid<>();
        setAlignItems(Alignment.CENTER);
        VerticalLayout verticalLayout = new VerticalLayout();
        this.addClassName("centered-grid");
        historyGrid.setItems(purchaseHistories); // products is a List<Product> containing the products to be displayed
        historyGrid.addColumn(PurchaseHistory::getDate).setHeader("Date");
        historyGrid.addColumn(PurchaseHistory::getUserId).setHeader("User ID");
        historyGrid.addColumn(PurchaseHistory::getPrice).setHeader("Price");
        historyGrid.addColumn(PurchaseHistory::productsToString).setHeader("Products description");
        historyGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        historyGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        verticalLayout.add(historyGrid);
        verticalLayout.setWidth("80%");
        add(verticalLayout);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String param) {
        if (param != null && !param.isEmpty()) {
            storeId = Integer.parseInt(param);
        } else {
            storeId = -1;
        }
        start();
    }

}
