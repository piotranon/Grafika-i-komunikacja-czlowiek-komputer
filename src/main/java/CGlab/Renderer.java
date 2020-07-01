package CGlab;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import static java.lang.Integer.compare;
import static java.lang.Integer.signum;

public class Renderer {

    public enum LineAlgo {NAIVE, DDA, BRESENHAM, BRESENHAM_INT;}

    private BufferedImage render;
    public int h = 200;
    public int w = 200;

    private String filename;
    private LineAlgo lineAlgo = LineAlgo.NAIVE;

    public Renderer(String filename, int width, int height) {
        w = width;
        h = height;
        render = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.filename = filename;
    }

    public void drawPoint(int x, int y) {
        int white = 255 | (255 << 8) | (255 << 16) | (255 << 24);
        render.setRGB(x, y, white);
    }

    public void drawLine(int x0, int y0, int x1, int y1, LineAlgo lineAlgo) {
        if (lineAlgo == LineAlgo.NAIVE) drawLineNaive(x0, y0, x1, y1);
        if (lineAlgo == LineAlgo.DDA) drawLineDDA(x0, y0, x1, y1);
        if (lineAlgo == LineAlgo.BRESENHAM) drawLineBresenham(x0, y0, x1, y1);
        if (lineAlgo == LineAlgo.BRESENHAM_INT) drawLineBresenhamInt(x0, y0, x1, y1);
    }

    public void drawLineNaive(int x0, int y0, int x1, int y1) {
        // TODO: zaimplementuj
        int roznicaX = Math.max(x0, x1) - Math.min(x0, x1);
        int roznicaY = Math.max(y0, y1) - Math.min(y0, y1);

        double licznik = y0 + y1;
        double mianownik = x0 + x1;
        double m = licznik / mianownik;

        if (x0 > x1) {
            int temp = x0;
            x0 = x1;
            x1 = temp;
        }

        for (int i = 0; i < roznicaX; i++) {
            drawPoint(x0 + i, (int) Math.round(m * (x0 + i)));
        }
    }

    public void drawLineDDA(int x0, int y0, int x1, int y1) {
        // TODO: zaimplementuj
    }

    public void drawLineBresenham(int x0, int y0, int x1, int y1) {
        // TODO: zaimplementuj
        int white = 255 | (255 << 8) | (255 << 16) | (255 << 24);

        int dx = x1 - x0;
        int dy = y1 - y0;
        float derr = Math.abs(dy / (float) (dx));
        float err = 0;

        int y = y0;

        for (int x = x0; x < x1; x++) {
            render.setRGB(x, y, white);
            err += derr;
            if (err > 0.5) {
                y += (y1 > y0 ? 1 : -1);
                err -= 1.;
            }
        } // Oktanty: 7,8
    }

    public void drawLineBresenhamInt(int x0, int y0, int x1, int y1) {
        // TODO: zaimplementuj
        int white = 255 | (255 << 8) | (255 << 16) | (255 << 24);

        int dx = x1 - x0;
        int dy = y1 - y0;
        int D = 2 * dy - dx;

        int y = y0;

        for (int x = x0; x < x1; x++) {
            render.setRGB(x, y, white);
            if (D > 0) {
                y++;
                D = D - 2 * dx;
            }
            D = D + 2 * dy;
        } // Oktanty: 8
    }

    public void save() throws IOException {
        File outputfile = new File(filename);
        render = Renderer.verticalFlip(render);
        ImageIO.write(render, "png", outputfile);
    }

    public void clear() {
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int black = 0 | (0 << 8) | (0 << 16) | (255 << 24);
                render.setRGB(x, y, black);
            }
        }
    }

    public static BufferedImage verticalFlip(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage flippedImage = new BufferedImage(w, h, img.getColorModel().getTransparency());
        Graphics2D g = flippedImage.createGraphics();
        g.drawImage(img, 0, 0, w, h, 0, h, w, 0, null);
        g.dispose();
        return flippedImage;
    }

    public Vec3f barycentric(Vec2f A, Vec2f B, Vec2f C, Vec2f P) {

        Vec3f v1 = new Vec3f((B.x - A.x), (C.x - A.x), (A.x - P.x));
        // tutaj potrzebujemy wektora składającego się ze współrzędnych
        // x wektorów AB, AC ora PA.

        Vec3f v2 = new Vec3f((B.y - A.y), (C.y - A.y), (A.y - P.y));
        // tutaj potrzebujemy wektora składającego się ze współrzędnych
        // y wektorów AB, AC ora PA.

        Vec3f cross = vectorMultiply(v1, v2);
        // iloczyn wektorowy v1 i v2. Wskazówka: zaimplementuj do tego oddzielną metodę
//        System.out.println(v1.toString()+"\n"+v2.toString()+"\n"+cross.toString());
        Vec2f uv = new Vec2f((cross.x / cross.z), (cross.y / cross.z));
        // wektor postaci: cross.x / cross.z, cross.y / cross.z
//        System.out.println("x: "+uv.x+" y: "+uv.y);
        Vec3f barycentric = new Vec3f(uv.x, uv.y, (1 - uv.x - uv.y));
        // współrzędne barycentryczne, uv.x, uv.y, 1- uv.x - uv.y

        return barycentric;
    }

    public Vec3f vectorMultiply(Vec3f v1, Vec3f v2) {
        return new Vec3f((v1.y * v2.z - v1.z * v2.y), (v1.z * v2.x - v2.z * v1.x), (v1.x * v2.y - v1.y * v2.x));
    }

    public void drawTriangle(Vec2f A, Vec2f B, Vec2f C, Vec3i color) {
        // dla każdego punktu obrazu this.render:
        //      oblicz współrzędne baryc.
        //      jeśli punkt leży wewnątrz, zamaluj (patrz wykład)

        Color pointColor = new Color(color.x,color.y,color.z);
        int triangleColor=pointColor.getRGB();

        int maxX = Math.max((int)A.x,(int)Math.max((int)B.x,(int)C.x));
        int maxY = Math.max((int)A.y,(int)Math.max((int)B.y,(int)C.y));

        int minX = Math.min((int)A.x,(int)Math.min((int)B.x,(int)C.x));
        int minY = Math.min((int)A.y,(int)Math.min((int)B.y,(int)C.y));

        for (int x = minX; x < maxX; x++)
            for (int y = minY; y < maxY; y++) {
                Vec2f P = new Vec2f(x, y);
                Vec3f bary = barycentric(A, B, C, P);

                if ((bary.x >= 0) && (bary.y >= 0) && (bary.z >= 0))
                    render.setRGB(x, y, triangleColor);
            }
    }

    //Wektory do obliczeń barycentrycznych
    public class Vec3i {
        public int x;
        public int y;
        public int z;

        public Vec3i(int x, int y, int z) {
            this.x = (x >= 255) ? 255 : (x <= 0) ? 0 : x;
            this.y = (y >= 255) ? 255 : (y <= 0) ? 0 : y;
            this.z = (z >= 255) ? 255 : (z <= 0) ? 0 : z;
        }

        @Override
        public String toString() {
            return x + " " + y + " " + z;
        }
    }

    public class Vec3f {
        public float x;
        public float y;
        public float z;

        public Vec3f(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public String toString() {
            return x + " " + y + " " + z;
        }
    }

    public class Vec2f {
        public float x;
        public float y;

        public Vec2f(float x, float y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return x + " " + y;
        }
    }

    public class Vec2i {
        public int x;
        public int y;

        public Vec2i(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return x + " " + y;
        }
    }
}
