import javafx.geometry.Pos;

import java.util.*;

public class Mower {

    //constant property
    private int mowerIndex;
    private int collisionDelay;

    //current property
    private Integer mowerX;
    private Integer mowerY;
    private Direction mowerDirection;

    private int stallTurn;
    private boolean isOnPuppy;// only set by sim
    private boolean isMowerOn;// only set by sim
    private int validStep; // only set by sim

    //reported action
    private String trackAction;
    private int trackMoveDistance;
    private Direction trackNewDirection;

    //future property
    private Queue<Direction> futureRoute;
    private Position futureTargetPos;
    private String trackNextAction;
    private Position nextGeneratedPos = null;

    private String trackMoveCheck; // only set by sim
    private int numOfSquaresMoved;
    private boolean initialStalling;


    public Mower(Integer mowerX, Integer mowerY, int collisionDelay, int index) {

//        scannedPos = new HashSet<Position>();
        futureRoute = null;
        this.mowerX = mowerX;
        this.mowerY = mowerY;
        /*mowerCurPos = new Position();
        mowerCurPos.setX(0);
        mowerCurPos.setY(0);*/
        this.collisionDelay = collisionDelay;
        trackNextAction = "unKnown";
        this.numOfSquaresMoved = 0;
        this.stallTurn = 0;
        isMowerOn = true;
        this.mowerIndex = index;
    }




            /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    /********************* TODO: adding or change logic use the new data structues   *******************************/
    /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    public void calculateAction() {
        boolean isAbleToMove =  checkIsAbleToMove();

        if (!isAbleToMove){
            return;
        } else {
//            System.out.println("----------------------------------------------------------------");
//            System.out.println("mower is able to move... ");
            // if target still has grass and the futureRoute can produce next action:
            if (this.futureRoute != null && this.futureRoute.size() > 0 && InfoCollection.getMemory().get(futureTargetPos).getState().getCode() == 1) {
//                System.out.println("mower has future route and target position still has grass ");
                genetateActionFromGivenRoute();
                // analyze if generated action is valid: mower is a problem.
                boolean isValid = analyzeGeneratedActionValidity();
                if (!isValid) {
//                    System.out.println("mower generated invalid action caused by mower, need to calculate route again  ");
                    calculateFutureRouteFromBFS(new Position(mowerX, mowerY), futureTargetPos); //analyzeGoAhead => analyze otherdirections within one step => analyze other directions within two steps => BFS search
                    genetateActionFromGivenRoute();
                }

            //if future target is mowered by other mower although we still have prepared action
            } else if (this.futureRoute != null && this.futureRoute.size() > 0 && InfoCollection.getMemory().get(futureTargetPos).getState().getCode() == 0){
                // do bfs search
                bfsSearchFutureSquare(new Position(mowerX, mowerY));// avoid original pos and also other mower's target pos: using infocollection
                if (futureTargetPos != null){
                    calculateFutureRouteFromBFS(new Position(mowerX, mowerY), futureTargetPos);
                    genetateActionFromGivenRoute();
                }else{
                    trackAction = "turn_off";
                }
            }else{
                //start over the whole set of analysis
                boolean hasUnknown = InfoCollection.hasUnknownDir(mowerX, mowerY);
                if (!hasUnknown) {
//                    System.out.println("mower knows everything around it");
                    startOverAllAnalysis();
                }
                else {
//                    System.out.println("mower has unknow around it");
                    trackAction = "scan";
                }
            }
        }
    }

    // check if mower is able to move based its current status
    private boolean checkIsAbleToMove() {
        if (!isMowerOn) return false;
        if (stallTurn > 0 || isOnPuppy) return false;
        return true;

    }

