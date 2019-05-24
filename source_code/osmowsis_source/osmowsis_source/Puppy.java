import java.util.*;

public class Puppy {
    private Integer puppyX;
    private Integer puppyY;
    private Double stayingPercentage;
    private String trackAction;
    private int trackNextX;
    private int trackNextY;
    private Lawn lawn;

    private HashMap<Direction, Integer> xDIR_MAP;
    private HashMap<Direction, Integer> yDIR_MAP;

    public Puppy(Integer puppyX, Integer puppyY, Double stayingPercentage, Lawn lawn) {
        this.puppyX = puppyX;
        this.puppyY = puppyY;
        this.stayingPercentage = stayingPercentage;
        this.lawn = lawn;

        xDIR_MAP = new HashMap<>();
        xDIR_MAP.put(Direction.N, 0);
        xDIR_MAP.put(Direction.NE, 1);
        xDIR_MAP.put(Direction.E, 1);
        xDIR_MAP.put(Direction.SE, 1);
        xDIR_MAP.put(Direction.S, 0);
        xDIR_MAP.put(Direction.SW, -1);
        xDIR_MAP.put(Direction.W, -1);
        xDIR_MAP.put(Direction.NW, -1);

        yDIR_MAP = new HashMap<>();
        yDIR_MAP.put(Direction.N, 1);
        yDIR_MAP.put(Direction.NE, 1);
        yDIR_MAP.put(Direction.E, 0);
        yDIR_MAP.put(Direction.SE, -1);
        yDIR_MAP.put(Direction.S, -1);
        yDIR_MAP.put(Direction.SW, -1);
        yDIR_MAP.put(Direction.W, 0);
        yDIR_MAP.put(Direction.NW, 1);
    }

    public Integer getPuppyX() {
        return puppyX;
    }

    public void setPuppyX(Integer puppyX) {
        this.puppyX = puppyX;
    }

    public Integer getPuppyY() {
        return puppyY;
    }

    public void setPuppyY(Integer puppyY) {
        this.puppyY = puppyY;
    }

    public Double getStayingPercentage() {
        return stayingPercentage;
    }

    public void setStayingPercentage(Double stayingPercentage) {
        this.stayingPercentage = stayingPercentage;
    }

    public String getTrackAction() {
        return trackAction;
    }

    public int getTrackNextX() {
        return trackNextX;
    }

    public int getTrackNextY() {
        return trackNextY;
    }

    // method to calcualte puppy's next move
    public void calculateAction(){

        Random rand = new Random();
        rand.setSeed(1);
        // use random value to see if puppy should be staying next turn
        boolean isStaying = (rand.nextInt(100) < this.stayingPercentage);
        if(isStaying){
            trackAction = "stay";
        } else{
            trackAction = "move";
            // temp list to save valid x & y positions that are valid for puppy to move to
            List<Integer> validXs = new ArrayList<>();
            List<Integer> validYs = new ArrayList<>();
            int xOrientation, yOrientation;
            // loop the surroundings of current puppy position
            for (int i = 0; i < 8; i++) {
                xOrientation = xDIR_MAP.get(Direction.valueOf(i));
                yOrientation = yDIR_MAP.get(Direction.valueOf(i));
                int newSquareX = this.puppyX + xOrientation;
                int newSquareY = this.puppyY + yOrientation;

                // check if the square is a fence or outisde of the lawn
                /*
                one square cannot have two puppies!!!
                */
                if (newSquareX < lawn.getWidth() && newSquareY < lawn.getHeight() && newSquareX >= 0 && newSquareY >= 0
                        && lawn.getGrids()[newSquareX][newSquareY].getState() != State.CRATER &&
                        !lawn.getGrids()[newSquareX][newSquareY].isHasPuppy()) {
                    // add position to list
                    validXs.add(newSquareX);
                    validYs.add(newSquareY);
                }
            }
            // randomly select which position to go to using a index
            int idxToMove = rand.nextInt(validXs.size());
            this.trackNextX = validXs.get(idxToMove);
            this.trackNextY = validYs.get(idxToMove);
        }
    }
}
