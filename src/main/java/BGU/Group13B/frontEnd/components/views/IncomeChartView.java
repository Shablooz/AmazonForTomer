package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.frontEnd.ResponseHandler;

import java.time.LocalDate;

public interface IncomeChartView extends ResponseHandler {
    void setChartValues(LocalDate start, LocalDate end);
    void refreshChart();
}
