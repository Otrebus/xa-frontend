package se.neava.communicator;

public class CommunicationEvent 
{
    private byte[] message;
    private int type;
    
    public static final int MESSAGE = 0;
    public static final int RETRANSMITTED = 1;
    public static final int FINISHED_UPLOADING = 2;
    public static final int GAVE_UP = 3;
    
    public CommunicationEvent(int type, byte[] message)
    {
        this.type = type;
        this.message = message.clone();
    }
    
    public CommunicationEvent(int type)
    {
        this.type = type;
    }
    
    public int getType()
    {
        return type;
    }
    
    public byte[] getMessage()
    {
        return message;
    }
}