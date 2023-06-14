package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.backend.User.UserPermissions;
import BGU.Group13B.backend.storePackage.WorkerCard;
import BGU.Group13B.frontEnd.ResponseHandler;
import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.frontEnd.components.store.IncomeChart;
import BGU.Group13B.service.BroadCaster;
import BGU.Group13B.service.Session;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.shared.Registration;

import java.time.LocalDate;
import java.util.List;

@PageTitle("Store Income")
@Route(value = "storeIncome", layout = MainLayout.class)
public class StoreIncomeView extends VerticalLayout implements HasUrlParameter<Integer>, BeforeEnterObserver, IncomeChartView {

    private final Session session;
    private final int userId = SessionToIdMapper.getInstance().getCurrentSessionId();
    private int storeId;

    private H1 title = new H1("Store Income");
    private IncomeChart storeIncomePieChart;
    private Button backToStore;

    private Registration registration;

    public StoreIncomeView(Session session) {
        this.session = session;
    }

    private void start(){
        removeAll();
        this.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        this.setSizeFull();

        LocalDate today = LocalDate.now();
        double[] incomeHistory = handleResponse(session.getStoreHistoryIncome(storeId, userId, today, today));
        if(incomeHistory == null)
            return;

        storeIncomePieChart = new IncomeChart(this, incomeHistory, today, today);
        add(title, storeIncomePieChart);

        Div spacer = new Div();
        add(spacer);
        setFlexGrow(1, spacer);

        backToStore = new Button("Back to Store", e -> navigate("store/" + storeId));
        backToStore.getStyle().set("margin-top", "auto");
        backToStore.getStyle().set("margin-right", "auto");

        add(backToStore);

        //real-time update
        var ui = UI.getCurrent();
        registration = BroadCaster.registerIncome(userId, () -> ui.access(this::refreshChart));
    }

    @Override
    public void setChartValues(LocalDate start, LocalDate end) {
        if(!hasAccess()){
            registration.remove();
            notifyWarning("You don't have access to this store's income");
            navigate("store/" + storeId);
            return;
        }

        double[] incomeHistory = handleResponse(session.getStoreHistoryIncome(storeId, userId, start, end));
        if(incomeHistory == null)
            return;

        storeIncomePieChart.setData(incomeHistory, start);
    }

    @Override
    public void refreshChart() {
        if(!hasAccess()){
            registration.remove();
            getUI().ifPresent(ui -> {
                notifyWarning("You don't have access to this store's income");
                ui.navigate("store/" + storeId);
            });
            return;
        }

        LocalDate start = storeIncomePieChart.getStartDate();
        LocalDate end = storeIncomePieChart.getEndDate();

        double[] incomeHistory = handleResponse(session.getStoreHistoryIncome(storeId, userId, start, end));
        if(incomeHistory == null){
            notifyWarning("Failed to refresh store income chart");
            return;
        }
        storeIncomePieChart.setData(incomeHistory, start);
    }

    private boolean hasAccess(){
        List<WorkerCard> res = handleResponse(session.getStoreWorkersInfo(1 /*mafhhhiiddd*/, storeId));
        if(res == null){
            return false;
        }
        return res.stream().anyMatch(wc -> wc.userId() == userId && wc.userPermissions().contains(UserPermissions.IndividualPermission.STATS));
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Integer storeIdParam) {
        if(storeIdParam == null){
            beforeEvent.rerouteTo("");
            return;
        }

        this.storeId = storeIdParam;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if(!hasAccess()){
            notifyWarning("You don't have permission to view this store's income history");
            beforeEnterEvent.rerouteTo("store/" + storeId);
        }
        start();
    }


}
