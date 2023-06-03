package BGU.Group13B.frontEnd.components.store.workers;

import BGU.Group13B.backend.User.UserPermissions.StoreRole;
import BGU.Group13B.backend.storePackage.WorkerCard;
import BGU.Group13B.frontEnd.ResponseHandler;
import BGU.Group13B.service.Session;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class StoreWorkersLayout extends VerticalLayout implements ResponseHandler, roleToString {

    private final Session session;
    private final int userId;
    private final int storeId;

    private List<WorkerCard> workers;
    private HashMap<StoreRole, List<WorkerCard>> rolesToWorkers;

    private HorizontalLayout workersHeaderLayout;
    private Accordion rolesAccordion;
    private HashMap<StoreRole, WorkersVerticalMenuBar> rolesToWorkersVerticalMenuBar;
    private Button addWorkerButton;

    private AddWorkerDialog addWorkerDialog;

    private final HashMap<Integer, String> userIdToUsername;

    /*
    @Deprecated
    public StoreWorkersLayout(int userId, int storeId, Session session){
        super();
        this.userId = userId;
        this.storeId = storeId;
        this.session = session;

        setHeader();
        //setWorkers(handleResponse(session.getAllStoreWorkersInfo(userId, storeId)));
        add(workersHeaderLayout, rolesAccordion);
        setStyle();
    }

     */

    public StoreWorkersLayout(int userId, int storeId, Session session, Collection<WorkerCard> workers, HashMap<Integer, String> userIdToUsername){
        super();
        this.userId = userId;
        this.storeId = storeId;
        this.session = session;
        this.userIdToUsername = userIdToUsername;
        this.addWorkerDialog = new AddWorkerDialog(this, session, userId, storeId);
        this.rolesAccordion = new Accordion();

        setHeader();
        setWorkers(workers);
        setStyle();
    }

    private void setStyle() {
        getStyle().set("margin-left", "auto");
        setMaxWidth("200px");
    }

    private void setHeader(){
        workersHeaderLayout = new HorizontalLayout();
        addWorkerButton = new Button(new Icon(VaadinIcon.PLUS));


        addWorkerButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        addWorkerButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        workersHeaderLayout.add(new H2("Workers"), addWorkerButton);
        workersHeaderLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        addWorkerButton.addClickListener(e -> {
            addWorkerDialog.open();
        });

        add(workersHeaderLayout);

    }

    public void setWorkers(Collection<WorkerCard> workers){
        rolesToWorkers = new HashMap<>();
        rolesToWorkersVerticalMenuBar = new HashMap<>();
        rolesAccordion.removeFromParent();
        rolesAccordion = new Accordion();

        for(StoreRole role : StoreRole.values()){
            rolesToWorkers.put(role, new LinkedList<>());
        }

        for(WorkerCard worker : workers){
            rolesToWorkers.get(worker.storeRole()).add(worker);
        }

        for(StoreRole role : rolesToWorkers.keySet()){
            rolesToWorkersVerticalMenuBar.put(role, new WorkersVerticalMenuBar(session, userId, storeId, rolesToWorkers.get(role), userIdToUsername));
        }

        //ensure founder is first
        if(rolesToWorkersVerticalMenuBar.containsKey(StoreRole.FOUNDER)){
            rolesAccordion.add(roleToStringTitle(StoreRole.FOUNDER), rolesToWorkersVerticalMenuBar.get(StoreRole.FOUNDER));
        }

        for(StoreRole role : rolesToWorkersVerticalMenuBar.keySet()){
            if(!role.equals(StoreRole.FOUNDER)){
                rolesAccordion.add(roleToStringTitle(role) + "s", rolesToWorkersVerticalMenuBar.get(role));
            }
        }

        add(rolesAccordion);
    }

    public void addWorkerGUI(Integer newUserId, String username) {
        notifySuccess("Worker added successfully");
        var workers = handleResponse(session.getStoreWorkersInfo(userId, storeId));
        if(workers == null){
            notifyWarning("Failed to get workers info, please try to refresh the page");
            return;
        }

        userIdToUsername.put(newUserId, username);
        setWorkers(workers);
    }
}
