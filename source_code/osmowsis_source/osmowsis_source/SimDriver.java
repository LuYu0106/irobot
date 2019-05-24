import java.util.*;
import java.io.*;

public class SimDriver {
    private static Random randGenerator;

    private Lawn lawn;
    private List<Mower> mowerList = new ArrayList<>();
    private List<Puppy> puppyList = new ArrayList<>();
    private List<Position> craters = new ArrayList<>();


    private boolean isSystemOn;
    private Integer totalTurn;
    private Integer maxTurn;

    private String trackMowercheck;
    private String action;

    private HashMap<Direction, Integer> xDIR_MAP;
    private HashMap<Direction, Integer> yDIR_MAP;

    private String trackScanResults;

    public static StringBuilder logPrintOut;
    public static StringBuilder initalLoading;


    public SimDriver() {

        randGenerator = new Random();
        randGenerator.setSeed(1);

        logPrintOut = new StringBuilder();
        initalLoading = new StringBuilder();

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

        isSystemOn = true;

        InfoCollection.initialize();

        FileWriterUtil fileWriterUtil = new FileWriterUtil();
        fileWriterUtil.initialize();
    }



    public void uploadStartingFile(String testFileName) {
        final String DELIMITER = ",";

        try {
            Scanner takeCommand = new Scanner(new File(testFileName));
            String[] tokens;
            int i, j, k;
            initalLoading.append(  "\n");
            initalLoading.append("     Initial information after loading   " +  "\n");
            initalLoading.append(  "\n");


            // read in the lawn information
            tokens = takeCommand.nextLine().split(DELIMITER);
            int lawnWidth = Integer.parseInt(tokens[0]);

           // System.out.println(lawnWidth);
            initalLoading.append("     Lawn width: " + lawnWidth + "    "+  "\n");

            tokens = takeCommand.nextLine().split(DELIMITER);
            int lawnHeight = Integer.parseInt(tokens[0]);

            // System.out.println(lawnHeight);
            initalLoading.append("     Lawn width: " + lawnHeight + "    "+  "\n");

            // generate the lawn information
            lawn = new Lawn(lawnWidth, lawnHeight);

            // read in the lawnmower starting information; record the largest x pos and largest y pos to update shared memory initally
            tokens = takeCommand.nextLine().split(DELIMITER);
            int numMowers = Integer.parseInt(tokens[0]);
            //System.out.println("# of mower " + numMowers);
            initalLoading.append("     NumBer of mower: " + numMowers + "    "+  "\n");

            tokens = takeCommand.nextLine().split(DELIMITER);
            int mowerCollisionDelay = Integer.parseInt(tokens[0]);
            //System.out.println("collision delay " + mowerCollisionDelay);
            initalLoading.append("     Collision delay: " + mowerCollisionDelay + "    "+  "\n");

            int largerstX = 0;
            int largestY = 0;
            for (k = 0; k < numMowers; k++) {
                tokens = takeCommand.nextLine().split(DELIMITER);
                int curMowerX = Integer.parseInt(tokens[0]);
                int curMowerY = Integer.parseInt(tokens[1]);
                if (curMowerX < 0 || curMowerX >= lawnWidth || curMowerY < 0 || curMowerY >= lawnHeight) {
                    new Error("invalid input for mower", "invalid input");
                }

                // add a new mower, set x & y, and set initial direction
                mowerList.add(new Mower(curMowerX, curMowerY, mowerCollisionDelay, k));
                String curDir = tokens[2];
                mowerList.get(k).setMowerDirection(getDirection(curDir));

              //  System.out.println("mower#" + k  + " pos" +" "+  curMowerX +" "+ curMowerY +" "+ curDir);
                initalLoading.append("     Mower#" + (k + 1)+": "  +" "+  curMowerX +" "+ curMowerY +" "+ curDir + "    "+  "\n");

                // mow the grass at the initial location
                lawn.getGrids()[curMowerX][curMowerY].setState(State.EMPTY);
                lawn.getGrids()[curMowerX][curMowerY].setHasMower(true);
                lawn.cutGrass();
                // TODO: update largest x and y pos among all mowers: (**** DONE *****)
                if (curMowerX > largerstX){
                    largerstX = curMowerX;
                }
                if (curMowerY > largestY){
                    largestY = curMowerY;
                }
            }

            // TODO: make initial update of shared memory based on mower inital position: (**** DONE *****)
            InfoCollection.initialUpdate( largerstX, largestY, mowerList );

            // read in the crater information
            tokens = takeCommand.nextLine().split(DELIMITER);
            int numCraters = Integer.parseInt(tokens[0]);
            // set original number of grass squares
            //System.out.println("crater number " + numCraters);
            initalLoading.append("     Number of craters: " + numCraters + "    "+  "\n");

            lawn.setOriginalGrassNumber(lawn.getSquareNumber() - numCraters);
            // loop through craters
            for (k = 0; k < numCraters; k++) {
                tokens = takeCommand.nextLine().split(DELIMITER);

                // place a crater at the given location
                Integer craterX = Integer.parseInt(tokens[0]);
                Integer craterY = Integer.parseInt(tokens[1]);
                lawn.getGrids()[craterX][craterY].setState(State.CRATER);
                lawn.cutGrass();
                craters.add(new Position(craterX, craterY));

              //  System.out.println("crater Location " + "crater #" + k + " " + craterX +" " +  craterY);
                initalLoading.append("     Crater#" + (k+1) + ": " + " " + craterX +" " +  craterY + "    "+  "\n");
            }

            // read puppies information
            tokens = takeCommand.nextLine().split(DELIMITER);
            if(tokens != null && tokens.length > 0){
                int numPuppies = Integer.parseInt(tokens[0]);
                //System.out.println("Number of puppy " + numPuppies);
                initalLoading.append("     Number of puppies: " + numPuppies + "    "+  "\n");
                tokens = takeCommand.nextLine().split(DELIMITER);
                Double puppyMovingPerc = Double.parseDouble(tokens[0]);
                //System.out.println("Percentage  " + puppyMovingPerc);
                initalLoading.append("     Puppy moving%: " + puppyMovingPerc + "    "+  "\n");
                for(k = 0; k < numPuppies; k++){
                    tokens = takeCommand.nextLine().split(DELIMITER);
                    int puppyX = Integer.parseInt(tokens[0]);
                    int puppyY = Integer.parseInt(tokens[1]);
                    puppyList.add(new Puppy(puppyX, puppyY, puppyMovingPerc, lawn));
                    lawn.getGrids()[puppyX][puppyY].setHasPuppy(true);
                  //  System.out.println(" puppy location " + "puppy #" + k + " "+ puppyX + " " +  puppyY);
                    initalLoading.append("     Puppy #" + (k+1) + ": " + puppyX + " " +  puppyY + "    "+  "\n");
                }

                // read in max number of turns allowed
                maxTurn = takeCommand.nextInt();
                //System.out.println("max number of turn " + maxTurn);
                initalLoading.append("     Max number of turns: " + maxTurn + "    "+  "\n");
            }
            takeCommand.close();

        } catch (Exception e) {
            new Error("Invalid file, something is wrong", "Invalid");
            e.printStackTrace();
        }
    }


