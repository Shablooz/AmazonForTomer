package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.backend.Pair;
import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.service.Response;
import BGU.Group13B.service.Session;
import BGU.Group13B.service.info.StoreInfo;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY_INLINE;

@PageTitle("All Stores")
@Route(value = "allstores", layout = MainLayout.class)
public class AllStoresView extends VerticalLayout{

    private final int userId = SessionToIdMapper.getInstance().getCurrentSessionId();
    private int selectedStoreId = -1;

    //components
    HorizontalLayout buttonsLayout = new HorizontalLayout();
    private final Button enterStoreButton = new Button("Enter Store");

    private Grid<StoreInfo> grid;

    private final Session session;
    @Autowired
    public AllStoresView(Session session){
        super();
        this.session = session;
        enterStoreButton.setEnabled(false);
        add(new H1("All Stores"));
        List<StoreInfo> storeInfos = handleResponse(session.getAllStoresTheUserCanView(userId));
        if(storeInfos == null){
            storeInfos = new LinkedList<>();
        }

        grid = new Grid<StoreInfo>();
        grid.setItems(storeInfos);
        grid.addColumn(StoreInfo::storeName).setHeader("Name");
        grid.addColumn(StoreInfo::category).setHeader("Category");
        grid.addColumn(StoreInfo::storeScore).setHeader("Score");

        //actions
        grid.addItemClickListener(item -> {
            selectedStoreId = item.getItem().storeId();
            enterStoreButton.setEnabled(true);
        });

        grid.addItemDoubleClickListener(item -> {
            selectedStoreId = item.getItem().storeId();
            enterStoreButton.setEnabled(true);


            //enter store page
            navigateToStore();

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
        enterStoreButton.addClickListener(e -> navigateToStore());

        add(grid);

        //add the buttons to the view in horizontal layout
        buttonsLayout.add(enterStoreButton);
        add(buttonsLayout);
    }

    private void handleResponseAndRefresh(Response<Integer> response) {
        String navInCaseOfFailure = "allstores";
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
        var storeInfoList = handleResponse(session.getAllStoresTheUserCanView(userId));
        //grid.setItems((DataProvider<StoreInfo, Void>) storeInfoList);
        grid.setItems(storeInfoList);
        buttonsLayout.add(enterStoreButton);
        add(buttonsLayout);
    }

    private void navigateToStore(){
        if(selectedStoreId == -1){
            Notification.show("Please select a store first");
            return;
        }
        getUI().ifPresent(ui -> ui.navigate("store/" + selectedStoreId));
    }

    private void navigateToCreateStore(){
        getUI().ifPresent(ui -> ui.navigate("createstore"));
    }




    private List<Pair<StoreInfo, String>> getDemoStoresAndRoles(){
        List<Pair<StoreInfo, String>> userStoresAndRoles = new LinkedList<>();

        userStoresAndRoles.add(Pair.of(
                new StoreInfo(3, "store 3", "category 1", 4.7),
                "OWNER"));
        userStoresAndRoles.add(Pair.of(
                new StoreInfo(4, "store 4", "category 1", 2.7),
                "OWNER"));
        userStoresAndRoles.add(Pair.of(
                new StoreInfo(5, "store 5", "category 1", 5.0),
                "OWNER"));

        userStoresAndRoles.add(Pair.of(
                new StoreInfo(6, "store 6", "category 1", 3.8),
                "MANAGER"));
        userStoresAndRoles.add(Pair.of(
                new StoreInfo(7, "store 7", "category 1", 2.9),
                "MANAGER"));

        userStoresAndRoles.add(Pair.of(
                new StoreInfo(0, "store 0", "category 0", 4.3),
                "FOUNDER"));
        userStoresAndRoles.add(Pair.of(
                new StoreInfo(1, "store 1", "category 1", 4.0),
                "FOUNDER"));
        userStoresAndRoles.add(Pair.of(
                new StoreInfo(2, "store 2", "category 2", 3.5),
                "FOUNDER"));

        return userStoresAndRoles;
    }

    private Notification createReportError(String msg) {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

        Icon icon = VaadinIcon.WARNING.create();
        Div info = new Div(new Text(msg));

        HorizontalLayout layout = new HorizontalLayout(icon, info,
                createCloseBtn(notification));
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(layout);

        return notification;
    }
    private Button createCloseBtn(Notification notification) {
        Button closeBtn = new Button(VaadinIcon.CLOSE_SMALL.create(),
                clickEvent -> notification.close());
        closeBtn.addThemeVariants(LUMO_TERTIARY_INLINE);

        return closeBtn;
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
}
