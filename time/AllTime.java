package time;
import java.awt.*;
import java.util.Calendar;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.border.EmptyBorder;

class AllTime extends Clock {

    final static int[] R = {1, 4, 16, 1, 4, 16, 1, 4}; //rate multiplier
    final static int[] I = {5, 5,  5, 4, 4,  4, 3, 3}; //Display index  
            //Display[] {sec, sec, sec, min, min, min, hour, hour};
    final Display[] disp;
    //final Display year, age, day, hour, min, sec; 
    int speed;
    int defYear, defAge, defDay, defHour; //defaults
    final Panel main;
    final Ear ear = new Ear();

    public AllTime() {
        main = new Panel();
        //String[] aa = {"Adem","Nuh","Musa","..."};
        Display year = new Rings("Yýl", -4000, 4000); 
        main.addDisplay(year);
        Display age  = new Colorful("Yaþ", 0, 71, null, null);
        main.addDisplay(age);
        //String[] ba = {"Kýþ","Bahar","Yaz","Güz"};
        Display day  = new Colorful("Gün", 1, 366, age, null); 
        main.addDisplay(day);
        String[] ca = {"Gece","Sabah","Öðle","Akþam"};
        Display hour = new Colorful("Saat", 0, 23, day, ca);
        main.addDisplay(hour);
        Display min  = new Display(null, 0, 59, hour, null);
        main.addDisplay(min);
        Display sec  = new Display(null, 0, 59, min, null);
        main.addDisplay(sec);
        disp = new Display[] {year, age, day, hour, min, sec};
        addListeners();
        setValues(1922, 49, 290, 17);
    }
    final static String TIP1 =
        "<HTML>Deðeri deðiþtirmek için sayýya,<BR>ilerletmek için kadrana týklayýn";
    final static String TIP2 ="Bu deðeri deðiþtiremezsiniz";
    void addListeners() {
        Cursor c = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        for (int i=1; i<4; i++) { //{age, day, hour}
            Display.Dial d = disp[i].dial;
            d.setToolTipText(TIP1);
            d.addMouseListener(ear);
            d.setCursor(c);
        } 
        for (int i=4; i<6; i++)  { //{min, sec}
            Display.Dial d = disp[i].dial;
            d.setToolTipText(TIP2);
            d.label = null; //no labels 
            disp[i].setMovingNumbers(true); //value at the tip 
        }
        disp[1].setChangeListener(ear); //age
        disp[4].setChangeListener(ear); //min
        main.slider.addChangeListener(ear);
        main.DATA.addActionListener(ear);
        main.RESET.addActionListener(ear);
    }
    public void setValues(int y, int a, int d, int h) {
        defYear = y;
        defAge = a;
        defDay = d;
        defHour = h;
        reset();
    }
    public void reset() {
        Calendar C = Calendar.getInstance();
        disp[0].setValue(defYear); //C.get(Calendar.YEAR));
        disp[1].setValue(defAge);  //age
        disp[2].setValue(defDay);  //C.get(Calendar.DAY_OF_YEAR));
        disp[3].setValue(defHour); //C.get(Calendar.HOUR_OF_DAY));
        disp[4].setValue(C.get(Calendar.MINUTE));
        disp[5].setValue(C.get(Calendar.SECOND));
        main.slider.setValue(0);
    }
    public void setData() {
        //System.out.println("not implemented");
        try {
            int y = disp[0].askUser(); //year
            int a = disp[1].askUser(); //age
            int d = disp[2].askUser("Senenin günü"); //day
            setValues(y, a, d, defHour);
        } catch (RuntimeException e) {
            //System.out.println(e.getMessage());
            //input may be cancelled
        }
    }
    public void setSpeed(int k) {
        Clock.checkRangeOf(k, 0, R.length-1);
        speed = k;
        disp[5].dial.setVisible(k<3); //sec
        disp[4].dial.setVisible(k<6); //min
        int r = R[speed];
        if (I[speed] == 4) r = 60*r; //min
        if (I[speed] == 3) r = 3600*r; //hour
        main.RATE.setText("x"+r);
    }
    void updateCounts() {
        count = 0; nano = 0;
        for (Display d : disp) {
            count += d.count;
            nano  += d.nano;
        }
    }
    public int doTick() {
        updateCounts();
        int r = R[speed];
        if (r < 10) { //slow lane
            disp[I[speed]].increment(1);
            return 1000/r;
        } else { //fast lane
            disp[I[speed]].increment(r/4);
            return 4000/r;
        }
    }
    int doTick_regular() { 
    //regular clock ticks once per second
        disp[5].increment(); //sec
        return 1000;
    }
    public void start() {
        super.start(); 
        disp[0].animate(); /*Rings*/
    }
    public void stop() {
        super.stop(); 
        disp[0].halt();  /*Rings*/
    }
    public JPanel getPanel() {
        return main;
    }
    
    class Ear extends MouseAdapter implements ChangeListener, ActionListener {
    
        public void mouseClicked(MouseEvent e) {
            Display.Dial d = (Display.Dial)e.getSource();
            if (!Display.pointAtCenter(e.getX(), e.getY())) d.incr();
            else try {
                d.ask();  
            } catch (RuntimeException x) {
                //input may be cancelled
            }
        }
        public void actionPerformed(ActionEvent e) {
            Object b = e.getSource();
            if (b == main.DATA) setData();
            else if (b == main.RESET) reset();
        }
        public void stateChanged(ChangeEvent e) {
            Object d = e.getSource();
            if (d == main.slider) 
                setSpeed(main.slider.getValue());
            else if (d == disp[1]) //adjust year
                disp[0].setValue(defYear-defAge+disp[1].getValue()); //age
            else if (d == disp[4]) //adjust label -- min
                disp[4].dial.setName(disp[3]+":"+disp[4]); //hour:min
            //else super.stateChanged(e);
        }
    }

class Panel extends JPanel {
    final static int GAP = 15;
    final JPanel pan = new JPanel();
    final JLabel RATE = new JLabel("x1");
    final JButton DATA = new JButton("Veriler");
    final JButton RESET = new JButton("Reset");
    final JSlider slider = new JSlider(0, R.length-1, 0);
    public Panel() {
        pan.setBackground(Color.LIGHT_GRAY);
        pan.setBorder(new EmptyBorder(GAP, GAP, GAP, GAP));
        pan.setLayout(new GridLayout(1, 6, GAP, GAP));
        setLayout(new BorderLayout());
        add(topPanel(), "North"); 
        add(pan, "Center");
    }
    JPanel topPanel() {
        JPanel p = new JPanel();
        p.add(new JLabel("Hýz:"));
        slider.setMajorTickSpacing(1);
        slider.setSnapToTicks(true);
        p.add(slider);
        RATE.setPreferredSize(new Dimension(100, 12));
        p.add(RATE);
        p.add(DATA);
        p.add(RESET);
        return p;
    }
    void addDisplay(Display d) {
        pan.add(d.dial);
    }
}
    public static void main(String[] args) {
        display(new AllTime(), "Ömür böyle geçiyor", 0, 0);
    }
}
