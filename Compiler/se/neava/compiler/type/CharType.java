package se.neava.compiler.type;


public class CharType extends Type {

    public CharType(boolean isArray) {
        this.isArray = isArray;
    }

    public int getSize()
    {
        return 1;
    }
    
    public boolean equals(Type b)
    {
        if(!(b instanceof CharType))
            return false;
        return (isArray == ((CharType) b).isArray);
    }
}
