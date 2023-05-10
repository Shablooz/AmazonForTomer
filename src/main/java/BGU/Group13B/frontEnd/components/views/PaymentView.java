package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.frontEnd.components.views.viewEntity.Address;
import BGU.Group13B.frontEnd.components.views.viewEntity.Card;
import BGU.Group13B.frontEnd.components.views.viewEntity.Country;
import BGU.Group13B.frontEnd.components.views.viewEntity.Person;
import BGU.Group13B.service.Response;
import BGU.Group13B.service.Session;
import BGU.Group13B.service.VoidResponse;
import BGU.Group13B.service.entity.ServiceProduct;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY_INLINE;


@Route(value = "payment", layout = MainLayout.class)
public class PaymentView extends Div implements BeforeLeaveObserver {

    private static final String PAYMENT = "Payment";
    private static final String BILLING_ADDRESS = "Billing address";
    private static final String CUSTOMER_DETAILS = "Customer details";
    private double totalPriceAfterDiscount;
    private ComboBox<Month> monthComboBox;
    private ComboBox<Integer> yearComboBox;
    private NumberField cvv;
    private TextField accountNumber;
    private TextField zipCode;
    private TextField city;
    private ComboBox<Country> countries;
    private TextField address;
    private TextField firstName;
    private TextField lastName;
    private TextField id;
    private Binder<Person> personBinder;
    private Binder<Card> cardBinder;
    private final Accordion accordion;
    private FormLayout customerDetailsFormLayout;
    private AccordionPanel customDetailsPanel;
    private FormLayout billingAddressFormLayout;
    private AccordionPanel billingAddressPanel;
    private FormLayout paymentFormLayout;
    private AccordionPanel paymentPanel;
    private Component combinedDateView;
    private final Session session;
    private boolean paymentSuccessful;

    public PaymentView(Session session) {
        this.session = session;
        paymentSuccessful = false;
        double totalPriceBeforeDiscount = session.getTotalPriceOfCart(SessionToIdMapper.getInstance().getCurrentSessionId());
        //coupon code in the future
        add(getPricesLayout(session, totalPriceBeforeDiscount));

        List<ServiceProduct> failedProducts = session.getAllFailedProductsAfterPayment(SessionToIdMapper.getInstance().getCurrentSessionId());
        if (failedProducts.size() > 0) {
            add(paymentFailedView(failedProducts, totalPriceAfterDiscount));
        }

        accordion = new Accordion();


        initBinders();
        initForLayoutsForAccordionPanels();

        // Customer details fields
        initCustomerDetailsFields();


        initCustomerFormLayout();

        // Billing address fields
        initBillingFields();

        bindAddressFields();
        initCountries();

        billingAddressFormLayout.add(address, zipCode, city, countries);

        initBillingAddressPanel();

        // Payment fields
        initPaymentFields();

        // Payment form layout

        bindAndInitPaymentPanel();


        add(accordion);


    }

    private HorizontalLayout getPricesLayout(Session session, double totalPriceBeforeDiscount) {
        totalPriceAfterDiscount = session.startPurchaseBasketTransaction(SessionToIdMapper.getInstance().getCurrentSessionId(), new HashMap<>(), "");
        Span spanBeforeDiscountTitle = new Span("Total price before discount: ");
        Span spanBeforeDiscount = new Span(String.valueOf(totalPriceBeforeDiscount));
        spanBeforeDiscount.getStyle().set("text-decoration", "line-through");

        Span spanAfterDiscount = new Span("Total price after discount: " + totalPriceAfterDiscount);
        spanAfterDiscount.getStyle().set("font-weight", "bold");
        return new HorizontalLayout(spanBeforeDiscountTitle, spanBeforeDiscount, spanAfterDiscount);
    }

    private void bindAddressFields() {
        bindPersonAddressField(personBinder, address, Address::getStreet, Address::setStreet);
        bindPersonAddressField(personBinder, zipCode, Address::getZip, Address::setZip);
        bindPersonAddressField(personBinder, city, Address::getCity, Address::setCity);
    }

