package structure;

public class NFA {

    private NFAnode init;
    private NFAnode term;
    private int numOfState;

    public NFA(NFAnode init, NFAnode term){
        this.init = init;
        this.term = term;
    }

    public NFAnode getInit(){
        return init;
    }

    public NFAnode getTerm() {
        return term;
    }

    public int getNumOfState() {
        return numOfState;
    }

    public void setNumOfState(int numOfState) {
        this.numOfState = numOfState;
    }
}
