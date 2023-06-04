package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.service.Session;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Admin Page")
@Route(value = "admin", layout = MainLayout.class)
public class AdminView extends VerticalLayout implements BeforeEnterObserver, AfterNavigationObserver {
    private final Session session;

    @Autowired
    public AdminView(Session session) {
        this.session = session;
        // You can initialise any data required for the connected UI components here.
        //joining as a guest
        //congratulations on logging in
        add(new H1("Admin Control Panel"));
        //center the text
        setAlignItems(Alignment.CENTER);
        String sourceLink = "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Fizz_0.jpg";

        /*CSS code*/
        getStyle().set("background-image", "url(" + sourceLink + ")");
        getStyle().set("background-size", "cover");
        getStyle().set("background-position", "center top");
        getStyle().set("background-repeat", "no-repeat");
        getStyle().set("background-attachment", "fixed");
        getStyle().set("height", "100vh");
        getStyle().set("width", "100vw");

       /* MenuBar menuBar = buildMenuBar();
        add(menuBar);*/
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {


    }

    private void setPosition(MenuBar component) {
        component.getElement().getStyle().set("position", "absolute");
        component.getElement().getStyle().set("top", "0");
        component.getElement().getStyle().set("left", "25");
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        if (SessionToIdMapper.getInstance().refreshRequired()) {
            UI.getCurrent().getPage().reload();
            SessionToIdMapper.getInstance().setRefreshRequired(false);
        }
    }


}
