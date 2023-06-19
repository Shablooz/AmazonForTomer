package BGU.Group13B.frontEnd.components.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Deleted")
@Route(value = "deleted", layout = PlainLayout.class)
public class DeletedView extends HorizontalLayout {

    public DeletedView() {
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        H1 messageLabel = new H1("Your user got deleted lol");
        add(messageLabel);
    }
}
