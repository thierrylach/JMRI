package jmri.jmrix.cmri.serial;

import java.io.DataInputStream;
import java.util.ArrayList;
import jmri.jmrix.AbstractMRListener;
import jmri.jmrix.AbstractMRMessage;
import jmri.jmrix.AbstractMRNodeTrafficController;
import jmri.jmrix.AbstractMRReply;
import jmri.jmrix.cmri.serial.cmrinetmetrics.CMRInetMetricsData;
import jmri.jmrix.cmri.serial.cmrinetmetrics.CMRInetMetricsCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts Stream-based I/O to/from C/MRI serial messages.
 * <p>
 * The "SerialInterface" side sends/receives message objects.
 * <p>
 * The connection to a SerialPortController is via a pair of *Streams, which
 * then carry sequences of characters for transmission. Note that this
 * processing is handled in an independent thread.
 * <p>
 * This handles the state transitions, based on the necessary state in each
 * message.
 * <p>
 * Handles initialization, polling, output, and input for multiple Serial Nodes.
 *
 * @author Bob Jacobsen Copyright (C) 2003
 * @author Bob Jacobsen, Dave Duchamp, multiNode extensions, 2004
 * @author Chuck Catania Copyright (C) 2014,2016 CMRInet extensions
 */
public class SerialTrafficController extends AbstractMRNodeTrafficController implements SerialInterface {

    /**
     * Create a new C/MRI SerialTrafficController instance.
     */
    CMRInetMetricsCollector metricsCollector;

    public SerialTrafficController() {
        super();

        // set node range
        super.init(0, 127);

        // entirely poll driven, so reduce interval
        mWaitBeforePoll = 5;  // default = 25

        metricsCollector = new CMRInetMetricsCollector();
        addSerialListener(metricsCollector);
    }

    // The methods to implement the SerialInterface
    @Override
    public synchronized void addSerialListener(SerialListener l) {
        this.addListener(l);
    }

    @Override
    public synchronized void removeSerialListener(SerialListener l) {
        this.removeListener(l);
    }

    /**
     * Initialize a CMRI node.
     *
     * @param node the node to initialize
     */
    public void initializeSerialNode(SerialNode node) {
        synchronized (this) {
            // find the node in the registered node list
            for (int i = 0; i < getNumNodes(); i++) {
                if (getNode(i) == node) {
                    // found node - set up for initialization
                    setMustInit(i, true);
                    return;
                }
            }
        }
    }

    @Override
    protected AbstractMRMessage enterProgMode() {
        log.warn("enterProgMode doesn't make sense for C/MRI serial");
        return null;
    }

    /**
     * Expose metrics data
     * @return metrics data
     */
    public CMRInetMetricsData getMetricsData()
    {
      return metricsCollector.getMetricData();
    }

    @Override
    protected AbstractMRMessage enterNormalMode() {
        // can happen during error recovery, null is OK
        return null;
    }

    /**
     * Forward a message to all registered listeners.
     *
     * @param client the listener to receive the message; may throw an uncaught
     *               exception if not a
     *               {@link jmri.jmrix.cmri.serial.SerialListener}
     * @param m      the message to forward; may throw an uncaught exception if
     *               not a {@link jmri.jmrix.cmri.serial.SerialMessage}
     */
    @Override
    protected void forwardMessage(AbstractMRListener client, AbstractMRMessage m) {
        ((SerialListener) client).message((SerialMessage) m);
//        log.info("forward Message");

    }

    /**
     * Forward a reply to all registered listeners.
     *
     * @param client the listener to receive the reply; may throw an uncaught
     *               exception if not a
     *               {@link jmri.jmrix.cmri.serial.SerialListener}
     * @param m      the reply to forward; may throw an uncaught exception if
     *               not a {@link jmri.jmrix.cmri.serial.SerialMessage}
     */
    @Override
    protected void forwardReply(AbstractMRListener client, AbstractMRReply m) {
        ((SerialListener) client).reply((SerialReply) m);
//        log.info("reply Message");
    }

    SerialSensorManager mSensorManager = null;

    public void setSensorManager(SerialSensorManager m) {
        mSensorManager = m;
    }

