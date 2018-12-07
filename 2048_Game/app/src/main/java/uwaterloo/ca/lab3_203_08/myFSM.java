package uwaterloo.ca.lab3_203_08;

public class myFSM {

    enum FSMState{WAIT, A_RISE, B_FALL, DETERMINED}
    private FSMState myState;
    enum mySig{SIG_A, SIG_B, SIG_X};
    private mySig Signature;

    private final float[] THRESHOLD_A = {0.6f, 2.0f};
    private final float[] THRESHOLD_B = {-0.6f, -2.0f};

    private float previousInput;

    private void mainFSM(float currentInput){

        float deltaInput = currentInput - previousInput;

        switch(myState){

            case WAIT:

                if(deltaInput >= THRESHOLD_A[0]) {
                    myState = FSMState.A_RISE;
                }
                else if(deltaInput <= THRESHOLD_B[0]){
                    myState = FSMState.B_FALL;
                }

                break;

            case A_RISE:

                if(deltaInput <= 0){
                    if(currentInput >= THRESHOLD_A[1]){
                        Signature = mySig.SIG_A;
                        myState = FSMState.DETERMINED;
                    }
                    else {
                        Signature = mySig.SIG_X;
                        myState = FSMState.DETERMINED;
                    }
                }

                break;

            case B_FALL:

                if(deltaInput >= 0){
                    if(currentInput <= THRESHOLD_B[1]){
                        Signature = mySig.SIG_B;
                        myState = FSMState.DETERMINED;
                    }
                    else {
                        Signature = mySig.SIG_X;
                        myState = FSMState.DETERMINED;
                    }
                }

                break;

            case DETERMINED:

                break;

            default:
                resetFSM();
                break;

        }

    }



    public myFSM(){
        myState = FSMState.WAIT;
        Signature = mySig.SIG_X;
        previousInput = 0.0f;
    }

    public void resetFSM(){
        myState = FSMState.WAIT;
        Signature = mySig.SIG_X;
        previousInput = 0.0f;
    }

    public void supplyInput(float input){

        mainFSM(input);

        previousInput = input;
    }

    public mySig getSignature(){
        if(myState == FSMState.DETERMINED){
            return Signature;
        }
        else{
            return mySig.SIG_X;
        }
    }

    public boolean isReady(){
        if(myState == FSMState.WAIT)
            return true;
        else
            return false;
    }

    public FSMState getState(){
        return myState;
    }

}

