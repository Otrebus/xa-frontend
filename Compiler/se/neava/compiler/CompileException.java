package se.neava.compiler;

public class CompileException extends Exception {

    public String what;
    public CompileException(String string) {
        what = string;
        // TODO Auto-generated constructor stub
    }

}
