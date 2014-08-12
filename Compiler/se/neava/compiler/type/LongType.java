package se.neava.compiler.type;

public class LongType extends Type 
{
    public LongType(boolean isArray)
    {
        this.isArray = isArray;
    }
    
    public int getSize()
    {
        return 4;
    }
    
    public String getSizeStr()
    {
        return "dword";
    }
    
    public boolean equals(Object b)
    {
        if(!(b instanceof LongType))
            return false;
        return (isArray == ((LongType) b).isArray);
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