    private Component paymentFailedView(List<ServiceProduct> failedProducts, double totalPriceAfterDiscount) {


        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Some of your items are out of stock");
        dialog.setText("Do you want to proceed the purchase anyway?");
        VerticalLayout dialogFailedProducts = new VerticalLayout();

        dialogFailedProducts.setSpacing(false);
        dialogFailedProducts.add(new Text("Total price after discount: " + totalPriceAfterDiscount));

        for (ServiceProduct product : failedProducts) {
            String productName = product.getName();
            Span productNameSpan = new Span(productName);
            productNameSpan.getElement().getStyle().set("list-style-type", "circle");
            dialogFailedProducts.add(productNameSpan);
        }
        dialog.add(dialogFailedProducts);
        dialog.setCancelable(true);
        dialog.addCancelListener(event -> {
            session.cancelPurchase(SessionToIdMapper.getInstance().getCurrentSessionId());
            UI.getCurrent().navigate("");
        });


        dialog.setConfirmText("Proceed anyway");
        dialog.addConfirmListener(event -> dialog.close());


        dialog.open();
        return dialog;

    }

    private void bindAndInitPaymentPanel() {
        cardBinder.forField(accountNumber).bind("accountNumber");
        cardBinder.forField(monthComboBox).bind("month");
        cardBinder.forField(yearComboBox).bind("year");
        cardBinder.forField(cvv).bind("cvv");
        paymentFormLayout.add(accountNumber, cvv, combinedDateView);

        paymentPanel.addOpenedChangeListener(e -> {
            if (e.isOpened()) {
                paymentPanel.setSummaryText(PAYMENT);
            } else if (cardBinder.getBean() != null) {
                Card cardValues = cardBinder.getBean();
                if (cardValues.getAccountNumber() == null || cardValues.getMonth() == null ||
                        cardValues.getYear() == null || cardValues.getCvv() == null) {
                    //createReportError("Please fill all the fields").open();
                    paymentPanel.setOpened(false);
                    return;
                }
                paymentPanel.setSummary(
                        createSummary(PAYMENT,
                                "Credit Card Number: " + cardValues.getAccountNumber(),
                                "Credit Card Expiration Date " + cardValues.getMonth().getValue() + "/" + cardValues.getYear(),
                                "Credit Card CVV: " + cardValues.getCvv().intValue()));
            }
        });

        Button paymentButton = new Button("Finish and pay", e -> {
            paymentPanel.setOpened(false);
            if (theFieldNonNull(cardBinder.getBean())) {
                createReportError("Please fill all the fields").open();
                paymentPanel.setOpened(false);
                return;
            }
            Response<VoidResponse> response = session.purchaseProductCart(SessionToIdMapper.getInstance().getCurrentSessionId(), cardBinder.getBean().getAccountNumber(),
                    "" + cardBinder.getBean().getMonth().getValue(),
                    "" + cardBinder.getBean().getYear(),
                    personBinder.getBean().getFirstName() + " " + personBinder.getBean().getLastName(),
                    "" + cardBinder.getBean().getCvv(),
                    personBinder.getBean().getPersonId());
            if (response.getStatus() == Response.Status.FAILURE) {
                Notification.show("Payment failed you can try again or cancel the purchase");
                paymentPanel.setOpened(true);
            } else {
                createSubmitSuccess().open();
                //success dialog
                paymentSuccessful = true;
                UI.getCurrent().navigate("");
            }

        });
        Button cancelButton = new Button("Cancel", e -> {
            Notification.show("Purchase canceled successfully");
            UI.getCurrent().navigate("");
        });
        paymentButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        paymentPanel.addContent(paymentButton);
        paymentPanel.addContent(cancelButton);
    }

    private boolean theFieldNonNull(Card cardValues) {
        return cardValues.getMonth() == null || cardValues.getYear() == null ||
                cardValues.getCvv() == null || cardValues.getAccountNumber() == null ||
                cardValues.getCvv() == 0.0 || cardValues.getAccountNumber().isEmpty() ||
                personBinder.getBean().getFirstName() == null || personBinder.getBean().getLastName() == null ||
                personBinder.getBean().getPersonId() == null || personBinder.getBean().getFirstName().isEmpty() ||
                personBinder.getBean().getLastName().isEmpty() || personBinder.getBean().getPersonId().isEmpty() ||
                personBinder.getBean().getAddress() == null || personBinder.getBean().getAddress().getCity().isEmpty() ||
                personBinder.getBean().getAddress().getStreet().isEmpty() || personBinder.getBean().getAddress().getZip().isEmpty();
    }

