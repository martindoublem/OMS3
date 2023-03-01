package graphTests;

import java.util.ArrayDeque;
import java.util.Iterator;
import oms3.ds.graph.DiGraph;
import oms3.ds.graph.traversers.BreadthFirstPaths;
import oms3.ds.graph.traversers.DepthFirstPaths;
import oms3.ds.graph.traversers.GraphSearchAlgo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author sidereus
 */
public class GraphTest {

    DiGraph diGraph;
    DiGraph reversedDiGraph;

    // child, parent, child outDegree, child inDegree
    String[][] connections = new String[][]{
        {"1", null, "0", "1"},
        {"16", null, "0", "1"},
        {"2", "1", "1", "3"},
        {"3", "2", "1", "2"},
        {"4", "3", "1", "0"},
        {"5", "3", "2", "2"},
        {"7", "5", "1", "0"},
        {"6", "5", "1", "0"},
        {"8", "2", "1", "1"},
        {"9", "8", "1", "2"},
        {"5", "9", "2", "2"},
        {"10", "9", "2", "3"},
        {"11", "10", "1", "0"},
        {"12", "10", "1", "0"},
        {"13", "10", "1", "0"},
        {"14", "2", "1", "1"},
        {"15", "14", "2", "1"},
        {"10", "15", "2", "3"},
        {"15", "16", "2", "1"}
    };

    @Before
    public void graphPopulate() {

        diGraph = new DiGraph();

        for (String[] connection : connections) {
            diGraph.addConnection(connection[1], connection[0]);
            diGraph.addVertex(connection[0], new String());
            diGraph.addVertex(connection[1], new String());
        }

        reversedDiGraph = diGraph.reverse();
    }

    @Test
    public void checkOutDegree() {
        for (String[] connection : connections) {
            String vertex = connection[0];
            int expectOutDegree = Integer.parseInt(connection[2]);

            Assert.assertEquals((long) expectOutDegree,
                    (long) diGraph.outDegree(vertex));
        }
    }

    @Test
    public void checkInDegree() {
        for (String[] connection : connections) {
            String vertex = connection[0];
            int expectInDegree = Integer.parseInt(connection[3]);

            Assert.assertEquals((long) expectInDegree,
                    (long) diGraph.inDegree(vertex));
        }
    }

    @Test
    public void checkReversedGraphOutDegree() {
        for (String[] connection : connections) {
            String vertex = connection[0];
            int expectOutDegree = Integer.parseInt(connection[3]);

            Assert.assertEquals((long) expectOutDegree,
                    (long) reversedDiGraph.outDegree(vertex));
        }
    }

    @Test
    public void checkReversedGraphInDegree() {
        for (String[] connection : connections) {
            String vertex = connection[0];
            int expectInDegree = Integer.parseInt(connection[2]);

            Assert.assertEquals((long) expectInDegree,
                    (long) reversedDiGraph.inDegree(vertex));
        }
    }

    @Test
    public void breathFirstPath() {

        ArrayDeque<String> expectedDownPath = new ArrayDeque<>();
        expectedDownPath.push("1");
        expectedDownPath.push("2");
        expectedDownPath.push("3");
        expectedDownPath.push("5");

        System.out.println("BreathFirstPath Algorithm");
        System.out.println("Downstream");
        GraphSearchAlgo downBreathTest = new BreadthFirstPaths();
        downBreathTest.compute("downstream", "5", diGraph);
        Iterator<String> downPath = downBreathTest.pathTo("1");

        Assert.assertTrue(downBreathTest.hasPathTo("1"));
        Assert.assertTrue(!downBreathTest.hasPathTo("10"));
        Assert.assertArrayEquals(expectedDownPath.toArray(),
                iteratorToArrayDeque(downPath).toArray());

        while (downPath.hasNext()) {
            System.out.println(downPath.next());
        }

        ArrayDeque<String> expectedUpPath = new ArrayDeque<>();
        expectedUpPath.push("2");
        expectedUpPath.push("3");
        expectedUpPath.push("5");
        expectedUpPath.push("6");

        System.out.println("Upstream");
        GraphSearchAlgo upBreathTest = new BreadthFirstPaths();
        upBreathTest.compute("upstream", "2", diGraph);
        Iterator<String> upPath = upBreathTest.pathTo("6");

        Assert.assertTrue(upBreathTest.hasPathTo("6"));
        Assert.assertTrue(!upBreathTest.hasPathTo("1"));
        Assert.assertArrayEquals(expectedUpPath.toArray(),
                iteratorToArrayDeque(upPath).toArray());

        while (upPath.hasNext()) {
            System.out.println(upPath.next());
        }

    }

    @Test
    public void depthFirstPath() {

        ArrayDeque<String> expectedDownPath = new ArrayDeque<>();
        expectedDownPath.push("1");
        expectedDownPath.push("2");
        expectedDownPath.push("8");
        expectedDownPath.push("9");
        expectedDownPath.push("5");

        System.out.println("DepthFirstPath Algorithm");
        System.out.println("Downstream");
        GraphSearchAlgo downDepthTest = new DepthFirstPaths();
        downDepthTest.compute("downstream", "5", diGraph);
        Iterator<String> downPath = downDepthTest.pathTo("1");

        Assert.assertTrue(downDepthTest.hasPathTo("1"));
        Assert.assertTrue(!downDepthTest.hasPathTo("10"));
        Assert.assertArrayEquals(expectedDownPath.toArray(),
                iteratorToArrayDeque(downPath).toArray());

        while (downPath.hasNext()) {
            System.out.println(downPath.next());
        }

        ArrayDeque<String> expectedUpPath = new ArrayDeque<>();
        expectedUpPath.push("2");
        expectedUpPath.push("8");
        expectedUpPath.push("9");
        expectedUpPath.push("5");
        expectedUpPath.push("6");

        System.out.println("Upstream");
        GraphSearchAlgo upDepthTest = new DepthFirstPaths();
        upDepthTest.compute("upstream", "2", diGraph);
        Iterator<String> upPath = upDepthTest.pathTo("6");

        Assert.assertTrue(upDepthTest.hasPathTo("6"));
        Assert.assertTrue(!upDepthTest.hasPathTo("1"));
        Assert.assertArrayEquals(expectedUpPath.toArray(),
                iteratorToArrayDeque(upPath).toArray());

        while (upPath.hasNext()) {
            System.out.println(upPath.next());
        }

    }

    private ArrayDeque<String> iteratorToArrayDeque(Iterator<String> iterator) {
        ArrayDeque<String> tmpArray = new ArrayDeque<>();

        while (iterator.hasNext()) {
            tmpArray.addLast(iterator.next());
        }

        return tmpArray;
    }
}
