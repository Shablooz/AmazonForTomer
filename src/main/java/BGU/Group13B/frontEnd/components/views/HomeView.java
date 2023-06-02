package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.service.Session;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "", layout = MainLayout.class)
public class HomeView extends VerticalLayout implements BeforeEnterObserver, AfterNavigationObserver {
    private final Session session;

    @Autowired
    public HomeView(Session session) {
        //TODO initialize the singleton collection is repository tables arent empty
        this.session = session;
        // You can initialise any data required for the connected UI components here.
        //joining as a guest
        //congratulations on logging in
        add(new H1("Welcome to the Super Duper Market!"));
        //center the text
        setAlignItems(Alignment.CENTER);
        String sourceLink = "https://i.dailymail.co.uk/i/pix/2012/11/01/article-2226519-15CC7B9F000005DC-738_634x471.jpg";

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

    public MenuBar buildMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_ICON);

        MenuItem item = menuBar.addItem(VaadinIcon.MENU.create());
        SubMenu subMenu = item.getSubMenu();
        addMenuItem(subMenu, "Item 1", "Item 1 clicked");
        addMenuItem(subMenu, "Item 2", "Item 2 clicked");

        setPosition(menuBar);

        return menuBar;
    }

    private void addMenuItem(SubMenu subMenu, String caption, String message) {
        subMenu.addItem(caption, e -> Notification.show(message));
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