    // check if current generated action based on future route is valid given all the new positions of other mowers
    //this action can only be "move"
    private boolean analyzeGeneratedActionValidity() {
        if (trackMoveDistance == 0) return true;
        Position nextOneStepPos = getPosOnDirection(mowerDirection, new Position(mowerX, mowerY));
        if (trackMoveDistance == 1){
            nextGeneratedPos = nextOneStepPos;
            if (InfoCollection.hasMowerOnPos(nextOneStepPos)){
                return false;
            }
        }
        if (trackMoveDistance == 2){
            Position nextTwoStepPos = getPosOnDirection(mowerDirection, nextOneStepPos);
            nextGeneratedPos = nextTwoStepPos;
            if (InfoCollection.hasMowerOnPos(nextTwoStepPos)){
                return false;
            }
        }
        return true;
    }

    private void startOverAllAnalysis() {
//        System.out.println("mower start OverAll Analysis, and mower's "+ new Position(mowerX, mowerY).toString() + ".  and mower's direction is: " +  mowerDirection.getCode());
        boolean hasFoundGrass = false;
        int[] res = analyzeGoAhead(mowerX, mowerY, mowerDirection);
        //System.out.println("You are back from analyzeGoAhead() and in pollMowerForAction() function now, the res is that: can you walk ahead?" + res[0] + ", if can, how many steps: " + res[1] + ", what is the future direction in new position? " +res[2] + ", " +res[3] );
        //System.out.println("start assign actions according to res " );
        if (res[0] == 0) {
//            System.out.println("you did not get anything if you going ahead, so searching other direction within 1 step");
            int nextDir = mowerDirection.getCode() + 1;
            while (!hasFoundGrass && nextDir != mowerDirection.getCode()) {
//                System.out.println("looop1");
                nextDir = (nextDir + 1) % 8;
//                System.out.println(nextDir);
//                System.out.println("you next snanning direction is: " + directions[nextDir] );
//                System.out.println("the status is : " + getStatusOnDirection(directions[nextDir], mowerCurPos) );
                if (getSquareOnDirection(Direction.valueOf(nextDir), new Position(mowerX, mowerY)).getState() == State.GRASS) {
                    trackMoveDistance = 0;
                    trackNewDirection = Direction.valueOf(nextDir);
                    trackAction = "move";
                    hasFoundGrass = true;
                }
            }

            if (hasFoundGrass){
//                System.out.println("you find grass within 1 step");
//                System.out.println("looop2");
                if (nextDir == mowerDirection.getCode()){
                    trackMoveDistance = 1;
                    trackNewDirection = Direction.valueOf(geneateHalfRandomDirection(getPosOnDirection(mowerDirection, new Position(mowerX, mowerY)), mowerDirection, false, null) );
                    trackAction = "move";
                }else{
                    trackMoveDistance = 0;
                    trackNewDirection = Direction.valueOf(nextDir);
                    trackAction = "move";
                }
            }else {
//                System.out.println("after seaching other directions, did you find a direction? " + hasFound+ "if found, what is it? "+ trackNewDirection );
//                System.out.println("you did not find anything within one step from other directions, you start searching two steps from other directions...  "  );
                nextDir = mowerDirection.getCode()+1;
                while (!hasFoundGrass && nextDir != mowerDirection.getCode()) {
//                    System.out.println("looop3");
                    nextDir = (nextDir + 1) % 8;
                    res = analyzeGoAhead(mowerX, mowerY, Direction.valueOf(nextDir));
                    if (res[0] != 0){
                        hasFoundGrass = true;
                    }
//                    System.out.println("did you find anything from this direction: " + nextDir + "---" + hasFound);
                }

                if (hasFoundGrass){
                    getActionFromGoAheadAnalysis(res);
                }else {
                    //System.out.println("you did not find anything within two steps, start doing BFS search and find new stratehies");
                    bfsSearchFutureSquare(new Position(mowerX, mowerY));// avoid original pos and also other mower's target pos: using infocollection
                    if (futureTargetPos != null){
                        calculateFutureRouteFromBFS(new Position(mowerX, mowerY), futureTargetPos);
                        genetateActionFromGivenRoute();
                    }else{
                        trackAction = "turn_off";
                    }
                }
            }
        } else {
            getActionFromGoAheadAnalysis(res);
        }

    }

