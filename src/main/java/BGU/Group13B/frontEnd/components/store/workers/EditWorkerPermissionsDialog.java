package BGU.Group13B.frontEnd.components.store.workers;

import BGU.Group13B.backend.User.UserPermissions;
import BGU.Group13B.backend.storePackage.WorkerCard;
import BGU.Group13B.frontEnd.ResponseHandler;
import BGU.Group13B.service.Session;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.*;


public class EditWorkerPermissionsDialog extends Dialog implements ResponseHandler {

    private final Session session;
    private final int userId;
    private final int storeId;
    private final WorkerCard worker;
    private final String username;
    private final List<UserPermissions.IndividualPermission> permissions;

    private final WorkerMenuBar workerMenuBar;

    private final CheckboxGroup<UserPermissions.IndividualPermission> permissionsCheckbox;

    public EditWorkerPermissionsDialog(Session session, int userId, int storeId, WorkerMenuBar workerMenuBar){
        super();
        this.session = session;
        this.userId = userId;
        this.storeId = storeId;
        this.worker = workerMenuBar.getWorkerCard();
        this.username = workerMenuBar.getWorkerName();
        this.workerMenuBar = workerMenuBar;
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        H2 header = new H2("Edit Worker Permissions");
        Div workerName = new Div();
        workerName.setText("Name: " + username);

        permissionsCheckbox = new CheckboxGroup<>();
        permissionsCheckbox.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        permissionsCheckbox.setLabel("Permissions");
        permissions = Arrays.stream(UserPermissions.IndividualPermission.values()).filter(p -> !p.equals(UserPermissions.IndividualPermission.FONLY)).toList();
        permissionsCheckbox.setItems(permissions);
        permissionsCheckbox.select(worker.userPermissions());

        Button saveButton = new Button("Save", event -> {
            updateUserPermissions();
        });

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(header, workerName, permissionsCheckbox, saveButton);
        add(dialogLayout);
    }

    private void updateUserPermissions(){
        List<UserPermissions.IndividualPermission> permissionsToAdd = permissionsCheckbox.getSelectedItems().stream()
                .filter(p -> !worker.userPermissions().contains(p)).toList();


        List<UserPermissions.IndividualPermission> permissionsToRemove = new LinkedList<>(permissions);
        permissionsToRemove.removeAll(permissionsCheckbox.getSelectedItems());
        permissionsToRemove = permissionsToRemove.stream().filter(p -> worker.userPermissions().contains(p)).toList();

        boolean failed = false;

        for(var p : permissionsToAdd){
            var res = handleResponse(session.addIndividualPermission(userId, worker.userId(), storeId, p));
            if(res == null)
                failed = true;
        }

        for(var p : permissionsToRemove){
            var res = handleResponse(session.removeIndividualPermission(userId, worker.userId(), storeId, p));
            if(res == null)
                failed = true;
        }

        if(!failed)
            notifySuccess("Permissions updated successfully");

        else
            notifyWarning("Some permissions failed to update");



        Set<UserPermissions.IndividualPermission> newPermissions = new HashSet<>(worker.userPermissions());
        newPermissions.removeAll(permissionsToRemove);
        newPermissions.addAll(permissionsToAdd);

        this.workerMenuBar.updateWorkerCard(new WorkerCard(worker.userId(), worker.storeRole(), newPermissions));
        close();
    }

}
