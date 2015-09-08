package time;
import java.util.ArrayList;
import java.awt.Component;
import java.awt.Window;
import java.awt.Frame;
import java.awt.event.WindowListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

public abstract class Clock implements Runnable {

    int count; //number of invocations of paintComponent
    long nano; //runtime of last invocation
    Thread thread;  //indicates running thread

    abstract public JPanel getPanel();
    abstract public int doTick();
    
    public void start() {
        if (thread != null) return;
        thread = new Thread(this);
        thread.start(); //invokes run() indirectly
        System.out.println("Start "+thread);
    }
    public void stop() {
        if (thread == null) return;
        System.out.println("Stop "+thread);
        thread = null; 
    }
    public void run() { //invoked by Thread.start()
        while (thread != null) {
            int t = doTick();
            try {
                Thread.sleep(t);
            } catch (Exception e) {
            }
        }
    }
    public String toString() { 
        String s = getClass().getName();
        int k = s.lastIndexOf('.');
        return s.substring(k+1);
    }
    
    static class Closer extends WindowAdapter {
        Clock src;
        public Closer(Clock c) {
            src = c;
        }
        public void windowClosed(WindowEvent e) {
            //Window w = (Window)e.getSource();
            //w.dispose();  infinite recursion!!
            src.stop(); //the thread
        }
    }   
        
    final static int[] days = {0,31,60,91,121,152,182,213,244,274,305,335,366};
    final static String[] month =
    {"Ocak","Sub","Mart","Nis","May","Haz","Tem","Agu","Eyl","Ekim","Kas","Ara"};
    static void checkRangeOf(int k, int m1, int m2) {
        if (k<m1 || k>m2) 
            throw new IndexOutOfBoundsException("value: "+k+", max: "+m2);
    }
    public static int dateToDay(int d, int m ) {
        checkRangeOf(d, 1, 31);
        checkRangeOf(m, 1, 12);
        return days[m-1]+d; 
    }
    public static String dayToDate(int x) {
        int d = dayToDayOfMonth(x);
        int m = dayToMonth(x);
        return Display.twoDigit(d)+" "+month[m-1];
    }
    public static int dayToDayOfMonth(int x) {
        int m = dayToMonth(x);
        return x - days[m-1];
    }
    public static int dayToMonth(int x) {
        checkRangeOf(x, 1, 366);
        int m = 1; while (x > days[m]) m++;
        return m;
    }
    public static int daysInMonth(int m, int y) {
         if (m==2) return (y%4 > 0? 28 : 29);
         if (m==4 || m==6 || m==9 || m==11) return 30;
         return 31;
    }
    public static Clock[] allClocks() {
        ArrayList<Clock> L = new ArrayList<Clock>();
        for (Frame f: Frame.getFrames())
            for (WindowListener w: f.getWindowListeners())
                if (w instanceof Clock.Closer) 
                    L.add(((Clock.Closer)w).src);
        Clock[] ca = new Clock[L.size()];
        return L.toArray(ca);
    }
    public static Clock newInstance(String cName) {
        try {
            Class c = Class.forName(cName);
            System.out.println("start "+cName);
            return (Clock)c.newInstance();
        } catch (Exception x) {
            if  (!cName.startsWith("time.")) 
                return newInstance("time."+cName);
            System.out.println("start Mirror \n"+x);
            return new Hello();  //Mirror();
        }
    }
    public static JFrame getFrame(Component cmp) {
        Window w = SwingUtilities.getWindowAncestor(cmp);
        if (w instanceof JFrame) return (JFrame)w;
        return null;  //getFrame(cmp, cmp.getClass().getName()); 
    }
    public static JFrame makeFrame(Component cmp) {
        JFrame frm = new JFrame();
        frm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        if (cmp instanceof JPanel) frm.setContentPane((JPanel)cmp); 
        else frm.getContentPane().add(cmp); 
        frm.pack(); 
        return frm;
    }
    public static void showFrame(Component cmp, String title, int x, int y) {
        JFrame frm = getFrame(cmp);
        if (frm == null) frm = makeFrame(cmp); 
        if (title != null && !title.equals("")) frm.setTitle(title);
        frm.setLocation(x, y);
        frm.setVisible(true);
    }
    public static Clock display(Clock c) {
        JPanel p = c.getPanel();
        JFrame frm = getFrame(p); 
        if (frm == null) {
            frm = makeFrame(p); 
            frm.addWindowListener(new Closer(c));
            c.start(); //invokes run() indirectly
        } 
        frm.setVisible(true);
        return c;
    }
    public static Clock display(Clock c, String title, int x, int y) {
        display(c);
        showFrame(c.getPanel(), title, x, y); 
        return c;
    }
    public static void main(String[] args) {
        //Digilog.main(null);
        Hello.main(null);
        //AllTime.main(null);
        //Mirror.main(null);
    }
}
