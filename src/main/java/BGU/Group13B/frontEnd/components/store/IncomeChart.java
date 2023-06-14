package BGU.Group13B.frontEnd.components.store;

import BGU.Group13B.frontEnd.components.views.IncomeChartView;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.charts.model.style.SolidColor;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class IncomeChart extends VerticalLayout {
    private final Chart chart = new Chart(ChartType.COLUMN);
    private final DatePicker startDate = new DatePicker("Start Date");
    private final DatePicker endDate = new DatePicker("End Date");

    private final HorizontalLayout dateLayout = new HorizontalLayout(startDate, endDate);

    public IncomeChart(IncomeChartView incomeChartView, double[] incomeHistory, LocalDate start, LocalDate end) {
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
            incomeChartView.setChartValues(startDate.getValue(), endDate.getValue());
        });

        endDate.addValueChangeListener(event -> {
            if(endDate.getValue().isBefore(startDate.getValue()))
                startDate.setValue(endDate.getValue());
            incomeChartView.setChartValues(startDate.getValue(), endDate.getValue());
        });

        setData(incomeHistory, start);
        add(dateLayout, chart);

        //style
        chart.getConfiguration().getTooltip().setPointFormat("<b>income: {point.y}</b>");
        chart.getConfiguration().getChart().setBackgroundColor(new SolidColor(0, 0, 0, 0)); // transparent background
        chart.getConfiguration().getTitle().getStyle().setColor(new SolidColor(255, 255, 255));
        chart.getConfiguration().getxAxis().getLabels().getStyle().setColor(new SolidColor(255, 255, 255));
        chart.getConfiguration().getyAxis().getLabels().getStyle().setColor(new SolidColor(255, 255, 255));
        chart.getConfiguration().getLegend().getItemStyle().setColor(new SolidColor(255, 255, 255));
        chart.getConfiguration().getyAxis().getTitle().getStyle().setColor(new SolidColor(255, 255, 255));
        chart.getConfiguration().getyAxis().setTitle("Income");
        chart.getConfiguration().getxAxis().setTitle("Date");
        chart.getConfiguration().getxAxis().getTitle().getStyle().setColor(new SolidColor(255, 255, 255));
        chart.getConfiguration().getyAxis().getTitle().getStyle().setColor(new SolidColor(255, 255, 255));
        chart.getConfiguration().getLegend().setEnabled(false);


    }

    public void setData(double[] incomeHistory, LocalDate start){
        DataSeries series = new DataSeries();
        String[] categories = new String[incomeHistory.length];
        for(int i = 0; i < incomeHistory.length; i++){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            series.add(new DataSeriesItem(start.plusDays(i).format(formatter), incomeHistory[i]));
            categories[i] = start.plusDays(i).format(formatter);

        }

        chart.getConfiguration().getxAxis().setCategories(categories);

        chart.getConfiguration().setSeries(series);
        chart.getConfiguration().setTitle("Total Income: " + Arrays.stream(incomeHistory).sum());
        chart.drawChart();
    }


    public LocalDate getStartDate() {
        return startDate.getValue();
    }

    public LocalDate getEndDate() {
        return endDate.getValue();
    }
}
