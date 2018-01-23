import structure.DFA;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class lexGenerator {

    private static lexGenerator generator;

    private lexExtractor extractor;
    private List<String> regulars;
    private List<int[]> state;
    private List<String> rules;

    private lexGenerator(){

    }

    public static lexGenerator getGenerator(){
        if(generator == null){
            generator = new lexGenerator();
        }
        return generator;
    }

    public void compile(String path) throws Exception{
        this.extractor = new lexExtractor(path).extract();
        rules = extractor.getRules();
        regulars = extractor.getRegulars();

        List<DFA> dfas = new ArrayList<>();
        for (int i = 0; i < regulars.size(); i++) {
            int[][] dfa = new RE2NFA(regulars.get(i)).getNFA().getDFA();
            dfas.add(new DFA(i+1, dfa));
        }
        state = new DFAUnifier(dfas).merge();

        this.lexGenerate();
    }

    private void lexGenerate(){

        File dir = new File("lex");
        if(!dir.exists()){
            dir.mkdir();
        }

        String delim = "\t";
        StringBuilder stateTable = new StringBuilder();
        StringBuilder analyzerContent = new StringBuilder();
        String content = "";
        for(int i = 0; i < state.size(); i ++){
            for (int j = 0; j < state.get(0).length; j ++){
                stateTable.append(state.get(i)[j]);
                if(j < state.get(0).length - 1){
                    stateTable.append(" ");
                }
            }
            if(i < state.size() - 1){
                stateTable.append("\n");
            }
        }
        try {
            File sample = new File("src/temp/Analyzer.java");
            BufferedReader reader = new BufferedReader(new FileReader(sample));
            String line = reader.readLine();
            while ((line = reader.readLine()) != null){
                analyzerContent.append(line+"\n");
            }
            content = analyzerContent.toString().replace("/*this is state table*/", "=new int["+state.size()+"][128]");
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            String analyzer = new String("lex/Analyzer.java");
            FileWriter writer = new FileWriter(analyzer);
            writer.write(content.toString());
            writer.flush();
            writer.close();

            writer = new FileWriter("lex/state");
            writer.write(stateTable.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder rulesContent = new StringBuilder();
        for (int i = 0; i < rules.size(); i++) {
            rulesContent.append(delim+delim+delim+"case "+ (i+1)+":\n");
            rulesContent.append(rules.get(i)+"\n"+"break;"+"\n");
        }
        StringBuilder lexerContent = new StringBuilder();
        lexerContent.append(extractor.getHeader());
        try {
            File sample = new File("src/temp/Lexer.java");
            BufferedReader reader = new BufferedReader(new FileReader(sample));
            String line = reader.readLine();
            while ((line = reader.readLine()) != null){
                if(line.startsWith("/*this is user-rules area*/")){
                    lexerContent.append(rulesContent.toString());
                }
                else if(!line.startsWith("}")){
                    lexerContent.append(line+"\n");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        lexerContent.append(extractor.getSubroutines()).append("\n}");
        try {
            String lexer = new String("lex/Lexer.java");
            FileWriter writer = new FileWriter(lexer);
            writer.write(lexerContent.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}