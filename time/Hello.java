package time;
import java.util.Date;
import java.awt.Font;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Hello extends Clock {

    final static Font FONT = new Font("Serif", 1, 18);
    final JPanel pan = new JPanel();
    final JLabel lab;

    public Hello() {
        pan.setBackground(Color.blue);
        pan.setLayout(new GridLayout(2, 1));
        JLabel j = new JLabel("Hello World", JLabel.CENTER); 
        j.setForeground(Color.cyan);
        j.setFont(FONT);
        pan.add(j); 
        lab = new JLabel("time", JLabel.CENTER); 
        lab.setForeground(Color.yellow);
        lab.setFont(FONT);
        pan.add(lab); 
        doTick();  //needed before pack()
    }
    public JPanel getPanel() {
        return pan;
    }
    public int doTick() {
        lab.setText(new Date().toString());
        return 1000; //msec tick time
    }

    public static void main(String[] args) {
        display(new Hello(), "Hello Clock", 50, 250);
    }
}
