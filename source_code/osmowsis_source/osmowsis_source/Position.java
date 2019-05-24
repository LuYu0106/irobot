public class Position {
    private int x;
    private int y;
    private static int[][] surround;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
        surround = new int[][]{ {0,1}, {1,1},{1,0}, {1,-1}, {0,-1}, {-1,-1}, {-1,0}, {-1,1} };
    }

    public Position() {

    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object ob) {
        if (ob == this) return true;
        if (!(ob instanceof Position)) return false;
        Position pos = (Position) ob;
        return (pos.getX() == this.x && pos.getY() == this.y);
    }

    @Override
    public int hashCode() {
        return this.x + this.y;
    }

    @Override
    public String toString() {
        return "Position is: (" + this.getX() + ", " + this.getY() + ")";
    }

    public static int[][] getSurround(){
        return surround;
    }
}


