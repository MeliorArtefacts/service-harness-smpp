/* __  __      _ _            
  |  \/  |    | (_)           
  | \  / | ___| |_  ___  _ __ 
  | |\/| |/ _ \ | |/ _ \| '__|
  | |  | |  __/ | | (_) | |   
  |_|  |_|\___|_|_|\___/|_|   
        Service Harness
*/
package org.melior.server.smpp;
import org.melior.client.core.ClientConfig;
import org.melior.util.number.Clamp;

/**
 * Configuration parameters for an {@code SmppListener}, with defaults.
 * @author Melior
 * @since 2.3
 */
public class SmppListenerConfig extends ClientConfig {

    private int threads = 1;

    /**
     * Constructor.
     */
    protected SmppListenerConfig() {

        super();
    }

    /**
     * Get threads.
     * @return The threads
     */
    public int getThreads() {
        return threads;
    }

    /**
     * Set threads.
     * @param threads The threads
     */
    public void setThreads(
        final int threads) {
        this.threads = Clamp.clampInt(threads, 1, Integer.MAX_VALUE);
    }

}
