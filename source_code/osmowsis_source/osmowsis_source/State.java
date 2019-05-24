import java.awt.*;

public enum State {
    EMPTY(0),
    GRASS(1),
    CRATER(2),
    FENCE(3),
    UNKNOWN(-1);

    private int code;

    State(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void draw(Graphics g, int x, int y, int gridSize) {
        int r;
        int gr;
        int b;
        int alpha;
        if (getCode() == 0) {
            r = 255;
            gr = 255;
            b = 255;
            alpha = 200;

        } else if (getCode() == 1) {
            r = 50;
            gr = 255;
            b = 50;
            alpha = 150;
        } else {
            r = 20;
            gr = 20;
            b = 20;
            alpha = 200;
        }
        g.setColor(new Color(r, gr, b, alpha));
        g.fillRect(x, y, gridSize, gridSize);
    }
}
