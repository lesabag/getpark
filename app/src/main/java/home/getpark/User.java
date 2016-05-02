package home.getpark;

/**
 * Created by liore on 5/2/2016.
 */
public class User {
    private String name, email, password, address,apartment,parkingNumber;

    public User(String name, String email, String password, String address, String apartment, String parkingNumber ) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.apartment = apartment;
        this.parkingNumber = parkingNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public void setParkingNumber(String parkingNumber) {
        this.parkingNumber = parkingNumber;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getAddress() {
        return address;
    }

    public String getApartment() {
        return apartment;
    }

    public String getParkingNumber() {
        return parkingNumber;
    }
}