    public void pollMowerForAction(Mower mower) {

        int indexMower = mowerList.indexOf(mower) + 1;
        //System.out.println("=====*****************==========");
       // System.out.println("mower no# " + indexMower + " cur pos(ui) and dir " + (mower.getMowerX()) +" "+ (mower.getMowerY())+" " + mower.getMowerDirection());

        mower.calculateAction();
    }

    //after valid, the judge from sim could be: pass(no need to move), ok(mower give right action including turnoff) , stall(give steps also), crash
    public void validateMowerAction(Mower mower) {
        int xOrientation, yOrientation;
        trackScanResults = "";

        // check if mower status before valid its action, since it might be no need to validate at all(mower might working even when it should not)
        if( !mower.isMowerOn() || (mower.getIsOnPuppy() && mower.getStallTurn() == 0)){ // mower turn off or mower hits puppy but not the other mower
            mower.setTrackMoveCheck("pass1");
            return;
        }else if(mower.getStallTurn() > 0){ // mower hits the other mower, count down the stall turns
            mower.setStallTurn(mower.getStallTurn() - 1);
            mower.setTrackMoveCheck("pass2");
            return;
        }

        if (mower.getTrackAction().equals("scan")) {

            /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
            /*********************TODO: adding new logic for scan:  *******************************/
            /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

            Square[] squaresScanned = new Square[8];
            Position[] posScanned = new Position[8];
            boolean[] withinScope = new boolean[8];
            for (int i = 0; i < 8; i++) {
                // looping thru x and y positions
                xOrientation = xDIR_MAP.get(Direction.valueOf(i));
                yOrientation = yDIR_MAP.get(Direction.valueOf(i));
                int newSquareX = mower.getMowerX() + xOrientation;
                int newSquareY = mower.getMowerY() + yOrientation;

                // add new rows or columns into memory and set them to unknow first
                InfoCollection.addNewRowColInMemory(newSquareX, newSquareY);
                String squareStatus = "";
                if (newSquareX >= lawn.getWidth() || newSquareY >= lawn.getHeight() || newSquareX < 0 || newSquareY < 0){

                    InfoCollection.addTopRightFence(newSquareX >= lawn.getWidth(), newSquareX, newSquareY >= lawn.getHeight(), newSquareY);
                    InfoCollection.addLeftBottomFence(newSquareX < 0, newSquareX, newSquareY < 0, newSquareY);
                    squareStatus = "fence";
                }else{
                    Square curlawnSquare = lawn.getGrids()[newSquareX][newSquareY];
                    squaresScanned[i] = new Square(curlawnSquare.isHasPuppy(), curlawnSquare.isHasMower(), curlawnSquare.getState());
                    posScanned[i] = new Position(newSquareX, newSquareY);
                    withinScope[i] = true;

                    if (curlawnSquare.isHasPuppy() && curlawnSquare.isHasMower()) squareStatus = "puppy_mower";
                    else if (curlawnSquare.isHasPuppy() && curlawnSquare.getState() == State.EMPTY) squareStatus = "puppy_empty";
                    else if (curlawnSquare.isHasPuppy() && curlawnSquare.getState() == State.GRASS) squareStatus = "puppy_grass";
                    else if (curlawnSquare.getState() == State.GRASS) squareStatus = "grass";
                    else if (curlawnSquare.getState() == State.CRATER) squareStatus = "crater";
                    else if (curlawnSquare.isHasMower()) squareStatus = "mower";
                    else squareStatus = "empty";

//                    System.out.println("Dir for " + i + ":...");
//                    System.out.println("do you have Puppy :  " + curlawnSquare.isHasPuppy());
//                    System.out.println("The state:  " + curlawnSquare.getState());
//                    System.out.println("do you have Mower:  " + curlawnSquare.isHasMower());
                }

                if (trackScanResults.length() == 0) trackScanResults = squareStatus;
                else trackScanResults = trackScanResults + "," + squareStatus;

//                // square to hold information about the square being scanned
//                Square square;
//                // check if the square is a fence or outisde of the lawn
//                if (newSquareX >= lawn.getWidth() || newSquareY >= lawn.getHeight() || newSquareX < 0 || newSquareY < 0)
//                    square = new Square(State.FENCE);
//                else square = lawn.getGrids()[newSquareX][newSquareY];
//                Position mowerPosition = new Position(mower.getMowerX(), mower.getMowerY());
//                InfoCollection.putMemory(mower.getPosOnDirection(Direction.valueOf(i), mowerPosition), square);
//                if (trackScanResults.length() == 0) trackScanResults = square.toString();
//                else trackScanResults = trackScanResults + "," + square.toString();
            }
            for (int i = 0; i < 8; i++) {
                if (withinScope[i]) {
                    InfoCollection.putMemory( posScanned[i], squaresScanned[i] );
//                    System.out.println("State for pos of (" + posScanned[i].getX() + ", " + posScanned[i].getY() + "): " + InfoCollection.getMemory().get(posScanned[i]));
//                    System.out.println("State for mower pos of (" + mower.getMowerX() + ", " + mower.getMowerY() + "): " + InfoCollection.getMemory().get(new Position(mower.getMowerX(), mower.getMowerY())).isHasMower());

                }
            }

        } else if (mower.getTrackAction().equals("move")) {
            validateMoveAction(mower.getTrackMoveDistance(), mower);
        } else if (mower.getTrackAction().equals("turn_off")) {
            mower.setMowerOn(false);
            updateSimStatus();// set sim to off if this is the last active mower, else, no change
            mower.setTrackMoveCheck("ok"); // not sure what to show

        }
        // Uncomment for mower collision
//        else if (mower.getTrackAction().equals("stall")) {
//            int stallTurn = mower.getStallTurn();
//            if(mower.isInitialStalling()){
//                mower.setStallTurn(mower.getCollisionDelay());
//                mower.setInitialStalling(false);
//            }
//            else if(stallTurn > 0){
//                mower.setStallTurn(stallTurn - 1);
//            }else{
//                mower.setTrackNextAction("unKown");
//            }
//        }
        checkIfAllCut();
}

