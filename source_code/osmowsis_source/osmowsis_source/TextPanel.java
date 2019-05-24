import javax.swing.*;
import java.awt.*;

public class TextPanel extends JPanel {
    private JTextArea area;

    public TextPanel(){
        area = new JTextArea("           Welcome to Osmowsis Simulation System", 5, 60);
//        area.setBounds(10,30, 200,200);
        setLayout(new BorderLayout());
        add(area, BorderLayout.WEST);
        area.setFont(area.getFont().deriveFont(16f)); // will only change size to 12pt
        area.setBackground(null);
    }

    public JTextArea getTextArea() {
        return area;
    }
}
