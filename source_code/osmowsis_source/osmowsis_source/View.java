import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class View extends JPanel{
    private static final long serialVersionUID = -2417015700213488315L;
    private static  int GRID_SIZE = 64;
    private BufferedImage dog;
    private BufferedImage mower;
    private BufferedImage dogHalf;
    private BufferedImage mowerHalf;
    private Square[][] lawnInfo;

    public View(Square[][] lawnInfo){
        this.lawnInfo = lawnInfo;
        if (lawnInfo.length * lawnInfo[0].length > 50){
            GRID_SIZE = 64;
            try {
                dog = ImageIO.read(new File("./imgs/pawprint64.png"));
                mower = ImageIO.read(new File("./imgs/lawn-mower64.png"));
                dogHalf = ImageIO.read(new File("./imgs/pawprint32.png"));
                mowerHalf = ImageIO.read(new File("./imgs/lawn-mower32.png"));
            } catch (IOException e) {
                System.err.println("Can't find image file");
            }
        }else {
            try {
                dog = ImageIO.read(new File("./imgs/pawprint64.png"));
                mower = ImageIO.read(new File("./imgs/lawn-mower64.png"));
                dogHalf = ImageIO.read(new File("./imgs/pawprint32.png"));
                mowerHalf = ImageIO.read(new File("./imgs/lawn-mower32.png"));
            } catch (IOException e) {
                System.err.println("Can't find image file");
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = GRID_SIZE-1;
        int height = GRID_SIZE-1;

        int ylen = lawnInfo.length; // lawn height
        int xlen = lawnInfo[0].length; // lawn width
        for (int i = 0; i < xlen; i++) {
            for (int j = 0; j < ylen; j++) {
                int factori = j * (width+1);
                int factorj = i * (height+1);

                Square square = lawnInfo[j][xlen-i-1];
                if (square.getState() == State.EMPTY){
                    g.setColor(new Color(224, 228, 229));
                    g.fillRect(factori, factorj, width, height);
                }else if (square.getState() == State.GRASS){
                    g.setColor(new Color(146, 238, 75,200));
                    g.fillRect(factori, factorj, width, height);
                }else if (square.getState() == State.CRATER){
                    g.setColor(Color.gray);
                    g.fillRect(factori, factorj, width, height);
                }
                if (square.isHasMower() && square.isHasPuppy()){
                    g.drawImage(mowerHalf, factori, factorj, this);
                    g.drawImage(dogHalf, factori+GRID_SIZE/2, factorj+GRID_SIZE/2, this);
                }else if (square.isHasMower()){
//                    g.setColor(Color.yellow);
                    g.drawImage(mower, factori, factorj, this);
                }else if (square.isHasPuppy()){
//                    g.setColor(Color.yellow);
                    g.drawImage(dog, factori, factorj, this);
                }
                g.setColor(Color.black);
                g.drawRect(factori, factorj, width+1, height+1);
            }
        }

        for (int i = 0 ; i < ylen; i++){
            g.setColor(Color.black);
            g.drawString(String.valueOf(i), i*GRID_SIZE + GRID_SIZE/5, xlen*GRID_SIZE + GRID_SIZE/5 );
        }

        for (int j = 0 ; j < xlen; j++){
            g.setColor(Color.black);
            g.drawString(String.valueOf(xlen-j-1), ylen*GRID_SIZE + GRID_SIZE/5, GRID_SIZE*j + GRID_SIZE/5 );
        }
    }

}
