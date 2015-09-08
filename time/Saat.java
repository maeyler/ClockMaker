package time;
import java.net.URL;
import java.util.Date;
import java.util.Calendar;
import java.awt.*;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JLabel;

class Saat extends Clock {

    final static int H = 40, M = 50, S = 60, R = 60;
    final static Stroke 
        HOUR = new BasicStroke(3.2f, 1, 1),
        MIN  = new BasicStroke(2f, 1, 1), 
        SEC  = new BasicStroke(1f); 
    float hour, min, sec;  //rotation in degrees
    Dial dial;
    JPanel pan = new JPanel(new BorderLayout());
    JLabel lab = new JLabel("", 0); //CENTER
    JLabel cal = new JLabel("", 0);

    public Saat() {
        this("saat.png");
    }
    public Saat(String n) {
        lab.setFont(new Font("SansSerif", 0, 20));
        pan.add(lab, "North");
        dial = new Dial(n);
        pan.add(dial, "Center");
        pan.add(cal, "South");
        reset();
    }
    public void reset() {
        Calendar C = Calendar.getInstance();
        int h = C.get(Calendar.HOUR_OF_DAY)%12; //ignore am-pm
        int m = C.get(Calendar.MINUTE);
        int s = C.get(Calendar.SECOND);
        setTime(h, m, s); setLabels(C);
    }
    public void setLabels(Calendar c) {
        String[] a = c.getTime().toString().split(" ");
        lab.setText(a[0]+" - "+a[1]+" "+a[2]);
        int today = c.get(Calendar.DAY_OF_MONTH);
        String s = "<html><font size=-1 face=Monospaced>\n"
           + "mo tu we th fr sa su <br><font color=gray>\n";
        int n = 12 + c.get(Calendar.DAY_OF_WEEK);
        c.add(Calendar.DAY_OF_MONTH, -n);
        for (int i=0; i<35; i++) {
            int d = c.get(Calendar.DAY_OF_MONTH);
            if (d == today) s += "<font color=blue>";
            if (d > 9) s += d; else s += "0"+d;
            if (d == today) s += "<font color=gray>";
            int w = c.get(Calendar.DAY_OF_WEEK);
            if (w == 1) s += "<br>\n"; else s += " ";
            c.add(Calendar.DAY_OF_MONTH, 1);
        } 
        cal.setText(s);
    }
    public void setDate(int y, int m, int d) { 
        Calendar C = Calendar.getInstance(); 
        C.set(y,m,d); setLabels(C);
    }
    public void setTime(int h, int m, int s) { 
    //convert values to degrees
        hour = 30*h + m/2;
        min  = 6*m;
        sec  = 6*s;
        dial.repaint();
    }
    public JPanel getPanel() {
        return pan;
    }
    public int doTick() {
        sec += 6; //degrees per sec
        if (sec  > 360) reset(); //each minute
        if (min  > 360) min -= 360;
        if (hour > 360) hour -= 360;
        dial.repaint();
        return 1000; //msec tick time
    }

    class Dial extends javax.swing.JComponent {
        Image img;
        public Dial(String n) {
            setBackground(Color.gray);
            /*
            URL u = getClass().getResource(n); 
            if (u == null) return;
            img = new ImageIcon(u).getImage();
            setPreferredSize(new Dimension(img.getWidth(this), img.getHeight(this)));
            */
            setPreferredSize(new Dimension(2*R, 2*R));
            img = null;
        }
        protected void paintComponent(Graphics g) {
            count++;
            long time = System.nanoTime();
            Graphics2D G = (Graphics2D)g;
            g.translate(R, R);
            if (img != null) 
                g.drawImage(img, -R, -R, null);
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
        Saat s = new Saat(); 
        String TZ = System.getProperties().get("user.timezone").toString();
        int k = TZ.indexOf("/"); if (k>0) TZ = TZ.substring(k+1);
        display(s, TZ, -100, 0);
        Component p = s.getPanel(); Frame f = getFrame(p);
        int x = Toolkit.getDefaultToolkit().getScreenSize().width - f.getWidth();
        int y = f.getComponent(0).getY();
        f.setLocation(x, 3-y);
    }
}
