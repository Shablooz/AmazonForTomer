package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.service.Session;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("My Stores")
@Route(value = "store", layout = MainLayout.class)
public class StoreView extends VerticalLayout implements HasUrlParameter<Integer> {

    private final int userId = SessionToIdMapper.getInstance().getCurrentSessionId();
    private int storeId = -1;


    @Autowired
    public StoreView(Session session) {
        super();
    }


    @Override
    public void setParameter(BeforeEvent beforeEvent, Integer storeIdParam) {
        this.storeId = storeIdParam;
    }
}
