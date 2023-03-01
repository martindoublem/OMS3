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
import oms3.annotations.InNode;
import oms3.annotations.Initialize;
import oms3.annotations.Out;
import oms3.annotations.OutNode;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author sidereus
 */
public class ThreeVerticesSum {

    /**
     * First component Compound 3
     */
    public static class Sum {
        
        @InNode public Integer inVal1;
        @InNode public Integer inVal2;
        @Out public Integer outVal;
        
        @Execute
        public void execute() {
            outVal = inVal1 + inVal2;
        }

    }

    /**
     * Second component Compound 3
     */
    public static class Multiplication1 {
        
        @In@Out public Integer value;
        
        @Execute
        public void execute() {
            value = value * 2;
        }

    }
    
    /**
     * Third component Compound 3
     */
    public static class Addition1 {
        
        @In@Out public Integer value;
        
        @Execute
        public void execute() {
            value += 3;
        }

    }
    
    /**
     * First component Compound 2
     */
    public static class Multiplication2 {
        
        @In public Integer invalue;
        @Out public Integer value;
        
        @Execute
        public void execute() {
            value = invalue * 2;
        }

    }
    
    /**
     * Second component Compound 2
     */
    public static class Addition2 {
        
        @In@OutNode public Integer value;
        
        @Execute
        public void execute() {
            value += 3;
        }
        
    }
    
    /**
     * First component Compound 2
     */
    public static class Multiplication3 {
        
        @In public Integer invalue;
        @Out public Integer value;
        
        @Execute
        public void execute() {
            value = invalue * 2;
        }

    }
    
    /**
     * Second component Compound 2
     */
    public static class Addition3 {
        
        @In@OutNode public Integer value;
        
        @Execute
        public void execute() {
            value += 3;
        }
        
    }
    
    public static class Node1 extends Compound {
        
        @Out public Integer outValue;
        
        Sum sum = new Sum();
        Multiplication1 m1 = new Multiplication1();
        Addition1 a1 = new Addition1();
        
        @Initialize
        public void init() {
            graph_in2in("2", "value", sum, "inVal1");
            graph_in2in("3", "value", sum, "inVal2");
            out2in(sum, "outVal", m1, "value");
            out2in(m1, "value", a1, "value");
            out2out("outValue", a1, "value");
        }

    }
    
    public static class Node2 extends Compound {
        
        @In public Integer inValue;

        Multiplication2 m2 = new Multiplication2();
        Addition2 a2 = new Addition2();
        
        @Initialize
        public void init() {
            in2in("inValue", m2, "invalue");
            out2in(m2, "value", a2, "value");
            graph_out2out("2", "value", a2);
        }

    }
    
    public static class Node3 extends Compound {
        
        @In public Integer inValue;

        Multiplication3 m3 = new Multiplication3();
        Addition3 a3 = new Addition3();
        
        @Initialize
        public void init() {
            in2in("inValue", m3, "invalue");
            out2in(m3, "value", a3, "value");
            graph_out2out("3", "value", a3);
        }

    }
    
//    @Test
//    public void threeVerticesSum() {
        public static void main(String[] args) {
        
        Node3 node3 = new Node3();
        node3.inValue = 3;
        
        ComponentAccess.callAnnotated(node3, Initialize.class, true);
        ComponentAccess.callAnnotated(node3, Execute.class, false);
        
        Node2 node2 = new Node2();
        node2.inValue = 2;
        
        ComponentAccess.callAnnotated(node2, Initialize.class, true);
        ComponentAccess.callAnnotated(node2, Execute.class, false);
        
        Node1 node1 = new Node1();
        
        ComponentAccess.callAnnotated(node1, Initialize.class, true);
        ComponentAccess.callAnnotated(node1, Execute.class, false);
        
        assertEquals((Integer) 35, node1.outValue);

    }
}
