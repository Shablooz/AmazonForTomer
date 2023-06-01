package BGU.Group13B.frontEnd.components.policyComponent;

import BGU.Group13B.frontEnd.ResponseHandler;
import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.ConditionEntity;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.LogicalConditions.LogicalConditionEntity;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.PlusConditionEntity;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.leaves.*;
import BGU.Group13B.service.Session;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import org.jsoup.Connection;

import java.util.List;


public class ConditionTreeGrid extends TreeGrid<ConditionEntity> implements ResponseHandler {
    private final TreeData<ConditionEntity> conditionsData = new TreeData<>();
    private final Session session;
    private final int storeId;
    private final int userId;

    public ConditionTreeGrid(Session session, int storeId) {
        this.userId = SessionToIdMapper.getInstance().getCurrentSessionId();
        this.session = session;
        this.storeId = storeId;
        this.setDataProvider(new TreeDataProvider<>(conditionsData));
        this.addComponentHierarchyColumn(conditionEntity -> {
            VerticalLayout layout = new VerticalLayout();
            Button plusBtn = new Button(new Icon(VaadinIcon.PLUS));
            Div text = new Div(new Text(conditionEntity.toString()));
            layout.add(plusBtn, text);
            if (conditionEntity instanceof PlusConditionEntity) {
                //show only the plus btn
                text.setVisible(false);
                plusBtn.setVisible(true);
            } else {
                //show only the text field
                text.setVisible(true);
                plusBtn.setVisible(false);
            }

            //style
            plusBtn.addThemeVariants(ButtonVariant.LUMO_ICON);
            plusBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);

            //actions
            ContextMenu contextMenu = new ContextMenu();
            contextMenu.setTarget(plusBtn);
            contextMenu.setOpenOnClick(true);

            contextMenu.addItem("AND", e -> {
                conditionsData.removeItem(conditionEntity);
                addAND(conditionEntity.getParent());

            });
            contextMenu.addItem("OR", e -> {
                conditionsData.removeItem(conditionEntity);
                addOR(conditionEntity.getParent());

            });
            contextMenu.addItem("XOR", e -> {
                conditionsData.removeItem(conditionEntity);
                addXOR(conditionEntity.getParent());

            });
            contextMenu.addItem("IMPLY", e -> {
                conditionsData.removeItem(conditionEntity);//magic
                addIMPLY(conditionEntity.getParent());

            });

            //add divider line to context menu
            contextMenu.add(new Hr()); //nodered
            //add leaves
            contextMenu.addItem("Age", e -> {
                new ConditionDialog(this, new UserAgeConditionEntity(conditionEntity.getParent()), session, storeId, conditionEntity).open();

            });
            contextMenu.addItem("Time", e -> {
                new ConditionDialog(this, new TimeConditionEntity(conditionEntity.getParent()), session, storeId, conditionEntity).open();

            });
            contextMenu.addItem("Date", e -> {
                new ConditionDialog(this, new DateConditionEntity(conditionEntity.getParent()), session, storeId, conditionEntity).open();

            });
            contextMenu.addItem("Total price", e -> {
                new ConditionDialog(this, new StorePriceConditionEntity(conditionEntity.getParent()), session, storeId, conditionEntity).open();

            });
            contextMenu.addItem("Category price", e -> {
                new ConditionDialog(this, new CategoryPriceConditionEntity(conditionEntity.getParent()), session, storeId, conditionEntity).open();

            });
            contextMenu.addItem("Product price", e -> {
                new ConditionDialog(this, new ProductPriceConditionEntity(conditionEntity.getParent()), session, storeId, conditionEntity).open();

            });
            contextMenu.addItem("Total quantity", e -> {
                new ConditionDialog(this, new StoreQuantityConditionEntity(conditionEntity.getParent()), session, storeId, conditionEntity).open();

            });
            contextMenu.addItem("Category quantity", e -> {
                new ConditionDialog(this, new CategoryQuantityConditionEntity(conditionEntity.getParent()), session, storeId, conditionEntity).open();

            });
            contextMenu.addItem("Product quantity", e -> {
                new ConditionDialog(this, new ProductQuantityConditionEntity(conditionEntity.getParent()), session, storeId, conditionEntity).open();

            });

            layout.setSpacing(false);
            layout.setPadding(false);
            layout.setMargin(false);

            return layout;


        });

