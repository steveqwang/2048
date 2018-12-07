package uwaterloo.ca.lab3_203_08;

import android.content.Context;
import android.graphics.Color;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.Gravity;

import java.util.Random;


public class GameBlock extends GameBlockTemplate {

    private final float GB_ACC = 4.0f;     // acceleration for blocks
    private final float IMAGE_SCALE = 0.6f; // Image scale for textviews
    float widthBlocks;float heightBlocks;
    private float IMAGE_SCALE_Blocks;      // image scale for blocks
    private final int TV_OFFSET = 25;     // margin value for textviews
    private int myCoordX;
    private int myCoordY;
    private int targetCoordX;
    private int targetCoordY;

    // declare necessary variables
    private GameLoopTask myGL;
    private RelativeLayout myRL;
    protected TextView myTV;
    protected int blockNumber;

    private int myVelocity;
    private GameLoopTask.eDir targetDirection;

    protected boolean toBeDestroyed = false;    // flag to see if the block should be destroyed
    private GameBlock targetMergeBlock = null;

    public GameBlock(Context gbCTX, RelativeLayout gbRL, int coordX, int coordY, GameLoopTask gbGL){

        super(gbCTX);

        targetDirection = GameLoopTask.eDir.NM;

        //set up the block
        this.setImageResource(R.drawable.gameblock);
        this.setPivotX(0);
        this.setPivotY(0);

        //
        setScaleBlocksDynamically();
        //
        this.setX(coordX);
        this.setY(coordY);
        this.setScaleX(IMAGE_SCALE);
        this.setScaleY(IMAGE_SCALE);
        gbRL.addView(this);
        this.bringToFront();

        this.myGL = gbGL;
        this.myRL = gbRL;

        Random myRandomGen = new Random();
        blockNumber = (myRandomGen.nextInt(2) + 1) * 2;
        myCoordX = coordX;
        myCoordY = coordY;
        targetCoordX = myCoordX;
        targetCoordY = myCoordY;
        myVelocity = 0;

        // set up the textview
        myTV = new TextView(gbCTX);
        myTV.setPivotX(0);
        myTV.setPivotY(0);

        myTV.setGravity(Gravity.CENTER);
        myTV.setWidth((int) (widthBlocks*IMAGE_SCALE_Blocks));
        myTV.setHeight((int) (heightBlocks*IMAGE_SCALE_Blocks));
        myTV.setX(coordX );
        myTV.setY(coordY );
        myTV.setText(String.format("%d", blockNumber));
        myTV.setTextSize(40.0f);
        myTV.setTextColor(Color.BLACK);
        myRL.addView(myTV);
        myTV.bringToFront();
    }

    // able to set the scale of the block dynamically
    private void setScaleBlocksDynamically() {
        float moveable_pixel;
        widthBlocks = getResources().getDrawable(R.drawable.gameblock).getIntrinsicWidth();
        heightBlocks = getResources().getDrawable(R.drawable.gameblock).getIntrinsicHeight();
        moveable_pixel = MainActivity.GAMEBOARD_DIMENSION / 4;
        IMAGE_SCALE_Blocks = (moveable_pixel - 2 * TV_OFFSET) / (float)widthBlocks; // make block smaller than board
    }

    public GameLoopTask.eDir getDirection(){
        return targetDirection;
    }

    //calculate how many blocks should be destroyed(merged)
    protected int calculateMerges(int[] numAhead, int numOfOccupants){
        int numOfMerges = 0;

        // number of occupants is the number of blocks in front of the target block
        switch(numOfOccupants){
            case 0:
                return 0;

            case 1:
                if(numAhead[0] == numAhead[1]) {
                    numOfMerges = 1;
                    toBeDestroyed = true;
                }
                break;

            case 2:
                if(numAhead[0] == numAhead[1]) {
                    numOfMerges = 1;
                }
                else if(numAhead[1] == numAhead[2]){
                    numOfMerges = 1;
                    toBeDestroyed = true;
                }
                break;

            case 3:
                if(numAhead[0] == numAhead[1]){
                    if(numAhead[2] == numAhead[3]) {
                        numOfMerges = 2;
                        toBeDestroyed = true;
                    }
                    else {
                        numOfMerges = 1;
                    }
                }
                else if(numAhead[1] == numAhead[2]){
                    numOfMerges = 1;
                }
                else if(numAhead[2] == numAhead[3]){
                    numOfMerges = 1;
                    toBeDestroyed = true;
                }

                break;
            default:
                return 0;
        }

        return numOfMerges;
    }

    public int[] getCurrentCoordinate(){
        int[] thisCoord = new int[2];
        thisCoord[0] = myCoordX;
        thisCoord[1] = myCoordY;
        return thisCoord;
    }

    public int[] getTargetCoordinate(){
        int[] thisCoord = new int[2];
        thisCoord[0] = targetCoordX;
        thisCoord[1] = targetCoordY;
        return thisCoord;
    }

    public int getBlockNumber(){
        return blockNumber;
    }

    public boolean isToBeDestroyed(){
        return toBeDestroyed;
    }

    public void doubleMyNumber(){
        blockNumber *= 2;
        myTV.setText(String.format("%d", blockNumber));
    }

