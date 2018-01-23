import structure.DFA;
import java.util.*;

public class DFAUnifier {

    private List<Map<Integer, Integer>> mergeState = new ArrayList<>(); //key为原dfa优先级 , value为原dfa状态名
    private List<DFA> allDfa = new ArrayList<>();
    private List<Integer[]> mergeEndState = new ArrayList<>();
    private List<int[]> newDfa = new ArrayList<>();

    public DFAUnifier(List<DFA> allDfa){
        this.allDfa = allDfa;
    }
    
    public List<int[]> merge(){
        Map<Integer, Integer> firstMergeState = new HashMap<>();
        allDfa.forEach(dfaEntity -> {
            firstMergeState.put(dfaEntity.getPrior(), 1);
        });
        mergeState.add(firstMergeState);

        for (int i = 0; i < mergeState.size(); i ++){
            int[] header = new int[NFA2DFA.CHAR_NUM];
            List<Integer> endState = new ArrayList<>();
            
            Iterator<Map.Entry<Integer, Integer>> it = mergeState.get(i).entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, Integer> state = it.next();
                if (allDfa.get(state.getKey() - 1).dfa[state.getValue()][0] == -1) {
                    header[0] = -1;
                    endState.add(state.getKey());
                }
            }

            for(int j = 1; j < NFA2DFA.CHAR_NUM; j ++) {

                Map<Integer, Integer> eachMergeState = new HashMap<>();
                it = mergeState.get(i).entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<Integer, Integer> state = it.next();
                    if (allDfa.get(state.getKey() - 1).dfa[state.getValue()][j] > 0) {
                        eachMergeState.put(state.getKey(), allDfa.get(state.getKey() - 1).dfa[state.getValue()][j]);
                    }
                }
                if(eachMergeState.size() > 0) {
                    if (mergeState.contains(eachMergeState)) {
                        header[j] = mergeState.indexOf(eachMergeState);
                    }
                    else {
                        mergeState.add(eachMergeState);
                        header[j] = mergeState.size() - 1;
                    }
                }
            }
            if(header[0] == -1){
                Integer[] temp = new Integer[endState.size()];
                endState.toArray(temp);
                mergeEndState.add(temp);
            }
            newDfa.add(header);
        }

        int j = 0;
        for(int i = 0; i < newDfa.size(); i ++){
            if(newDfa.get(i)[0] == -1){
                newDfa.get(i)[0] = - highestPrior(mergeEndState.get(j));
                j ++;
            }
        }

        return newDfa;
    }

    public void print(){
        String delim = "\t";
        System.out.print(delim);

        for (int i = 0; i < NFA2DFA.CHAR_NUM; i ++) {
            System.out.print( i + delim);
        }
        System.out.println();
        for (int i = 0; i < newDfa.size(); i ++){
            System.out.print((i) + delim);
            for(int j = 0; j < newDfa.get(i).length; j ++){
                System.out.print(newDfa.get(i)[j] + delim);
            }
            System.out.println();
        }
    }

    private int highestPrior(Integer[] ends){
        int highest = ends[0];

        if(ends.length > 1){
            for (Integer end : ends) {
                if(end < highest){
                    highest = end;
                }
            }
        }
        return highest;
    }
}