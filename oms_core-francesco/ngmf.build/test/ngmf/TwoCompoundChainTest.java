/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ngmf;

import oms3.ComponentAccess;
import oms3.Compound;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Initialize;
import oms3.annotations.Out;
import oms3.annotations.InNode;
import oms3.annotations.OutNode;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author sidereus
 */
public class TwoCompoundChainTest {
    
    public static class Cmd1 {
        
        @InNode public String inGraphMessage;
        @Out public String outGraphMessage;
        
        @In public String message;
        
        @Execute
        public void execute() {
            outGraphMessage = inGraphMessage + " - " + message;
        }
        
    }
    
    public static class Cmd2 {
        
        @OutNode public String outGraphMessage;
        
        @In public String message;
        
        @Execute
        public void execute() {
            outGraphMessage = message;
        }

    }

    public static class Node1 extends Compound {
        
        @In public String inparam;
        @Out public String outParam;
        
        Cmd1 op1 = new Cmd1();
        
        @Initialize
        public void init() {
            in2in("inparam", op1, "message");
            graph_in2in("2", "outGraphMessage", op1, "inGraphMessage");
            out2out("outParam", op1, "outGraphMessage");
        }
        
    }
    
    public static class Node2 extends Compound {
    
        @In public String inparam;
        
        Cmd2 op2 = new Cmd2();
        
        @Initialize
        public void init() {
            in2in("inparam", op2, "message");
            graph_out2out("2", "outGraphMessage", op2);
        }
    }
    
    @Test
    public void twoCompoundChainTest() {
        
        Node2 node2 = new Node2();
        node2.inparam = "Node 2";
        
        ComponentAccess.callAnnotated(node2, Initialize.class, true);
        ComponentAccess.callAnnotated(node2, Execute.class, false);
        
        Node1 node1 = new Node1();
        node1.inparam = "Node 1";
        
        ComponentAccess.callAnnotated(node1, Initialize.class, true);
        ComponentAccess.callAnnotated(node1, Execute.class, false);
        
        assertEquals("Node 2 - Node 1", node1.outParam);
    }
}