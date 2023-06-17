package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.backend.User.UserCard;
import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.service.Response;
import BGU.Group13B.service.Session;
import BGU.Group13B.service.info.StoreInfo;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@PageTitle("Admin Page")
@Route(value = "admin", layout = MainLayout.class)
public class AdminView extends VerticalLayout implements BeforeEnterObserver, AfterNavigationObserver {
    private final int userId = SessionToIdMapper.getInstance().getCurrentSessionId();
    private int selectedUserId = -1;

    //components
    HorizontalLayout buttonsLayout = new HorizontalLayout();
    private final Button createStoreButton = new Button("Create Store"); //fixme remove?
    private final Button deleteUserButton = new Button("Delete User");
    private final Button purchaseHistoryButton = new Button("Purchase History");

    private Grid<UserCard> grid;

    private final Session session;

    @Autowired
    public AdminView(Session session) {
        super();
        this.session = session;

    }

    private void start(){
        this.removeAll();
        // You can initialise any data required for the connected UI components here.
        //joining as a guest
        //congratulations on logging in
        add(new H1("Admin Control Panel"));
        //center the text
        setAlignItems(Alignment.CENTER);
        String sourceLink = "https://as1.ftcdn.net/v2/jpg/05/60/39/98/1000_F_560399816_HxOAAYcTEjS6OlAdT9TOu9TQxGiph8jz.jpg";

        /*CSS code*/
        getStyle().set("background-image", "url(" + sourceLink + ")");
        getStyle().set("background-size", "contain");
        getStyle().set("background-position", "center");
        getStyle().set("background-repeat", "no-repeat");
        getStyle().set("background-attachment", "fixed");
        getStyle().set("height", "100vh");
        getStyle().set("width", "100vw");

        deleteUserButton.setEnabled(false);
        deleteUserButton.getElement().getThemeList().add("primary");
        purchaseHistoryButton.setEnabled(false);
        purchaseHistoryButton.getElement().getThemeList().add("primary");
        add(new H2("All Users"));
        List<UserCard> userCards = handleResponse(session.getAllUserCards(userId));
        List<UserCard> validUserCards = userCards.stream()
                .filter(card -> card.userName() != null && !card.userName().isEmpty())
                .toList();

        grid = new Grid<UserCard>();
        grid.setItems(validUserCards);
        grid.addColumn(UserCard::userId).setHeader("User ID");
        grid.addColumn(UserCard::userName).setHeader("Username");
        grid.addColumn(UserCard::email).setHeader("Email");
        grid.addColumn(UserCard::userPermissions).setHeader("Permission");
        grid.setItemDetailsRenderer(new ComponentRenderer<>(userCard -> new Text(userCard.userPermissions())));
        grid.addItemClickListener(event -> {
            UserCard userCard = event.getItem();
            if (grid.isDetailsVisible(userCard)) {
                grid.setDetailsVisible(userCard, false);
            } else {
                grid.setDetailsVisible(userCard, true);
            }
        });

        /*grid.addColumn(new ComponentRenderer<>(userCard -> { this has a tooltip feature
            Label label = new Label(userCard.userPermissions());
            label.getElement().setProperty("title", userCard.userPermissions());
            return label;
        })).setHeader("Permission");*/


        //actions
        grid.addItemClickListener(item -> {
            selectedUserId = item.getItem().userId();
            deleteUserButton.setEnabled(true);
            purchaseHistoryButton.setEnabled(true);
        });

        grid.addItemDoubleClickListener(item -> {
            selectedUserId = item.getItem().userId();
            deleteUserButton.setEnabled(true);
            purchaseHistoryButton.setEnabled(true);


            //enter store page
            //navigateToStore(); fixme delete user?

        });


        //styling
        grid.getStyle().set("margin", "10px");
        grid.setAllRowsVisible(true);
        grid.setMaxWidth("960px");
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);


        initButtons();
    }

    private void initButtons() {
        //buttons actions
        deleteUserButton.addClickListener(e -> deleteUser());
        purchaseHistoryButton.addClickListener(e -> viewPurchase());

        add(grid);

        //add the buttons to the view in horizontal layout
        buttonsLayout.add(deleteUserButton);
        buttonsLayout.add(purchaseHistoryButton);
        add(buttonsLayout);
    }

    private void deleteUser(){
        if(selectedUserId == -1){
            Notification.show("Please select a user first");
            return;
        }
        deleteUserDialog();
    }

    private void viewPurchase(){
        if(selectedUserId == -1){
            Notification.show("Please select a user first");
            return;
        }
        getUI().ifPresent(ui -> ui.navigate("userPurchaseHistory/" + selectedUserId));
    }

    private void deleteUserDialog() {
        Dialog dialog = new Dialog();
        dialog.setWidth("350px");
        dialog.setHeight("550px");
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);
        H2 dialogTitle = new H2("Are you sure you want to delete this user?");
        Label label = new Label(Integer. toString(selectedUserId));

        Button confirmButton = new Button("Confirm");
        confirmButton.addClickListener(e2 -> {

            handleResponseAndRefresh(session.removeUser(userId, selectedUserId));
            dialog.close();
        });


        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(dialogTitle, label, confirmButton);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(Alignment.STRETCH);

        dialog.add(dialogLayout);
        dialog.open();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if(!handleResponse(session.isAdmin(userId))){
            event.rerouteTo("");
            return;
        }
        start();


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

    private <T> T handleResponse(Response<T> response) {
        if (response.didntSucceed()) {
            Notification errorNotification = Notification.show("Error: " + response.getMessage());
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            navigate("");
        }
        return response.getData();
    }

    private void navigate(String nav) {
        UI.getCurrent().navigate(nav);
    }

    private void handleResponseAndRefresh(Response<Integer> response) {
        String navInCaseOfFailure = "admin";
        if (response.didntSucceed()) {
            Notification errorNotification = Notification.show("Error: " + response.getMessage());
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            UI.getCurrent().navigate(navInCaseOfFailure);
            return;
        }
        updateGrid();
    }

    private void updateGrid() {
        this.removeAll();
        List<UserCard> userCards = handleResponse(session.getAllUserCards(userId));
        List<UserCard> validUserCards = userCards.stream()
                .filter(card -> card.userName() != null && !card.userName().isEmpty())
                .toList();
        //grid.setItems((DataProvider<StoreInfo, Void>) storeInfoList);
        grid.setItems(validUserCards);
        buttonsLayout.add(deleteUserButton);
        add(buttonsLayout);
    }


}
