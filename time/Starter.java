package time;
import javax.swing.JApplet;

public class Starter extends JApplet {
    
    Clock c;
    
    public Starter() {
        System.out.println("Starter begins");
        //getParameter() fails if called before init()
    }
    public void init() { //called only in Applet mode
        //System.out.println("init()");
        Clock c = Clock.newInstance(getParameter("cName"));
        setContentPane(c.getPanel()); 
        c.start();
    }
    public void stop() { //called only in Applet mode
        c.stop();
    }

    public static void main(String[] args) {
        Clock.main(null);
    }
}