    private void updateSimStatus() {
        int total = mowerList.size();
        for (Mower mower : mowerList){
            if (!mower.isMowerOn()){
                total --;
            }
        }
        if (total == 0) isSystemOn = false;
    }

    //question: what if crash at the second step, should we update its position
    //? question: crash, does mower know what happend? it is a fence or a crater, does it know what is the position of these obstatcles
    private void validateMoveAction(int distance, Mower mower) {

        /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
        /********************* TODO: adding new logic for mower validation  *******************************/
        /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
        int xOrientation, yOrientation;
        // in the case of a move, ensure that the move doesn't cross craters or fences
        xOrientation = xDIR_MAP.get(mower.getMowerDirection());
        yOrientation = yDIR_MAP.get(mower.getMowerDirection());

        if (distance == 0) {
            mower.setMowerDirection(mower.getTrackNewDirection());
            mower.setTrackMoveCheck("ok");
        } else{

            //check first step directly no matter one step or two step
            int oneStepX = mower.getMowerX() + 1 * xOrientation;
            int oneStepY = mower.getMowerY() + 1 * yOrientation;

            //only return "ok", "crash", "stall by mower"(may have mower and puppy at the same time, "stall by puppy"
            String checkres = checkIfSafe(oneStepX, oneStepY);
            if (checkres.equals("crash")){ // sim do related crash work, could put them into one method
                InfoCollection.putMemory(new Position(oneStepX, oneStepY), new Square(State.CRATER));// random give it, we should not have this happened
                mower.setMowerOn(false);
                updateSimStatus();
                mower.setTrackMoveCheck("crash");
            }else if (checkres.equals("stall by mower")){
                mower.setStallTurn(mower.getCollisionDelay());
                mower.setValidStep(0);
                mower.setTrackMoveCheck("stall by mower");
            }else if (checkres.equals("stall by puppy")){
                // change lawn square status, mower position, not direction, memory square status
                updateMowerLawnStatus(mower,oneStepX, oneStepY, mower.getMowerX(), mower.getMowerY(),  true); // everything is ok. change mower position, lawn square status, memory square status

                mower.setValidStep(1);
                mower.setTrackMoveCheck("stall by puppy");
            }else{
                updateMowerLawnStatus(mower,oneStepX, oneStepY, mower.getMowerX(), mower.getMowerY(), false); // change mower position, lawn square status, memory square status
                mower.setTrackMoveCheck("ok");
                if (distance == 1) { // if everything is good
                   // System.out.println("you are going to move 1 steps... and ok" );
                    return;
                }else{ // if it is 2
//                    System.out.println("you are going to move 2 steps..., checking if 2nd step is valid " );
                    int twoStepX = oneStepX + 1 * xOrientation;
                    int twoStepY = oneStepY + 1 * yOrientation;
                    String checkTwoStepPosRes = checkIfSafe(twoStepX, twoStepY);
                    if (checkTwoStepPosRes.equals("crash")){ // sim do related crash work, could put them into one method
                        mower.setMowerOn(false);
                        updateSimStatus();
                        mower.setTrackMoveCheck("crash");
                    }else if (checkTwoStepPosRes.equals("stall by mower")){
//                        System.out.println("check if stall by mower: state in step 2" + InfoCollection.getMemory().get(new Position(twoStepX, twoStepY)).getState() );
                        mower.setStallTurn(mower.getCollisionDelay());
                        mower.setValidStep(1);
                        mower.setTrackMoveCheck("stall by mower");
                    }else if (checkTwoStepPosRes.equals("stall by puppy")){
//                        System.out.println("check if stall by puppy: state in step 2" + InfoCollection.getMemory().get(new Position(twoStepX, twoStepY)).getState() );

                        // change lawn square status, mower position, not direction, memory square status
                        updateMowerLawnStatus(mower,twoStepX, twoStepY, oneStepX, oneStepY, true); // everything is ok. change mower position, lawn square status, memory square status

                        mower.setValidStep(2);
                        mower.setTrackMoveCheck("stall by puppy");

                    }else{
                        updateMowerLawnStatus(mower,twoStepX, twoStepY, oneStepX, oneStepY, false); // everything is ok. change mower position, lawn square status, memory square status
                        mower.setTrackMoveCheck("ok");
                        //update grass number
                    }
                }
            }
        }
    }

