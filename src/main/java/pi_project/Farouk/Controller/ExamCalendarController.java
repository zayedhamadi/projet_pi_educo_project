package pi_project.Farouk.Controller;

import com.calendarfx.model.*;
import com.calendarfx.view.CalendarView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.StackPane;
import pi_project.Farouk.Models.Exam;
import pi_project.Farouk.Services.ExamService;
import pi_project.Fedi.entites.classe;
import pi_project.Fedi.services.classeservice;
import javafx.util.StringConverter;


import java.sql.SQLException;
import java.time.ZoneId;
import java.util.List;

public class ExamCalendarController {

    @FXML
    private StackPane calendarContainer;

    @FXML
    private CalendarView calendarView;

    @FXML
    private ComboBox<classe> classComboBox;

    private final ExamService examService = new ExamService();

    private final Calendar examCalendar = new Calendar("Exams");

    @FXML
    public void initialize() {
        if (calendarView == null) {
            calendarView = new CalendarView();
        }

        // Add calendar to the view
        calendarView.getCalendarSources().clear();
        CalendarSource source = new CalendarSource("Exam Source");
        source.getCalendars().add(examCalendar);
        calendarView.getCalendarSources().add(source);

        // UI Settings
        calendarView.setShowAddCalendarButton(false);
        calendarView.setShowPrintButton(true);
        calendarView.setShowPageSwitcher(true);

        //Adds the calendar component into the StackPane UI placeholder.
        calendarContainer.getChildren().clear();
        calendarContainer.getChildren().add(calendarView);

        // Load classes in ComboBox
        List<classe> classes = examService.getAll();
        classComboBox.getItems().addAll(classes);

        // Disable until class is selected
        calendarView.setDisable(true);


        // Use StringConverter to display only class names
        classComboBox.setConverter(new StringConverter<classe>() {
            @Override
            public String toString(classe classe) {
                return classe != null ? classe.getNomclasse() : "";
            }

            @Override
            public classe fromString(String string) {
                return null; // We don’t need to convert the string back to an object, as it's handled in the ComboBox selection
            }
        });

        // Handle class selection
        classComboBox.setOnAction(event -> {
            classe selectedClass = classComboBox.getSelectionModel().getSelectedItem();
            if (selectedClass != null) {
                try {
                    loadExamsByClass(selectedClass.getId());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        // Workaround to render calendar properly //refresh
        Platform.runLater(() -> {
            calendarView.setDate(java.time.LocalDate.now());
            calendarView.showWeekPage();

        });
    }

    private void loadExamsByClass(int classId) throws SQLException {
        examCalendar.clear(); // Clear previous entries
        List<Exam> exams = examService.getExamsByClass(classId);

        for (Exam exam : exams) {
            Entry<String> entry = new Entry<>(exam.getSubject());
            entry.setLocation(exam.getLocation());
            entry.setInterval(exam.getStartTime(), exam.getEndTime()); // Use LocalDateTime directly
            examCalendar.addEntry(entry);
        }

        calendarView.setDisable(false); // Enable calendar once exams loaded
    }

}
