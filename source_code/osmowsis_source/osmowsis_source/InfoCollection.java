import java.util.*;

public class InfoCollection {
    private static HashMap<Position, Square> memory;
    private static HashSet<Position> scannedPos;
    private static Position[] tagetPositions;
    // add mowers target positions
    private static int numOfSquareWithGrass;

    private static int highestAxis; // this is inclusive,I mean it has object at this position
    private static int rightestAxis;

    private static boolean hasRightFence;
    private static boolean hasTopFence;
    private static int rightFencePos = -9;
    private static int topFencePos = -9;

    public static int getHighestAxis() {
        return highestAxis;
    }

    public static void setHighestAxis(int newhighestAxis) {
        InfoCollection.highestAxis = newhighestAxis;

    }

    public static int getRightestAxis() {
        return rightestAxis;
    }

    public static void setRightestAxis(int rightestAxis) {
        InfoCollection.rightestAxis = rightestAxis;
    }


    public InfoCollection(){

    }


    public static boolean isScanned(int x, int y) {
        Position pos = new Position(x, y);
        if (scannedPos.contains(pos)) return true;
        else if (!hasUnknownDir(x, y)) {
            scannedPos.add(pos);
            return true;
        } else return false;
    }

    public static HashSet<Position> getScannedPos() {
        return scannedPos;
    }
    public static HashMap<Position, Square> getMemory(){ return memory; }

    public static void initialize() {
        scannedPos = new HashSet<Position>();
        memory = new HashMap<Position, Square>();
    }

    // TODO: initiate memory once all mowers positions are known ***** DONE ******
    public static void initialUpdate(int longestX, int longestY, List<Mower> mowers) {
//        System.out.println("the rightest axis that mower knows: " + longestX);
//        System.out.println("the highest axis that mower knows: " + longestY);
        for (int i = 0; i <= longestX; i++) {
            for (int j = 0; j <= longestY; j++) {
                memory.put(new Position(i, j), new Square(State.UNKNOWN));
            }
        }
        for (int i = -1; i <= longestX; i++) {
            memory.put(new Position(i, -1), new Square(State.FENCE));
        }
        for (int j = 0; j <= longestX; j++) {
            memory.put(new Position(-1, j), new Square(State.FENCE));
        }
        for (int i = 0; i < mowers.size(); i++){
            Mower mower = mowers.get(i);
            memory.put(new Position(mower.getMowerX(), mower.getMowerY()), new Square(false, true, State.EMPTY ));
        }
        tagetPositions = new Position[mowers.size()];
        numOfSquareWithGrass = 0;

        rightestAxis = longestX;
        highestAxis = longestY;



    }

//    public static boolean checkHasGrassInMemory() {
//        if (numOfSquareWithGrass == 0) return false;
//        return true;
//
//    }

    // check if a mower is within this given position
    public static boolean hasMowerOnPos(Position nextOneStepPos) {
        if (memory.get(nextOneStepPos).isHasMower()){
            return true;
        }
        return false;
    }

    //check if there are any unkown given a position
    public static boolean hasUnknownDir(int mowerX, int mowerY) {
        Position pos = new Position(mowerX, mowerY);
        int totalNumUnkown = 0;
        Position surPos = new Position();
        for (int i = 0; i<8; i++) {//
            int x = pos.getX() + Position.getSurround()[i][0];
            int y = pos.getY() + Position.getSurround()[i][1];
            surPos.setX(x);
            surPos.setY(y);
//            System.out.println("rightFencePos: " + rightFencePos);
//            System.out.println("topFencePos: " + topFencePos);
//            System.out.println("each direction is knowun: " + getSquareOnPos(surPos).getState()) ;
            if ( getSquareOnPos(surPos).getState() == State.UNKNOWN ) totalNumUnkown += 1;
        }

        if (totalNumUnkown > 0) return true;
        else {
            scannedPos.add(pos);
            return false;
        }
    }

    public static Square getSquareOnPos(Position pos) {
        if ((hasRightFence && pos.getX() >= rightFencePos) || (hasTopFence && pos.getY() >= topFencePos)) {
            return new Square(State.FENCE);
        }else if (memory.containsKey(pos)) {
            return memory.get(pos);
        }else{
            return new Square(State.UNKNOWN);
        }
    }

    public static void putMemory(Position p, Square s){
        memory.put(p, s);
    }

    public static void updateVertical(int newSquareX) {
        rightestAxis = newSquareX;
        for (int i = -1; i <= InfoCollection.highestAxis; i++) {
            if (i == -1) {
                memory.put(new Position(newSquareX, i), new Square(State.FENCE));
            }else {
                memory.put(new Position(newSquareX, i), new Square(State.UNKNOWN));
            }
        }
    }

    public static void updateParallel(int newSquareY) {
        highestAxis = newSquareY;
        for (int j = -1; j <= rightestAxis; j++) {
            if (j == -1){
                memory.put(new Position(j, newSquareY), new Square(State.FENCE));
            }else {
                memory.put(new Position(j, newSquareY), new Square(State.UNKNOWN));
            }
        }
    }

    public static void addNewRowColInMemory(int newSquareX, int newSquareY) {
        if (newSquareX > rightestAxis){
            updateVertical(newSquareX);
        }

        if (newSquareY > highestAxis){
            updateParallel(newSquareY);
        }
    }

//    public static void addFence(boolean overX, int newSquareX, boolean overY, int newSquareY) {
//        if (overX){
//            hasRightFence = true;
//            rightestAxis = newSquareX;
//            rightFencePos = newSquareX;
//        }
//
//        if (overY){
//            hasTopFence = true;
//            highestAxis = newSquareY;
//            topFencePos = newSquareY;
//        }
//
//    }
    public static void addTopRightFence(boolean overX, int newSquareX, boolean overY, int newSquareY) {
        if (overX){
            hasRightFence = true;
            rightestAxis = newSquareX;
            rightFencePos = newSquareX;
        }

        if (overY){
            hasTopFence = true;
            highestAxis = newSquareY;
            topFencePos = newSquareY;
        }
    }

    public static void addLeftBottomFence(boolean lessThanX0, int newSquareX, boolean lessThanY0, int newSquareY) {
        if (lessThanX0){
            memory.put(new Position(newSquareX, newSquareY), new Square(State.FENCE));
        }

        if (lessThanY0){
            memory.put(new Position(newSquareX, newSquareY), new Square(State.FENCE));
        }
    }
}
