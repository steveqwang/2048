package uwaterloo.ca.lab3_203_08;


import android.content.Context;

// making the code to be extendable for further development

public abstract class GameBlockTemplate extends android.support.v7.widget.AppCompatImageView{

    public GameBlockTemplate(Context gbCTX){
        super(gbCTX);
    }

    public abstract boolean setDestination(GameLoopTask.eDir myDir);

    public abstract void move();

}