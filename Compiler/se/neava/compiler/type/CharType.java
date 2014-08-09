package se.neava.compiler.type;


public class CharType extends Type {

    public CharType(boolean isArray) {
        this.isArray = isArray;
    }

    public int getSize()
    {
        return 1;
    }
    
}
