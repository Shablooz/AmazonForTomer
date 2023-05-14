package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.backend.Pair;
import BGU.Group13B.backend.storePackage.Store;
import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.service.Response;
import BGU.Group13B.service.Session;
import BGU.Group13B.service.info.StoreInfo;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.button.Button;

import java.util.*;
import java.util.stream.Collectors;

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY_INLINE;

@PageTitle("My Stores")
@Route(value = "mystores", layout = MainLayout.class)
public class MyStoresView extends VerticalLayout implements BeforeEnterObserver {

    private final int userId = SessionToIdMapper.getInstance().getCurrentSessionId();
    private final HashMap<String, List<StoreInfo>> rolesToStores = new HashMap<>();
    private int selectedStoreId = -1;

    //components
    private final HashMap<String, Grid<StoreInfo>> rolesToGrids = new HashMap<>();
    HorizontalLayout buttonsLayout = new HorizontalLayout();
    private final Button createStoreButton = new Button("Create Store");
    private final Button enterStoreButton = new Button("Enter Store");

    private final Session session;
    @Autowired
    public MyStoresView(Session session){
        super();
        this.session = session;
        enterStoreButton.setEnabled(false);

        List<Pair<StoreInfo, String>> userStoresAndRoles = handleResponse(session.getAllUserAssociatedStores(userId));
        //List<Pair<StoreInfo, String>> userStoresAndRoles = getDemoStoresAndRoles();

        for (Pair<StoreInfo, String> pair : userStoresAndRoles) {
            if (!rolesToStores.containsKey(pair.getSecond())) {
                rolesToStores.put(pair.getSecond(), new LinkedList<>());
            }
            rolesToStores.get(pair.getSecond()).add(pair.getFirst());
        }
        String[] roles = {"FOUNDER", "OWNER", "MANGER"};
        for (String role : roles) {

            var grid = new Grid<StoreInfo>();
            grid.setItems(!rolesToStores.containsKey(role) ? new LinkedList<>() : rolesToStores.get(role));
            grid.addColumn(StoreInfo::storeName).setHeader("Name");
            grid.addColumn(StoreInfo::category).setHeader("Category");
            grid.addColumn(StoreInfo::storeScore).setHeader("Score");

            //actions
            grid.addItemClickListener(item -> {
                selectedStoreId = item.getItem().storeId();
                enterStoreButton.setEnabled(true);
                //deselect from other grids
                for (Map.Entry<String, Grid<StoreInfo>> entry : rolesToGrids.entrySet()) {
                    if (!entry.getKey().equals(role)) {
                        entry.getValue().deselectAll();
                    }
                }
            });

            grid.addItemDoubleClickListener(item -> {
                selectedStoreId = item.getItem().storeId();
                enterStoreButton.setEnabled(true);

                //deselect from other grids
                for (Map.Entry<String, Grid<StoreInfo>> entry : rolesToGrids.entrySet()) {
                    if (!entry.getKey().equals(role)) {
                        entry.getValue().deselectAll();
                    }
                }

                //enter store page
                navigateToStore();

            });


            //styling
            grid.getStyle().set("margin", "10px");
            grid.setAllRowsVisible(true);
            grid.setMaxWidth("960px");
            grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
            grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);

            rolesToGrids.put(role, grid);
        }

        initButtons();
    }

    private void initButtons() {
        //buttons actions
        enterStoreButton.addClickListener(e -> navigateToStore());
        createStoreButton.addClickListener(e -> addStoreDialog());


        //add the role headers and the grids to the view
        for(Map.Entry<String, Grid<StoreInfo>> entry : rolesToGrids.entrySet()){
            add(new H3("Role: " + entry.getKey()));
            add(entry.getValue());
        }

        //add the buttons to the view in horizontal layout
        buttonsLayout.add(createStoreButton, enterStoreButton);
        add(buttonsLayout);
    }

    private void addStoreDialog() {
        Dialog dialog = new Dialog();
        dialog.setWidth("350px");
        dialog.setHeight("550px");
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        H2 dialogTitle = new H2("Add Store");
        TextField storeName = new TextField("Name");
        TextField storeCategory = new TextField("Category");

        Button confirmButton = new Button("Confirm");
        confirmButton.addClickListener(e2 -> {

            handleResponseAndRefresh(session.addStore(userId, storeName.getValue(), storeCategory.getValue()));
            dialog.close();
        });


        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(dialogTitle, storeName, storeCategory, confirmButton);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(Alignment.STRETCH);

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void handleResponseAndRefresh(Response<Integer> response) {
        String navInCaseOfFailure = "mystores";
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
        /*var storesAndRoles = getDemoStoresAndRoles();
        //convert to Map<String, List<StoreInfo>>
        var storesAndRolesMap =
                storesAndRoles.
                        stream().
                        collect(Collectors.
                                groupingBy(Pair::getSecond,
                                        Collectors.mapping(Pair::getFirst, Collectors.toList())));*/

        for(var entry : rolesToGrids.entrySet()){
            String role = entry.getKey();
            var storeInfoList =
                    new ArrayList<>(handleResponse(session.getAllUserAssociatedStores(userId)).
                            stream().
                            filter(pair -> pair.getSecond().equals(role))
                            .map(Pair::getFirst).toList());
            //for development
//            storeInfoList.addAll(storesAndRolesMap.get(role));
            entry.getValue().setItems(storeInfoList);
            add(new H3("Role: " + role));
            add(entry.getValue());
        }
        buttonsLayout.add(createStoreButton, enterStoreButton);
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


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if(!session.isUserLogged(userId)){
            createReportError("Permission denied, please login first.").open();
            event.rerouteTo(LoginView.class);
        }
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