    private Button createCloseBtn(Notification notification) {
        Button closeBtn = new Button(VaadinIcon.CLOSE_SMALL.create(),
                clickEvent -> notification.close());
        closeBtn.addThemeVariants(LUMO_TERTIARY_INLINE);

        return closeBtn;
    }

    private Notification createSubmitSuccess() {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        Icon icon = VaadinIcon.CHECK_CIRCLE.create();
        Div info = new Div(new Text("Payment successful!"));

        Button viewBtn = new Button("View", clickEvent -> Notification.show("Not implemented yet"));
        viewBtn.getStyle().set("margin", "0 0 0 var(--lumo-space-l)");

        HorizontalLayout layout = new HorizontalLayout(icon, info, viewBtn,
                createCloseBtn(notification));
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(layout);

        return notification;
    }

    private Notification createReportError(String msg) {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

        Icon icon = VaadinIcon.WARNING.create();
        Div info = new Div(new Text(msg));

/*        Button retryBtn = new Button("Retry",
                clickEvent -> notification.close());
        retryBtn.getStyle().set("margin", "0 0 0 var(--lumo-space-l)");*/

        HorizontalLayout layout = new HorizontalLayout(icon, info,
                createCloseBtn(notification));
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(layout);

        return notification;
    }

    @SuppressWarnings("unchecked")
    private void initPaymentFields() {
        accountNumber = new TextField("Card number");
        accountNumber.addValueChangeListener(event -> {
            String value = event.getValue();
            if (value != null && !value.isEmpty()) {
                value = value.replaceAll("-", "");
                value = value.replaceAll("\\s+", "");
                StringBuilder sb = new StringBuilder(value);
                for (int i = 4; i < sb.length(); i += 5) {
                    sb.insert(i, "-");
                }
                accountNumber.setValue(sb.toString());
            }
        });
        accountNumber.setReadOnly(false);
        List<Component> componentList = DatePickerIndividualInputFields();
        combinedDateView = componentList.get(0);
        monthComboBox = (ComboBox<Month>) componentList.get(1);
        monthComboBox.setReadOnly(false);
        yearComboBox = (ComboBox<Integer>) componentList.get(2);
        yearComboBox.setReadOnly(false);
        cvv = new NumberField("CVV");
        cvv.setReadOnly(false);
    }

    private void initBillingAddressPanel() {
        billingAddressPanel.addOpenedChangeListener(e -> {
            if (e.isOpened()) {
                billingAddressPanel.setSummaryText(BILLING_ADDRESS);
            } else if (personBinder.getBean().getAddress() != null) {
                Address addressValues = personBinder.getBean().getAddress();
                billingAddressPanel.setSummary(createSummary(BILLING_ADDRESS,
                        "Street: " + addressValues.getStreet(),
                        "Zip: " + addressValues.getZip(),
                        "City: " + addressValues.getCity(),
                        "Country: " + addressValues.getCountry()));
            }
        });

        Button billingAddressButton = new Button("Continue",
                (e) -> paymentPanel.setOpened(true));
        billingAddressButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        billingAddressPanel.addContent(billingAddressButton);
    }

    private void initCountries() {
        countries.setItems(Country.countriesList);
        countries.setItemLabelGenerator(Country::getName);
        personBinder.forField(countries).bind(person -> {
            if (person.getAddress() != null) {

                return new Country(person.getAddress().getCountry());
            }
            return null;
        }, (person, value) -> {
            if (person.getAddress() == null) {
                person.setAddress(new Address());
            }
            person.getAddress().setCountry(value.getName());
        });
    }

    private void bindPersonAddressField(Binder<Person> personBinder, HasValue<?, String> field, Function<Address, String> getter, BiConsumer<Address, String> setter) {
        personBinder.forField(field).bind(person -> {
            if (person.getAddress() != null) {
                return getter.apply(person.getAddress());
            }
            return "";
        }, (person, value) -> {
            if (person.getAddress() == null) {
                person.setAddress(new Address());
            }
            setter.accept(person.getAddress(), value);
        });
    }

    private void initBillingFields() {
        address = new TextField("Address");
        zipCode = new TextField("ZIP code");
        city = new TextField("City");
        countries = new ComboBox<>("Country");

    }

