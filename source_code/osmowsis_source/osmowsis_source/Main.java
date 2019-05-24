import javax.swing.*;
import javax.swing.border.Border;
import javax.xml.soap.Text;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.ByteOrder;

public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(1350, 800);
        frame.setTitle("SimDriver");

        // check for the test scenario file name
        if (args.length == 0) {
            new Error("Please provide input files", "Missing Input");
            System.out.println("ERROR: Test scenario file name not found.");
        } else {
            // Text panel
            TextPanel textPanel = new TextPanel();

            SimDriver monitorSim = new SimDriver();
            Initialization init = new Initialization(args[0], monitorSim, textPanel.getTextArea());
            View view = new View(monitorSim.getLawn().getGrids());
     //       System.out.println(view.getSize());

            // Button panel
            ButtonPanel btnPanel = new ButtonPanel(init);

            frame.setLayout(new BorderLayout());

            frame.add(btnPanel, BorderLayout.SOUTH);
            frame.add(view, BorderLayout.CENTER);
            //frame.add(new TextField("   "), BorderLayout.WEST);
            frame.add(textPanel, BorderLayout.NORTH);

            // adding another Jtext to the left
            JTextArea info = new JTextArea(monitorSim.getInitalLoading().toString());
            info.setBackground(null);
            frame.add(info, BorderLayout.WEST);

            frame.setVisible(true);

            init.startSimulation(view);
        }
    }


}
