package pi_project.Farouk.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import pi_project.Farouk.Models.Exam;
import pi_project.Farouk.Services.ExamService;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public class ViewExamController {

    private ExamService examService;

    // FXML components
    @FXML private GridPane calendarGrid;
    @FXML private Label monthLabel;

    // Constructor
    public ViewExamController() {
        this.examService = new ExamService(); // Initialize the service to fetch exams
    }

    // Method to load the exam view and populate the calendar
    public void loadExamData() {
        // Set the date for the month you want to display (e.g., May 2025)
        LocalDate currentMonth = LocalDate.of(2025, 5, 1);

        // Fetch exams from the database
        List<Exam> exams = null;
        try {
            exams = examService.getAllExams(); // Fetch exams from the database
        } catch (SQLException e) {
            e.printStackTrace();  // Handle exception (maybe show an error message in a real app)
        }

        // Title for the month
        monthLabel.setText(currentMonth.getMonth().toString() + " " + currentMonth.getYear());

        // Add the weekdays
        String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < 7; i++) {
            Label dayLabel = new Label(daysOfWeek[i]);
            dayLabel.setFont(new Font(15));
            calendarGrid.add(dayLabel, i, 1);
        }

        // Get the first day of the month
        DayOfWeek firstDayOfMonth = currentMonth.getDayOfWeek();
        int firstDayIndex = firstDayOfMonth.getValue() % 7;  // Convert to Sunday = 0, Monday = 1, etc.

        // Add empty cells for the days before the first day of the month
        int dayCounter = 1;
        for (int i = 2; i < 7; i++) {  // Start from row 2
            for (int j = firstDayIndex; j < 7; j++) {
                if (dayCounter <= currentMonth.lengthOfMonth()) {
                    Label dayLabel = new Label(String.valueOf(dayCounter));
                    dayLabel.setFont(new Font(14));
                    calendarGrid.add(dayLabel, j, i);

                    // Show exams for this day
                    LocalDate currentDay = LocalDate.of(currentMonth.getYear(), currentMonth.getMonth(), dayCounter);
                    for (Exam exam : exams) {
                        if (exam.occursOn(currentDay)) {
                            Label examLabel = new Label(exam.getSubject() + " " + exam.getStartTime());
                            examLabel.setStyle("-fx-background-color: lightblue; -fx-padding: 5;");
                            calendarGrid.add(examLabel, j, i + 1); // Add exam label below the day
                        }
                    }
                    dayCounter++;
                }
            }
        }
    }
}
