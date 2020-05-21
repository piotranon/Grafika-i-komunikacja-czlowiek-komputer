package CGlab;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

    String version = "0.02";

    public static void main(String[] args) {
        if(args.length<3)
        {
            System.out.println("====================================================");
            System.out.println("nie podano wszystkich argumentow");
            System.out.println("====================================================");
            System.out.println("Poprawnie podane argumenty:");
            System.out.println("/home/student/Desktop/grafika/renderer.png 640 480");
            System.out.println("====================================================");
            return;
        }

        //rendered object dir
        String dir=args[0];
        //object width
        int w=Integer.parseInt(args[1]);
        //object height
        int h=Integer.parseInt(args[2]);

        Renderer mainRenderer = new Renderer(dir,w,h);
        mainRenderer.clear();

        mainRenderer.drawLine(0,0,200,200, Renderer.LineAlgo.NAIVE);

        try {
            mainRenderer.save();
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getVersion() {
	return this.version;
    }
}
