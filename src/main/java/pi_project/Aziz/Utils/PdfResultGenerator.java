package pi_project.Aziz.Utils;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import pi_project.Aziz.Entity.Quiz;
import pi_project.Fedi.entites.eleve;

import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PdfResultGenerator {
    public static String generateQuizResultPdf(eleve student, Quiz quiz, int correctAnswers, int totalQuestions) {
        String fileName = "quiz_result_" + student.getId() + "_" + quiz.getId() + ".pdf";
        float percentage = (float) correctAnswers / totalQuestions * 100;

        try {
            PdfWriter writer = new PdfWriter(new FileOutputStream(fileName));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Add title
            Paragraph title = new Paragraph("Quiz Results")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(18)
                    .setBold();
            document.add(title);

            // Add content
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Student: " + student.getPrenom() + " " + student.getNom()).setBold());
            document.add(new Paragraph("Quiz: " + quiz.getNom()).setBold());
            document.add(new Paragraph("Date: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).setBold());
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Score: " + correctAnswers + "/" + totalQuestions));
            document.add(new Paragraph("Percentage: " + String.format("%.2f%%", percentage)));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Congratulations on completing the quiz!"));

            document.close();
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}