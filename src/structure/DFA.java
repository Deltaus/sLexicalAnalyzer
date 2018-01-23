package structure;

public class DFA {

    private int prior;

    public int[][] dfa;

    public DFA(int prior){
        this.prior = prior;
    }

    public DFA(int prior, int[][] dfa){
        this.prior = prior;
        this.dfa = dfa;
    }

    public int getPrior() {
        return prior;
    }
}
