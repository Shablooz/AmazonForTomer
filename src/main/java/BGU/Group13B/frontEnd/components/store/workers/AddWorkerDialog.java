package BGU.Group13B.frontEnd.components.store.workers;

import BGU.Group13B.frontEnd.ResponseHandler;
import BGU.Group13B.service.Session;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class AddWorkerDialog extends Dialog implements ResponseHandler {

    enum rolesToAdd {
        Owner, Manager
    }

    public AddWorkerDialog(StoreWorkersLayout storeWorkersLayout, Session session, int userId, int storeId){
        super();
        setWidth("350px");
        setHeight("300px");
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        H2 dialogTitle = new H2("Add Worker");
        TextField workerName = new TextField("Username");
        workerName.setRequired(true);
        workerName.setRequiredIndicatorVisible(true);
        ComboBox<rolesToAdd> workerRole = new ComboBox<>("Role");
        workerRole.setRequired(true);
        workerRole.setRequiredIndicatorVisible(true);
        workerRole.setItems(rolesToAdd.values());

        Button confirmButton = new Button("Confirm");
        confirmButton.addClickListener(e2 -> {
            if (workerName.getValue().isEmpty() || workerRole.getValue() == null) {
                return;
            }

            switch (workerRole.getValue()) {

                case Owner -> {
                    Integer newUserId = handleResponse(session.getUserIdByUsername(workerName.getValue()));
                    if(newUserId == null)
                        return;
                    if(handleResponse(session.addOwner(userId, newUserId, storeId)) == null){
                        return;
                    }
                    notifySuccess("Vote for worker created successfully");
                    storeWorkersLayout.updateWorkers();
                    close();
                }
                case Manager -> {
                    Integer newUserId = handleResponse(session.getUserIdByUsername(workerName.getValue()));
                    if(newUserId == null)
                        return;
                    if (handleResponse(session.addManager(userId, newUserId, storeId)) == null) {
                        return;
                    }
                    notifySuccess("Worker added successfully");
                    storeWorkersLayout.updateWorkers();
                    close();
                }


            }
        });

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(dialogTitle, workerName, workerRole, confirmButton);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);

        add(dialogLayout);
    }
}
