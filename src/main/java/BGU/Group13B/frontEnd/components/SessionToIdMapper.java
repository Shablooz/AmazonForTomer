package BGU.Group13B.frontEnd.components;

import BGU.Group13B.frontEnd.components.appnav.AppNav;
import BGU.Group13B.frontEnd.components.appnav.AppNavItem;
import BGU.Group13B.service.Session;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;


public class SessionToIdMapper {

    private static SessionToIdMapper instance;
    private ConcurrentHashMap<String,Integer> sessionToId;
    private SessionToIdMapper() {
        // Private constructor to prevent instantiation from outside
        this.sessionToId = new ConcurrentHashMap<>();
    }

    public synchronized static SessionToIdMapper getInstance() {
            if (instance == null) {
                instance = new SessionToIdMapper();
            }
        return instance;
    }

    public void add(String session, int id) {
        this.sessionToId.put(session,id);
    }
    public int get(String session) {
        return this.sessionToId.get(session);
    }
}




