package time;
import java.net.URL;
import java.util.Calendar;
import java.awt.*;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

class Mirror extends Clock {

    final static int H = 75, M = 90, S = 115;
    final static Stroke 
        HOUR = new BasicStroke(5.0f, 1, 1),
        MIN  = new BasicStroke(2.8f, 1, 1), 
        SEC  = new BasicStroke(1.0f); 
    float hour, min, sec;  //rotation in degrees
    Dial dial;
    JPanel pan;

    public Mirror() {
        this("mirror.png");
    }
    public Mirror(String n) {
        dial = new Dial(n);
        pan = new JPanel();
        pan.add(dial);
        reset();
    }
    public void reset() {
        Calendar C = Calendar.getInstance();
        int h = C.get(Calendar.HOUR_OF_DAY)%12; //ignore am-pm
        int m = C.get(Calendar.MINUTE);
        int s = C.get(Calendar.SECOND);
        setTime(h, m, s);
    }
    public void setTime(int h, int m, int s) { 
    //convert values to degrees
        hour = 360 - 30*h - m/2;
        min  = 360 - 6*m;
        sec  = 360 - 6*s;
        dial.repaint();
    }
    public JPanel getPanel() {
        return pan;
    }
    public int doTick() {
        sec -= 6; //degrees per sec
        if (sec < 0) {
            sec  += 360;
            min  -= 6;
            hour -= 0.5f;
        }
        if (min  < 0) min += 360;
        if (hour < 0) hour += 360;
        dial.repaint();
        return 1000; //msec tick time
    }

    class Dial extends javax.swing.JComponent {
        Image img;
        public Dial(String n) {
            setBackground(Color.gray);
            URL u = getClass().getResource(n); 
            if (u == null) return;
            img = new ImageIcon(u).getImage();
            setPreferredSize(new Dimension(img.getWidth(this), img.getHeight(this)));
        }
        /*public Dimension getPreferredSize() {
            return new Dimension(img.getWidth(this), img.getHeight(this));
        }*/
        protected void paintComponent(Graphics g) {
            count++;
            long time = System.nanoTime();
            Graphics2D G = (Graphics2D)g;
            g.translate(125, 125);
            if (img != null) 
                g.drawImage(img, -125, -125, null);
            g.setColor(Color.darkGray);
            drawHand(G, hour, H, HOUR);
            drawHand(G, min,  M, MIN);
            g.setColor(Color.blue);
            drawHand(G, sec,  S, SEC);
            g.fillOval(-4,-4,8,8); //blue
            nano = System.nanoTime() - time;
        }
    }
    
    static void drawHand(Graphics2D g, float a, int h, Stroke s) {
        double teta = Math.PI*a/180;
        int x =  Math.round(h*(float)Math.sin(teta));
        int y = -Math.round(h*(float)Math.cos(teta));
        g.setStroke(s);
        g.drawLine(0, 0, x, y);
    }
    public static void main(String[] args) {
        display(new Mirror(), "Berber saati", 50, 335);
    }
}
