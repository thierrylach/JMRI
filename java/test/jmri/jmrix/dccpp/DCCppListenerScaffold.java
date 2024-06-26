/**
 * DCCppListenerScaffold.java
 *
 * test implementation of DCCppListener
 *
 * @author Bob Jacobsen
 * @author Mark Underwood
 */
package jmri.jmrix.dccpp;

public class DCCppListenerScaffold implements jmri.jmrix.dccpp.DCCppListener {

    public DCCppListenerScaffold() {
        rcvdMsg = null;
        rcvdRply = null;
        timeOutMsg = null;
        rcvCount = 0;
    }

    @Override
    public void message(DCCppReply m) {
        rcvdRply = m;
        rcvCount++;
    }

    @Override
    public void message(DCCppMessage m) {
        rcvdMsg = m;
    }

    @Override
    public void notifyTimeout(DCCppMessage m) {
        timeOutMsg = m;
    }

    public int rcvCount;
    public DCCppReply rcvdRply;
    public DCCppMessage timeOutMsg;
    public DCCppMessage rcvdMsg;

}
