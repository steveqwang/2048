package uwaterloo.ca.lab3_203_08;

import android.app.Activity;
import android.content.Context;
import android.widget.RelativeLayout;

import java.util.LinkedList;
import java.util.Random;
import java.util.TimerTask;


public class GameLoopTask extends TimerTask{

    public enum eDir{LEFT, RIGHT, UP, DOWN, NM}

    // set up variables for construtor
    private RelativeLayout gameloopRL;
    private Context gameloopCTX;
    private Activity thisActivity;

    // values for the distance of the block
    public static final int LEFT_BOUNDARY = 22;
    public static final int UP_BOUNDARY = 22;
    public static final int SLOT_ISOLATION = 233;
    public static final int RIGHT_BOUNDARY = LEFT_BOUNDARY + 3*SLOT_ISOLATION;
    public static final int DOWN_BOUNDARY = UP_BOUNDARY + 3*SLOT_ISOLATION;
    public static final int NUMBER_OF_SLOTS = 16;
    public static final int WINNING_NUMBER = 256;

    private LinkedList<GameBlock> myGBList;

    private Random myRandomGen;
    private boolean generateBlock;

    private boolean endGameFlag = false;

    private void createBlock(){

        // test if the random number generated is already occupied by previous blocks
        boolean[][] boardOccupence = {{false, false, false, false},
                {false, false, false, false},
                {false, false, false, false},
                {false, false, false, false}};
        int[] currentGBCoord;
        int[] currentGBindex = {0,0};
        int[] newGBCoord = {0,0};
        int numberOfEmptySlots = NUMBER_OF_SLOTS;
        int randomSlotNum = 0;

        myRandomGen = new Random();

        for(GameBlock gb : myGBList){
            currentGBCoord = gb.getTargetCoordinate();
            currentGBindex[0] = (currentGBCoord[0] - LEFT_BOUNDARY) / SLOT_ISOLATION;
            currentGBindex[1] = (currentGBCoord[1] - UP_BOUNDARY) / SLOT_ISOLATION;
            boardOccupence[currentGBindex[1]][currentGBindex[0]] = true;
            numberOfEmptySlots--;
        }
        if(numberOfEmptySlots == 0){
            endGameFlag = true;
            return;
        }
        randomSlotNum = myRandomGen.nextInt(numberOfEmptySlots);
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                if(!boardOccupence[i][j]){
                    if(randomSlotNum == 0){
                        newGBCoord[0] = LEFT_BOUNDARY + j * SLOT_ISOLATION;
                        newGBCoord[1] = UP_BOUNDARY + i * SLOT_ISOLATION;
                    }
                    randomSlotNum--;
                }
            }
        }
        GameBlock newBlock;
        newBlock = new GameBlock(gameloopCTX, gameloopRL, newGBCoord[0], newGBCoord[1], this);
        myGBList.add(newBlock);

    }


    public GameLoopTask (Activity myActivity, RelativeLayout rl, Context ctx){
        thisActivity = myActivity;
        gameloopCTX = ctx;
        gameloopRL = rl;
        myGBList = new LinkedList<GameBlock>();
        createBlock();
    }
    // pass the directions got from accelerometer to the blocks
    public void setDirection(eDir targetDir){

        boolean noPendingMovement = true;
        if(endGameFlag) return;

        for(GameBlock gb : myGBList)
            if(gb.setDestination(targetDir))
                noPendingMovement = false;
        generateBlock = !noPendingMovement;
    }

    public GameBlock isOccupied(int coordX, int coordY){
        int[] checkCoord;
        for(GameBlock gb : myGBList){
            checkCoord = gb.getCurrentCoordinate();
            if(checkCoord[0] == coordX && checkCoord[1] == coordY){
                return gb;
            }
        }

        return null;
    }

    public void run(){

        thisActivity.runOnUiThread(
                new Runnable(){
                    public void run() {
                        boolean noMotion = true;
                        LinkedList<GameBlock> removalList = new LinkedList<GameBlock>();

                        for(GameBlock gb : myGBList) {
                            gb.move();

                            if(gb.getBlockNumber() == WINNING_NUMBER){
                                endGameFlag = true;
                            }

                            if(gb.isToBeDestroyed()){
                                removalList.add(gb);
                            }

                            if(gb.getDirection() != eDir.NM) {
                                noMotion = false;
                            }
                        }

                        if(noMotion){

                            if(generateBlock) {
                                createBlock();
                                generateBlock = false;
                            }

                            for(GameBlock gb : removalList){
                                gb.destroyMe();
                                myGBList.remove(gb);
                            }
                        }
                    }
                }
        );
    }

}
