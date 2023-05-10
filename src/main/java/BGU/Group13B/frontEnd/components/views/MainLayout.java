package BGU.Group13B.frontEnd.components.views;


import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.frontEnd.components.appnav.AppNav;
import BGU.Group13B.frontEnd.components.appnav.AppNavItem;
import BGU.Group13B.service.Session;
import com.vaadin.flow.component.UI;
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
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H2 viewTitle;
    private final Session session;

    @Autowired
    public MainLayout(Session session) {
        this.session = session;
        UI currentUI = UI.getCurrent();
        VaadinSession currentSession = currentUI.getSession();
        String sessionId = currentSession.getSession().getId();
        if(!SessionToIdMapper.getInstance().containsKey(sessionId)){
            int tempId = session.enterAsGuest();
            SessionToIdMapper.getInstance().add(sessionId,tempId);
        }
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        H1 appName = new H1("trading-system");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());
        FlexLayout flexLayout = new FlexLayout();
        flexLayout.setJustifyContentMode(FlexLayout.JustifyContentMode.END);
        flexLayout.setWidthFull();
        int id = SessionToIdMapper.getInstance().getCurrentSessionId();
        if (!session.isUserLogged(id)) {
            Button loginButton = new Button("Login");
            loginButton.addClickListener(event -> getUI().ifPresent(ui -> ui.navigate("login")));
            flexLayout.add(loginButton);

            Button signUpButton = new Button("Sign up");
            signUpButton.addClickListener(event -> getUI().ifPresent(ui -> ui.navigate("register")));
            flexLayout.add(signUpButton);
        } else {
            Button logoutButton = new Button("Logout");
            logoutButton.addClickListener(event -> {
                session.logout(5/*todo chase to use sessionId*/);
                getUI().ifPresent(ui -> ui.getPage().reload());
            });
            flexLayout.add(logoutButton);
        }

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        horizontalLayout.add(flexLayout);

        addToDrawer(header, scroller, horizontalLayout, createFooter());
    }

    private AppNav createNavigation() {
        // AppNav is not yet an official component.
        // For documentation, visit https://github.com/vaadin/vcf-nav#readme
        AppNav nav = new AppNav();
        nav.addItem(new AppNavItem("Home View", HomeView.class, LineAwesomeIcon.GLOBE_SOLID.create()));

        //my stores
        if(true){ //TODO! check if the user is logged in
            nav.addItem(new AppNavItem("My Stores", MyStoresView.class, LineAwesomeIcon.STORE_SOLID.create()));
        }


        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
