package time;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.util.Date;
import javax.swing.JComponent;
import javax.swing.JPanel;

class Bencil extends Clock {

    final static int H = 124, HH = 2*H, R = H-12;
    final static Point[] pA = new Point[60];
    static { 
        for (int k=0; k<60; k++) {
            double teta = Math.PI*k/30;
            int x = Math.round(H - (H-8)*(float)Math.sin(teta));
            int y = Math.round(H + (H-8)*(float)Math.cos(teta));
            pA[k] = new Point(x, y);
        }
    }
    int angle;  //rotation in degrees
    final Dial dial;
    final JPanel pan = new JPanel();
    final Ear ear = new Ear();
    Point mouseP; //(mouseP != null) means mousePressed

    public Bencil() { this('o',"BEN"); }
    public Bencil(char c, String s) {
        dial = new Dial();
        setMessage(c, s);
        setTime();
        pan.add(dial);
    }
    public void setMessage(char c, String m) { 
        dial.CH = c; dial.MSG = m; 
        dial.repaint();
    }
    public void setTime() { 
        String[] a = new Date().toString().split(" |:");
        dial.setName(a[3]+':'+a[4]);
        //setSecond(Integer.parseInt(a[5]));
        dial.repaint();
    }
    public void setSecond(int s) {
        angle = 6*s; 
        dial.repaint();
    }
    public JPanel getPanel() {
        return pan;
    }
    public int doTick() {
        dial.repaint();
        if (mouseP != null) return Integer.MAX_VALUE; 
            //user activity will interrupt sleeping thread
        angle += 6; 
        if (angle >= 360) angle = angle%360;
        //reset time 2 times per minute
        if (angle%180 == 0) setTime();
        return 1000; //msec tick time
    }
    public void interrupt() {
        if (thread != null) thread.interrupt(); //invokes doTick() 
    }
    
    class Dial extends JComponent {
        char CH = 'o';
        String MSG = "B";
        Font NORM = new Font("Dialog", 0, 10);  //f.deriveFont(s-2);
        Font BOLD = new Font("Dialog", 1, 13);  //f.deriveFont(1, s+1); 
        public Dial() { 
            addMouseListener(ear);
            addMouseMotionListener(ear);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(HH+2, HH+2));
        }
        protected void paintComponent(Graphics g) {
            //count++;   moved to Ear
            //long time = System.nanoTime();
            g.setColor(Color.white); 
            g.fillRect(0, 0, HH, HH);
            g.setFont(NORM);
            //fm = g.getFontMetrics();
            for (Point p: pA) 
                drawCentered(g, ""+CH, p.x, p.y, Color.red, false);
            if (mouseP == null) drawHand(g);
            g.setFont(BOLD);
            Point p = (mouseP == null ? pA[angle/6] : mouseP);
            drawCentered(g, MSG, p.x, p.y, Color.black, true);
            FontMetrics fm = g.getFontMetrics();
            g.drawString(getName(), 15, fm.getHeight()+8);
            //nano = System.nanoTime() - time;
        }
        void drawHand(Graphics g) {
            g.setColor(Color.blue); 
            double teta = Math.PI*angle/180;
            int x = Math.round(H - R*(float)Math.sin(teta));
            int y = Math.round(H + R*(float)Math.cos(teta));
            g.drawLine(H, H, x, y);
        }
        void drawCentered(Graphics g, String s, int x, int y, Color c, boolean erase) {
            FontMetrics fm = g.getFontMetrics();
            int h = fm.getHeight();    y = y+h/2;
            int w = fm.stringWidth(s)+1; x = x-w/2;
            if (erase) {
                g.setColor(Color.white); 
                g.fillRect(x+1, y-h+3, w-1, h-3);
            }
            g.setColor(c); 
            g.drawString(s, x+1, y-2);
        }
    }
        
    class Ear extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            count = 0; nano = System.nanoTime();
            int m = e.getModifiers();
            //System.out.println("mousePressed -- Modifiers "+m); 
            if ((m & e.CTRL_MASK) > 0) { //Ctrl pressed
                setMessage('s', "S"); return;
            }
            mouseP = e.getPoint(); 
            interrupt(); //invokes doTick() 
        }
        public void mouseDragged(MouseEvent e) {
            count++;
            mouseP = e.getPoint();
            interrupt();
            //System.out.println("mouseDragged to "+mouseP);
        }
        public void mouseReleased(MouseEvent e) {
            nano = System.nanoTime() - nano;
            mouseP = null;
            interrupt(); 
            System.out.println("mouseReleased in "+nano/1000/1000+" msec");
            if (count == 0) return;
            System.out.println("mouseDragged  "+count+" times");
        }
    }
    
    public static void main(String[] args) {
        display(new Bencil(), "BEN! Saati", 50, 50);
    }
}
