package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.backend.Pair;
import BGU.Group13B.backend.storePackage.Store;
import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.service.Session;
import BGU.Group13B.service.info.StoreInfo;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.button.Button;

import java.util.*;
import java.util.stream.Collectors;

@PageTitle("My Stores")
@Route(value = "mystores", layout = MainLayout.class)
public class MyStoresView extends VerticalLayout {

    private final int userId = SessionToIdMapper.getInstance().getCurrentSessionId();
    private final HashMap<String, List<StoreInfo>> rolesToStores = new HashMap<>();
    private int selectedStoreId = -1;

    //components
    private final HashMap<String, Grid<StoreInfo>> rolesToGrids = new HashMap<>();
    HorizontalLayout buttonsLayout = new HorizontalLayout();
    private final Button createStoreButton = new Button("Create Store");
    private final Button enterStoreButton = new Button("Enter Store");


    @Autowired
    public MyStoresView(Session session){
        super();

        enterStoreButton.setEnabled(false);

        //List<Pair<StoreInfo, String>> userStoresAndRoles = session.getAllUserAssociatedStores(userId);
        List<Pair<StoreInfo, String>> userStoresAndRoles = getDemoStoresAndRoles();

        for (Pair<StoreInfo, String> pair : userStoresAndRoles) {
            if (!rolesToStores.containsKey(pair.getSecond())) {
                rolesToStores.put(pair.getSecond(), new LinkedList<>());
            }
            rolesToStores.get(pair.getSecond()).add(pair.getFirst());
        }

        for (String role : rolesToStores.keySet()) {
            Grid<StoreInfo> grid = new Grid<>();
            grid.setItems(rolesToStores.get(role));
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

        //buttons actions
        enterStoreButton.addClickListener(e -> navigateToStore());
        createStoreButton.addClickListener(e -> navigateToCreateStore());


        //add the role headers and the grids to the view
        for(Map.Entry<String, Grid<StoreInfo>> entry : rolesToGrids.entrySet()){
            add(new H3("Role: " + entry.getKey()));
            add(entry.getValue());
        }

        //add the buttons to the view in horizontal layout
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



}
