package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.backend.User.Message;
import BGU.Group13B.backend.storePackage.Review;
import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.service.Response;
import BGU.Group13B.service.Session;
import BGU.Group13B.service.VoidResponse;
import BGU.Group13B.service.entity.ReviewService;
import BGU.Group13B.service.info.ProductInfo;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.button.Button;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@PageTitle("product")
@Route(value = "product", layout = MainLayout.class)
public class ProductView extends VerticalLayout implements HasUrlParameter<String> {
    private final int userId = SessionToIdMapper.getInstance().getCurrentSessionId();
    private int productId;
    private int storeId;
    private Session session;
    private HorizontalLayout seller;
    private HorizontalLayout category;
    private HorizontalLayout price;
    private HorizontalLayout description;
    private HorizontalLayout score;
    private Button buyNow;
    private Button addToCart;
    private Button offerBid;

    private String USER_NAME_COL = "User Name";
    private String REVIEW_COL = "Review";

    Grid<ReviewService> grid ;
    @Autowired
    public ProductView(Session session) {
        this.session=session;
    }

    @Override
    public void setParameter(BeforeEvent event, @WildcardParameter String parameters) {
        String[] params = parameters.split("/");
        productId = Integer.parseInt(params[0]);
        storeId = Integer.parseInt(params[1]);
        start();
    }
    private void start(){
        ProductInfo info = session.getStoreProductInfo(userId,storeId,productId).getData();
        seller = getIconLabel("Seller :  "+info.seller(),VaadinIcon.MALE);
        category = getIconLabel("Category :  "+info.category(), VaadinIcon.TAGS);
        price = getIconLabel("Price :  " + info.price(), VaadinIcon.CASH);
        description = getIconLabel("Description :  " + info.description(), VaadinIcon.INFO_CIRCLE);
        score = getIconLabel("Score :  "+ info.score(), VaadinIcon.STAR);
        add(new H1(info.name()));
        setAlignItems(Alignment.CENTER);
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(seller, category, price, description, score);
        verticalLayout.getStyle().set("background-color", "#171C41");
        verticalLayout.setWidth("50%");
        add(verticalLayout);
        buyNow = buyNow();
        addToCart = addToCart();
        offerBid = offerBid();
        HorizontalLayout Buttons = new HorizontalLayout();
        Buttons.add(buyNow, addToCart, offerBid);
        add(Buttons);
        Button reviewButton = reviewButton();
        Buttons.add(reviewButton);
        grid = reviewGridCreator();
        add(grid);
    }

    private Button offerBid() {
        Button button = new Button("Offer Bid");
        button.setIcon(VaadinIcon.CASH.create());
        Dialog dialog = new Dialog();
        offerBidDialog(dialog);

        button.addClickListener(event -> dialog.open());
        return button;
    }

    private void offerBidDialog(Dialog dialog)
    {
        Button exit = new Button();
        exit.setIcon(new Icon(VaadinIcon.CLOSE));
        exit.addClickListener(event -> dialog.close());
        dialog.getHeader().add(exit);
        FormLayout form = new FormLayout();
        TextField price = new TextField("Price");
        TextField quantity = new TextField("Quantity");
        form.add(price, quantity);
        dialog.add(form);
        Button submit = new Button("Submit");
        submit.addClickListener(event -> {
            Response<VoidResponse> response = session.purchaseProposalSubmit
                    (userId, storeId, productId, Integer.parseInt(price.getValue()), Integer.parseInt(quantity.getValue()));
            if (response.didntSucceed()) {
                Notification.show(response.getMessage());
            } else {
                Notification.show("Bid offered successfully");
            }
            dialog.close();
        });
        dialog.add(submit);
    }

    private Button buyNow() {
        Button button = new com.vaadin.flow.component.button.Button("Buy Now");
        button.setIcon(VaadinIcon.CREDIT_CARD.create());
        button.addClickListener(event -> {
            Response<VoidResponse> response = session.addToCart(userId, storeId, productId);
            UI.getCurrent().navigate("payment");
            if (response.didntSucceed()) {
                Notification.show(response.getMessage());
            } else {
                Notification.show("Added to cart successfully");
            }
        });
        return button;
    }