    //update status when check result is ok
    private void updateMowerLawnStatus(Mower mower, int x, int y, int oldX, int oldY, boolean stallByPuppy) {
        // update mower status including mower position and direction
//        System.out.println("SIM: you are allowed to move one step and stallByPuppy: " + stallByPuppy );
        mower.setMowerX(x);
        mower.setMowerY(y);
        if (!stallByPuppy){
            mower.setMowerDirection(mower.getTrackNewDirection());
        }
        //update lawns status
        lawn.getGrids()[x][y].setHasMower(true);
        if (lawn.getGrids()[x][y].getState() == State.GRASS){
            lawn.cutGrass();
        }
        lawn.getGrids()[x][y].setState(State.EMPTY);
        lawn.getGrids()[oldX][oldY].setHasMower(false);
        //update memory
        InfoCollection.putMemory(new Position(x, y), new Square(State.EMPTY));
    }

    private String checkIfSafe(int oneStepX, int oneStepY) {
        Square square = InfoCollection.getMemory().get(new Position(oneStepX, oneStepY));
        String collideWithMower = mowerCollide(oneStepX, oneStepY);
        String collideWithPuppy = collidePuppy(oneStepX, oneStepY);
        String collideWithStatic = collideWithStaticObstable(oneStepX, oneStepY);
        if (collideWithMower.equals("stall by mower")) return "stall by mower";
        if (collideWithPuppy.equals("stall by puppy")) return "stall by puppy";
        if (collideWithStatic.equals("crash")) return "crash";
        return "ok";

    }

