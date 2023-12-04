/* __  __      _ _            
  |  \/  |    | (_)           
  | \  / | ___| |_  ___  _ __ 
  | |\/| |/ _ \ | |/ _ \| '__|
  | |  | |  __/ | | (_) | |   
  |_|  |_|\___|_|_|\___/|_|   
        Service Harness
*/
package org.melior.client.smpp;
import java.lang.reflect.Method;
import java.net.URI;
import org.jsmpp.bean.AlertNotification;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.DataSm;
import org.jsmpp.bean.DeliverSm;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ProcessRequestException;
import org.jsmpp.session.DataSmResult;
import org.jsmpp.session.MessageReceiverListener;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.session.Session;
import org.melior.client.exception.RemotingException;
import org.melior.client.pool.ConnectionPool;
import org.melior.server.smpp.SmppListener;
import org.melior.service.exception.ExceptionType;

/**
 * Implements a wrapper around an SMPP {@code Connection} delegate.  The connection
 * is pooled until it experiences a connectivity error, or until it expires, either
 * due to being in surplus at the timeout interval, or due to it reaching the maximum
 * lifetime for a connection.
 * @author Melior
 * @since 2.3
 */
public class Connection extends org.melior.client.core.Connection<SmppClientConfig, Connection, SMPPSession> {

    private BindType bindType;

    private SmppListener listener;

    /**
     * Constructor.
     * @param configuration The client configuration
     * @param connectionPool The connection pool
     * @param bindType The bind type
     * @param listener The message listener
     * @throws RemotingException if an error occurs during the construction
     */
    public Connection(
        final SmppClientConfig configuration,
        final ConnectionPool<SmppClientConfig, Connection, SMPPSession> connectionPool,
        final BindType bindType,
        final SmppListener listener) throws RemotingException {

        super(configuration, connectionPool);

        this.bindType = bindType;
        this.listener = listener;
    }

    /**
     * Check whether connection is still valid.
     * @param fullValidation The full validation indicator
     * @return true if the connection is still valid, false otherwise
     */
    public boolean isValid(
        final boolean fullValidation) {

        if (lastException != null) {

            if (lastException instanceof NegativeResponseException) {
                return true;
            }

            return false;
        }

        return true;
    }

    /**
     * Open raw connection.
     * @return The raw connection
     * @throws Exception if unable to open the raw connection
     */
    protected SMPPSession openConnection() throws Exception {

        URI uri;
        SMPPSession connection;

        try {

            uri = new URI(configuration.getUrl());
        }
        catch (Exception exception) {
            throw new RemotingException(ExceptionType.LOCAL_APPLICATION, "Failed to parse URL: " + exception.getMessage(), exception);
        }

        connection = new SMPPSession();
        connection.setPduProcessorDegree(listener.getThreads());
        connection.connectAndBind(uri.getHost(), uri.getPort(), bindType,
            configuration.getUsername(), configuration.getPassword(),
            configuration.getSystemType(), configuration.getSourceTon(),
            configuration.getSourceNpi(), configuration.getAddressRange(),
            configuration.getConnectionTimeout());
        connection.setEnquireLinkTimer(configuration.getConnectionTimeout());
        connection.setTransactionTimer(configuration.getRequestTimeout());
        connection.setMessageReceiverListener(new MessageReceiverListener() {
            
            public void onAcceptDeliverSm(
                final DeliverSm deliverSm) throws ProcessRequestException {
                listener.receive(deliverSm);
            }

            public DataSmResult onAcceptDataSm(
                final DataSm dataSm,
                final Session source) throws ProcessRequestException {
                return null;
            }

            public void onAcceptAlertNotification(
                final AlertNotification alertNotification) {
            }

        });

        return connection;
    }

    /**
     * Close raw connection.
     * @param connection The raw connection
     * @throws Exception if unable to close the raw connection
     */
    protected void closeConnection(
        final SMPPSession connection) throws Exception {

        connection.unbindAndClose();
    }

    /**
     * Handle proxy invocation.
     * @param object The object on which the method was invoked
     * @param method The method to invoke
     * @param args The arguments to invoke with
     * @return The result of the invocation
     * @throws Throwable if the invocation fails
     */
    public Object invoke(
        final Object object,
        final Method method,
        final Object[] args) throws Throwable {

        String methodName;
        Object invocationResult;

        methodName = method.getName();

        if (methodName.equals("close") == true) {

            releaseConnection(this);

            invocationResult = null;
        }
        else {

            invocationResult = invoke(method, args);
        }

        return invocationResult;
    }

}
