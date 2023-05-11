package BGU.Group13B.frontEnd.components.views.viewEntity;


public class Person {

    private Address address;
    private String personId;
    private String firstName;
    private String lastName;


    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }


    public String getPersonId() {
        return personId;
    }
    public void setPersonId(String personId) {
         this.personId = personId;
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
