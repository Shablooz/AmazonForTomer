package BGU.Group13B.frontEnd.components.store.workers;

import BGU.Group13B.backend.User.UserPermissions;
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
import java.util.stream.Collectors;

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
    private HashMap<Integer, String> userIdToUsername;

    private final WorkerCard currentWorker;

    public StoreWorkersLayout(int userId, int storeId, Session session, Collection<WorkerCard> workers, HashMap<Integer, String> userIdToUsername, WorkerCard currentWorker){
        super();
        this.userId = userId;
        this.storeId = storeId;
        this.session = session;
        this.userIdToUsername = userIdToUsername;
        this.currentWorker = currentWorker;

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
            if(!currentWorker.userPermissions().contains(UserPermissions.IndividualPermission.STAFF)){
                notifyWarning("You don't have permission to add workers");
            }
            else{
                addWorkerDialog.open();
            }
        });

        if(!currentWorker.userPermissions().contains(UserPermissions.IndividualPermission.STAFF)){
            addWorkerButton.setVisible(false);
        }

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
            rolesToWorkersVerticalMenuBar.put(role, new WorkersVerticalMenuBar(session, userId, storeId, rolesToWorkers.get(role), userIdToUsername, currentWorker));
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

    public void updateWorkers() {
        var workers = handleResponse(session.getStoreWorkersInfo(userId, storeId));
        if(workers == null){
            notifyWarning("Failed to get workers info, please try to refresh the page");
            return;
        }

        List<Integer> userIds = workers.stream().map(WorkerCard::userId).collect(Collectors.toList());
        userIdToUsername = handleResponse(session.getUserIdsToUsernamesMapper(userIds), "");
        setWorkers(workers);
    }
}