    //this function is used when mower standing in one position and using bfs to search the shortest route and using this route to generate action
    public void genetateActionFromGivenRoute(){
        //System.out.println("your current direction: " + mowerDirection);
        //System.out.println("you are going to this direction: " + futureRoute.peek());
        Direction routeTopDir = futureRoute.peek();
        if (routeTopDir != null){ // it will never be null
            if (!routeTopDir.equals(mowerDirection)){
                trackMoveDistance = 0;
                //System.out.println("do not have to move but need to change to this direction: " + routeTopDir);
                trackNewDirection = routeTopDir; // do not poll
            }else{
                futureRoute.poll();
                Direction routeNextDir = futureRoute.peek();
                if (routeNextDir != null && routeNextDir == routeTopDir){
                    futureRoute.poll();
                    trackMoveDistance = 2;
                    Direction routeThirdDir = futureRoute.peek();
                    if (routeThirdDir != null){
                        trackNewDirection = routeThirdDir;
                    }else{
                        trackNewDirection = routeNextDir;
                    }

                }else if (routeNextDir != null && routeNextDir.getCode() == routeTopDir.getCode()){
                    trackMoveDistance = 1;
                    trackNewDirection = routeNextDir;
                }else{
                    trackMoveDistance = 1;
                    trackNewDirection = routeTopDir;
                }
            }
        }
        trackAction = "move";
    }

    // BFS method to calculate shortest another position with grass and without puppy given a position
    private void bfsSearchFutureSquare(Position pos) {
        futureTargetPos = null;
        Queue<Position> queue = new LinkedList<Position>();
        queue.add(pos);
        HashSet<Position> visited =  new HashSet<Position>();
        visited.add(pos);
        while (!queue.isEmpty()){
            Position temp = queue.poll();
            for (int i = 0; i<8; i++) {
                Position newPos = new Position(temp.getX() + Position.getSurround()[i][0], temp.getY() + Position.getSurround()[i][1]);
                if (InfoCollection.getMemory().containsKey(newPos) && !visited.contains(newPos)) {
                    if (InfoCollection.getSquareOnPos(newPos).getState() == State.GRASS) {
                        futureTargetPos =  newPos;
                    }
                    queue.offer(newPos);
                    visited.add(newPos);
                }
            }
        }
    }

    // this function is trying to calculate future route from current position to future position
    public void calculateFutureRouteFromBFS(Position cur, Position future) {
        futureRoute = new LinkedList<Direction>();
        Queue<Queue<Direction>> queue = new LinkedList<Queue<Direction>>();
        Queue<Position> posQueue = new LinkedList<Position>();
        Queue<Direction> tempStack = new LinkedList<Direction>();
        queue.offer(tempStack);
        posQueue.offer(cur);
        HashSet<Position> visited = new HashSet<Position>();
        visited.add(cur);
        bfsSearchShortestRoute(queue, posQueue, visited);
    }

    // help function for calculating shortest route
    private void bfsSearchShortestRoute(Queue<Queue<Direction>> queue, Queue<Position> posQueue, HashSet<Position> visited) {
        while (!queue.isEmpty()){
            Queue<Direction> temp = queue.poll();
            Position tempPos = posQueue.poll();
            boolean isEnd = false;
            //System.out.println("you start second round BFS to search shorted route, here you are standing at : " + tempPos);
            for (int i=0; i<8;i++){
                Position tempFuturePos = getPosOnDirection(Direction.valueOf(i), tempPos);

                if (!visited.contains(tempFuturePos) && (InfoCollection.getSquareOnPos(tempFuturePos).getState() == State.EMPTY
                        || InfoCollection.getSquareOnPos(tempFuturePos).getState() == State.GRASS)){
                    visited.add(tempFuturePos);
                    Queue<Direction> tempCopy = new LinkedList<>(temp);
                    tempCopy.offer(Direction.valueOf(i));
                    queue.offer(tempCopy);
                    posQueue.offer(tempFuturePos);//
                    if (InfoCollection.getSquareOnPos(tempFuturePos).getState() == State.GRASS) {
                        isEnd = true;
                        futureRoute = tempCopy;
                        //System.out.println("you find the route: " + tempCopy.size());
                        break;
                    }

                }

            }
            if (isEnd) break;
        }
    }

