package BGU.Group13B.frontend.views;

import BGU.Group13B.service.Session;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainLayout.class)

public class MainView extends VerticalLayout implements BeforeEnterObserver {

    private final Session session;

    public MainView(Session session) {
        this.session = session;

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(
                greeting(),
                login());
        horizontalLayout.setAlignItems(Alignment.CENTER);
        add(horizontalLayout);

    }

    private Component login() {
        VerticalLayout verticalLayout = new VerticalLayout();
        Button loginButton = new Button("Login");
        loginButton.addClickListener(e -> UI.getCurrent().navigate(LoginView.class));
        //todo direct move to signup
        //put the text in the top right corner


        setUpperRightComponent(loginButton, 50, 0);
        if (session.isUserLoggedIn())
            loginButton.setVisible(false);
        verticalLayout.add(loginButton);
        return verticalLayout;
    }

    private void setUpperRightComponent(Component component, int topMargin, int rightMargin) {
        component.getElement().getStyle().set("position", "absolute");
        component.getElement().getStyle().set("top", "" + topMargin + "px");
        component.getElement().getStyle().set("right", "0" + rightMargin + "px");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (session.isUserLoggedIn()) {
            event.rerouteTo(HomeView.class);
        }
    }

    private static H1 greeting() {
        H1 h1 = new H1("Welcome to the system!");
        h1.getStyle().set("display", "flex");
        h1.getStyle().set("align-items", "center");
        h1.getStyle().set("justify-content", "center");
        h1.getStyle().set("height", "100%"); // set the height of the parent element

        return h1;
    }
}
