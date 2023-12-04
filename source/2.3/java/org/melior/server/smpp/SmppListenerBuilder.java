/* __  __      _ _            
  |  \/  |    | (_)           
  | \  / | ___| |_  ___  _ __ 
  | |\/| |/ _ \ | |/ _ \| '__|
  | |  | |  __/ | | (_) | |   
  |_|  |_|\___|_|_|\___/|_|   
        Service Harness
*/
package org.melior.server.smpp;
import java.util.Arrays;
import java.util.Collection;
import org.melior.client.smpp.SmppClient;

/**
 * Convenience class for building an {@code SmppListener}.  Requires an
 * {@code SmppClient} to be provided.
 * @author Melior
 * @since 2.3
 * @see SmppClient
 */
public class SmppListenerBuilder {

    private Collection<SmppClient> smppClients;

    /**
     * @param entityClass The entity class
     */
    private SmppListenerBuilder() {

        super();
    }

    /**
     * Create SMPP listener builder.
     * @return The SMPP listener builder
     */
    public static SmppListenerBuilder create() {

        return new SmppListenerBuilder();
    }

    /**
     * Build SMPP listener.
     * @return The SMPP listener
     * @throws RuntimeException if unable to build the SMPP listener
     */
    public SmppListener build() {

        if ((smppClients == null) || (smppClients.size() == 0)) {
            throw new RuntimeException( "SMPP client(s) must be provided.");
        }

        return new SmppListener(smppClients);
    }

    /**
     * Set SMPP client.
     * @param smppClients The SMPP clients
     * @return The SMPP listener builder
     */
    public SmppListenerBuilder client(
        final SmppClient... smppClients) {

        this.smppClients = Arrays.asList(smppClients);

        return this;
    }

    /**
     * Set SMPP client.
     * @param smppClients The SMPP clients
     * @return The SMPP listener builder
     */
    public SmppListenerBuilder client(
        final Collection<SmppClient> smppClients) {

        this.smppClients = smppClients;

        return this;
    }

}
