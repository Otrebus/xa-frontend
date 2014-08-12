package se.neava.compiler.type;


public class CharType extends Type {

    public CharType(boolean isArray) {
        this.isArray = isArray;
    }

    public int getSize()
    {
        return 1;
    }
    
    public String getSizeStr()
    {
        return "byte";
    }
    
    public boolean equals(Object b)
    {
        if(!(b instanceof CharType))
            return false;
        return (isArray == ((CharType) b).isArray);
    }
}
