import javax.swing.*;
import java.util.List;

public class Initialization {

    private SimDriver monitorSim;
    private boolean go;
    private boolean pause;
    private boolean fastforward;
    private JTextArea area;
    private int totalTurns;


    public Initialization(String inputFile, SimDriver monitorSim, JTextArea area) {
        this.monitorSim = monitorSim;
        //monitorSim.uploadStartingFile("D:\\GT\\6310\\assignment 3\\test_scenarios\\test_scenarios\\scenario_1.csv");
        monitorSim.uploadStartingFile(inputFile);
        this.area = area;
        totalTurns = 0;

        go = false;
        pause = true;
        fastforward = false;
    }

    public void startSimulation(View view){
        // simulation sleeps at the beginning, waiting for buttons to be pressed
        while (!go){
            sleep();
        }
        // run the simulation for a fixed number of steps
        outerloop:
        for(int turns = 0; turns < monitorSim.getMaxTurn(); turns++) {

            //TODO: modify logic here: check if monitorSim is off: no need to pull even the first mower (**** DONE *****)
            if (!monitorSim.isSystemOn()) {
                System.out.println(monitorSim.getLawn().getSquareNumber() + "," + monitorSim.getLawn().getOriginalGrassNumber() + "," + (monitorSim.getLawn().getOriginalGrassNumber() - monitorSim.getLawn().getNumberOfGrass()) + "," + totalTurns);
                break;
            }
            totalTurns++;

            //loop through all mowers
            for (int m = 0; m < monitorSim.getMowerList().size(); m++) {
                // if no button is pressed, the simulation goes to sleep; until step button or fast button pressed, simulation wakes up to continue
                while (pause && !fastforward) {
                    sleep();
                }

                if (!monitorSim.isSystemOn()){
                    terminate();
                    break outerloop;
                }

                Mower mower = monitorSim.getMowerList().get(m);
                monitorSim.pollMowerForAction(mower);
                monitorSim.validateMowerAction(mower);
                monitorSim.displayMowerActionAndResponses(m);
                // finish an object step, repaint the frame
                view.repaint();
                // dialog output
                area.setText(SimDriver.logPrintOut.toString() + "\n                         Turn number: " + totalTurns + "     " + "Number of grass cut so far: " + Integer.valueOf(monitorSim.getLawn().getOriginalGrassNumber() - monitorSim.getLawn().getNumberOfGrass()));

          //      area.setText(SimDriver.logPrintOut.toString());

                // reverse pause setting
                reversePause();
                sleep();
            }

            //loop through all puppies
            List<Puppy> puppyList = monitorSim.getPuppyList();
            for(int p = 0; p < puppyList.size(); p++){
                // if no button is pressed, the simulation goes to sleep; until step button or fast button pressed, simulation wakes up to continue
                while (pause && !fastforward) {
                    sleep();
                }

                if (!monitorSim.isSystemOn()){
                    terminate();
                    break outerloop;
                }

                Puppy curPuppy = puppyList.get(p);
                monitorSim.pollPuppyForAction(curPuppy);
                monitorSim.validatePuppyAction(curPuppy);
                monitorSim.displayPuppyActionAndResponses(p+1, curPuppy);
                // finish an object step, repaint the frame
                view.repaint();

                // dialog output
                area.setText(SimDriver.logPrintOut.toString() + "\n                         Turn number: " + totalTurns +"     " + "Number of grass cut so far: " + Integer.valueOf(monitorSim.getLawn().getOriginalGrassNumber() - monitorSim.getLawn().getNumberOfGrass()));
                //area.setText(SimDriver.logPrintOut.toString());
                // reverse pause setting
                reversePause();

                sleep();
            }

            if (!monitorSim.isSystemOn() || totalTurns == monitorSim.getMaxTurn()) {
                terminate();
                break;
            }

        }
    }

    private void sleep(){
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void terminate(){
        FileWriterUtil.writeLine(monitorSim.getLawn().getSquareNumber() + "," + monitorSim.getLawn().getOriginalGrassNumber() + "," + (monitorSim.getLawn().getOriginalGrassNumber() - monitorSim.getLawn().getNumberOfGrass()) + "," + totalTurns);
        System.out.println(monitorSim.getLawn().getSquareNumber() + "," + monitorSim.getLawn().getOriginalGrassNumber() + "," + (monitorSim.getLawn().getOriginalGrassNumber() - monitorSim.getLawn().getNumberOfGrass()) + "," + totalTurns);
        String s = "Total square number: " + monitorSim.getLawn().getSquareNumber() + ", " +
                "Original grass number: " + monitorSim.getLawn().getOriginalGrassNumber() + ", " +
                "Cut grass number: " + monitorSim.getLawn().getCutNumberOfGrass() + ", " +
                "Total number of turns: " + totalTurns;
        area.setText(s);
        FileWriterUtil.closeFile();
    }

    public SimDriver getMonitorSim() {
        return monitorSim;
    }

    public void setGo(boolean go){
        this.go = go;
    }

    public void reversePause(){
        pause = !pause;
    }

    public void setFastForward(boolean fast) {
        fastforward = fast;
    }
}