    public void destroyMe(){
        myRL.removeView(myTV);
        myRL.removeView(this);
        targetMergeBlock.doubleMyNumber();
    }
    @Override
    public boolean setDestination(GameLoopTask.eDir thisDir){

        targetDirection = thisDir;

        int testCoord;
        int numOfOccupants = 0;
        int numOfMerges = 0;
        int[] occupantNumbers = {0,0,0,0};
        GameBlock testBlock;

        switch(thisDir){
            case LEFT:

                testCoord = GameLoopTask.LEFT_BOUNDARY;

                while(testCoord != myCoordX){

                    testBlock = myGL.isOccupied(testCoord, myCoordY);

                    if(testBlock != null){
                        targetMergeBlock = testBlock;
                        occupantNumbers[numOfOccupants] = testBlock.getBlockNumber();
                        numOfOccupants++;
                    }

                    testCoord += GameLoopTask.SLOT_ISOLATION;
                }

                occupantNumbers[numOfOccupants] = blockNumber;
                numOfMerges = calculateMerges(occupantNumbers, numOfOccupants);

                targetCoordX = GameLoopTask.LEFT_BOUNDARY
                        + numOfOccupants * GameLoopTask.SLOT_ISOLATION
                        - numOfMerges * GameLoopTask.SLOT_ISOLATION;


                break;

            case RIGHT:

                testCoord = GameLoopTask.RIGHT_BOUNDARY;

                while(testCoord != myCoordX){

                    testBlock = myGL.isOccupied(testCoord, myCoordY);

                    if(testBlock != null){
                        targetMergeBlock = testBlock;
                        occupantNumbers[numOfOccupants] = testBlock.getBlockNumber();
                        numOfOccupants++;
                    }

                    testCoord -= GameLoopTask.SLOT_ISOLATION;
                }

                occupantNumbers[numOfOccupants] = blockNumber;
                numOfMerges = calculateMerges(occupantNumbers, numOfOccupants);

                targetCoordX = GameLoopTask.RIGHT_BOUNDARY
                        - numOfOccupants * GameLoopTask.SLOT_ISOLATION
                        + numOfMerges * GameLoopTask.SLOT_ISOLATION;

                break;

            case UP:

                testCoord = GameLoopTask.UP_BOUNDARY;

                while(testCoord != myCoordY){

                    testBlock = myGL.isOccupied(myCoordX, testCoord);

                    if(testBlock != null){
                        targetMergeBlock = testBlock;
                        occupantNumbers[numOfOccupants] = testBlock.getBlockNumber();
                        numOfOccupants++;
                    }

                    testCoord += GameLoopTask.SLOT_ISOLATION;
                }

                occupantNumbers[numOfOccupants] = blockNumber;
                numOfMerges = calculateMerges(occupantNumbers, numOfOccupants);

                targetCoordY = GameLoopTask.UP_BOUNDARY
                        + numOfOccupants * GameLoopTask.SLOT_ISOLATION
                        - numOfMerges * GameLoopTask.SLOT_ISOLATION;

                break;

            case DOWN:

                testCoord = GameLoopTask.DOWN_BOUNDARY;

                while(testCoord != myCoordY){

                    testBlock = myGL.isOccupied(myCoordX, testCoord);

                    if(testBlock != null){
                        targetMergeBlock = testBlock;
                        occupantNumbers[numOfOccupants] = testBlock.getBlockNumber();
                        numOfOccupants++;
                    }

                    testCoord -= GameLoopTask.SLOT_ISOLATION;
                }

                occupantNumbers[numOfOccupants] = blockNumber;
                numOfMerges = calculateMerges(occupantNumbers, numOfOccupants);

                targetCoordY = GameLoopTask.DOWN_BOUNDARY
                        - numOfOccupants * GameLoopTask.SLOT_ISOLATION
                        + numOfMerges * GameLoopTask.SLOT_ISOLATION;

                break;
            default:
                break;
        }

        return !(targetCoordX == myCoordX && targetCoordY == myCoordY);

    }

    @Override
    public void move(){

        switch(targetDirection){

            case LEFT:

                //targetCoordX = GameLoop.LEFT_BOUNDARY;

                if(myCoordX > targetCoordX){
                    if((myCoordX - myVelocity) <= targetCoordX){
                        myCoordX = targetCoordX;
                        myVelocity = 0;
                    }
                    else {
                        myCoordX -= myVelocity;
                        myVelocity += GB_ACC;
                    }
                }

                break;

            case RIGHT:

                //targetCoordX = GameLoop.RIGHT_BOUNDARY;

                if(myCoordX < targetCoordX){
                    if((myCoordX + myVelocity) >= targetCoordX){
                        myCoordX = targetCoordX;
                        myVelocity = 0;
                    }
                    else {
                        myCoordX += myVelocity;
                        myVelocity += GB_ACC;
                    }
                }

                break;

            case UP:

                //targetCoordY = GameLoop.UP_BOUNDARY;

                if(myCoordY > targetCoordY){
                    if((myCoordY - myVelocity) <= targetCoordY){
                        myCoordY = targetCoordY;
                        myVelocity = 0;
                    }
                    else {
                        myCoordY -= myVelocity;
                        myVelocity += GB_ACC;
                    }
                }

                break;

            case DOWN:

                //targetCoordY = GameLoop.DOWN_BOUNDARY;

                if(myCoordY < targetCoordY){
                    if((myCoordY + myVelocity) >= targetCoordY){
                        myCoordY = targetCoordY;
                        myVelocity = 0;
                    }
                    else {
                        myCoordY += myVelocity;
                        myVelocity += GB_ACC;
                    }
                }

                break;

            default:
                break;

        }

        this.setX(myCoordX);
        this.setY(myCoordY);

        myTV.setX(myCoordX + TV_OFFSET);
        myTV.setY(myCoordY + TV_OFFSET);
        myTV.bringToFront();

        if(myVelocity == 0) {
            targetDirection = GameLoopTask.eDir.NM;
        }

    }


}
