package BGU.Group13B.frontEnd.components.store.workers;

import BGU.Group13B.backend.User.UserPermissions;
import BGU.Group13B.backend.storePackage.WorkerCard;
import BGU.Group13B.frontEnd.ResponseHandler;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class WorkerProfileDialog extends Dialog implements ResponseHandler, roleToString {

    private final Grid<UserPermissions.IndividualPermission> workerPermissions;

    public WorkerProfileDialog(WorkerCard worker, String username){
        super();
        setWidth("350px");
        setHeight("550px");
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        H2 dialogTitle = new H2("Worker Profile");
        Div workerName = new Div();
        workerName.setText("Name: " + username);
        Div workerRole = new Div();
        workerRole.setText("Role: " + roleToStringTitle(worker.storeRole()));
        workerPermissions = new Grid<>();
        workerPermissions.setItems(worker.userPermissions());
        workerPermissions.addColumn(p -> p).setHeader("Permissions");
        workerPermissions.setAllRowsVisible(true);
        workerPermissions.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        workerPermissions.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);


        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(dialogTitle, workerName, workerRole, workerPermissions);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);

        add(dialogLayout);
    }

    public void updateWorkerCard(WorkerCard workerCard) {
        workerPermissions.setItems(workerCard.userPermissions());
    }
}