    private String collideWithStaticObstable(int x, int y) {
        if (x >= lawn.getWidth() || y >= lawn.getHeight() || x < 0 || y < 0 ) return "crash";
        for (int i = 0; i < craters.size(); i++) {
            // check which one is the unlucky mower
            if(craters.get(i).getX() == x && craters.get(i).getY() == y){
                // mowerList.get(i).mowerCollide();
                return "crash";
            }
        }
        return "";
    }

    // TODO: use this method when mower collide with each other
    private String mowerCollide(int x, int y){
        for (int i = 0; i < mowerList.size(); i++) {
            // check which one is the unlucky mower
            if(mowerList.get(i).getMowerX() == x && mowerList.get(i).getMowerY() == y){
                // mowerList.get(i).mowerCollide();
                return "stall by mower";
            }
        }
        return "";
    }

    private String collidePuppy(int x, int y){
        for (int i = 0; i < puppyList.size(); i++) {
            // check which one is the unlucky mower
            if(puppyList.get(i).getPuppyX() == x && puppyList.get(i).getPuppyY() == y){
                return "stall by puppy";
            }
        }
        return "";
    }


    // method to poll puppies actions
    public void pollPuppyForAction(Puppy puppy) {
        int indexPuppy = puppyList.indexOf(puppy) + 1;
   //     System.out.println("=====*****************==========");
    //    System.out.println("puppy no# " + indexPuppy + " cur pos(ui)" + (puppy.getPuppyX()) +" "+ (puppy.getPuppyY()));
        puppy.calculateAction();
    }

