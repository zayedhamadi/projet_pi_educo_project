package pi_project.Aziz.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import pi_project.Aziz.Entity.Question;

import java.util.List;

public class QuestionItemController {
    @FXML private Label questionText;
    @FXML private VBox optionsContainer;

    private Question question;
    private ToggleGroup toggleGroup = new ToggleGroup();

    public void setQuestionData(Question question) {
        this.question = question;
        initializeQuestionUI();
    }

    private void initializeQuestionUI() {
        if (question == null) return;

        questionText.setText(question.getTexte());
        questionText.setStyle("-fx-font-weight: bold; -fx-font-size: 14pt;");

        addOptionsToContainer();
    }

    private void addOptionsToContainer() {
        optionsContainer.getChildren().clear();
        List<String> options = question.getOptions();

        if (options == null || options.isEmpty()) {
            Label noOptionsLabel = new Label("No options available");
            noOptionsLabel.setStyle("-fx-text-fill: #e74c3c;");
            optionsContainer.getChildren().add(noOptionsLabel);
            return;
        }

        for (String option : options) {
            RadioButton radioButton = createOptionButton(option);
            optionsContainer.getChildren().add(radioButton);
        }
    }

    private RadioButton createOptionButton(String option) {
        RadioButton radioButton = new RadioButton(option);
        radioButton.setToggleGroup(toggleGroup);
        radioButton.setUserData(option);
        radioButton.setStyle("-fx-font-size: 12pt; -fx-padding: 5 0;");
        return radioButton;
    }

    public boolean isCorrect() {
        RadioButton selected = (RadioButton) toggleGroup.getSelectedToggle();
        return selected != null && selected.getUserData().equals(question.getReponse());
    }
}