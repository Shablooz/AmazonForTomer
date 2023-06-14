package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.backend.System.UserTrafficRecord;
import BGU.Group13B.frontEnd.ResponseHandler;
import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.frontEnd.components.SystemInfo.UserTrafficPieChart;
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
public class SystemInfoView extends VerticalLayout implements ResponseHandler, BeforeEnterObserver {

    private int userId;
    private final Session session;

    //components
    private UserTrafficPieChart userTrafficPieChart;
    private final H1 title = new H1("System Info");

    private Registration registration;


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

        userTrafficPieChart = new UserTrafficPieChart(this, userTraffic, today, today);

        add(title, userTrafficPieChart);

        //real-time update
        var ui = UI.getCurrent();
        registration = BroadCaster.registerUserTraffic(userId, () -> ui.access(this::refreshUserTrafficChart));
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
