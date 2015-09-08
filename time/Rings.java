package time;
import java.net.URL;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import javax.swing.ImageIcon;

class Rings extends Colorful {

    final String rsrc; //image file name
    int angle;  //rotation in degrees

    public Rings(String n, int m1, int m2) {
        this(null, n, m1, m2);
    }
    public Rings(String r, String n, int m1, int m2) {
        super(null, m1, m2);
        rsrc = r;
        dial = new Dial(n);
    }
    public void run() { //invoked by Thread.start()
        String t = Thread.currentThread().getName(); 
        System.out.println(t+" started");
        while (running) {
            if (dial.getGraphics() == null) halt(); //window closed
            angle++; dial.repaint();
            try {
                Thread.sleep(166); //6 times per second
            } catch (Exception e) {
            }
        }
        //System.out.println(t+" ended");
        halt();  //make sure running = false
    }

    class Dial extends Colorful.Dial {
        Image img;
        public Dial(String n) {
            super(n); 
            if (rsrc == null) img = makeImage();
            else {
                URL u = getClass().getResource(rsrc); /*"rings.png"*/
                img = new ImageIcon(u).getImage();
            }
        }
        Image makeImage() {
            long time = System.nanoTime();
            Image i = new BufferedImage(HH, HH, 1);
            Graphics2D g = (Graphics2D)i.getGraphics();;
            g.setStroke(THICK);
            g.setColor(Color.lightGray);
            g.fillRect(0, 0, HH, HH);
            g.setColor(Color.white);
            g.fillOval(3, 3, HH-6, HH-6);
            for (int x=11; x<=H; x+=4) 
                drawRing(g, x);
            nano = System.nanoTime() - time;
            return i;
        }
        protected void paintComponent(Graphics g) {
            count++;
            long time = System.nanoTime();
            Graphics2D G = (Graphics2D)g;
            G.setStroke(THICK);
            g.setFont(NORM);
            AffineTransform at = G.getTransform();
            g.translate(H, H);
            if (angle >= 360) angle = angle%360;
            G.rotate(Math.PI*angle/180); //degree to radian
            g.drawImage(img, -H, -H, null);
            G.setTransform(at); //back to original state
            //float t = (val-min)/(max-min+1f);
            g.setColor(Color.black); //COL[5]);
            //g.fillRect(H-5, H-5, 10, 10); //Kabe
            g.setFont(BOLD);
            int y = g.getFontMetrics().getHeight();
            drawCentered(g, Rings.this.toString(), HH-17, y/2, true);
            if (getName() != null) g.drawString(getName(), 0, y);
            nano = System.nanoTime() - time;
        }
    }
    
    static int ringNumber(int x) {
        return Math.min(59, 8 + 10*(x-5)/13);
    }
    static void drawRing(Graphics g, int x) {
        drawRing(g, x, COL[ringNumber(x)]);
    }
    static void drawRing(Graphics g, int x, Color c) {
        g.setColor(c);
        g.drawOval(H-x, H-x, 2*x, 2*x);
    }
    public static void main(String[] args) {
        Display d = new Rings("Hour", 0, 23);
        Clock.showFrame(d.dial, "Rings", 500, 100);
        d.setValue(20);
        d.animate(); //takes 24 seconds
    }
}
