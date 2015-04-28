package cn.com.gps169.bos.resource;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

public class CaptchaProducer {

    public final static String CAPTCHA_SESSION_KEY = "CAPTHCA_TEXT";

    // The width of the validate code.
    private static int width = 105;

    // The height of the validate code.
    private static int height = 30;

    // The count of characters of validate code.
    private static int codeCount = 4;

    private static int charWidth = width / (codeCount + 1);;
    private static int charMargin = charWidth / 2;
    private static int fontHeight = height - 2;
    private static int codeY = height - 4;

    /*
     * 0, o, O, 1, l, should be removed
     */
    private static char[] codeSequence = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
            'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c',
            'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z', '2', '3', '4', '5', '6', '7', '8', '9' };

    public static int getCodeCount() {
        return codeCount;
    }

    public static String GenerateRandomCode() {
        return GenerateRandomCode(codeCount);
    }

    private static String GenerateRandomCode(int codeCount) {

        StringBuffer randomCode = new StringBuffer();
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());

        for (int i = 0; i < codeCount; i++) {
            String strRand = String.valueOf(codeSequence[random.nextInt(codeSequence.length)]);
            randomCode.append(strRand);
        }

        return randomCode.toString();
    }

    public static BufferedImage DrawPicture(String randomCode) {

        Random random = new Random();
        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = buffImg.createGraphics();

        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);

        Font font = new Font("Times New Roman", Font.PLAIN, fontHeight);
        graphics.setFont(font);

        graphics.setColor(Color.BLACK);
        graphics.drawRect(0, 0, width - 1, height - 1);

        graphics.setColor(randomRgbColor(160, 40));
        for (int i = 0; i < 100; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int w = random.nextInt(4);
            int d = random.nextInt(4);

            graphics.fillOval(x, y, w, d);
        }

        for (int i = 0; i < codeCount; i++) {

            graphics.setColor(randomRgbColor(120, 80));
            graphics.drawString(randomCode.substring(i, i + 1), i * charWidth + charMargin, codeY);
        }

        return buffImg;
    }

    private static Color randomRgbColor(int base, int step) {
        Random random = new Random();
        int r = base + random.nextInt(step);
        int g = base + random.nextInt(step);
        int b = base + random.nextInt(step);

        if (r > 255) {
            r = 255;
        }

        if (g > 255) {
            g = 255;
        }

        if (b > 255) {
            b = 255;
        }

        return new Color(r, g, b);
    }
}
