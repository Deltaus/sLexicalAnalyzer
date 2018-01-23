package structure;

import java.util.ArrayList;
import java.util.List;

public class NFAnode {

    private int state;
    private boolean isEndState;
    private List<Integer> outEdge;
    private List<NFAnode> outNeigh;

    public NFAnode(int state){
        //this.state = state;
        //this.isEndState = false;
        this(state, false);
    }

    public NFAnode(int state, boolean isEndState){
        this.state = state;
        this.isEndState = isEndState;
        outEdge = new ArrayList<>();
        outNeigh = new ArrayList<>();
    }

    public boolean addNode(int nextEdge, NFAnode nextState){
        if(nextState == null){
            return false;
        }
        this.outEdge.add(nextEdge);
        this.outNeigh.add(nextState);
        return true;
    }

    public void setEndState(){
        this.isEndState = true;
    }

    public void resetEndState(){
        this.isEndState = false;
    }

    public int getState(){
        return state;
    }

    public List<Integer> getOutEdge(){
        return outEdge;
    }

    public List<NFAnode> getOutNeigh() {
        return outNeigh;
    }
}
