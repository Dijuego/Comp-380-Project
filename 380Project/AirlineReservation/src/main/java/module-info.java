module debuggerenjoyers.airlinereservation {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens debuggerenjoyers.airlinereservation to javafx.fxml, com.google.gson;
    exports debuggerenjoyers.airlinereservation;
}