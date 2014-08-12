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
    
    public String popTo(int fpOffset)
    {
        String sgn = fpOffset >= 0 ? "" : "-";  
        return "pop " + getSizeStr() + "[$fp" + sgn + Math.abs(fpOffset) + "]";
    }
    
    public String popTo(String label)
    {
        return "pop " + getSizeStr() + " [" + label + "]";
    }
    
    public String pushFrom(int fpOffset)
    {
        String sgn = fpOffset >= 0 ? "+" : "-";  
        return "push " + getSizeStr() + "[$fp" + sgn + Math.abs(fpOffset) + "]";
    }
    
    public String pushFrom(String label)
    {
        return "push " + getSizeStr() + " [" + label + "]";
    }
}
