import structure.NFA;
import structure.NFAnode;

import java.util.*;

public class NFA2DFA {

    private NFA nfa;
    private Set<Character> header;
    private int[][] eTable; //e-closure table start from row 1
    private List<Set<Integer>> stateSets = new ArrayList<>();
    private List<Set<NFAnode>> nodeSets = new ArrayList<>();
    public static final int CHAR_NUM = 128;
    public static final int LARGE_ROW = 1000;

    public NFA2DFA(NFA nfa, Set<Character> header){
        this.header = header;
        this.nfa = nfa;
        this.eTable = new int[LARGE_ROW][CHAR_NUM];
    }

    public void printTable(){
        String delim = "\t";
        System.out.print(delim);
        Set<Character> print = new HashSet<>(header);
        print.add('\0');

        for (Character c : print) {
            System.out.print(c + delim);
        }
        System.out.println();
        for (int i = 0; i < stateSets.size(); i ++){
            System.out.print((i+1) + delim);
            for (Character c : print) {
                System.out.print(eTable[i+1][c] + delim);
            }
            System.out.println();
        }
    }

    public int[][] getDFA(){
        eTable();
        simplify();
        return eTable;
    }

    private void eTable(){
        Set<Integer> firstClosure = new HashSet<>();
        Set<NFAnode> nodeSet = new HashSet<>();

        eClosure(nfa.getInit(), firstClosure, nodeSet);
        stateSets.add(firstClosure);
        nodeSets.add(nodeSet);

        int now = 0;
        while (now < stateSets.size()){
            for (Character edge : header) {
                Map<Integer, NFAnode> stateSet = nextSet(edge, nodeSets.get(now));
                nodeSet = new HashSet<>();
                Set<Integer> closureSet = new HashSet<>();
                for (Integer i : stateSet.keySet()) {
                    eClosure(stateSet.get(i), closureSet, nodeSet);
                }

                int hasSame = withSameClosure(closureSet);
                if(hasSame > 0){
                    eTable[now+1][edge] = hasSame;
                }
                else if(closureSet.size() > 0){
                    stateSets.add(closureSet);
                    nodeSets.add(nodeSet);
                    eTable[now+1][edge] = stateSets.size();
                    if(closureSet.contains(nfa.getTerm().getState())){
                        eTable[stateSets.size()][0] = -1;
                    }
                }
            }
            now++;
        }
    }

    private void simplify(){
        Set<Character> check = new HashSet<>(header);
        check.add('\0');
        for (int i = 1; i <= stateSets.size(); i++) {
            int j = i+1;
            for( ; j <= stateSets.size(); j ++){
                boolean isSame = true;
                for (Character c : check) {
                    if(eTable[i][c] != eTable[j][c]){
                        isSame = false;
                        break;
                    }
                }
                if(isSame){
//                    System.out.println(i +" <=> " +j);
                }
            }
        }
    }

    private int withSameClosure(Set<Integer> target){
        int result = 0;
        for (int i = 0; i < stateSets.size(); i++) {
            if(stateSets.get(i).equals(target)){
                result = i+1;
                break;
            }
        }
        return result;
    }
    
    private Map<Integer, NFAnode> nextSet(char edge, Set<NFAnode> nodeSet){
        Map<Integer, NFAnode> result = new HashMap<>();
        for (NFAnode node : nodeSet) {
            if(node.getOutEdge().contains(edge - '\0')){
                for (int i = 0; i < node.getOutEdge().size(); i++) {
                    if(node.getOutEdge().get(i) == edge - '\0'
                            && !result.containsKey(node.getOutNeigh().get(i).getState())){
                        result.put(node.getOutNeigh().get(i).getState(), node.getOutNeigh().get(i));
                    }
                }
            }
        }
        return result;
    }

    private void eClosure(NFAnode target, Set<Integer> closureSet, Set<NFAnode> nodeSet){
        if(closureSet.contains(target.getState())){
            return;
        }else {
            closureSet.add(target.getState());
            nodeSet.add(target);
        }
        for (int i = 0; i < target.getOutEdge().size(); i++) {
            if(target.getOutEdge().get(i) == -1){
                eClosure(target.getOutNeigh().get(i), closureSet, nodeSet);
            }
        }
    }
}