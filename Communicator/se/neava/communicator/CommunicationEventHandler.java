package se.neava.communicator;

/**
 * Handles an event generated within the context of the {@link #Communicator} class. 
 */
public interface CommunicationEventHandler 
{
    /**
     * Called when the Communicator generates an event to be handled by this interface. 
     */
    public void handleEvent(CommunicationEvent e);
}
