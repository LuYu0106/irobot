import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonPanel extends JPanel{
    private JButton stopBtn;
    private JButton slowBtn;
    private JButton fastBtn;
    private Initialization init;

    public ButtonPanel(Initialization init){
        this.init = init;
        // three buttons: stop, step and fastforward
        stopBtn = new JButton("STOP");
        slowBtn = new JButton("NEXT");
        fastBtn = new JButton("FAST-FORWARD");

        // Button design
        stopBtn.setPreferredSize(new Dimension(100, 36));
        stopBtn.setFont(new Font("Arial",Font.PLAIN, 18));
        Dimension btnSize = stopBtn.getPreferredSize();
        slowBtn.setPreferredSize(btnSize);
        slowBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        fastBtn.setPreferredSize(new Dimension(200, 36));
        fastBtn.setFont(new Font("Arial", Font.PLAIN, 18));

        // Button listeners
        stopBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                init.getMonitorSim().setSystemOn(false);
                init.terminate();
            }
        });

        slowBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                init.setGo(true);
                init.reversePause();
                init.setFastForward(false);
            }
        });

        fastBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                init.setGo(true);
                init.setFastForward(true);
            }
        });


        // Button layout
        setBorder(BorderFactory.createEtchedBorder());
        setLayout(new FlowLayout(FlowLayout.CENTER));
        add(stopBtn);
        add(slowBtn);
        add(fastBtn);
    }
}