        //add plus at the start
        conditionsData.addItem(null, new PlusConditionEntity(null));

        //style
        setAllRowsVisible(true);
        addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
        addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);


    }

    public void addLogicalCondition(LogicalConditionEntity parent, LogicalConditionEntity.LogicalOperator operator) {
        //no dialog
        LogicalConditionEntity op = new LogicalConditionEntity(parent, operator);

        conditionsData.addItem(parent, op);
        conditionsData.addItem(op, new PlusConditionEntity(op));
        conditionsData.addItem(op, new PlusConditionEntity(op));
        this.getDataProvider().refreshAll();  // Refresh the UI

    }

    public void addAND(LogicalConditionEntity parent) {
        addLogicalCondition(parent, LogicalConditionEntity.LogicalOperator.AND);
    }

    public void addOR(LogicalConditionEntity parent) {
        addLogicalCondition(parent, LogicalConditionEntity.LogicalOperator.OR);
    }

    public void addXOR(LogicalConditionEntity parent) {
        addLogicalCondition(parent, LogicalConditionEntity.LogicalOperator.XOR);
    }

    public void addIMPLY(LogicalConditionEntity parent) {
        addLogicalCondition(parent, LogicalConditionEntity.LogicalOperator.IMPLIES);
    }

    public void addLeafCondition(ConditionEntity conditionEntityLeaf, ConditionEntity plusToDelete) {
        conditionsData.removeItem(plusToDelete);
        conditionsData.addItem(conditionEntityLeaf.getParent(), conditionEntityLeaf);
        this.getDataProvider().refreshAll();  // Refresh the UI
    }


    public void reset() {
        conditionsData.clear();
        conditionsData.addItem(null, new PlusConditionEntity(null));
        this.getDataProvider().refreshAll();  // Refresh the UI
    }

    public int confirmCondition() {
        // Get the root of your tree
        ConditionEntity root = conditionsData.getRootItems().iterator().next();

        // Traverse and process the tree starting from root
        try{
            return processCondition(root);
        }catch (Exception ignore){
            return -1;
        }
    }

    private int processCondition(ConditionEntity entity) {
        if (entity instanceof LogicalConditionEntity logicalConditionEntity) {
            // If it's a logical condition, process its children according to the operator
            LogicalConditionEntity.LogicalOperator operator = logicalConditionEntity.getOperator();
            List<ConditionEntity> children = conditionsData.getChildren(logicalConditionEntity);

            // Ensure that there are two children for the logical operators
            if (children.size() != 2) {
                throw new IllegalArgumentException("Logical condition must have exactly two children");
            }

            // Process the children
            int firstChildResult = processCondition(children.get(0));
            int secondChildResult = processCondition(children.get(1));

            return switch (operator) {
                case AND -> handleOptionalResponse(session.addANDCondition(storeId, userId, firstChildResult, secondChildResult)).orElseThrow();
                case OR -> handleOptionalResponse(session.addORCondition(storeId, userId, firstChildResult, secondChildResult)).orElseThrow();
                case XOR -> handleOptionalResponse(session.addXORCondition(storeId, userId, firstChildResult, secondChildResult)).orElseThrow();
                case IMPLIES -> handleOptionalResponse(session.addIMPLYCondition(storeId, userId, firstChildResult, secondChildResult)).orElseThrow();
            };

        } else {
            if(!(entity instanceof LeafConditionEntity leafCondition)){
                //plus btn -> the user didn't input a valid condition
                notifyWarning("The condition isn't valid, please resolve all plus signs");
                throw new IllegalArgumentException("Condition must be either logical or leaf");
            }

            // If it's a leaf condition, process it
            return handleOptionalResponse(leafCondition.addToBackend(session, storeId, userId)).orElseThrow();
        }
    }


}