    //return the pointed neightbour position's status
    public Square getSquareOnDirection(Direction dir, Position pos) {
//        Position pos = new Position(x, y);
        Position targetPos = getPosOnDirection(dir, pos);
        return InfoCollection.getSquareOnPos(targetPos);
    }

    // return an array based on at least you know surrounding within one step
    // 1 tells if it should go ahead within two steps(1 mean yes, 0 mean no), 2 tells how many steps if yes,
    // 3 tells future direction in future pos(mightbe -1),
    private int[] analyzeGoAhead(int x, int y, Direction dir) {//used when this pos is scanned
//        System.out.println("you are analyze the square ahead of you and your direction now is: " + dir.getCode());
        Position pos = new Position(x, y);
        int[] aheadAnalysisRes = {0, 0, 9};
        Position oneStepFurtherPos = getPosOnDirection(dir, pos);
        Position twoStepFurtherPos = getPosOnDirection(dir, oneStepFurtherPos);

//        System.out.println("one Step  " + oneStepFurtherPos.toString());
//        System.out.println("one Step status is: " + getSquareOnDirection(dir, pos).getState());
//        System.out.println("two Step " + twoStepFurtherPos.toString());
//        System.out.println("two Step status is: " + getSquareOnDirection(dir, oneStepFurtherPos).getState());

        Square pointedSquare = getSquareOnDirection(dir, pos);
        // pointed pos has obstacles: return res[0] = 0;
        if (pointedSquare.getState() == State.CRATER || pointedSquare.getState() == State.FENCE){
            aheadAnalysisRes[0] = 0;
        // pointed pos has no obstacles and it is grass; check if can go two steps
        }else if(pointedSquare.getState() == State.GRASS) {
            // check the second step further pos(this might be unknown): if it is grass and no puppy
//            System.out.println("go ahead analyis: you can go one step and it is grass, now check if you can go 2 step  ");
            Square twoStepSquare = getSquareOnDirection(dir, oneStepFurtherPos);
            // furthur two steps have grass
            if(twoStepSquare.getState() == State.GRASS) {
//                System.out.println("go ahead analyis: 2nd step is also grass  ");
                aheadAnalysisRes[0] = 1;
                aheadAnalysisRes[1] = 2;
                aheadAnalysisRes[2] = geneateHalfRandomDirection(twoStepFurtherPos, dir, true, pos);
                //aheadAnalysisRes[2] = directionsMap.get(dir);
            }else if(twoStepSquare.getState() == State.UNKNOWN) {
//                System.out.println("go ahead analyis: 2nd step is UNKNOWN  ");
                aheadAnalysisRes[0] = 1;
                aheadAnalysisRes[1] = 1;
                aheadAnalysisRes[2] = dir.getCode();
                //aheadAnalysisRes[2] = directionsMap.get(dir);
            }else{
//                System.out.println("go ahead analyis: 2nd step is: state is might be crater, fence or empty " );
                aheadAnalysisRes[0] = 1;
                aheadAnalysisRes[1] = 1;
                aheadAnalysisRes[2] = geneateHalfRandomDirection(oneStepFurtherPos, dir, false, null);
                //aheadAnalysisRes[2] = directionsMap.get(dir);
            }
        }else if( pointedSquare.getState() == State.UNKNOWN){
            trackAction = "scan";
        }else{ // furthur one square be empty
//            System.out.println("go ahead analyis: 1st step is empty, you check if you it has grass on 2nd step...  ");
            Square twoStepSquare = getSquareOnDirection(dir, oneStepFurtherPos);
            if( twoStepSquare.getState() == State.GRASS ) {
                //System.out.println("line 369:you are going to walk 1 step, and calculate next direction  ");
                aheadAnalysisRes[0] = 1;
                aheadAnalysisRes[1] = 2;
                aheadAnalysisRes[2] = geneateHalfRandomDirection(twoStepFurtherPos, dir, true, pos);
                //aheadAnalysisRes[2] = directionsMap.get(dir);
            }else if( twoStepSquare.getState() == State.UNKNOWN ) {
                //System.out.println("line 369:you are going to walk 1 step, and calculate next direction  ");
                aheadAnalysisRes[0] = 1;
                aheadAnalysisRes[1] = 1;
                aheadAnalysisRes[2] = dir.getCode();;
                //aheadAnalysisRes[2] = directionsMap.get(dir);
            }else aheadAnalysisRes[0] = 0;
        }
        return aheadAnalysisRes;
    }