    public void validatePuppyAction(Puppy puppy) {
        /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
        /********************* validate puppy action   *******************************/
        /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
        if (puppy.getTrackAction().equals("move")){
            int puppyNewX = puppy.getTrackNextX();
            int puppyNewY = puppy.getTrackNextY();
            // set square hasPuppy to false before moving puppy
            lawn.getGrids()[puppy.getPuppyX()][puppy.getPuppyY()].setHasPuppy(false);
            puppy.setPuppyX(puppyNewX);
            puppy.setPuppyY(puppyNewY);
            Square puppySqaure = lawn.getGrids()[puppyNewX][puppyNewY];
            if (puppySqaure.isHasMower()){
                puppyCollide(puppyNewX, puppyNewY);
            }
            lawn.getGrids()[puppyNewX][puppyNewY].setHasPuppy(true);
        } else if (puppy.getTrackAction().equals("stay")){
            if(lawn.getGrids()[puppy.getPuppyX()][puppy.getPuppyY()].isHasMower()){
                puppyCollide(puppy.getPuppyX(), puppy.getPuppyY());
            }
        }
    }



    private void puppyCollide(int x, int y){
        for (int i = 0; i < mowerList.size(); i++) {
            // check which one is the unlucky mower
            if(mowerList.get(i).getMowerX() == x && mowerList.get(i).getMowerY() == y){
                mowerList.get(i).setOnPuppy(true);
            }
        }
    }


    // TODO: ADD LOGIC TO DO THE FOLLOWING THINGS:
    // things not clear in new logic:
    // who is going to check if all mowered are stoped.: simulation
    // 1. who is going to check if all grass is mowered : simulation
    // update the status of all the mowers:
    // 2. when to update the status


    public void checkIfAllCut() {
        this.isSystemOn = lawn.getNumberOfGrass() != 0;
    }

