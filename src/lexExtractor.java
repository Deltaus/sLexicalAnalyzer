import exception.FileException;
import exception.REException;

import java.io.*;
import java.util.*;

public class lexExtractor {

    private File lexFile;
    private StringBuilder header = new StringBuilder();
    private Map<String, String> regularExp = new HashMap<>();
    private StringBuilder rulesContent = new StringBuilder();
    private StringBuilder subroutines = new StringBuilder();
    private List<String> regulars = new ArrayList<>();
    private List<String> rules = new ArrayList<>();
    
    public lexExtractor(String filePath){
        lexFile = new File(filePath);
    }

    public lexExtractor extract() throws FileNotFoundException, FileException{
        if(!lexFile.exists()){
            throw new FileNotFoundException();
        }
        BufferedReader reader = new BufferedReader(new FileReader(lexFile));
        String line;
        int lineNum = 0, part = 0;

        try {
            while ((line = reader.readLine()) != null){
                lineNum ++;
                if(line.trim().equals("%%")){
                    part ++;
                }else if(line.trim().startsWith("%{")){
                    header.append(line.replace("%{", "") + "\n");
                    while ((line = reader.readLine()) != null){
                        lineNum ++;
                        if(line.trim().endsWith("%}")){
                            header.append(line.replace("%}", "") + "\n");
                            break;
                        }else {
                            header.append(line + "\n");
                        }
                    }
                    if(line == null){
                        throw new FileException(lineNum);
                    }
                }else {
                    if(part == 0){
                        if(line.trim().length() > 0) {
                            String[] temp = line.trim().split(" +");
                            if(temp.length > 1){
                                regularExp.put(temp[0], normalize(temp[1]));
                            }else {
                                throw new FileException(lineNum);
                            }
                        }
                    }else if(part == 1){
                        if(line.trim().length() > 0) {
                            rulesContent.append(line + "\n");
                        }
                    }else if(part == 2){
                        subroutines.append(line + "\n");
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.handleContent();

        return this;
    }
    
    private String normalize(String nonStandard) throws FileException {
        Stack<String> stack = new Stack<>();
        String standardExp = "";
        char[] element = nonStandard.toCharArray();

        for (int i = 0; i < element.length; i++) {
            if(element[i] == '[' || element[i] == '('){
                stack.push(String.valueOf(element[i]));
            }else if(element[i] == ']'){
                String array = "[".equals(stack.peek())? "":stack.pop();
                while ( !"[".equals(stack.peek())){
                    array = stack.pop() + "|" + array;
                }
                stack.pop();
                stack.push("("+array+")");
            }else if(element[i] == ')'){
                String array = ")";
                while (!"(".equals(stack.peek())){
                    array = stack.pop() + array;
                }
                array = stack.pop() + array;
                stack.push(array);
            }else if(element[i] == '+'){
                String more = stack.pop();
                more = more + more + "*";
                stack.push(more);
            }else if(element[i] == '-'){
                if(i < element.length - 1 && stack.peek() != null){
                    char[] start = stack.pop().toCharArray();
                    if(start.length == 1){
                        if(start[0] < element[i+1]){
                            for(int j = 0; j <= (element[i+1] - start[0]); j ++){
                                char c = start[0];
                                stack.push(String .valueOf((char)(start[0]+j)));
                            }
                            i ++;
                        }else {
                            throw new FileException("Wrong format in regular expression");
                        }
                    }else {
                        throw new FileException("Wrong format in regular expression");
                    }
                }else {
                    throw new FileException("Wrong format in regular expression");
                }
            }else if(element[i] == '\\'){
                if(i < element.length - 1){
                    stack.push(String.valueOf(new char[]{element[i], element[++i]}));
                }else {
                    throw new FileException("Wrong format in regular expression");
                }
            }else if(element[i] == '{'){
                String id = "";
                i ++;
                while (element[i] != '}'){
                    id += element[i];
                    i ++;
                }
                if(regularExp.containsKey(id)){
                    stack.push(regularExp.get(id));
                }else {
                    throw new FileException("No declared variable {" + id+"}");
                }
            }else {
                stack.push(String .valueOf(element[i]));
            }
        }
        while (!stack.empty()){
            standardExp = stack.pop() + standardExp;
        }
        String plusAdded = "";
        char[] each = standardExp.toCharArray();
        for (int i = 0; i < each.length; i++) {
            plusAdded += each[i];
            if(each[i] == '\\'){
                if(i < each.length - 2){
                    if(each[i+2] == '(' || (each[i+2] != '|' && each[i+2] != '*' && each[i+2] != '(' && each[i+2] != ')')){
                        plusAdded += each[++i] + ".";
                    }
                }else if(i == each.length - 1){
                    throw new FileException();
                }
            }else if(each[i] == '*' || each[i] == ')' ||
                    (each[i] != '|' && each[i] != '*' && each[i] != '(' && each[i] != ')')){
                if(i < each.length - 1) {
                    if (each[i + 1] == '(' || (each[i + 1] != '|' && each[i + 1] != '*' && each[i + 1] != '(' && each[i + 1] != ')')) {
                        plusAdded += ".";
                    }
                }
            }
        }
        return plusAdded;
    }

    private void handleContent() throws FileException{
        int tag = 0;
        char[] input = rulesContent.toString().toCharArray();

        for (int i = 0; i < input.length; i++) {
            if(input[i] == '{'){
                i ++;
                if(tag == 0){
                    String id = "";
                    while (input[i] != '}'){
                        id += input[i];
                        if(i < input.length - 1){
                            i ++;
                        }else {
                            throw new FileException();
                        }
                    }

                    if(regularExp.containsKey(id)) {
                        regulars.add(regularExp.get(id));
                    }else {
                        throw new FileException("No declared variable {" + id+"}");
                    }
                    i ++;
                    tag = 1;
                }else{
                    String rule = "";
                    while (input[i] != '}'){
                        if(input[i] == '\\' && i < input.length && input[i+1] == '}'){
                            rule += input[i] + input[++i];
                        }else {
                            rule += input[i];
                        }
                        if(i < input.length - 1){
                            i ++;
                        }else {
                            throw new FileException();
                        }
                    }
                    rules.add(rule);
                    i ++;
                    tag = 0;
                }
            }else if(input[i] == '"'){
                String id = "";
                i ++;
                while (input[i] != '"'){
                    id += "\\"+input[i];
                    if(i < input.length - 1){
                        i ++;
                    }else {
                        throw new FileException();
                    }
                }
                regulars.add(normalize(id));
                tag = 1;
            }else if(input[i] != ' ' && input[i] != '\t' && input[i] != '\n') {
                String id = "";
                while (input[i] != ' '){
                    id += input[i];
                    if(i < input.length - 1){
                        i ++;
                    }else {
                        throw new FileException("Error occured index " + i + " of " + (input.length-1));
                    }
                }
                regulars.add(normalize(id));
                i ++;
                tag = 1;
            }
        }

        regulars.forEach(s -> System.out.println(s));
    }

    public StringBuilder getHeader() {
        return header;
    }

    public List<String> getRegulars() {
        return regulars;
    }

    public List<String> getRules() {
        return rules;
    }

    public StringBuilder getSubroutines() {
        return subroutines;
    }

}