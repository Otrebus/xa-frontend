package se.neava.compiler.type;

public class IntType extends Type 
{
    public IntType(boolean isArray)
    {
        this.isArray = isArray;
    }
    
    public int getSize()
    {
        return 2;
    }
    
    public String getSizeStr()
    {
        return "word";
    }
    
    public boolean equals(Object b)
    {
        if(!(b instanceof IntType))
            return false;
        return (isArray == ((IntType) b).isArray);
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