    // help function to get next action straight from  aheadAnalysisRes function
    public void getActionFromGoAheadAnalysis(int[] aheadAnalysisRes){
        trackMoveDistance = aheadAnalysisRes[1];
        if (aheadAnalysisRes[2] != -1){
            trackNewDirection = Direction.valueOf(aheadAnalysisRes[2]); //should tell mower he should has know the next step
        }else{
            trackNewDirection = Direction.N;
        }
        trackAction = "move";
//        System.out.println("you finished go ahead analysism and the genetated action is:  ");
//        System.out.println("trackMoveDistance:  " + trackMoveDistance);
//        System.out.println("trackNewDirection:  " + trackNewDirection);
//        System.out.println("trackAction:  " + trackAction);
    }

//    //get next direction from future route
//    public void getNextDirGivenRoute(Position pos){
//        if (futureRoute.size()>0) trackNewDirection = futureRoute.peek();
//        else {
//            int newDirection = getNextDirection(pos, mowerDirection);
//            if (newDirection == -1){
//                trackNewDirection = mowerDirection;
//                trackNextAction = "turn_off";
//            }else{
//                trackNewDirection = Direction.valueOf(newDirection);
//            }
//        }
//    }

    // not sure what is this for? I do not know who write it
    public void mowerCollide(){
        // check if the mower is already stalling, prevent duplicate stalls
        if(this.stallTurn == 0){
            trackAction = "stall";
            this.trackNextAction = "stall";
            this.setInitialStalling(true);
        }

    }


    // return the pointed neightbour position given a direction and a position
    public Position getPosOnDirection(Direction dir, Position pos) {
        //System.out.println("you are trying calculate the further step postion" );
        Position tempPos = new Position();
        if (dir.getCode() == Direction.N.getCode()) {
            //System.out.println(" ( " + surround[0][] + " , " +  surround[0][0]+ " ) ");
            tempPos.setX(pos.getX() + Position.getSurround()[0][0]);
            tempPos.setY(pos.getY() + Position.getSurround()[0][1]);
        }else if(dir.getCode() == Direction.NE.getCode()) {
            tempPos.setX(pos.getX() + Position.getSurround()[1][0]);
            tempPos.setY(pos.getY() + Position.getSurround()[1][1]);
        }else if(dir.getCode() == Direction.E.getCode()) {
            tempPos.setX(pos.getX() + Position.getSurround()[2][0]);
            tempPos.setY(pos.getY() + Position.getSurround()[2][1]);
        }else if(dir.getCode() == Direction.SE.getCode()) {
            tempPos.setX(pos.getX() + Position.getSurround()[3][0]);
            tempPos.setY(pos.getY() + Position.getSurround()[3][1]);
        }else if(dir.getCode() == Direction.S.getCode()) {
            tempPos.setX(pos.getX() + Position.getSurround()[4][0]);
            tempPos.setY(pos.getY() + Position.getSurround()[4][1]);
        }else if(dir.getCode() == Direction.SW.getCode()) {
            tempPos.setX(pos.getX() + Position.getSurround()[5][0]);
            tempPos.setY(pos.getY() + Position.getSurround()[5][1]);
        }else if(dir.getCode() == Direction.W.getCode()) {
            tempPos.setX(pos.getX() + Position.getSurround()[6][0]);
            tempPos.setY(pos.getY() + Position.getSurround()[6][1]);
        }else{
            tempPos.setX(pos.getX() + Position.getSurround()[7][0]);
            tempPos.setY(pos.getY() + Position.getSurround()[7][1]);
        }
        return tempPos;
    }

