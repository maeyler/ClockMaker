package time;
import java.awt.Color;
import java.awt.Graphics;

class Colorful extends Display {

    final static Color[] COL = new Color[60];
    final static Color[] BASE = { new Color(90, 90, 90), new Color(30, 200,  90),
        new Color(250, 250,  30), new Color(220, 150,  30), new Color(90, 90, 90) };
    static {
        for (int k=0, j=0; k<4; k++) 
            for (float i=0; i<15; i++) 
                COL[j++] = interpolate(BASE[k], BASE[k+1], i/15);
    }
    
    public Colorful(String n, int m1, int m2) {
        this(n, m1, m2, null, null);
    }
    public Colorful(String n, int m1, int m2, Display p, String[] a) {
        super(null, m1, m2, p, a);
        dial = new Dial(n); setLabels(a);
    }
    public String toString() {
        if (max==365 || max==366) 
             return Clock.dayToDate(val); 
        else return super.toString();
    }
    
    class Dial extends Display.Dial {
        public Dial(String n) {
            super(n); 
        }
        void drawDial(Graphics g, float t) {
            main = COL[(int)(60*t)];
            super.drawDial(g, t);
        }
    }
    
    static float interpolate(int a, int b, float t) {
        // 0.01 < t < 0.99
        return (a + (b-a)*t)/256;
    }
    public static Color interpolate(Color c1, Color c2, float t) {
        if (t < 0.01) return c1;
        if (t > 0.99) return c2;
        float r = interpolate(c1.getRed(), c2.getRed(), t);
        float g = interpolate(c1.getGreen(), c2.getGreen(), t);
        float b = interpolate(c1.getBlue(), c2.getBlue(), t);
        return new Color(r, g, b);
    }
    public static void main(String[] args) {
        Display d = new Colorful("0-23", 0, 23);
        Clock.showFrame(d.dial, "Colorful", 500, 100);
        d.setMovingNumbers(true);
        d.setColors(null, Color.blue, Color.darkGray);
        d.animate(); //takes 24 seconds
    }
}
