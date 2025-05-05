package pi_project.Farouk.Controller;


import javafx.fxml.FXML;
import javafx.scene.control.*;
import pi_project.Farouk.Models.Exam;
import pi_project.Farouk.Services.ExamService;
import javafx.event.ActionEvent;


import javafx.scene.control.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AddExamController {

    @FXML
    private TextField subjectField;

    @FXML
    private TextField locationField;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private Spinner<Integer> startHourSpinner;

    @FXML
    private Spinner<Integer> startMinuteSpinner;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Spinner<Integer> endHourSpinner;

    @FXML
    private Spinner<Integer> endMinuteSpinner;

    @FXML
    private TextField classeIdField;

    private final ExamService examService = new ExamService();

    @FXML
    public void initialize() {
        startHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 8));
        startMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        endHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 10));
        endMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
    }

    @FXML
    private void handleAddExam(ActionEvent event) {
        try {
            Exam exam = new Exam();

            exam.setSubject(subjectField.getText());
            exam.setLocation(locationField.getText());

            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            LocalTime startTime = LocalTime.of(startHourSpinner.getValue(), startMinuteSpinner.getValue());
            LocalTime endTime = LocalTime.of(endHourSpinner.getValue(), endMinuteSpinner.getValue());

            exam.setStartTime(LocalDateTime.of(startDate, startTime));
            exam.setEndTime(LocalDateTime.of(endDate, endTime));
            exam.setClasseId(Integer.parseInt(classeIdField.getText()));

            examService.addExam(exam);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Exam added successfully!");
            alert.showAndWait();

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to add exam");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
