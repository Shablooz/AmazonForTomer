package BGU.Group13B.frontEnd.components.views.viewEntity;

import BGU.Group13B.service.entity.AbstractEntity;

import java.time.Month;

public class Card extends AbstractEntity {
    private String accountNumber;
    private Month month;
    private Integer year;

    private Double cvv;

    public Card() {
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public Double getCvv() {
        return cvv;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    public void setCvv(Double cvv) {
        this.cvv = cvv;
    }

    public Month getMonth() {
        return month;
    }

    public void setMonth(Month month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
