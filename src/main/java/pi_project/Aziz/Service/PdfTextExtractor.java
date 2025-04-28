package pi_project.Aziz.Service;



import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.File;
import java.io.IOException;

public class PdfTextExtractor {
    public static String extractText(String filePath) throws IOException {
        PDDocument document = PDDocument.load(new File(filePath));


        try {
            return new PDFTextStripper().getText(document);
        } finally {
            document.close();
        }
    }
}