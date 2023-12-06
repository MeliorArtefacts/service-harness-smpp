/* __  __      _ _            
  |  \/  |    | (_)           
  | \  / | ___| |_  ___  _ __ 
  | |\/| |/ _ \ | |/ _ \| '__|
  | |  | |  __/ | | (_) | |   
  |_|  |_|\___|_|_|\___/|_|   
        Service Harness
*/
package org.melior.client.smpp;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.DataCoding;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.GeneralDataCoding;
import org.jsmpp.bean.OptionalParameter;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.session.ClientSession;
import org.jsmpp.session.SubmitSmResult;
import org.melior.client.exception.RemotingException;
import org.melior.logging.core.Logger;
import org.melior.logging.core.LoggerFactory;
import org.melior.server.smpp.SmppListener;
import org.melior.service.exception.ExceptionType;
import org.melior.util.object.ObjectUtil;
import org.melior.util.time.Timer;
import org.springframework.util.StringUtils;

/**
 * Implements an easy to use, auto-configuring SMPP client with connection pooling.
 * <p>
 * The client writes timing details to the logs while dispatching SMPP requests
 * to the SMSC implementation.  The client automatically converts any exception
 * that occurs during communication with the SMSC implementation into a standard
 * {@code RemotingException}.
 * @author Melior
 * @since 2.3
 */
public class SmppClient extends SmppClientConfig {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private BindType bindType;

    private boolean flipMmts;

    private ConnectionManager connectionManager;

    private Random messageNumber;

    private SmppListener listener;

    /**
     * Constructor.
     * @param bindType The bind type
     * @param flipMmts The flip more-messages-to-send indicator
     */
    SmppClient(
        final BindType bindType,
        final boolean flipMmts) {

        super();

        this.bindType = bindType;

        this.flipMmts = flipMmts;

        messageNumber = new Random();
    }

    /**
     * Configure client.
     * @param clientConfig The new client configuration parameters
     * @return The SMPP client
     */
    public SmppClient configure(
        final SmppClientConfig clientConfig) {
        super.configure(clientConfig);

        return this;
    }

    /**
     * Initialize client.
     * @throws RemotingException if unable to initialize the client
     */
    private void initialize() throws RemotingException {

        if (connectionManager != null) {
            return;
        }

        if (StringUtils.hasLength(getUrl()) == false) {
            throw new RemotingException(ExceptionType.LOCAL_APPLICATION, "URL must be configured.");
        }

        if (StringUtils.hasLength(getUsername()) == false) {
            throw new RemotingException(ExceptionType.LOCAL_APPLICATION, "User name must be configured.");
        }

        if (StringUtils.hasLength(getPassword()) == false) {
            throw new RemotingException(ExceptionType.LOCAL_APPLICATION, "Password must be configured.");
        }

        connectionManager = new ConnectionManager(this, new ConnectionFactory(this, bindType, listener));
    }

