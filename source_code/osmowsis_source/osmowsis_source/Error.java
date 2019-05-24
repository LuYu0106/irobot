import javax.swing.*;

public class Error {
    String rrorMessage;
    String title;

    public Error(String errorMessage, String title) {
        errorMessage = errorMessage;
        this.title = title;
        JOptionPane.showMessageDialog(new JFrame(),
                errorMessage,
                title,
                JOptionPane.ERROR_MESSAGE);
    }
}
