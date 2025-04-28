package pi_project.Aziz.Service;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import pi_project.Aziz.Entity.Quiz;
import pi_project.Aziz.Entity.Question;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class QuizGeneratorService {
    private final GeminiService geminiService;
    private static final Pattern JSON_MARKDOWN_PATTERN = Pattern.compile("^```json\\s*(.*?)\\s*```$", Pattern.DOTALL);

    public QuizGeneratorService(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    public QuizData generateQuizFromPdf(String pdfPath) throws QuizGenerationException {
        try {
            // 1. Extract text
            String text = PdfTextExtractor.extractText(pdfPath);
            if (text == null || text.trim().isEmpty()) {
                throw new QuizGenerationException("Failed to extract text from PDF");
            }

            // 2. Create prompt
            String prompt = createQuizPrompt(text);

            // 3. Get response
            String jsonResponse = geminiService.getResponse(prompt);
            if (jsonResponse == null || jsonResponse.trim().isEmpty()) {
                throw new QuizGenerationException("Empty response from Gemini API");
            }

            // 4. Clean and parse response
            return parseQuiz(jsonResponse);
        } catch (Exception e) {
            throw new QuizGenerationException("Quiz generation failed: " + e.getMessage(), e);
        }
    }

    private String createQuizPrompt(String text) {
        return """
            Generate a quiz in French as JSON with:
            - 5 multiple-choice questions
            - 4 options each (a, b, c, d)
            - Correct answer marked
            - Format:
            {
                "quizTitle": "Titre du Quiz",
                "questions": [
                    {
                        "texte": "Question...",
                        "options": ["a) Option1", "b) Option2", "c) Option3", "d) Option4"],
                        "reponse": "a"
                    }
                ]
            }
            Return ONLY the raw JSON without any markdown formatting or additional text.
            Text: """ + text.substring(0, Math.min(text.length(), 20000));
    }

    private QuizData parseQuiz(String json) throws QuizGenerationException {
        try {
            // Clean the JSON string first
            String cleanedJson = cleanJsonResponse(json);
            JsonObject root = JsonParser.parseString(cleanedJson).getAsJsonObject();
            return createQuizFromJson(root);
        } catch (Exception e) {
            throw new QuizGenerationException("Failed to parse quiz JSON: " + e.getMessage(), e);
        }
    }

    private String cleanJsonResponse(String json) {
        // Remove markdown formatting if present
        var matcher = JSON_MARKDOWN_PATTERN.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return json;
    }

    private QuizData createQuizFromJson(JsonObject root) {
        Quiz quiz = new Quiz();
        quiz.setNom(root.get("quizTitle").getAsString());
        quiz.setDescription("Quiz généré automatiquement");

        List<Question> questions = new ArrayList<>();
        for (JsonElement q : root.getAsJsonArray("questions")) {
            JsonObject qObj = q.getAsJsonObject();
            Question question = new Question();
            question.setTexte(qObj.get("texte").getAsString());

            List<String> options = new ArrayList<>();
            for (JsonElement opt : qObj.getAsJsonArray("options")) {
                options.add(opt.getAsString());
            }
            question.setOptions(options);
            question.setReponse(qObj.get("reponse").getAsString());

            questions.add(question);
        }

        return new QuizData(quiz, questions);
    }

    public static class QuizData {
        public final Quiz quiz;
        public final List<Question> questions;

        public QuizData(Quiz quiz, List<Question> questions) {
            this.quiz = quiz;
            this.questions = questions;
        }
    }

    public static class QuizGenerationException extends Exception {
        public QuizGenerationException(String message) {
            super(message);
        }

        public QuizGenerationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}