    public void displayPuppyActionAndResponses(int puppyIdx, Puppy puppy){
        logPrintOut = new StringBuilder();

        if (puppyIdx + 1 > puppyList.size()){
            logPrintOut.append("                         ");
            logPrintOut.append("Next object: mower ,");
            logPrintOut.append(Integer.toString(1));
            logPrintOut.append(" (" + (mowerList.get(0).getMowerX()) +", "+ (mowerList.get(0).getMowerY())+ ")");
            logPrintOut.append("\n");
        }else{
            logPrintOut.append("                         ");
            logPrintOut.append("Next object: puppy,");
            logPrintOut.append(Integer.toString(puppyIdx + 1));
            logPrintOut.append(" (" + (puppyList.get(puppyIdx).getPuppyX()) +", "+ (puppyList.get(puppyIdx).getPuppyY())+ ")");
            logPrintOut.append("\n");
        }

        FileWriterUtil.writeLine("puppy," + puppyIdx);
        System.out.println("puppy," + puppyIdx);
        logPrintOut.append("                         ");
        logPrintOut.append("puppy,");
        logPrintOut.append(Integer.toString(puppyIdx));


        logPrintOut.append("\n");
        if (puppy.getTrackAction().equals("stay")){
            FileWriterUtil.writeLine("stay");
            System.out.println("stay");
            logPrintOut.append("                         ");
            logPrintOut.append("stay");
        }else{
            logPrintOut.append("                         ");
            String s = "move," + puppy.getTrackNextX() + "," + puppy.getTrackNextY();
            FileWriterUtil.writeLine(s);
            System.out.println(s);
            logPrintOut.append(s);
        }
        FileWriterUtil.writeLine("ok");
        System.out.println("ok");
        logPrintOut.append("\n");
        logPrintOut.append("                         ");
        logPrintOut.append("ok");
    }


    public void displayMowerActionAndResponses(int mowerIdx) {

        /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
        /********************* adding new logic for display  *******************************/
        /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
        logPrintOut = new StringBuilder();
        Mower curMower = mowerList.get(mowerIdx);
        String s  = "";
        // display next polling object and its location
        if (mowerIdx + 2 > mowerList.size()){
            logPrintOut.append("                         ");
            logPrintOut.append("Next object: puppy,");
            logPrintOut.append(Integer.toString(1));
            logPrintOut.append(" (" + (puppyList.get(0).getPuppyX()) +", "+ (puppyList.get(0).getPuppyY())+ ")");
            logPrintOut.append("\n");
        }else{
            logPrintOut.append("                         ");
            logPrintOut.append("Next object: mower,");
            logPrintOut.append(Integer.toString(mowerIdx + 2));
            logPrintOut.append(" (" + (mowerList.get(mowerIdx+1).getMowerX()) +", "+ (mowerList.get(mowerIdx+1).getMowerY())+ ")");
            logPrintOut.append("\n");
        }
        // display the current mower's actions
        // if mower is on top of a puppy
        if(curMower.getIsOnPuppy()){
            curMower.setOnPuppy(false);
            s = "mower," + Integer.toString(mowerIdx + 1) + " (" + (curMower.getMowerX()) +", "+ (curMower.getMowerY())+ ")" +"\n                         stall";
            logPrintOut.append("                         ");
            logPrintOut.append(s);
            FileWriterUtil.writeLine("mower," + Integer.toString(mowerIdx + 1) + "\n" + "stall");
            System.out.println("mower," + Integer.toString(mowerIdx + 1) + "\n" + "stall");
            return;
        }

        // print out mower index and its current location
        FileWriterUtil.writeLine("mower," + (mowerIdx + 1));
        System.out.println("mower," + (mowerIdx + 1));
        logPrintOut.append("                         ");
        logPrintOut.append("mower,");
        logPrintOut.append(Integer.toString(mowerIdx + 1));
        logPrintOut.append(" (" + (curMower.getMowerX()) +", "+ (curMower.getMowerY())+ ")");
        logPrintOut.append("\n");
        // print out mower action: move, stall, scan...
        FileWriterUtil.write(curMower.getTrackAction());
        System.out.print(curMower.getTrackAction());
        logPrintOut.append("                         ");
        logPrintOut.append(curMower.getTrackAction());
        if (curMower.getTrackAction().equals("move")) {
            s = "," + curMower.getTrackMoveDistance() + "," + Direction.stringOf(curMower.getTrackNewDirection());
            System.out.println(s);
            FileWriterUtil.writeLine(s);
            logPrintOut.append(s);
        } else if (curMower.getTrackAction().equals("stall")) {
//            s = "," + curMower.getNumOfSquaresMoved();
//            System.out.println(s);
//            logPrintOut.append(s);
        } else {
            System.out.println();
            FileWriterUtil.writeLine("");
        }

        // display the simulation checks and/or responses
        if (curMower.getTrackAction().equals("move") || curMower.getTrackAction().equals("turn_off")) {
            if (curMower.getTrackMoveCheck().equals("pass1") || curMower.getTrackMoveCheck().equals("stall by puppy")){
                s = "stall";
            }else if (curMower.getTrackMoveCheck().equals("pass2") || curMower.getTrackMoveCheck().equals("stall by mower")){
                s = "stall," + curMower.getStallTurn();
            }else {
                s = curMower.getTrackMoveCheck();
            }
            System.out.println(s);
            FileWriterUtil.writeLine(s);
            logPrintOut.append("\n");
            logPrintOut.append("                         ");
            logPrintOut.append(s);
        } else if (curMower.getTrackAction().equals("scan")) {
            System.out.println(trackScanResults);
            FileWriterUtil.writeLine(trackScanResults);
            logPrintOut.append("\n");
            logPrintOut.append("                         ");
            logPrintOut.append(trackScanResults);
        }
        System.out.println("total cut grass: " + lawn.getCutNumberOfGrass());
    }