    private void initCustomerFormLayout() {
        customerDetailsFormLayout.add(firstName, lastName, id);

        customDetailsPanel.addOpenedChangeListener(e -> {
            if (e.isOpened()) {
                customDetailsPanel.setSummaryText(CUSTOMER_DETAILS);
            } else if (personBinder.getBean() != null) {
                Person personValues = personBinder.getBean();
                customDetailsPanel.setSummary(createSummary(CUSTOMER_DETAILS,
                        !personValues.getFirstName().isEmpty() ? "First Name: " + personValues.getFirstName() : "",
                        !personValues.getFirstName().isEmpty() ? "Last Name: " + personValues.getLastName() : "",
                        !personValues.getPersonId().isEmpty() ? "Personal ID: " + personValues.getPersonId() : ""));
            }
        });

        Button customDetailsButton = new Button("Continue",
                (e) -> billingAddressPanel.setOpened(true));
        customDetailsButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        customDetailsPanel.addContent(customDetailsButton);
    }

    private void initCustomerDetailsFields() {
        firstName = new TextField("First name");
        personBinder.forField(firstName).bind("firstName");

        lastName = new TextField("Last name");
        personBinder.forField(lastName).bind("lastName");

        id = new TextField("ID");
        personBinder.forField(id).bind("personId");
        id.setReadOnly(false);
    }

    private void initBinders() {
        personBinder = new Binder<>(Person.class);
        personBinder.setBean(new Person());

        cardBinder = new Binder<>(Card.class);
        cardBinder.setBean(new Card());
    }

    private void initForLayoutsForAccordionPanels() {
        //Creating form layouts for each accordion panel
        customerDetailsFormLayout = createFormLayout();
        customDetailsPanel = accordion.add(CUSTOMER_DETAILS, customerDetailsFormLayout);
        billingAddressFormLayout = createFormLayout();
        billingAddressPanel = accordion.add(BILLING_ADDRESS, billingAddressFormLayout);
        paymentFormLayout = createFormLayout();
        paymentPanel = accordion.add(PAYMENT, paymentFormLayout);
    }

    private FormLayout createFormLayout() {
        FormLayout billingAddressFormLayout = new FormLayout();
        billingAddressFormLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("20em", 2));
        return billingAddressFormLayout;
    }

    private VerticalLayout createSummary(String title, String... details) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(false);
        layout.setPadding(false);

        layout.add(title);

        if (details.length > 0) {
            VerticalLayout detailsLayout = new VerticalLayout();
            detailsLayout.setSpacing(false);
            detailsLayout.setPadding(false);
            detailsLayout.getStyle().set("font-size",
                    "var(--lumo-font-size-s)");

            for (String detail : details) {
                if (detail != null && !detail.isEmpty()) {
                    detailsLayout.add(new Span(detail));
                }
            }

            layout.add(detailsLayout);
        }

        return layout;
    }

    private List<Component> DatePickerIndividualInputFields() {
        final ComboBox<Month> monthPicker = new ComboBox<>("Month", Month.values());

        ComboBox<Integer> yearPicker;
        LocalDate now = LocalDate.now(ZoneId.systemDefault());

        List<Integer> selectableYears = IntStream
                .range(now.getYear(), now.getYear() + 1 + 12).boxed()
                .collect(Collectors.toList());

        yearPicker = new ComboBox<>("Year", selectableYears);
        yearPicker.setWidth(6, Unit.EM);
        yearPicker.addValueChangeListener(e -> {
            updateMonthPicker(yearPicker, monthPicker);
        });

        monthPicker.setItemLabelGenerator(
                m -> m.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " - " + m.getValue());
        monthPicker.setWidth(11, Unit.EM);
        monthPicker.setEnabled(false);


        return List.of(new HorizontalLayout(yearPicker, monthPicker),
                monthPicker,
                yearPicker);
    }


    private void updateMonthPicker(ComboBox<Integer> yearPicker,
                                   ComboBox<Month> monthPicker) {
        if (yearPicker.getValue() == null) {
            monthPicker.setValue(null);
            monthPicker.setEnabled(false);
            return;
        }

        monthPicker.setValue(null);
        monthPicker.setEnabled(true);
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent) {
        if (!paymentSuccessful) {
            session.cancelPurchase(SessionToIdMapper.getInstance().getCurrentSessionId());
            Notification.show("Payment cancelled");
        }
    }
}
