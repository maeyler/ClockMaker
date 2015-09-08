package time;
import java.awt.*;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class Display implements Runnable {

    final static int H = 70, HH = 2*H, R = H-11;
    final static Stroke THICK = new BasicStroke(2.6f, 1, 1); 

    int count; /**/
    long nano; /**/
    boolean running;
    int val, min, max;
    Display next;
    Dial dial;
    ChangeListener callback;
    
    public Display(String n, int m1, int m2) {
        this(n, m1, m2, null, null); 
    }
    public Display(String n, int m1, int m2, Display p, String[] a) {
        val = m1; min = m1; max = m2; next = p; 
        dial = new Dial(n); setLabels(a); 
    }
    public Dial component() {
        return dial;
    }
    public int getValue() {
        return val;
    }
    public String getName() {
        return dial.getName();
    }
    public void animate() {
        if (running) return;
        Thread t = new Thread(this);
        System.out.println("Animation: "+getName()+" at v=" +val);
        running = true; 
        t.start();   //invokes run() indirectly
    }
    public void halt() {
        if (!running) return;
        running = false; 
        System.out.println("Animation ends: "+getName());
    }
    public void run() {
        int v = val;
        increment(); //first step
        while (val!=v && running)
            try {
                Thread.sleep(1000);
                increment();
            } catch (InterruptedException e) {
            }
        halt();  //make sure running = false
    }
    public void setChangeListener(ChangeListener cb) {
        callback = cb;
    }
    public void setMax(int m) {
        max = m;
        if (val > m) setValue(m);
    }
    public void setValue(int v) {
        if (val == v) return;
        Clock.checkRangeOf(v, min, max);
        val = v;
        if (callback != null) {
            ChangeEvent e = new ChangeEvent(this);
            callback.stateChanged(e);
        }
        dial.repaint();
    }
    public void increment() {
        increment(1);
    }
    public void increment(int k) {
        Clock.checkRangeOf(k, 1, max);
        int v = val + k;
        if (v <= max) setValue(v);
        else {
            setValue(v+min-max-1);
            if (next != null) 
                next.increment();
        } 
    }
    public void setMovingNumbers(boolean m) {
        dial.moving = m;
    }
    public void setColors(Color c1, Color c2, Color c3) {
        if (c1 != null) dial.main = c1;
        if (c2 != null) dial.text = c2;
        if (c3 != null) dial.hand = c3;
    }
    public void showLabels(boolean b) {
        if (b) setLabels(null);
        else dial.label = null;
    }
    public void setLabels(String[] a) {
        if (a == null) {
            float q = (max-min+1)/4f;
            a = new String[4];
            for (int i=0; i<4; i++) {
                int v = (int)(min+i*q); //Math.round
                a[i] = twoDigit(v);
            }
        }
        dial.label = a;
        dial.repaint();
    }
    public int askUser() {
        return askUser(getName());
    }
    public int askUser(String s) {
    //either returns user input in correct range, or throws an Exception
        String msg = s;
        String rep = ""+val; //initial value of the reply
        while (true) {
            rep = JOptionPane.showInputDialog(dial, msg, rep);
            if (rep == null) throw new RuntimeException("input cancelled");
            try {
                int v = Integer.parseInt(rep);
                setValue(v);
                return v;
            } catch (RuntimeException x) {
                //repeat loop silently
                msg = s+": "+min+"-"+max+" arasinda bir deger olmali\n"
                        +x.getClass().getName();
            }
        }
    }
    public String toString() { 
        return twoDigit(val); 
    }
    
    class Dial extends JComponent {
        Color main = new Color(200,215,255); //light blue
        Color text = Color.blue;
        Color hand = Color.darkGray;
        final Font NORM = new Font("Dialog", 0, 10);  //f.deriveFont(s-2);
        final Font BOLD = new Font("Dialog", 1, 13);  //f.deriveFont(1, s+1); 
        String[] label;
        boolean moving = false;
        public Dial(String n) {
            setName(n);
            setPreferredSize(new Dimension(HH+1, HH+1));
        }
        public void incr() { //used in mouse listener
            increment();
        }
        public void ask()  { //used in mouse listener
            askUser();
        }
        protected void paintComponent(Graphics g) {
            count++;
            long time = System.nanoTime();
            Graphics2D g2 = (Graphics2D)g;
            g2.setStroke(THICK);
            g.setFont(NORM);
            float t = (val-min)/(max-min+1f);
            drawDial(g, t);
            g.setFont(BOLD);
            drawHand(g, t);
            if (getName() != null)
                g.drawString(getName(), 0, 12);
            nano = System.nanoTime() - time;
        }
        void drawDial(Graphics g, float t) {
            g.setColor(main);
            g.fillOval(0, 0, HH, HH);
            if (label == null) return;
            FontMetrics fm = g.getFontMetrics();
            int h = fm.getHeight()/2;
            g.setColor(text);
            drawCentered(g, label[0], H, HH-h-4);
            g.drawString(label[1], 3, H+h-2); 
            //drawCentered(g, label[1], h+4, H);
            drawCentered(g, label[2], H, h);
            drawCentered(g, label[3], HH-h-4, H);
        }
        void drawHand(Graphics g, float t) {
            double teta = 2*Math.PI*t;
            int x = Math.round(H - R*(float)Math.sin(teta));
            int y = Math.round(H + R*(float)Math.cos(teta));
            g.setColor(hand);
            g.drawLine(H, H, x, y);
            String s = Display.this.toString();  //twoDigit(val);
            if (moving) drawCentered(g, s, x, y, true);
            else drawCentered(g, s, H, H, true);
        }
        public void drawCentered(Graphics g, String s, int x, int y) {
            drawCentered(g, s, x, y, false);
        }
        public void drawCentered(Graphics g, String s, int x, int y, boolean framed) {
            FontMetrics fm = g.getFontMetrics();
            int h = fm.getHeight();    y = y+h/2;
            int w = fm.stringWidth(s)+1; x = x-w/2;
            if (framed) {
                Color c = g.getColor();
                g.setColor(Color.white);
                g.fillRoundRect(x-3, y-h, w+5, h+2, 8, 8);
                g.setColor(hand);
                g.drawRoundRect(x-3, y-h, w+5, h+2, 8, 8);
                g.setColor(c);
            }
            g.drawString(s, x+1, y-2);
        }
    }

    static String twoDigit(int v) {
        String s = ""+v;
        if (s.length() < 2) s = "0"+s;
        return s;
    }
    public static boolean pointAtCenter(int x, int y) {
        //H±8 returns 0
        int xx = (x-H)/9;
        int yy = (y-H)/9;
        return (xx==0 && yy==0);
    }
    public static void main(String[] args) {
        Display d1 = new Display("0-23", 0, 23);
        Clock.showFrame(d1.dial, "Default", 100, 0);
        d1.animate(); //takes 24 seconds

        Display d2 = new Display("1-32", 1, 32);
        Clock.showFrame(d2.dial, "Modified", 250, 0);
        d2.setMovingNumbers(true); //value at the tip of the dial
        d2.dial.label = null; //no labels
        d2.setColors(new Color(130, 240, 120), null, Color.blue);
        d2.animate(); //takes 32 seconds
    }
}
