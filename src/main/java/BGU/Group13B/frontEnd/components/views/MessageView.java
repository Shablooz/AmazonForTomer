package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.service.Session;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

//@Component
@Route(value = "messages", layout = MainLayout.class)
@PageTitle("messages")
public class MessageView extends VerticalLayout {
    private final Session session;

    private Button changeToOldMessagesButton;
    private Button nextMessageButton;
    private Button aswerMessageButton;
    private Text messageText;

    private Text answerHeader;
    private Text answerText;
    @Autowired
    public MessageView(Session session) {
        this.session = session;


        HorizontalLayout horizontal1 = new HorizontalLayout();
        VerticalLayout vertical1 = new VerticalLayout();

        changeToOldMessagesButton = new Button("change to old messages");
        nextMessageButton = new Button("next message");
        aswerMessageButton = new Button("answer message");
        messageText = new Text("message text");
        answerHeader = new Text("answer header");
        answerText = new Text("answer text");
        add(horizontal1);
        horizontal1.add(changeToOldMessagesButton,messageText,nextMessageButton);
        vertical1.add(answerHeader,answerText,aswerMessageButton);


        changeToOldMessagesButton.addClickListener(e -> {
            if (vertical1.getParent().isEmpty()) {
                add(vertical1);
                changeToOldMessagesButton.setText("change to new messages");
            } else {
                remove(vertical1);
                changeToOldMessagesButton.setText("change to old messages");
            }
        });
    }

}
