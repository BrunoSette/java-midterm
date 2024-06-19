module com.example.midterm {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.midterm to javafx.fxml;
    exports com.example.midterm;
}