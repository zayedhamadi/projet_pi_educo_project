package pi_project.louay.Utils;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Random;

public class captcha {


    public static Image genererCaptchaImage(String captchaText, int width, int height) {

        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();


        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, width, height);


        gc.setFill(Color.BLUE);
        gc.setFont(Font.font("Arial", 24));
        gc.fillText(captchaText, 20, 35);


        Random rand = new Random();
        for (int i = 0; i < 12; i++) {
            int x1 = rand.nextInt(width);
            int y1 = rand.nextInt(height);
            int x2 = rand.nextInt(width);
            int y2 = rand.nextInt(height);
            gc.setStroke(Color.GRAY);
            gc.strokeLine(x1, y1, x2, y2);
        }


        return canvas.snapshot(null, null);
    }


    public static String genererTexteAleatoire(int longueur) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < longueur; i++) {
            sb.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
