public class Lawn {
    private int width;
    private int height;
    private Square[][] grids;
    private int numberOfGrass;
    private int squareNumber;
    private int originalGrassNumber;

    public Lawn(int width, int height) {
        this.width = width;
        this.height = height;
        grids = new Square[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                grids[i][j] = new Square(State.GRASS);
            }
        }
        numberOfGrass = width * height;
        squareNumber = width * height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Square[][] getGrids() {
        return grids;
    }

    public void setGrids(Square[][] grids) {
        this.grids = grids;
    }

    public int getNumberOfGrass() {
        return numberOfGrass;
    }

    public void setNumberOfGrass(int numberOfGrass) {
        this.numberOfGrass = numberOfGrass;
    }

    public int getSquareNumber() {
        return squareNumber;
    }

    public void setSquareNumber(int squareNumber) {
        this.squareNumber = squareNumber;
    }

    public int getOriginalGrassNumber() {
        return originalGrassNumber;
    }

    public void setOriginalGrassNumber(int originalGrassNumber) {
        this.originalGrassNumber = originalGrassNumber;
    }

    public void cutGrass(){
        this.numberOfGrass --;
    }

    public int getCutNumberOfGrass(){return originalGrassNumber - numberOfGrass;}

}
