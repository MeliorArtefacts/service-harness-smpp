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
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import org.jsmpp.SMPPConstant;
import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.DeliveryReceipt;
import org.jsmpp.bean.MessageType;
import org.jsmpp.extra.ProcessRequestException;
import org.jsmpp.util.InvalidDeliveryReceiptException;
import org.melior.client.exception.RemotingException;
import org.melior.client.smpp.SmppClient;
import org.melior.client.smpp.SmppMessage;
import org.melior.context.transaction.TransactionContext;
import org.melior.logging.core.Logger;
import org.melior.logging.core.LoggerFactory;
import org.melior.service.work.SingletonProcessor;
import org.melior.util.number.Counter;

/**
 * Implements an easy to use, auto-configuring SMPP listener which listens
 * for messages and receipts and processes any new messages and receipts
 * that arrive.
 * <p>
 * If a {@code SingletonProcessor} is configured for messages, then any
 * new messages that arrive will be processed by the listener individually.
 * <p>
 * If a {@code SingletonProcessor} is configured for receipts, then any
 * new receipts that arrive will be processed by the listener individually.
 * <p>
 * The listener may be configured with multiple threads to speed up processing.
 * @author Melior
 * @since 2.3
 * @see SmppMessage
 * @see SmppReceipt
 */
public class SmppListener extends SmppListenerConfig {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Collection<SmppClient> clients;

    private SingletonProcessor<SmppMessage> messageProcessor;

    private SingletonProcessor<SmppReceipt> receiptProcessor;

    private Counter totalMessages;

    private Counter failedMessages;

    private Counter totalReceipts;

    private Counter failedReceipts;

    /**
     * Constructor.
     * @param client The SMPP client
     */
    SmppListener(
        final Collection<SmppClient> clients) {

        super();

        this.clients = clients;

        totalMessages = Counter.of(0);
        failedMessages = Counter.of(0);
        totalReceipts = Counter.of(0);
        failedReceipts = Counter.of(0);
    }

    /**
     * Set message processor.  New messages that arrive from the SMSC implementation
     * will be processed individually.
     * @param messageProcessor The message processor
     * @return The SMPP listener
     */
    public SmppListener message(
        final SingletonProcessor<SmppMessage> messageProcessor) {
        this.messageProcessor = messageProcessor;

        return this;
    }

    /**
     * Set receipt processor.  New receipts that arrive from the SMSC implementation
     * will be processed individually.
     * @param receiptProcessor The receipt processor
     * @return The SMPP listener
     */
    public SmppListener receipt(
        final SingletonProcessor<SmppReceipt> receiptProcessor) {
        this.receiptProcessor = receiptProcessor;

        return this;
    }

    /**
     * Start listening for messages and receipts.
     * @throws RemotingException if unable to start listening for messages and receipts
     */
    public void start() throws RemotingException {
        listen();
    }

    /**
     * Listen for messages and receipts and process new arrivals.
     */
    private void listen() {

        String methodName = "listen";

        logger.debug(methodName, "Started listening for messages and receipts.");

        for (SmppClient client : clients) {
            client.setListener(this);
        }

    }

    /**
     * Get message processor.
     * @return The message processor
     */
    SingletonProcessor<SmppMessage> getMessageProcessor() {
        return messageProcessor;
    }

    /**
     * Get receipt processor.
     * @return The receipt processor
     */
    SingletonProcessor<SmppReceipt> getReceiptProcessor() {
        return receiptProcessor;
    }

    /**
     * Process received message.
     * @param deliverSm The received message
     * @throws ProcessRequestException if unable to process the received message
     */
    public void receive(
        final DeliverSm deliverSm) throws ProcessRequestException {

        String methodName = "receive";
        DeliveryReceipt deliveryReceipt;
        SmppReceipt receipt;
        SmppMessage message;

        if (MessageType.SMSC_DEL_RECEIPT.containedIn(deliverSm.getEsmClass()) == true) {
            logger.debug(methodName, "Receipt has been received: total=", totalReceipts.get(), ", failed=", failedReceipts.get());

            try {

                deliveryReceipt = deliverSm.getShortMessageAsDeliveryReceipt();

                receipt = SmppReceipt.of(deliverSm.getSourceAddr(), deliverSm.getDestAddress(),
                    new String(deliverSm.getShortMessage()), deliveryReceipt.getId(),
                    convert(deliveryReceipt.getSubmitDate()), convert(deliveryReceipt.getDoneDate()),
                    deliveryReceipt.getFinalStatus(), deliveryReceipt.getError());
            }
            catch (InvalidDeliveryReceiptException exception) {
                logger.warn(methodName, "Receipt will be rejected because it cannot be understood.");

                throw new ProcessRequestException(exception.getMessage(), SMPPConstant.STAT_ESME_RX_R_APPN, exception);
            }

            logger.debug(methodName, "receipt = ", receipt);

            processReceipt(receipt);
        }
        else {
            logger.debug(methodName, "Message has been received: total=", totalMessages.get(), ", failed=", failedMessages.get());

            message = SmppMessage.of(deliverSm.getSourceAddr(), deliverSm.getDestAddress(),
                new String(deliverSm.getShortMessage()), deliverSm.getId());

            logger.debug(methodName, "message = ", message);

            processMessage(message);
        }

    }

    /**
     * Process message.
     * @param message The message
     * @throws ProcessRequestException if unable to process the message
     */
    private void processMessage(
        final SmppMessage message) throws ProcessRequestException {

        String methodName = "processMessage";
        TransactionContext transactionContext;

        if (messageProcessor == null) {
            logger.warn(methodName, "Message will be discarded because no processor has been registered.");

            return;
        }

        totalMessages.increment();

        try {

            transactionContext = TransactionContext.get();
            transactionContext.startTransaction();
            transactionContext.setTransactionId(getTransactionId());
            transactionContext.setCorrelationId(transactionContext.getTransactionId());

            try {

                messageProcessor.process(message);
            }
            finally {

                transactionContext.reset();
            }

        }
        catch (Throwable exception) {

            failedMessages.increment();

            throw new ProcessRequestException(exception.getMessage(), SMPPConstant.STAT_ESME_RX_T_APPN, exception);
        }

    }
    
    /**
     * Process receipt.
     * @param receipt The receipt
     * @throws ProcessRequestException if unable to process the receipt
     */
    private void processReceipt(
        final SmppReceipt receipt) throws ProcessRequestException {

        String methodName = "processReceipt";
        TransactionContext transactionContext;

        if (receiptProcessor == null) {
            logger.warn(methodName, "Receipt will be discarded because no processor has been registered.");

            return;
        }

        totalReceipts.increment();

        try {

            transactionContext = TransactionContext.get();
            transactionContext.startTransaction();
            transactionContext.setTransactionId(getTransactionId());
            transactionContext.setCorrelationId(transactionContext.getTransactionId());

            try {

                receiptProcessor.process(receipt);
            }
            finally {

                transactionContext.reset();
            }

        }
        catch (Throwable exception) {

            failedReceipts.increment();

            throw new ProcessRequestException(exception.getMessage(), SMPPConstant.STAT_ESME_RX_T_APPN, exception);
        }

    }

    /**
     * Convert {@code Date} to {@code LocalDateTime}.
     * @param date The date
     * @return The local date time
     */
    private LocalDateTime convert(
        final Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * Get transaction identifier.  Generates a UUID.
     * @return The resultant transaction identifier
     */
    private String getTransactionId() {

        return UUID.randomUUID().toString();
    }

}