    /**
     * Send message.
     * @param message The SMPP message
     * @param registeredDelivery true if the message should be sent using registered delivery, false otherwise
     * @return The message identifier
     * @throws RemotingException if unable to send the message
     */
    public String send(
        final SmppMessage message,
        final boolean registeredDelivery) throws RemotingException {

        String methodName = "send";
        Timer timer;
        ClientSession connection;
        DataCoding dataCoding;
	    short messageReference;
        String[] segments;
        byte segmentSeq;
        SubmitSmResult response;
        String messageId = null;
        long duration;

        initialize();

        logger.debug(methodName, "message = ", message);

        timer = Timer.ofNanos().start();

        try {

            connection = connectionManager.getConnection();

            try {

                dataCoding = getDataCoding();

                if (message.getMessageText().length() > 160) {

                    messageReference = (short) messageNumber.nextInt();

                    segments = getSegments(message.getMessageText());

                    for (byte i = 0; i < segments.length; i++) {

                        segmentSeq = (byte) (i + 1);

                        response = sendSegment(connection, message.getSourceAddress(), message.getDestinationAddress(),
                            new ESMClass(), segments[i], getRegisteredDelivery((i == 0) && registeredDelivery), dataCoding,
                            new OptionalParameter.More_messages_to_send((byte) (((segmentSeq == segments.length) ? 0 : 1) ^ ((flipMmts == true) ? 1 : 0))),
                            new OptionalParameter.Sar_msg_ref_num(messageReference),
                            new OptionalParameter.Sar_segment_seqnum(segmentSeq),
                            new OptionalParameter.Sar_total_segments((byte) segments.length));

                        messageId = ObjectUtil.coalesce(messageId, response.getMessageId());
                    }

                }
                else {

                    response = sendSegment(connection, message.getSourceAddress(), message.getDestinationAddress(),
                        new ESMClass(), message.getMessageText(), getRegisteredDelivery(registeredDelivery), dataCoding,
                        new OptionalParameter.More_messages_to_send((byte) (0 ^ ((flipMmts == true) ? 1 : 0))));

                    messageId = response.getMessageId();
                }

            }
            finally {

                connection.close();
            }

            duration = timer.elapsedTime(TimeUnit.MILLISECONDS);

            logger.debug(methodName, "Message sent successfully.  Duration = ", duration, " ms.");

            logger.debug(methodName, "messageId = ", messageId);
        }
        catch (NegativeResponseException exception) {

            duration = timer.elapsedTime(TimeUnit.MILLISECONDS);

            logger.debug(methodName, "Message send failed.  Duration = ", duration, " ms.");

            throw new RemotingException(ExceptionType.REMOTING_APPLICATION, exception.getMessage(), exception);
        }
        catch (Exception exception) {

            duration = timer.elapsedTime(TimeUnit.MILLISECONDS);

            logger.debug(methodName, "Message send failed.  Duration = ", duration, " ms.");

            throw new RemotingException(ExceptionType.REMOTING_COMMUNICATION, exception.getMessage(), exception);
        }
        catch (Throwable exception) {
            throw new RemotingException(ExceptionType.REMOTING_COMMUNICATION, "Failed to send message: " + exception.getMessage(), exception);
        }

        return messageId;
    }

    /**
     * Set listener.
     * @param listener The listener
     */
    public void setListener(
        final SmppListener listener) {
        this.listener = listener;
    }

    /**
     * Send message segment.
     * @param connection The SMPP connection
     * @param sourceAddress The source address
     * @param destinationAddress The destination address
     * @param esmClass The ESM class
     * @param segmentText The message text
     * @param registeredDelivery The registered delivery directive
     * @param dataCoding The data coding
     * @param optionalParameters The optional parameters [more-messages-to-send, multi-part messages]
     * @return The response
     * @throws Exception if unable to send the message segment
     */
    private SubmitSmResult sendSegment(
        final ClientSession connection,
        final String sourceAddress,
        final String destinationAddress,
        final ESMClass esmClass,
        final String segmentText,
        final RegisteredDelivery registeredDelivery,
        final DataCoding dataCoding,
        final OptionalParameter... optionalParameters) throws Exception {

        SubmitSmResult response;

        response = connection.submitShortMessage("",
            getSourceTon(), getSourceNpi(), sourceAddress,
            getDestinationTon(), getDestinationNpi(), destinationAddress,
            esmClass, (byte) 0, (byte) 1, null, null, registeredDelivery,
            (byte) 0, dataCoding, (byte) 0, segmentText.getBytes(),
            optionalParameters);

        return response;
    }

    /**
     * Get message segments.
     * @param messageText The message text
     * @return The message segments
     */
    private String[] getSegments(
        final String messageText) {

        int segmentCount;
        String[] segments;

        segmentCount = (int) Math.ceil(messageText.length() / 140d);

        segments = new String[segmentCount];

        for (int i = 0; i < segmentCount; i++) {
            segments[i] = messageText.substring((i * 140), Math.min(((i + 1) * 140), messageText.length()));
        }

        return segments;
    }

    /**
     * Get registered delivery directive.
     * @param deliveryReceipt The delivery receipt indicator
     * @return The registered delivery directive
     */
    private RegisteredDelivery getRegisteredDelivery(
        final boolean deliveryReceipt) {
        return new RegisteredDelivery((deliveryReceipt == true) ? SMSCDeliveryReceipt.SUCCESS_FAILURE : SMSCDeliveryReceipt.DEFAULT);
    }

    /**
     * Get data coding.
     * @return The data coding
     */
    private DataCoding getDataCoding() {
        return new GeneralDataCoding(getAlphabet(), getMessageClass(), false);
    }

}
