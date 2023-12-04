/* __  __      _ _            
  |  \/  |    | (_)           
  | \  / | ___| |_  ___  _ __ 
  | |\/| |/ _ \ | |/ _ \| '__|
  | |  | |  __/ | | (_) | |   
  |_|  |_|\___|_|_|\___/|_|   
        Service Harness
*/
package org.melior.server.smpp;
import java.time.LocalDateTime;
import org.jsmpp.util.DeliveryReceiptState;

/**
 * An SMPP delivery receipt.
 * @author Melior
 * @since 2.3
 */
public class SmppReceipt {

    private String sourceAddress;

    private String destinationAddress;

    private String messageText;

    private String messageId;

    private LocalDateTime submitDate;

    private LocalDateTime doneDate;

    private DeliveryReceiptState state;

    private String error;

    /**
     * Constructor.
     * @param sourceAddress The source address
     * @param destinationAddress The destination address
     * @param messageText The message text
     * @param messageId The message identifier
     * @param submitDate The submit date
     * @param doneDate The done date
     * @param state The delivery state
     * @param error The error
     */
    SmppReceipt(
        final String sourceAddress,
        final String destinationAddress,
        final String messageText,
        final String messageId,
        final LocalDateTime submitDate,
        final LocalDateTime doneDate,
        final DeliveryReceiptState state,
        final String error) {

        super();

        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.messageText = messageText;
        this.messageId = messageId;
        this.submitDate = submitDate;
        this.doneDate = doneDate;
        this.state = state;
        this.error = error;
    }

    /**
     * Get instance of SMPP message.
     * @param sourceAddress The source address
     * @param destinationAddress The destination address
     * @param messageText The message text
     * @param messageId The message identifier
     * @param submitDate The submit date
     * @param doneDate The done date
     * @param state The delivery state
     * @param error The error
     * @return The SMPP message
     */
    public static SmppReceipt of(
        final String sourceAddress,
        final String destinationAddress,
        final String messageText,
        final String messageId,
        final LocalDateTime submitDate,
        final LocalDateTime doneDate,
        final DeliveryReceiptState state,
        final String error) {
        return new SmppReceipt(sourceAddress, destinationAddress, messageText, messageId, submitDate, doneDate, state, error);
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
     * Get submit date.
     * @return The submit date
     */
    public LocalDateTime getSubmitDate() {
        return submitDate;
    }

    /**
     * Get done date.
     * @return The done date
     */
    public LocalDateTime getDoneDate() {
        return doneDate;
    }

    /**
     * Get delivery state.
     * @return The delivery state
     */
    public DeliveryReceiptState getState() {
        return state;
    }

    /**
     * Get error.
     * @return The error
     */
    public String getError() {
        return error;
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
            quoted("messageId") + ": " + quoted(messageId) + ", " +
            quoted("submitDate") + ": " + quoted(submitDate) + ", " +
            quoted("doneDate") + ": " + quoted(doneDate) + ", " +
            quoted("state") + ": " + quoted(state.name()) + ", " +
            quoted("error") + ": " + quoted(error) +
            "}";
    }

    /**
     * Returns a quoted version of the string.
     * @param string The string
     * @return The quoted version of the string
     */
    private String quoted(
        final Object object) {
        return (object == null) ? null : '"' + object.toString() + '"';
    }

}
