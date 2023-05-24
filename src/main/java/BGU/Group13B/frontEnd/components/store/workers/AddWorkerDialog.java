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

    private String[] rolesToAdd = {"Owner", "Manager"};

    public AddWorkerDialog(StoreWorkersLayout storeWorkersLayout){
        super();
        setWidth("350px");
        setHeight("300px");
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        H2 dialogTitle = new H2("Add Worker");
        TextField workerName = new TextField("Username");
        ComboBox<String> workerRole = new ComboBox<>("Role");
        workerRole.setItems(rolesToAdd);

        Button confirmButton = new Button("Confirm");
        confirmButton.addClickListener(e2 -> {
            notifyInfo("not implemented yet in GUI :(");
            close();
        });

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(dialogTitle, workerName, workerRole, confirmButton);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);

        add(dialogLayout);
    }
}
