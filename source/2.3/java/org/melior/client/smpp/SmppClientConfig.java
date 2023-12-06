/* __  __      _ _            
  |  \/  |    | (_)           
  | \  / | ___| |_  ___  _ __ 
  | |\/| |/ _ \ | |/ _ \| '__|
  | |  | |  __/ | | (_) | |   
  |_|  |_|\___|_|_|\___/|_|   
        Service Harness
*/
package org.melior.client.smpp;
import org.jsmpp.bean.Alphabet;
import org.jsmpp.bean.MessageClass;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.TypeOfNumber;
import org.melior.client.core.ClientConfig;
import org.melior.util.exception.ExceptionUtil;

/**
 * Configuration parameters for a {@code SmppClient}, with defaults.
 * @author Melior
 * @since 2.3
 */
public class SmppClientConfig extends ClientConfig {

    private String systemType = "";

    private TypeOfNumber sourceTon = TypeOfNumber.INTERNATIONAL;

    private NumberingPlanIndicator sourceNpi = NumberingPlanIndicator.ISDN;

    private TypeOfNumber destinationTon = TypeOfNumber.INTERNATIONAL;

    private NumberingPlanIndicator destinationNpi = NumberingPlanIndicator.ISDN;

    private String addressRange = "[^]*";

    private Alphabet alphabet = Alphabet.ALPHA_DEFAULT;

    private MessageClass messageClass = null;

    private int connections = 1;

    /**
     * Constructor.
     */
    protected SmppClientConfig() {

        super();

        setConnections(1);
    }

    /**
     * Configure client.
     * @param clientConfig The new client configuration parameters
     * @return The client configuration parameters
     */
    public SmppClientConfig configure(
        final SmppClientConfig clientConfig) {
        super.configure(clientConfig);
        this.systemType = clientConfig.systemType;
        this.sourceTon = clientConfig.sourceTon;
        this.sourceNpi = clientConfig.sourceNpi;
        this.destinationTon = clientConfig.destinationTon;
        this.destinationNpi = clientConfig.destinationNpi;
        this.addressRange = clientConfig.addressRange;
        this.alphabet = clientConfig.alphabet;
        this.messageClass = clientConfig.messageClass;
        this.connections = clientConfig.connections;

        return this;
    }

    /**
     * Get system type.
     * @return The system type
     */
    public String getSystemType() {
        return systemType;
    }

    /**
     * Set system type.
     * @param systemType The system type
     */
    public void setSystemType(
        final String systemType) {
        this.systemType = systemType;
    }

    /**
     * Get source TON.
     * @return The source TON
     */
    public TypeOfNumber getSourceTon() {
        return sourceTon;
    }

    /**
     * Set source TON.
     * @param sourceTon The source TON
     */
    public void setSourceTon(
        final TypeOfNumber sourceTon) {
        this.sourceTon = sourceTon;
    }

    /**
     * Get source NPI.
     * @return The source NPI
     */
    public NumberingPlanIndicator getSourceNpi() {
        return sourceNpi;
    }

    /**
     * Set source NPI.
     * @param sourceNpi The source NPI
     */
    public void setSourceNpi(
        final NumberingPlanIndicator sourceNpi) {
        this.sourceNpi = sourceNpi;
    }

    /**
     * Get destination TON.
     * @return The destination TON
     */
    public TypeOfNumber getDestinationTon() {
        return destinationTon;
    }

    /**
     * Set destination TON.
     * @param destinationTon The destination TON
     */
    public void setDestinationTon(
        final TypeOfNumber destinationTon) {
        this.destinationTon = destinationTon;
    }

    /**
     * Get destination NPI.
     * @return The destination NPI
     */
    public NumberingPlanIndicator getDestinationNpi() {
        return destinationNpi;
    }

    /**
     * Set destination NPI.
     * @param destinationNpi The destination NPI
     */
    public void setDestinationNpi(
        final NumberingPlanIndicator destinationNpi) {
        this.destinationNpi = destinationNpi;
    }

    /**
     * Get address range.
     * @return The address range
     */
    public String getAddressRange() {
        return addressRange;
    }

    /**
     * Set address range.
     * @param addressRange The address range
     */
    public void setAddressRange(
        final String addressRange) {
        this.addressRange = addressRange;
    }

    /**
     * Get alphabet.
     * @return The alphabet
     */
    public Alphabet getAlphabet() {
        return alphabet;
    }

    /**
     * Set alphabet.
     * @param alphabet The alphabet
     */
    public void setAlphabet(
        final Alphabet alphabet) {
        this.alphabet = alphabet;
    }

    /**
     * Get message class.
     * @return The message class
     */
    public MessageClass getMessageClass() {
        return messageClass;
    }

    /**
     * Set message class.
     * @param messageClass The message class
     */
    public void setMessageClass(
        final MessageClass messageClass) {
        this.messageClass = messageClass;
    }

    /**
     * Get number of connections.
     * @return The number of connections
     */
    public int getConnections() {
        return connections;
    }

    /**
     * Set number of connections.
     * @param connections The number of connections
     */
    public void setConnections(
        final int connections) {
        this.connections = connections;
        ExceptionUtil.swallow(() -> setMinimumConnections(connections));
        ExceptionUtil.swallow(() -> setMaximumConnections(connections));
        setInactivityTimeout(0);
    }

}
