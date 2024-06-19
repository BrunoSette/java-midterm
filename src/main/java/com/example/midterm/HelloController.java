package com.example.midterm;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;

public class HelloController {

    @FXML
    private TextField patientIDField;
    @FXML
    private TextField symptomsField;
    @FXML
    private TextField diagnosisField;
    @FXML
    private TextField medicinesField;
    @FXML
    private CheckBox wardRequiredCheckBox;
    @FXML
    private TableView<Diagnosis> tableView;
    @FXML
    private TableColumn<Diagnosis, String> column1;
    @FXML
    private TableColumn<Diagnosis, String> column2;
    @FXML
    private TableColumn<Diagnosis, String> column3;
    @FXML
    private TableColumn<Diagnosis, String> column4;

    private Connection connection;

    @FXML
    public void initialize() {
        // Initialize the database connection
        connectToDatabase();

        // Set up the table columns
        column1.setCellValueFactory(cellData -> cellData.getValue().patientIDProperty());
        column2.setCellValueFactory(cellData -> cellData.getValue().symptomsProperty());
        column3.setCellValueFactory(cellData -> cellData.getValue().diagnosisProperty());
        column4.setCellValueFactory(cellData -> cellData.getValue().medicinesProperty());
    }

    @FXML
    protected void onSaveButtonClick() {
        addDiagnosis(
                patientIDField.getText(),
                symptomsField.getText(),
                diagnosisField.getText(),
                medicinesField.getText(),
                wardRequiredCheckBox.isSelected()
        );
    }

    @FXML
    protected void onSearchButtonClick() {
        searchDiagnosis();
    }

    @FXML
    protected void onCloseButtonClick() {
        Stage stage = (Stage) patientIDField.getScene().getWindow();
        stage.close();
    }

    private void connectToDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/DiagnosisDB";
            String user = "root";
            String password = "root";
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Database connection established successfully.");
        } catch (SQLException e) {
            System.err.println("Failed to establish database connection.");
            e.printStackTrace();
        }
    }

    private void addDiagnosis(String patientID, String symptoms, String diagnosis, String medicines, boolean wardRequired) {
        String sql = "INSERT INTO Diagnosis (patientID, symptoms, diagnosis, medicines, wardRequired) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, patientID);
            pstmt.setString(2, symptoms);
            pstmt.setString(3, diagnosis);
            pstmt.setString(4, medicines);
            pstmt.setBoolean(5, wardRequired);
            pstmt.executeUpdate();
            System.out.println("Diagnosis added successfully.");
        } catch (SQLException e) {
            System.err.println("Failed to add diagnosis.");
            e.printStackTrace();
        }
    }

    private void searchDiagnosis() {
        ObservableList<Diagnosis> data = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Diagnosis";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Diagnosis diagnosis = new Diagnosis(
                        rs.getString("patientID"),
                        rs.getString("symptoms"),
                        rs.getString("diagnosis"),
                        rs.getString("medicines"),
                        rs.getBoolean("wardRequired")
                );
                data.add(diagnosis);
            }
            tableView.setItems(data);
            System.out.println("Diagnosis search completed successfully.");
        } catch (SQLException e) {
            System.err.println("Failed to search diagnosis.");
            e.printStackTrace();
        }
    }

    public static class Diagnosis {
        private final StringProperty patientID;
        private final StringProperty symptoms;
        private final StringProperty diagnosis;
        private final StringProperty medicines;
        private final BooleanProperty wardRequired;

        public Diagnosis(String patientID, String symptoms, String diagnosis, String medicines, boolean wardRequired) {
            this.patientID = new SimpleStringProperty(patientID);
            this.symptoms = new SimpleStringProperty(symptoms);
            this.diagnosis = new SimpleStringProperty(diagnosis);
            this.medicines = new SimpleStringProperty(medicines);
            this.wardRequired = new SimpleBooleanProperty(wardRequired);
        }

        public StringProperty patientIDProperty() {
            return patientID;
        }

        public StringProperty symptomsProperty() {
            return symptoms;
        }

        public StringProperty diagnosisProperty() {
            return diagnosis;
        }

        public StringProperty medicinesProperty() {
            return medicines;
        }

        public BooleanProperty wardRequiredProperty() {
            return wardRequired;
        }
    }
}
