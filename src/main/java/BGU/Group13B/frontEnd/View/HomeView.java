package BGU.Group13B.frontEnd.View;

import BGU.Group13B.frontEnd.LoginView;
import BGU.Group13B.frontEnd.service.Session;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.html.Image;


@Route(value = "home")
public class HomeView extends VerticalLayout implements BeforeEnterObserver {
    private Session session;
    @Autowired
    public HomeView(Session _session) {
        this.session = _session;
        // You can initialise any data required for the connected UI components here.
        //congratulations on logging in
        add(new H1("Welcome to the Super Duper Market!"));
        //center the text
        setAlignItems(Alignment.CENTER);
        String sourceLink = "https://i.dailymail.co.uk/i/pix/2012/11/01/article-2226519-15CC7B9F000005DC-738_634x471.jpg";
        Image image = new Image(sourceLink, "Sheep-image");
        // Set the image as the background image of the layout
        add(image);
        getStyle().set("background-size", "cover");
        getStyle().set("background-position", "center center");
        getStyle().set("background-repeat", "no-repeat");
    }
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
       /* if (!session.isUserLoggedIn()) {
            event.rerouteTo(LoginView.class);
            //UI.getCurrent().navigate(LoginView.class);
        }*/
    }
}
