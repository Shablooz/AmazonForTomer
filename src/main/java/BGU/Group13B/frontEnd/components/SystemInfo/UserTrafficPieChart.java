package BGU.Group13B.frontEnd.components.SystemInfo;

import BGU.Group13B.backend.System.UserTrafficRecord;
import BGU.Group13B.frontEnd.components.views.SystemInfoView;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.style.Color;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.charts.model.style.SolidColor;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.time.LocalDate;

public class UserTrafficPieChart extends VerticalLayout {

    private final Chart chart = new Chart();

    private final DatePicker startDate = new DatePicker("Start Date");
    private final DatePicker endDate = new DatePicker("End Date");

    private final HorizontalLayout dateLayout = new HorizontalLayout(startDate, endDate);


    public UserTrafficPieChart(SystemInfoView systemInfoView, UserTrafficRecord userTrafficRecord, LocalDate start, LocalDate end){
        this.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        //date range picker
        startDate.setRequired(true);
        endDate.setRequired(true);
        startDate.setRequiredIndicatorVisible(true);
        endDate.setRequiredIndicatorVisible(true);
        startDate.setValue(start);
        endDate.setValue(end);

        startDate.addValueChangeListener(event -> {
            if(startDate.getValue().isAfter(endDate.getValue()))
                endDate.setValue(startDate.getValue());
            systemInfoView.setUserTrafficChartValues(startDate.getValue(), endDate.getValue());
        });

        endDate.addValueChangeListener(event -> {
            if(endDate.getValue().isBefore(startDate.getValue()))
                startDate.setValue(endDate.getValue());
            systemInfoView.setUserTrafficChartValues(startDate.getValue(), endDate.getValue());
        });

        chart.getConfiguration().getChart().setType(ChartType.PIE);
        setData(userTrafficRecord);

        add(dateLayout, chart);

        //style
        chart.getConfiguration().getTooltip().setPointFormat("{series.name}: <b>{point.percentage:.1f}%</b>");
        chart.getConfiguration().getChart().setBackgroundColor(new SolidColor(0, 0, 0, 0)); // transparent background
        chart.getConfiguration().getTitle().getStyle().setColor(new SolidColor(255, 255, 255));
    }

    public void setData(UserTrafficRecord userTrafficRecord){
        DataSeries series = new DataSeries();
        series.add(new DataSeriesItem("Guests", userTrafficRecord.numOfGuests()));
        series.add(new DataSeriesItem("Members", userTrafficRecord.numOfRegularMembers()));
        series.add(new DataSeriesItem("Managers", userTrafficRecord.numOfStoreManagersThatAreNotOwners()));
        series.add(new DataSeriesItem("Owners", userTrafficRecord.numOfStoreOwners()));
        series.add(new DataSeriesItem("Admins", userTrafficRecord.numOfAdmins()));
        chart.getConfiguration().setSeries(series);
        chart.getConfiguration().setTitle("Number Of Visitors: " + userTrafficRecord.getTotalVisitors());
        chart.drawChart();
    }


    public LocalDate getStartDate() {
        return startDate.getValue();
    }

    public LocalDate getEndDate() {
        return endDate.getValue();
    }
}
