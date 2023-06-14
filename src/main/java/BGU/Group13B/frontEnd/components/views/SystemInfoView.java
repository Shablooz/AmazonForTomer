package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.backend.System.UserTrafficRecord;
import BGU.Group13B.frontEnd.ResponseHandler;
import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.frontEnd.components.SystemInfo.UserTrafficPieChart;
import BGU.Group13B.frontEnd.components.store.IncomeChart;
import BGU.Group13B.service.BroadCaster;
import BGU.Group13B.service.Session;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

@PageTitle("System Info")
@Route(value = "systeminfo", layout = MainLayout.class)
public class SystemInfoView extends VerticalLayout implements BeforeEnterObserver, IncomeChartView {

    private int userId;
    private final Session session;

    //components
    private UserTrafficPieChart userTrafficPieChart;
    private IncomeChart systemIncomeChart;
    private final H1 title = new H1("System Info");

    @Autowired
    public SystemInfoView(Session session){
        this.session = session;
    }

    private void start(){
        removeAll();
        this.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        LocalDate today = LocalDate.now();
        UserTrafficRecord userTraffic = handleResponse(session.getUserTrafficOfRange(userId, today, today));
        if(userTraffic == null)
            return;

        double[] systemIncome = handleResponse(session.getSystemHistoryIncome(userId, today, today));
        if(systemIncome == null)
            return;


        userTrafficPieChart = new UserTrafficPieChart(this, userTraffic, today, today);
        systemIncomeChart = new IncomeChart(this, systemIncome, today, today);

        add(title, userTrafficPieChart, systemIncomeChart);

        //real-time update
        var ui = UI.getCurrent();
        BroadCaster.registerUserTraffic(userId, () -> ui.access(this::refreshUserTrafficChart));
        BroadCaster.registerIncome(userId, () -> ui.access(this::refreshChart));

    }

    public void setUserTrafficChartValues(LocalDate start, LocalDate end){
        UserTrafficRecord userTraffic = handleResponse(session.getUserTrafficOfRange(userId, start, end));
        if(userTraffic == null)
            return;

        userTrafficPieChart.setData(userTraffic);
    }

    public void refreshUserTrafficChart(){
        getUI().ifPresent(ui -> {
            UserTrafficRecord userTraffic = handleResponse(session.getUserTrafficOfRange(userId, userTrafficPieChart.getStartDate(), userTrafficPieChart.getEndDate()));
            if(userTraffic == null){
                notifyWarning("Failed to refresh user traffic chart");
                return;
            }
            userTrafficPieChart.setData(userTraffic);
        });

    }

    @Override
    public void setChartValues(LocalDate start, LocalDate end) {
        double[] systemIncome = handleResponse(session.getSystemHistoryIncome(userId, start, end));
        if(systemIncome == null)
            return;

        systemIncomeChart.setData(systemIncome, start);
    }

    @Override
    public void refreshChart() {
        getUI().ifPresent(ui -> {

            LocalDate start = systemIncomeChart.getStartDate();
            LocalDate end = systemIncomeChart.getEndDate();

            double[] systemIncome = handleResponse(session.getSystemHistoryIncome(userId, start, end));
            if(systemIncome == null){
                notifyWarning("Failed to refresh income chart");
                return;
            }
            systemIncomeChart.setData(systemIncome, start);
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        userId = SessionToIdMapper.getInstance().getCurrentSessionId();

        var isAdmin = handleResponse(session.isAdmin(userId));

        if(isAdmin == null || !isAdmin){
            beforeEnterEvent.rerouteTo(HomeView.class);
            return;
        }
        start();
    }
}