    public boolean pollNetwork = true;  // true if network polling enabled

    // For later enhancements to network manager
    //------------------------------------------
    private int initTimeout = 500;
    private int xmitTimeout = 2;
    // cpNode poll list
    public ArrayList<Integer> cmriNetPollList = new ArrayList<>();

    public void setPollNetwork(boolean OnOff) {
        pollNetwork = OnOff;
    }

    public boolean getPollNetwork() {
        return pollNetwork;
    }

    public void setInitTimeout(int init_Timeout) {
        initTimeout = init_Timeout;
    }

    public int getInitTimeout() {
        return initTimeout;
    }

    public void setXmitTimeout(int init_XmitTimeout) {
        xmitTimeout = init_XmitTimeout;
    }

    public int getXmitTimeout() {
        return xmitTimeout;
    }

    /**
     * Handles initialization, output and polling for C/MRI Serial Nodes from
     * within the running thread.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected synchronized AbstractMRMessage pollMessage() {
        log.trace("pollMessage starts");
        // ensure validity of call
        if (getNumNodes() <= 0) {
            log.trace("pollMessage ends with zero nodes");
            return null;
        }

        // If total network polling not enabled, exit
        if (!getPollNetwork()) {
            log.trace("pollMessage ends with getPollNetwork disabled");
            return null;
        }

        int previousPollPointer = curSerialNodeIndex;
        updatePollPointer(); // service next node next

        // ensure that each node is initialized
        SerialNode n = (SerialNode) getNode(curSerialNodeIndex);

        if (getMustInit(curSerialNodeIndex)) {
            setMustInit(curSerialNodeIndex, false);
            AbstractMRMessage m = getNode(curSerialNodeIndex).createInitPacket();
            log.debug("send init message: {}", m);
            m.setTimeout(500);  // wait for init to finish (milliseconds)
            // m.setTimeout( getInitTimeout() );  //c2
            n.setPollStatus(SerialNode.POLLSTATUS_INIT); //c2

            log.trace("pollMessage provides Init message");
            return m;
        }
        // send Output packet if needed
        if (getNode(curSerialNodeIndex).mustSend()) {
            log.debug("request write command to send");
            getNode(curSerialNodeIndex).resetMustSend();
            AbstractMRMessage m = getNode(curSerialNodeIndex).createOutPacket();
            m.setTimeout(2);  // no need to wait for output to answer
            // m.setTimeout( getXmitTimeout() );  // no need to wait for output to answer

            // reset poll pointer update, so next increment will poll from here
            curSerialNodeIndex = previousPollPointer;
            log.trace("pollMessage provides Transmit message");
            return m;
        }

        // poll for Sensor input
        //-------------------------------------
        // Poll node if polling enabled for this node  //c2
        // update polling status for the node
        //-------------------------------------

        if (!n.getPollingEnabled()) {
            n.setPollStatus(SerialNode.POLLSTATUS_IDLE);
            log.trace("pollMessage ends with getPollingEnabled disabled");
            return null;
        } else if (getNode(curSerialNodeIndex).getSensorsActive()) {
            if (n.getPollStatus() != SerialNode.POLLSTATUS_POLLING) {
                n.setPollStatus(SerialNode.POLLSTATUS_POLLING);
            }

            // Some sensors are active for this node, issue poll
            SerialMessage m = SerialMessage.getPoll(
                    getNode(curSerialNodeIndex).getNodeAddress());
            log.trace("pollMessage ends with poll message");
            return m;
        } else {
            // no Sensors (inputs) are active for this node
            log.trace("pollMessage ends with no sensors active");
            return null;
        }
    }

    /**
     * Update the curSerialNodeIndex so next node polled next time
     */
    private void updatePollPointer() {
        curSerialNodeIndex++;
        if (curSerialNodeIndex >= getNumNodes()) {
            curSerialNodeIndex = 0;
        }
    }

