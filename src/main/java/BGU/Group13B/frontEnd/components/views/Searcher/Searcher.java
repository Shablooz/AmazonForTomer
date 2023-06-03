package BGU.Group13B.frontEnd.components.views.Searcher;

import BGU.Group13B.service.info.ProductInfo;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class Searcher extends HorizontalLayout{
    private TextField searchField;
    public static Button searchButton;

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



