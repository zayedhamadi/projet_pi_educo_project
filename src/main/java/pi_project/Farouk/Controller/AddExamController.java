package pi_project.Farouk.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pi_project.Farouk.Models.Exam;
import pi_project.Farouk.Services.ExamService;
import pi_project.Fedi.entites.classe;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AddExamController {

    @FXML private ComboBox<classe> classComboBox;
    @FXML private TextField subjectField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField locationField;

    private final ExamService examService = new ExamService();

    public void initialize() {
        try {
            // Load classes into the ComboBox
            List<classe> classes = examService.getAllClasses();  // New method you fixed
            ObservableList<classe> classList = FXCollections.observableArrayList(classes);
            classComboBox.setItems(classList);
            // Display only the class name in the ComboBox
            classComboBox.setCellFactory(param -> new ListCell<classe>() {
                @Override
                protected void updateItem(classe item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getNomclasse());  // Display only the class name
                    }
                }
            });

            // Show the selected class name when an item is selected
            classComboBox.setButtonCell(new ListCell<classe>() {
                @Override
                protected void updateItem(classe item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getNomclasse());  // Display only the class name
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    @FXML
//    public void handleSaveExam() {
//        try {
//            // Get selected class ID (You would need to load this from the database)
//            int classId = classComboBox.getSelectionModel().getSelectedItem();
//
//            String subject = subjectField.getText();
//            LocalDate startDate = startDatePicker.getValue();
//            LocalDate endDate = endDatePicker.getValue();
//            String location = locationField.getText();
//
//            // Convert LocalDate to LocalDateTime (for simplicity, use start of the day)
//            LocalDateTime startTime = startDate.atStartOfDay();
//            LocalDateTime endTime = endDate.atStartOfDay();
//
//            Exam exam = new Exam();
//            exam.setClasseId(classId);
//            exam.setSubject(subject);
//            exam.setStartTime(startTime);
//            exam.setEndTime(endTime);
//            exam.setLocation(location);
//
//            examService.addExam(exam);
//            // You can add a success alert here
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
@FXML
public void handleSaveExam() {
    try {
        classe selectedClass = classComboBox.getSelectionModel().getSelectedItem();
        if (selectedClass == null) {
            System.out.println("No class selected!");
            return;
        }

        int classId = selectedClass.getId(); // getId() from classe

        String subject = subjectField.getText();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String location = locationField.getText();

        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atStartOfDay();

        Exam exam = new Exam();
        exam.setClasseId(classId);
        exam.setSubject(subject);
        exam.setStartTime(startTime);
        exam.setEndTime(endTime);
        exam.setLocation(location);

        examService.addExam(exam);
        System.out.println("Exam added successfully!");

        // Optional: Clear fields after saving
        subjectField.clear();
        locationField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        classComboBox.getSelectionModel().clearSelection();

    } catch (Exception e) {
        e.printStackTrace();
    }
}
}
