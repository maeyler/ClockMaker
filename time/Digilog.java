package time;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.Date;
import java.util.Calendar;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JPanel;

class Digilog extends Clock {

    final static int GAP = 20, G2 = GAP;
    final Display year, mon, day, hour, min, sec;
    final JPanel pan = new JPanel();
    final Ear ear = new Ear();
    long resetTime;
    
    public Digilog() {
        pan.setBackground(Color.LIGHT_GRAY);
        pan.setBorder(new EmptyBorder(G2, G2, G2, G2));
        pan.setLayout(new GridLayout(2, 3, GAP, G2));
        year = makeDisplay("Year", 1950, 2049, null);
        mon = makeDisplay("Month", 1, 12, year); 
        day = makeDisplay("Day", 1, 31, mon);
        hour = makeDisplay("Hour", 0, 23, day);
        min = makeDisplay("Min", 0, 59, hour);
        sec = makeDisplay("Sec", 0, 59, min);
        String[] a = {"Jan","Apr","July","Oct"};
        mon.setLabels(a);
        mon.setChangeListener(ear);
        //hour.setChangeListener(ear);
        reset();
    }
    public void reset() {
        Calendar C = Calendar.getInstance();
        year.setValue(C.get(Calendar.YEAR));
        mon.setValue(C.get(Calendar.MONTH)+1); //dikkat!!
        day.setValue(C.get(Calendar.DAY_OF_MONTH));
        hour.setValue(C.get(Calendar.HOUR_OF_DAY));
        min.setValue(C.get(Calendar.MINUTE));
        sec.setValue(C.get(Calendar.SECOND));
        resetTime = System.currentTimeMillis() + 3600*1000+1; //an hour later
        //System.err.println("next time to reset: "+new Date(resetTime));
    }
    Display makeDisplay(String n, int m1, int m2, Display p) {
        int k = pan.getComponentCount();  // 0<=k<=5
        Display d = new Display(null, m1, m2, p, null); //name is ignored
        pan.add(d.dial);
        if (k < 3) { //first 3 dials 
            d.setColors(new Color(50, 250, 80), Color.blue, Color.darkGray);
        } else { //last 3 dials (bottom)
            d.setColors(null, Color.blue, Color.darkGray);
            d.setMovingNumbers(true);
        }
        return d;
    }
    public JPanel getPanel() {
        return pan;
    }
    public int doTick() {
        if (resetTime < System.currentTimeMillis()) reset();
        sec.increment();
        return 1000; //msec tick time
    }

    class Ear implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            Display d = (Display)e.getSource();
            //if (d == hour) reset(); //check time every hour --> doTick()
            if (d == mon) { //adjust number of days
                day.setMax(Clock.daysInMonth(mon.val, year.val));
                if (day.dial.label != null)
                    day.setLabels(null);
            } 
        }
    }

    public static void main(String[] args) {
        String t = "Digilog -- Digital & Analog Clock";
        display(new Digilog(), t, 330, 250);
    }
}
