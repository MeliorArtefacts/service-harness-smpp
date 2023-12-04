/* __  __      _ _            
  |  \/  |    | (_)           
  | \  / | ___| |_  ___  _ __ 
  | |\/| |/ _ \ | |/ _ \| '__|
  | |  | |  __/ | | (_) | |   
  |_|  |_|\___|_|_|\___/|_|   
        Service Harness
*/
package org.melior.client.smpp;
import org.jsmpp.session.SMPPSession;

/**
 * Implements a manager for persistent SMPP {@code Connection} objects, for connections to
 * SMSC implementations. The manager writes statistics from the underlying connection pool
 * to the logs whenever a {@code Connection} is borrowed from the pool.
 * @author Melior
 * @since 2.3
 */
public class ConnectionManager extends org.melior.client.pool.ConnectionManager<SmppClientConfig, Connection, SMPPSession> {

    /**
     * Constructor.
     * @param configuration The client configuration
     * @param connectionFactory The connection factory
     */
    public ConnectionManager(
        final SmppClientConfig configuration,
        final ConnectionFactory connectionFactory) {

        super(configuration, connectionFactory);
    }

}
