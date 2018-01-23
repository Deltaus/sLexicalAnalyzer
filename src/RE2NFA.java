import structure.NFAnode;
import structure.NFA;
import exception.*;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class RE2NFA {

    private String regularExpression;
    private Set<Character> header = new HashSet<>();
    private NFA nfa;

    public RE2NFA(String regularExpression){
        this.regularExpression = regularExpression;
    }

    public NFA2DFA getNFA() throws REException {
        postfix();
        convert();
        return new NFA2DFA(nfa, header);
    }

    private void postfix() throws REException{
        Stack<Character> operater = new Stack<>();
        StringBuilder postfixExpression = new StringBuilder();
        char[] cha = ("(" + regularExpression + ")").toCharArray();

        for(int i = 0; i < cha.length; i++){
            switch (cha[i]) {
                case '\\':
                    if(i < cha.length - 1){
                        if(cha[i+1] == 'n'){
                            postfixExpression.append('\n');
                            i++;
                        }
                        else if(cha[i+1] == 't'){
                            postfixExpression.append('\t');
                            i++;
                        }
                        else{
                            postfixExpression.append(cha[i]).append(cha[++i]);
                        }
                    }
                    else{
                        throw new REException();
                    }
                    break;
                case '(':
                    operater.push(cha[i]);
                    break;
                case '*':
                    while(operater.peek() == '*'){
                        postfixExpression.append(operater.pop());
                    }
                    operater.push(cha[i]);
                    break;
                case '.':
                    while(operater.peek() == '*' || operater.peek() == '.'){
                        postfixExpression.append(operater.pop());
                    }
                    operater.push(cha[i]);
                    break;
                case '|':
                    while(operater.peek() == '*' || operater.peek() == '.' || operater.peek() == '|'){
                        postfixExpression.append(operater.pop());
                    }
                    operater.push(cha[i]);
                    break;
                case ')':
                    while(operater.peek() != null && operater.peek() != '('){
                        postfixExpression.append(operater.pop());
                    }
                    if(operater.pop() != '('){
                        throw new REException();
                    }
                    break;
                default:
                    postfixExpression.append(cha[i]);
            }
        }
        regularExpression = postfixExpression.toString();

    }

    private void convert() throws REException{
        char[] cha = regularExpression.toCharArray();
        Stack<NFA> nfaStack = new Stack<>();
        int nextState = 1;

        for(int i = 0; i < cha.length; i++){
            if(cha[i] == '.'){
                NFA back = nfaStack.pop();
                NFA front = nfaStack.pop();
                if(back == null || front == null){
                    throw new REException();
                }
                front.getTerm().addNode(-1, back.getInit());
                front.getTerm().resetEndState();
                nfaStack.push(new NFA(front.getInit(), back.getTerm()));
            }
            else if(cha[i] == '|'){
                NFA back, front;
                back = nfaStack.pop();
                front = nfaStack.pop();
                if(back == null || front == null){
                    throw new REException();
                }
                NFAnode newInit = new NFAnode(nextState++);
                newInit.addNode(-1, front.getInit());
                newInit.addNode(-1, back.getInit());

                NFAnode newTerm = new NFAnode(nextState++,true);
                front.getTerm().addNode(-1, newTerm);
                front.getTerm().resetEndState();
                back.getTerm().addNode(-1, newTerm);
                back.getTerm().resetEndState();
                nfaStack.push(new NFA(newInit, newTerm));
            }
            else if(cha[i] == '*'){
                NFA self = nfaStack.pop();
                if(self == null){
                    throw new REException();
                }
                NFAnode newInit = new NFAnode(nextState++);
                NFAnode newTerm = new NFAnode(nextState++,true);
                newInit.addNode(-1, newTerm);
                newInit.addNode(-1, self.getInit());
                self.getTerm().resetEndState();
                self.getTerm().addNode(-1, self.getInit());
                self.getTerm().addNode(-1, newTerm);
                nfaStack.push(new NFA(newInit, newTerm));
            }
            else if(cha[i] == '\\'){
                if( i < cha.length - 1){
                    header.add(cha[++i]);
                    NFAnode init = new NFAnode(nextState++);
                    NFAnode term = new NFAnode(nextState++, true);
                    init.addNode(cha[i], term);
                    nfaStack.push(new NFA(init, term));
                }
                else {
                    throw new REException();
                }
            }
            else {
                header.add(cha[i]);
                NFAnode init = new NFAnode(nextState++);
                NFAnode term = new NFAnode(nextState++, true);
                init.addNode(cha[i] , term);
                nfaStack.push(new NFA(init, term));
            }
        }

        nfa = nfaStack.pop();
        nfa.setNumOfState(nextState - 1);
    }
}
