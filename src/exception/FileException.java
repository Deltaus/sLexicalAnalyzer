package exception;

public class FileException extends Exception {

    public FileException(int line){
        super("Wrong format in lex file at line " + line);
    }

    public FileException(){
        super("Wrong format in lex file! ");
    }

    public FileException(String details){
        super(details);
    }
}
