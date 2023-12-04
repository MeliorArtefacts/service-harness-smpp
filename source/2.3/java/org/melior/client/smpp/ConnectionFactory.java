/* __  __      _ _            
  |  \/  |    | (_)           
  | \  / | ___| |_  ___  _ __ 
  | |\/| |/ _ \ | |/ _ \| '__|
  | |  | |  __/ | | (_) | |   
  |_|  |_|\___|_|_|\___/|_|   
        Service Harness
*/
package org.melior.client.smpp;
import org.jsmpp.bean.BindType;
import org.jsmpp.session.SMPPSession;
import org.melior.client.exception.RemotingException;
import org.melior.client.pool.ConnectionPool;
import org.melior.server.smpp.SmppListener;

/**
 * Implements a factory for persistent SMPP {@code Connection} objects.
 * @author Melior
 * @since 2.3
 */
public class ConnectionFactory implements org.melior.client.core.ConnectionFactory<SmppClientConfig, Connection, SMPPSession> {

    private BindType bindType;

    private SmppListener listener;

    /**
     * Constructor.
     * @param configuration The client configuration
     * @param bindType The bind type
     * @param listener The message listener
     * @throws RemotingException if unable to initialize the connection factory
     */
    public ConnectionFactory(
        final SmppClientConfig configuration,
        final BindType bindType,
        final SmppListener listener) throws RemotingException {

        super();

        this.bindType = bindType;
        this.listener = listener;
    }

    /**
     * Create a new connection.
     * @param configuration The client configuration
     * @param connectionPool The connection pool
     * @return The new connection
     * @throws RemotingException if unable to create a new connection
     */
    public Connection createConnection(
        final SmppClientConfig configuration,
        final ConnectionPool<SmppClientConfig, Connection, SMPPSession> connectionPool) throws RemotingException {

        Connection connection;

        connection = new Connection(configuration, connectionPool, bindType, listener);
        connection.open();

        return connection;
    }

    /**
     * Destroy the connection.
     * @param connection The connection
     */
    public void destroyConnection(
        final Connection connection) {

        connection.close();
    }

}
