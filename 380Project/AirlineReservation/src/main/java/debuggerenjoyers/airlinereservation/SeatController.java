/**
 * SeatController.java
 * November 21, 2023
 * @author Diego Hernandez
 *
 * The purpose of this class is designed to act as the controller for the Seat Selection User Interface
 * It is responsible to handle user inputs and displaying the Passenger info in a table view.
 * This also leads into Purchase User Interface.
 */
package debuggerenjoyers.airlinereservation;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.SpinnerValueFactory.ListSpinnerValueFactory;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.w3c.dom.Text;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class SeatController implements Initializable {

    @FXML
    private TableView<CombinedObject> ticketTableView; // TableView for displaying ticket details
    @FXML
    private TableColumn<CombinedObject, String> passengerColumn;
    @FXML
    private TableColumn<CombinedObject, String> firstNameColumn; // Columns for ticket details
    @FXML
    private TableColumn<CombinedObject, String> lastNameColumn;
    @FXML
    private TableColumn<CombinedObject, String> dobColumn;
    @FXML
    private TableColumn<CombinedObject, Integer> checkedInBagsColumn;
    @FXML
    private TableColumn<CombinedObject, String> seatNumberColumn;


    @FXML
    private Spinner<String> ticketsSpinner;
    @FXML
    private Spinner<Integer> bagsSpinner;
    @FXML
    private TextField lastNameTxt;
    @FXML
    private TextField firstNameTxt;
    @FXML
    private TextField DOBTxt;
    @FXML
    private ComboBox<Integer> seatComboBox;
    @FXML
    private Label seatErrorMsg;

    Reservation reservation = Reservation.getInstance();

    private final ObservableList<String> optionsList = FXCollections.observableArrayList();
    private final ObservableList<Integer> seatList = FXCollections.observableArrayList(reservation.getTickets().getFirst().getFlight().getSeatList());
    private final ObservableList<Ticket> ticketsList = FXCollections.observableArrayList();


    /**
     * Configures the Tableview columns with cell value factories, initializes the Passenger Spinner,
     * bag spinner and Seat Combo Box.
     * @param url
     * @param resourceBundle
     */
    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ListSpinnerValueFactory<Integer> valueFactory = new ListSpinnerValueFactory<>(FXCollections.observableArrayList(0,1,2,3,4,5));
        bagsSpinner.setValueFactory(valueFactory);
        initList();
        DisplayTickets();

        passengerColumn.setCellValueFactory((cellData-> new SimpleStringProperty(cellData.getValue().getComboPass())));
        seatNumberColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getComboTix().getSeatNum()).asObject().asString());
        firstNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getComboTix().getPassenger().getFirstName()));
        lastNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getComboTix().getPassenger().getLastName()));
        dobColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getComboTix().getPassenger().getDOB()));
        checkedInBagsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getComboTix().getPassenger().getBags()).asObject());


    }

    /**
     * This method updates the SeatList Observable List to change open seat to taken on the seat chart.
     * @param selectedSeat
     */
    private void updateSeatList(Integer selectedSeat){
        seatList.set(selectedSeat, 1);
        String seatChart = "";
        int count = 0;

        for(Integer seat : seatList){
            seatChart = seatChart + seat.toString();
            if (count == 99){
                break;
            }
            else{
                seatChart = seatChart + ", ";
            }
            count++;
        }

        for(Ticket ticket : reservation.getTickets()){
            Flight og = ticket.getFlight();
            og.setSeatChart(seatChart);
            ticket.setFlight(og);
        }
        setSeatList();
    }

    /**
     * This Method updates the Seat ComboBox to the Observable SeatList
     */
    private void setSeatList(){
        seatComboBox.setItems(FXCollections.observableArrayList(ReservationSystem.getOpenSeats(seatList)));
        seatComboBox.getSelectionModel().clearSelection();
        seatComboBox.setPromptText("Select Seat");
    }

    /**
     * This Method updates the passenger spinner options list
     */
    private void updateOptionsList() {
        ListSpinnerValueFactory<String> valueFactory = new ListSpinnerValueFactory<>(optionsList);
        ticketsSpinner.setValueFactory(valueFactory);
    }

    /**
     * This sets the observable OptionsList to the parameter
     * @param newOptionsList
     */
    public void setOptionsList(ObservableList<String> newOptionsList) {
        optionsList.setAll(newOptionsList);
        updateOptionsList();
    }

    /**
     * Method to Verify all the needed information as been filled out
     * Leads to the Purchase Scene
     * @param event
     */
    @FXML
    private void PassengertoPurchaseButton(ActionEvent event) {
        Boolean result =reservation.checkTickets();
        if(result == false){
            seatErrorMsg.setText("Not all Passenger Info has been Filled in");
            return;
        }

        for (Ticket ticketprice : reservation.getTickets()){
            ticketprice.updatePrice();
        }
        reservation.updatePrice();


        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PurchaseUI.fxml"));
            Parent root = loader.load();

            // Create a new stage for the seat selection UI
            Stage seatStage = new Stage();
            seatStage.setTitle("Purchase");
            seatStage.setScene(new Scene(root));

            // Get the current scene and window
            Scene currentScene = ((Node) event.getSource()).getScene();
            Stage currentStage = (Stage) currentScene.getWindow();

            // Close the current window
            currentStage.close();

            // Show the new stage
            seatStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the users Input, Stores it into the relevant Ticket and updates the Table view`
     * @param event
     */
    @FXML
    private void SavePassengerButton(ActionEvent event){
        if(IncompleteInfo()){
            int selectedPassenger = Character.getNumericValue(ticketsSpinner.getValue().charAt(ticketsSpinner.getValue().length() - 1)) - 1;

            List<Ticket> tickets = reservation.getTickets();
            Ticket ticket = tickets.get(selectedPassenger);
            Passenger passenger = ticket.getPassenger();


            passenger.setFirstName(firstNameTxt.getText());
            passenger.setLastName(lastNameTxt.getText());
            passenger.setDOB(DOBTxt.getText());
            passenger.setBags(bagsSpinner.getValue());
            ticket.setSeatNum(seatComboBox.getValue());

            ticket.getFlight().setSeatsOpen(ticket.getFlight().getSeatsOpen() - 1);
            updateSeatList(seatComboBox.getValue());


            reservation.setTickets(tickets);

            DisplayTickets();


            firstNameTxt.setText("");
            lastNameTxt.setText("");
            DOBTxt.setText("");
            firstNameTxt.setPromptText("Passenger First Name");
            firstNameTxt.setStyle("");
            lastNameTxt.setPromptText("Passenger Last Name");
            lastNameTxt.setStyle("");
            DOBTxt.setPromptText("Enter DOB: yyyy-MM-dd");
            DOBTxt.setStyle("");
            seatErrorMsg.setText("");
            if(reservation.checkTickets()){
                seatErrorMsg.setText("All Passenger Info is Complete. \n Please Continue to Purchase.");
            }
        }
    }

    /**
     * This method handles the cancel reservation button
     * It clears the Reservation Singleton
     * And Starts the Home Scene
     * @param event
     */
    @FXML
    private  void CancelRes(ActionEvent event){
        reservation.clearReservation();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Parent root = loader.load();

            // Create a new stage for the seat selection UI
            Stage seatStage = new Stage();
            seatStage.setTitle("Airline Reservation");

            // Set a fixed size for the new stage
            seatStage.setWidth(1550);
            seatStage.setHeight(900);

            seatStage.setScene(new Scene(root));

            // Get the current scene and window
            Scene currentScene = ((Node) event.getSource()).getScene();
            Stage currentStage = (Stage) currentScene.getWindow();

            // Close the current window
            currentStage.close();

            // Show the new stage
            seatStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Boolean IncompleteInfo(){
        if(Objects.equals(firstNameTxt.getText(), "")){
            firstNameTxt.setPromptText("NAME MISSING!!!");
            firstNameTxt.setStyle("-fx-prompt-text-fill: red;");
            return Boolean.FALSE;
        }
        if(Objects.equals(lastNameTxt.getText(), "")){
            lastNameTxt.setPromptText("LAST NAME MISSING!!!");
            lastNameTxt.setStyle("-fx-prompt-text-fill: red;");
            return Boolean.FALSE;
        }
        if(Objects.equals(DOBTxt.getText(), "")){
            DOBTxt.setPromptText("DOB MISSING!!!");
            DOBTxt.setStyle("-fx-prompt-text-fill: red;");
            return Boolean.FALSE;
        }
        Boolean out = validDate(DOBTxt.getText());

        if(out) {
            if (Objects.equals(seatComboBox.getValue(), null)) {
                seatErrorMsg.setText("PLEASE SELECT A SEAT!");
                return Boolean.FALSE;
            }
        }

        return out;
    }

    private Boolean validDate(String date){
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate.parse(date, formatter);
            LocalDate currentDate = LocalDate.now();
            LocalDate input = LocalDate.parse(date);
            if(input.isAfter(currentDate)  || input.isEqual(currentDate)){
                seatErrorMsg.setText("DOB NOT VALID: YYYY-MM-DD");
                DOBTxt.setPromptText("Enter DOB: yyyy-MM-dd");
                DOBTxt.setStyle("");
                DOBTxt.setText("");
                return Boolean.FALSE;
            }
            return true; // Date input matches the specified format
        } catch (DateTimeParseException e) {
            seatErrorMsg.setText("DOB NOT VALID: YYYY-MM-DD");
            DOBTxt.setPromptText("Enter DOB: yyyy-MM-dd");
            DOBTxt.setStyle("");
            DOBTxt.setText("");
            return false; // Date input doesn't match the specified format
        }
    }

    /**
     * Display Tickets method creates the CombinedList Object, Populates it,
     * and sets tableview items to the Combined List Object
     */
    private void DisplayTickets(){
        ObservableList<Ticket> tickets = FXCollections.observableArrayList(reservation.getTickets());
        ObservableList<CombinedObject> combinedList = FXCollections.observableArrayList();

        for (int i = 0; i < Math.min(tickets.size(), optionsList.size()); i++) {
            combinedList.add(new CombinedObject(tickets.get(i), optionsList.get(i)));
        }
        ticketTableView.setItems(combinedList);
    }

    /**
     * initList  method is a switch case to select the correct amount of options for the passenger spinner and calls the setSeatList method.
     */
    private void initList(){
        int passengerNum = reservation.getTickets().size();
        reservation.setTripType(false);

        switch (passengerNum){
            case 1:
                setOptionsList(FXCollections.observableArrayList("Passenger 1"));
                break;
            case 2:
                setOptionsList(FXCollections.observableArrayList("Passenger 1","Passenger 2"));
                break;
            case 3:
                setOptionsList(FXCollections.observableArrayList("Passenger 1","Passenger 2","Passenger 3"));
                break;
            case 4:
                setOptionsList(FXCollections.observableArrayList("Passenger 1","Passenger 2","Passenger 3","Passenger 4"));
                break;
            case 5:
                setOptionsList(FXCollections.observableArrayList("Passenger 1","Passenger 2","Passenger 3","Passenger 4","Passenger 5"));
                break;
            case 6:
                setOptionsList(FXCollections.observableArrayList("Passenger 1","Passenger 2","Passenger 3","Passenger 4","Passenger 5","Passenger 6"));
                break;
            case 7:
                setOptionsList(FXCollections.observableArrayList("Passenger 1","Passenger 2","Passenger 3","Passenger 4","Passenger 5","Passenger 6","Passenger 7"));
                break;
            case 8:
                setOptionsList(FXCollections.observableArrayList("Passenger 1","Passenger 2","Passenger 3","Passenger 4","Passenger 5","Passenger 6","Passenger 7","Passenger 8"));
                break;
            case 9:
                setOptionsList(FXCollections.observableArrayList("Passenger 1","Passenger 2","Passenger 3","Passenger 4","Passenger 5","Passenger 6","Passenger 7","Passenger 8","Passenger 9"));
                break;
        }

        setSeatList();
    }

    /**
     * The CombinedObject class is made to package two objects into one for the tableview to access the info.
     */
    public static class CombinedObject{
        private final Ticket ticket;
        private final String passenger;

        /**
         * CombinedObject Constructor takes ticket and passengerNum as parameter input
         * @param ticket
         * @param passenger
         */
        public CombinedObject(Ticket ticket, String passenger){
            this.ticket = ticket;
            this.passenger = passenger;
        }

        /**
         * Access method for CombinedObject ticket. Returns ticket
         * @return ticket
         */
        public Ticket getComboTix(){
            return ticket;
        }

        /**
         * Access method for CombinedObject passenger. Returns String
         * @return passenger
         */
        public String getComboPass(){
            return passenger;
        }
    }

}