    // do not use this function, it is having bugs
//    public void generateRandomAction(int x, int y){
//        Position pos = new Position(x, y);
//        Direction randomDir = Direction.valueOf(geneateHalfRandomDirection(pos, mowerDirection));
//
//        Position oneStep = getPosOnDirection(randomDir, pos);
//        Position twoStep = getPosOnDirection(randomDir, oneStep);
//        if (mowerDirection.equals(randomDir)){
//            if (InfoCollection.getSquareOnPos(twoStep).getState() == State.EMPTY){
//                trackMoveDistance = 2;
//                trackNewDirection = Direction.valueOf(geneateHalfRandomDirection(twoStep, mowerDirection));
//            }else{
//                trackMoveDistance = 1;
//                trackNewDirection = Direction.valueOf(geneateHalfRandomDirection(oneStep, mowerDirection));
//            }
//        }else{
//            trackMoveDistance = 0;
//            trackNewDirection = randomDir;
//        }
//
//    }

    //mower use this to generate a random direction if no grass direction found
    public int geneateHalfRandomDirection(Position pos, Direction dir, boolean isGeneratBasedOnStep2, Position stepOnePos){
//        System.out.println("in  geneateHalfRandomDirection function..., standing in position: " + pos.toString() + ", dir is: " + dir.getCode());
        Position surPos = new Position();
        ArrayList<Integer> availableRoutes = new ArrayList<Integer>();
        int dirCodeWithGrass = -1;
        int bestDirCode = -1;
        for (int i = 0; i<8; i++) {//

            surPos.setX(pos.getX() + Position.getSurround()[i][0]);
            surPos.setY(pos.getY() + Position.getSurround()[i][1]);
//            System.out.println("you are scanning this position: " + surPos.toString());
//            System.out.println("this position's status is " + InfoCollection.getSquareOnPos(surPos).getState());
            // if for calculating step2 future direction, need to consider the trouble bring by step1 position status: imagine take step one as empty directly
            if (isGeneratBasedOnStep2){
                if (surPos.getX() == stepOnePos.getX() && surPos.getY() == stepOnePos.getY()){
                    availableRoutes.add(i);
                    continue;
                }
            }
            if (InfoCollection.getSquareOnPos(surPos).getState() == State.GRASS ){
                dirCodeWithGrass = i;
            }
            if ((InfoCollection.getSquareOnPos(surPos).getState() == State.GRASS && i == dir.getCode()) || InfoCollection.getSquareOnPos(surPos).getState() == State.UNKNOWN  && i == dir.getCode()){
                bestDirCode = i;
            }
            if ( InfoCollection.getSquareOnPos(surPos).getState() == State.EMPTY || InfoCollection.getSquareOnPos(surPos).getState() == State.UNKNOWN) {
                availableRoutes.add(i);
            }
        }
//        System.out.println("do you find your best direction: " + bestDirCode + ". if it is -1, then you did not find the best one, you need to change direction");
//        System.out.println("do you find your a direction with grass: " + dirCodeWithGrass + ". if it is -1, then you did not a direction with grass, maybe because some is unknow");
        if (bestDirCode != -1) return bestDirCode;

        if (dirCodeWithGrass == -1){
            Random rn = new Random();
            rn.setSeed(1);
            int index = rn.nextInt(availableRoutes.size() - 1 + 1);
            int targetdir = availableRoutes.get(index);
            return targetdir;
        }else return dirCodeWithGrass;

    }

    public String getTrackNextAction() {
        return trackNextAction;
    }

    public void setTrackNextAction(String trackNextAction) {
        this.trackNextAction = trackNextAction;
    }

