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

/**
 * Convenience class for building an {@code SmppClient}.
 * @author Melior
 * @since 2.3
 */
public class SmppClientBuilder {

    private BindType bindType = BindType.BIND_TX;

    private boolean flipMmts = false;

    /**
     * Constructor.
     */
    private SmppClientBuilder() {

        super();
    }

    /**
     * Create SMPP client builder.
     * @return The SMPP client builder
     */
    public static SmppClientBuilder create() {

        return new SmppClientBuilder();
    }

    /**
     * Build SMPP client.
     * @return The SMPP client
     */
    public SmppClient build() {

        return new SmppClient(bindType, flipMmts);
    }

    /**
     * Set bind type.
     * @param bindType The bind type
     * @return The SMPP client builder
     */
    public SmppClientBuilder bindType(
        final BindType bindType) {

        this.bindType = bindType;

        return this;
    }

    /**
     * Enable more-messages-to-send flip.  The SMPP specification states that for the {@code more_messages_to_send}
     * parameter a value of {@code 1} should be used if more messages are to follow, otherwise a value of
     * {@code 0}.  Some SMSC implementation have it the other way around.  This method enables a flip of
     * the {@code more_messages_to_send} parameter value to make it compatible with the non-compliant
     * SMSC implementations.
     * @return The SMPP client builder
     */
    public SmppClientBuilder flipMmts() {

        this.flipMmts = true;

        return this;
    }

}
