/* __  __      _ _            
  |  \/  |    | (_)           
  | \  / | ___| |_  ___  _ __ 
  | |\/| |/ _ \ | |/ _ \| '__|
  | |  | |  __/ | | (_) | |   
  |_|  |_|\___|_|_|\___/|_|   
        Service Harness
*/
package org.melior.client.smpp;

/**
 * An SMPP message.
 * @author Melior
 * @since 2.3
 */
public class SmppMessage {

    private String sourceAddress;

    private String destinationAddress;

    private String messageText;

    private String messageId;

    /**
     * Constructor.
     * @param sourceAddress The source address
     * @param destinationAddress The destination address
     * @param messageText The message text
     * @param messageId The message identifier
     */
    SmppMessage(
        final String sourceAddress,
        final String destinationAddress,
        final String messageText,
        final String messageId) {

        super();

        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.messageText = messageText;
        this.messageId = messageId;
    }

    /**
     * Get instance of SMPP message.
     * @param sourceAddress The source address
     * @param destinationAddress The destination address
     * @param messageText The message text
     * @param messageId The message identifier
     * @return The SMPP message
     */
    public static SmppMessage of(
        final String sourceAddress,
        final String destinationAddress,
        final String messageText,
        final String messageId) {
        return new SmppMessage(sourceAddress, destinationAddress, messageText, messageId);
    }

    /**
     * Get instance of SMPP message.
     * @param sourceAddress The source address
     * @param destinationAddress The destination address
     * @param messageText The message text
     * @return The SMPP message
     */
    public static SmppMessage of(
        final String sourceAddress,
        final String destinationAddress,
        final String messageText) {
        return new SmppMessage(sourceAddress, destinationAddress, messageText, null);
    }

    /**
     * Get source address.
     * @return The source address
     */
    public String getSourceAddress() {
        return sourceAddress;
    }

    /**
     * Get destination address.
     * @return The destination address
     */
    public String getDestinationAddress() {
        return destinationAddress;
    }

    /**
     * Get message text.
     * @return The message text
     */
    public String getMessageText() {
        return messageText;
    }

    /**
     * Get message identifier.
     * @return The message identifier
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Returns a string representation of the SMPP message.
     * @return The string representation
     */
    public String toString() {
        return "{" +
            quoted("sourceAddress") + ": " + quoted(sourceAddress) + ", " +
            quoted("destinationAddress") + ": " + quoted(destinationAddress) + ", " +
            quoted("messageText") + ": " + quoted(messageText) + ", " +
            quoted("messageId") + ": " + quoted(messageId) +
            "}";
    }

    /**
     * Returns a quoted version of the string.
     * @param string The string
     * @return The quoted version of the string
     */
    private String quoted(
        final String string) {
        return (string == null) ? null : '"' + string + '"';
    }

}