    public String getTrackAction() {
        return trackAction;
    }

    public void setTrackAction(String trackAction) {
        this.trackAction = trackAction;
    }

    public Integer getTrackMoveDistance() {
        return trackMoveDistance;
    }

    public void setTrackMoveDistance(Integer trackMoveDistance) {
        this.trackMoveDistance = trackMoveDistance;
    }

    public Direction getTrackNewDirection() {
        return trackNewDirection;
    }

    public void setTrackNewDirection(Direction trackNewDirection) {
        this.trackNewDirection = trackNewDirection;
    }

    public String getTrackMoveCheck() {
        return trackMoveCheck;
    }

    public void setTrackMoveCheck(String trackMoveCheck) {
        this.trackMoveCheck = trackMoveCheck;
    }

    public Direction getMowerDirection() {
        return mowerDirection;
    }

    public void setMowerDirection(Direction mowerDirection) {
        this.mowerDirection = mowerDirection;
    }

    public Integer getMowerX() {
        return mowerX;
    }

    public void setMowerX(Integer mowerX) {
        this.mowerX = mowerX;
    }

    public Integer getMowerY() {
        return mowerY;
    }

    public void setMowerY(Integer mowerY) {
        this.mowerY = mowerY;
    }

    public int getNumOfSquaresMoved() { return this.numOfSquaresMoved; }

    public void addNumOfSquaresMoved(int num) { this.numOfSquaresMoved += num; }

    public int getStallTurn() { return this.stallTurn; }

    public void setStallTurn(int s) { this.stallTurn = s; }

    public int getCollisionDelay() { return collisionDelay; }

    public boolean isInitialStalling() {
        return initialStalling;
    }

    public void setInitialStalling(boolean initialStalling) {
        this.initialStalling = initialStalling;
    }

    public boolean getIsOnPuppy(){ return isOnPuppy; }

    public void setOnPuppy(boolean b){ this.isOnPuppy = b; }

    public boolean isMowerOn() {
        return isMowerOn;
    }

    public void setMowerOn(boolean mowerOn) {
        isMowerOn = mowerOn;
    }

    public int getValidStep() {
        return validStep;
    }

    public void setValidStep(int validStep) {
        this.validStep = validStep;
    }

//    // calculating the next direction suppose mower is standing on the future position
//    private int getNextDirection(Position futurePos, Direction dir) {
////        //System.out.println("you are in getNextDirection() function, suppose you are already in this new position: " + futurePos);
//        //calculate the next direction suppose mower has moved to the new postion
//        Direction targetDir = Direction.N;//generate random
//        //System.out.println("the status code you are point to in new positon with original direction: " + gridStatusMap.get(getStatusOnDirection(dir, futurePos)));
//        if ( getSquareOnDirection(dir, futurePos).getState() == State.GRASS
//                || getSquareOnDirection(dir, futurePos).getState() == State.UNKNOWN) {
//            //System.out.println("no need complicated calculating since you are facing grass or unknow things!");
//            return dir.getCode();
//        }else {
//            //System.out.println("you start scanning different direction clockwisely..., and your postion is scanned? " + isScanned(futurePos));
//            int i = 0;
//            boolean findTarget = false;
//            int curDirNum = dir.getCode();
//            while (i < 8 && !findTarget){
//                curDirNum = (curDirNum + 1) % 8;
//                //System.out.println("you are scanning suppose in new pos(see the pic in this turn): " + directions[curDirNum]);
//                if ( !findTarget && getSquareOnDirection(Direction.valueOf(curDirNum), futurePos).getState() == State.GRASS){
//                    targetDir = Direction.valueOf(curDirNum);
//                    //System.out.println("after scanning, your chosen dir is : " + targetDir);
//                    findTarget = true;
//                }
//                i++;
//            }
//            if (!findTarget) {
//                targetDir = dir;
//            }
//            //System.out.println("your calculated direction in getNextDirection() function is: " + targetDir);
//            return targetDir.getCode();
//        }
//    }
}