    private void renderHorizontalBar(int size) {
        System.out.print(" ");
        for (int k = 0; k < size; k++) {
            System.out.print("-");
        }
        System.out.println("");
    }

    public void renderLawn() {
        int i, j;
        int charWidth = 2 * lawn.getWidth() + 2;

        /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
        /********************* ***************************/
        /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

        // display the rows of the lawn from top to bottom
        for (j = lawn.getHeight() - 1; j >= 0; j--) {
            renderHorizontalBar(charWidth);

            // display the Y-direction identifier
            System.out.print(j);

            // display the contents of each square on this row
            for (i = 0; i < lawn.getWidth(); i++) {
                System.out.print("|");

                // the mower overrides all other contents
                // TODO: display multiple mowers
//                if (i == mower.getMowerX() & j == mower.getMowerY()) {
//                    System.out.print("M");
//                } else {
//                    switch (lawn.getGrids()[i][j].getState()) {
//                        case EMPTY:
//                            System.out.print(" ");
//                            break;
//                        case GRASS:
//                            System.out.print("g");
//                            break;
//                        case CRATER:
//                            System.out.print("c");
//                            break;
//                        default:
//                            break;
//                    }
//                }
            }
            System.out.println("|");
        }
        renderHorizontalBar(charWidth);

        // display the column X-direction identifiers
        System.out.print(" ");
        for (i = 0; i < lawn.getWidth(); i++) {
            System.out.print(" " + i);
        }
        System.out.println("");

        // display the mower's direction
        // TODO: print out mowers directions
//        System.out.println("dir: " + mower.getMowerDirection());
        System.out.println("");
    }


    public boolean isSystemOn() {
        return isSystemOn;
    }

    public void setSystemOn(boolean systemOn) {
        isSystemOn = systemOn;
    }

    public Lawn getLawn() {
        return lawn;
    }

    public List<Mower> getMowerList() {
        return mowerList;
    }

    public List<Puppy> getPuppyList() {
        return puppyList;
    }

    // method to convert direction string to direction enum
    private Direction getDirection(String dir){
        switch (dir.toLowerCase()){
            case "north":
                return Direction.N;
            case "northeast":
                return Direction.NE;
            case "east":
                return Direction.E;
            case "southeast":
                return Direction.SE;
            case "south":
                return Direction.S;
            case "southwest":
                return Direction.SW;
            case "west":
                return Direction.W;
            case "northwest":
                return Direction.NW;
        }
        return Direction.E;
    }
    public Integer getMaxTurn() {
        return maxTurn;
    }

    public void setMaxTurn(Integer maxTurn) {
        this.maxTurn = maxTurn;
    }

    public static StringBuilder getInitalLoading() {
        return initalLoading;
    }

    public static void setInitalLoading(StringBuilder initalLoading) {
        SimDriver.initalLoading = initalLoading;
    }
}