    private Button addToCart() {
        Button button = new Button("Add To Cart");
        button.setIcon(VaadinIcon.CART_O.create());
        button.addClickListener(event -> {
            Response<VoidResponse> response = session.addToCart(userId, storeId, productId);
            if (response.didntSucceed()) {
                Notification.show(response.getMessage());
            } else {
                Notification.show("Added to cart successfully");
            }
        });
        return button;
    }

    private HorizontalLayout getIconLabel(String text, VaadinIcon icon){
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        Label label = new Label(text);
        label.getStyle().set("font-size", "20px");
        horizontalLayout.add(icon.create(), label);
        return horizontalLayout;
    }

    private Button reviewButton() {
        Button messageButton = new Button("Review Manager");
        messageButton.setIcon(VaadinIcon.COMMENTS_O.create());
        Dialog myDialog = new Dialog();
        reviewDialogContent(myDialog);

        messageButton.addClickListener(event -> myDialog.open());
        return messageButton;
    }
    private void reviewDialogContent(Dialog currentDialog)
    {
        currentDialog.removeAll();
        currentDialog.getFooter().removeAll();
        currentDialog.getHeader().removeAll();

        currentDialog.setHeaderTitle("Review Manager");

        Button closeButton = new Button(new Icon("lumo", "cross"),
                (e) -> currentDialog.close());
        currentDialog.getHeader().add(closeButton);

        Button addReview = new Button("Add Review");
        Button deleteReview = new Button("Delete Review");
        TextArea review = new TextArea("Review");
        Select<String> scoreSelect = new Select<>();


        currentDialog.add(review,scoreSelect);
        currentDialog.getFooter().add(addReview,deleteReview);



        Response<Review> reviewResponse = session.getReview(userId,storeId,productId);
        String message;

        Response<Float> scoreResponse = session.getProductScoreUser(userId,storeId,productId,userId);
        String scoreMessage;

        scoreSelect.setLabel("Score");
        if(scoreResponse.didntSucceed()){
            currentDialog.add(scoreSelect);
        }else {
            scoreMessage = scoreResponse.getData().toString();
            scoreSelect.setReadOnly(true);
            scoreSelect.setValue(scoreMessage);
            currentDialog.add(scoreSelect);
        }

        if(reviewResponse.didntSucceed()){
            message = reviewResponse.getMessage();
            review.setReadOnly(false);
        }else {
            message = reviewResponse.getData().getReview();
            review.setReadOnly(true);
        }

        review.setLabel("The message:");
        review.setWidthFull();
        review.setMinWidth("300px");
        review.setValue(message);
        scoreSelect.setLabel("Score");
        scoreSelect.setItems("0", "1", "2", "3", "4","5");

        addReview.addClickListener(event -> {
            Response<VoidResponse> response = session.addReview(userId,review.getValue(),storeId,productId);
            if(response.didntSucceed()){
                Notification.show(response.getMessage());
            }else {
                Notification.show("Review added successfully");
                session.addAndSetProductScore(userId,storeId,productId,Integer.parseInt(scoreSelect.getValue()));
                refreshGrid();
            }

            reviewDialogContent(currentDialog);
        });
        deleteReview.addClickListener(event -> {
            Response<VoidResponse> response = session.removeReview(userId,storeId,productId);

            if(response.didntSucceed()){
                Notification.show(response.getMessage());
            }else {
                Notification.show("Review deleted successfully");
                session.removeProductScore(userId,storeId,productId);
                refreshGrid();
            }

            reviewDialogContent(currentDialog);
        });


    }



    public Grid<ReviewService> reviewGridCreator()
    {
        Grid<ReviewService> grid = new Grid<>();
        grid.addColumn(ReviewService::getUserName).setHeader("User Name");
        grid.addColumn(ReviewService::getReview).setHeader("Review");
        grid.addColumn(ReviewService::getScore).setHeader("Score");
        List<ReviewService> reviews = session.getAllReviews(userId,storeId,productId).getData();
        grid.setItems(reviews);


        return grid;
    }


    public void refreshGrid()
    {
        List<ReviewService> reviews = session.getAllReviews(userId,storeId,productId).getData();
        grid.setItems(reviews);
        //maybe grid.getDataProvider().refreshAll();
    }




}
