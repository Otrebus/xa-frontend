package se.neava.communicator;

/**
 * Represents an event generated within the context of the {@link #Communicator} class. 
 */
public class CommunicationEvent 
{
    private byte[] message;
    private int type;
    
    public static final int MESSAGE = 0;
    public static final int RETRANSMITTED = 1;
    public static final int FINISHED_UPLOADING = 2;
    public static final int GAVE_UP = 3;
    public static final int GOT_ACK = 4;
    
    /**
     * Creates a communication event of type MESSAGE containing the frame provided.
     * @param message The byte contents of the frame that generated the event.
     */
    public CommunicationEvent(byte[] message)
    {
        this.message = message.clone();
    }

    /**
     * Creates a communication event of the provided type.
     * @param type Information about the cause of this event.
     */    
    public CommunicationEvent(int type)
    {
        this.type = type;
    }
    
    /**
     * Returns an integer representing the cause of this event.
     * @return an integer representing the cause of this event.
     */
    public int getType()
    {
        return type;
    }
    
    /**
     * Returns the message that caused this event. Only appropriate if the type was MESSAGE.
     * @return the message that caused this event. Only appropriate if the type was MESSAGE.
     */
    public byte[] getMessage()
    {
        return message;
    }
}