    @Override
    /**
     * Log an error message for a message received in an unexpected state.
     *
     * The severity depends on whether this is a network connection or not.
     *
     * @param State message state.
     * @param msgString message string.
     */
    protected void unexpectedReplyStateError(int State, String msgString) {
        String[] packages = this.getClass().getName().split("\\.");
        String name = (packages.length>=2 ? packages[packages.length-2]+"." :"")
                     +(packages.length>=1 ? packages[packages.length-1] :"");
        // determine the connection type
        if (controller instanceof jmri.jmrix.cmri.serial.networkdriver.NetworkDriverAdapter) {
            // these are probably normal
            log.debug("reply complete in unexpected state: {} was {} in class {}", State, msgString, name);
        } else {
            // other types of connections, make visible
            log.warn("reply complete in unexpected state: {} was {} in class {}", State, msgString, name);
        }


    }

    @Override
    protected synchronized void handleTimeout(AbstractMRMessage m, AbstractMRListener l) {
        // don't use super behavior, as timeout to init, transmit message is normal
        SerialNode n = (SerialNode) getNode(curSerialNodeIndex);

        // inform node, and if it resets then reinitialize
        if (getNode(curSerialNodeIndex).handleTimeout(m, l)) {
            if (n.getPollingEnabled()) //c2
            {
                n.setPollStatus(SerialNode.POLLSTATUS_TIMEOUT);
             metricsCollector.getMetricData().incMetricErrValue(CMRInetMetricsData.CMRInetMetricTimeout);
            }
            setMustInit(curSerialNodeIndex, true);
        }

    }

    @Override
    protected synchronized void resetTimeout(AbstractMRMessage m) {
        // don't use super behavior, as timeout to init, transmit message is normal

        // and inform node
        getNode(curSerialNodeIndex).resetTimeout(m);

    }

    @Override
    protected AbstractMRListener pollReplyHandler() {
        return mSensorManager;
    }

    /**
     * Forward a pre-formatted message to the actual interface.
     *
     * @param m     the message to forward
     * @param reply the listener for the response to m
     */
    @Override
    public void sendSerialMessage(SerialMessage m, SerialListener reply) {
        sendMessage(m, reply);
    }

    @Override
    protected AbstractMRReply newReply() {
        return new SerialReply();
    }

    @Override
    protected boolean endOfMessage(AbstractMRReply msg) {
        // our version of loadChars doesn't invoke this, so it shouldn't be called
        log.error("Not using endOfMessage, should not be called");
        return false;
    }

    @Override
    protected void loadChars(AbstractMRReply msg, DataInputStream istream) throws java.io.IOException {
        int i;
        for (i = 0; i < msg.maxSize(); i++) {
            byte char1 = readByteProtected(istream);
            if (char1 == 0x03) {
                break;           // check before DLE handling
            }
            if (char1 == 0x10) {
                char1 = readByteProtected(istream);
            }
            msg.setElement(i, char1 & 0xFF);
        }
    }

    @Override
    protected void waitForStartOfReply(DataInputStream istream) throws java.io.IOException {
        // loop looking for the start character
        while (readByteProtected(istream) != 0x02) {
        }
    }

    /**
     * Add header to the outgoing byte stream.
     *
     * @param msg the output byte stream
     * @param m   the message in msg
     * @return next location in the stream to fill
     */
    @Override
    protected int addHeaderToOutput(byte[] msg, AbstractMRMessage m) {
        msg[0] = (byte) 0xFF;
        msg[1] = (byte) 0xFF;
        msg[2] = (byte) 0x02;  // STX
        return 3;
    }

    /**
     * Add trailer to the outgoing byte stream.
     *
     * @param msg    the output byte stream
     * @param offset the first byte not yet used
     * @param m      the message in msg
     */
    @Override
    protected void addTrailerToOutput(byte[] msg, int offset, AbstractMRMessage m) {
        msg[offset] = 0x03;  // etx
    }

    /**
     * Determine how much many bytes the entire message will take, including
     * space for header and trailer
     *
     * @param m The message to be sent
     * @return Number of bytes
     */
    @Override
    protected int lengthOfByteStream(AbstractMRMessage m) {
        int len = m.getNumDataElements();
        int cr = 4;
        return len + cr;
    }

    private final static Logger log = LoggerFactory.getLogger(SerialTrafficController.class);

}
