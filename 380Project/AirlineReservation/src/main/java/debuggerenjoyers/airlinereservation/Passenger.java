package debuggerenjoyers.airlinereservation;

public class Passenger {
    private String firstName;
    private String lastName;
    private String DOB;
    private int bags;

    public Passenger(String firstName, String lastName, String DOB, int bags){
        this.firstName = firstName;
        this.lastName = lastName;
        this.DOB = DOB;
        this.bags = bags;
    }

    public Passenger(){
        this.firstName = null;
        this.lastName = null;
        this.DOB = null;
        this.bags = -1;
    }

    public String getFirstName(){
        return this.firstName;
    }

    public String getLastName(){
        return this.lastName;
    }

    public String getDOB(){
        return this.DOB;
    }

    public int getBags() {
        return bags;
    }
}