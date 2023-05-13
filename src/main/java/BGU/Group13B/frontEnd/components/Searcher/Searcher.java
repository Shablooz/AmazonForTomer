package BGU.Group13B.frontEnd.components.Searcher;

import BGU.Group13B.service.Session;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.QueryParameters;

import java.util.HashMap;

import static org.apache.el.lang.ELArithmetic.add;


public class Searcher extends HorizontalLayout{
    private TextField searchField;
    private Button searchButton;

    public Searcher() {
        searchField = new TextField();
        searchField.setWidth("95%");
        searchField.setClearButtonVisible(true);
        searchField.setPlaceholder("Search by name, category or keywords...");
        searchButton = new Button();
        searchButton.setIcon(VaadinIcon.SEARCH.create());
        searchButton.addClickListener(e -> {
            try {
                String searchTerm = searchField.getValue();
                getUI().ifPresent(ui -> ui.navigate("Search results/" + searchTerm));
            } catch (Exception exception) {
                Notification.show(exception.getMessage());
            }
        });
        add(searchField, searchButton);
        setWidth("600%");
    }
}



