package BGU.Group13B.frontEnd.components.views.viewEntity;

import BGU.Group13B.service.entity.AbstractEntity;

public class Address extends AbstractEntity {

    private String street;
    private String zip;
    private String country;
    private String city;

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreet() {
        return street;
    }

    public String getZip() {
        return zip;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
