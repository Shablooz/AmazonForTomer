package BGU.Group13B.frontEnd.components.views.viewEntity;


public class Person {

    private Address address;
    private String id;
    private String firstName;
    private String lastName;


    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }


    public String getId() {
        return id;
    }
    public String setId(String id) {
        return this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
