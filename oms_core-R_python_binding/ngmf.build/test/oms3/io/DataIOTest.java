/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oms3.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import oms3.Conversions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author od
 */
public class DataIOTest {

    File r;

    @Before
    public void init() throws FileNotFoundException {
        r = new File(this.getClass().getResource("test.csv").getFile());
    }

    private Reader[] open(String... f) throws FileNotFoundException {
        Reader[] r = new Reader[f.length];
        for (int i = 0; i < r.length; i++) {
            r[i] = new FileReader(this.getClass().getResource(f[i]).getFile());
        }
        return r;
    }

    @Test
    public void testTableInfo() throws Exception {
        CSTable t = DataIO.table(r, "Table");
        Assert.assertNotNull(t);

        Assert.assertEquals("Table", t.getName());
        Assert.assertEquals("temp", t.getColumnName(1));
        Assert.assertEquals("precip", t.getColumnName(2));

        Assert.assertEquals("today", t.getInfo().get("created"));
        Assert.assertEquals("this", t.getInfo().get("description"));
        Assert.assertEquals(2, t.getInfo().size());

        Assert.assertEquals("F", t.getColumnInfo(1).get("unit"));
        Assert.assertEquals(1, t.getColumnInfo(1).size());
        Assert.assertEquals("mm", t.getColumnInfo(2).get("unit"));
        Assert.assertEquals(1, t.getColumnInfo(2).size());
    }

    @Test
    public void testTableLayout() throws Exception {
        CSTable t = DataIO.table(r, "Table");
        Assert.assertNotNull(t);
        Assert.assertEquals(2, t.getColumnCount());
        int rows = 0;
        for (String[] row : t.rows()) {
            rows++;
//            System.out.println(Arrays.toString(row));
            Assert.assertEquals(3, row.length);
        }
        Assert.assertEquals(5, rows);
    }

    @Test
    public void testTableName() throws Exception {
        CSTable t = DataIO.table(r, "Table");
        Assert.assertNotNull(t);
        Assert.assertEquals("Table", t.getName());
    }

    @Test
    public void testFirstTable() throws Exception {
        CSTable t = DataIO.table(r, null);
        Assert.assertNotNull(t);
        Assert.assertEquals(t.getName(), "Table");
    }

    @Test
    public void testTableData() throws Exception {
        CSTable t = DataIO.table(r, "Table");
        Assert.assertNotNull(t);
        Iterator<String[]> rows = t.rows().iterator();
        Assert.assertNotNull(rows);
        Assert.assertTrue(rows.hasNext());
        Assert.assertArrayEquals(new String[]{"1", "2.4", "3.5"}, rows.next());
        Assert.assertTrue(rows.hasNext());
        Assert.assertArrayEquals(new String[]{"2", "2.4", "2.5"}, rows.next());
        Assert.assertTrue(rows.hasNext());
        Assert.assertArrayEquals(new String[]{"3", "4.7", "4.1"}, rows.next());
        Assert.assertTrue(rows.hasNext());
    }

    @Test
    public void testTableDataOffset() throws Exception {
        CSTable t = DataIO.table(r, "Table");
        Assert.assertNotNull(t);
        Iterator<String[]> rows = t.rows(2).iterator();
        Assert.assertNotNull(rows);
        Assert.assertTrue(rows.hasNext());
        Assert.assertArrayEquals(new String[]{"3", "4.7", "4.1"}, rows.next());
        Assert.assertTrue(rows.hasNext());
    }

    @Test
    public void testtwoTables() throws Exception {
        CSTable t = DataIO.table(r, "Table");
        Assert.assertNotNull(t);
        Iterator<String[]> rows = t.rows().iterator();
        Assert.assertNotNull(rows);
        Assert.assertTrue(rows.hasNext());
        Assert.assertArrayEquals(new String[]{"1", "2.4", "3.5"}, rows.next());
        Assert.assertTrue(rows.hasNext());
        Assert.assertArrayEquals(new String[]{"2", "2.4", "2.5"}, rows.next());
        Assert.assertTrue(rows.hasNext());
        Assert.assertArrayEquals(new String[]{"3", "4.7", "4.1"}, rows.next());
        Assert.assertTrue(rows.hasNext());
    }

    @Test
    public void testSkip() throws Exception {
        CSTable t = DataIO.table(r, "EFCarson");
        Assert.assertNotNull(t);

        Iterator<String[]> rows = t.rows().iterator();
        Assert.assertNotNull(rows);
        Assert.assertTrue(rows.hasNext());
        Assert.assertEquals("84.0", rows.next()[2]);
        Assert.assertTrue(rows.hasNext());
        ((TableIterator) rows).skip(5);
        Assert.assertEquals("78.0", rows.next()[2]);
        ((TableIterator) rows).skip(10);
        Assert.assertEquals("99.0", rows.next()[2]);
        ((TableIterator) rows).skip(1);
        Assert.assertEquals("96.0", rows.next()[2]);
        Assert.assertTrue(rows.hasNext());
    }
//

    @Test
    public void testTableCol() throws Exception {
        CSTable t = DataIO.table(r, "Table");
        Assert.assertNotNull(t);
        Double[] vals = DataIO.getColumnDoubleValues(t, "precip");
        Assert.assertEquals(5, vals.length);
        Assert.assertEquals(3.5, vals[0], 0);
        Assert.assertEquals(2.5, vals[1], 0);
        Assert.assertEquals(4.1, vals[2], 0);
        Assert.assertEquals(4.2, vals[3], 0);
        Assert.assertEquals(4.3, vals[4], 0);
    }

    @Test
    public void testCreateTable() {
        MemoryTable mt = new MemoryTable();
        mt.setName("MyTable");
        mt.getInfo().put("table_metadata", "here");
        mt.getInfo().put("table_metadata1", "here_too");
        mt.setColumns(new String[]{"c1", "c2", "c3", "c4"});
        mt.getColumnInfo(1).put("unit", "C");
        mt.getColumnInfo(2).put("unit", "F");
        mt.getColumnInfo(3).put("unit", "");
        mt.getColumnInfo(4).put("unit", "");

        mt.addRow(new Object[]{"1", "2", "3", "4"});
        mt.addRow(new Object[]{"1", "2", "3", "4"});
        mt.addRow(new Object[]{"1", "2", "3", "4"});
        mt.addRow("1", "2", "3", "4v");
//        
//        DataIO.print(mt, new PrintWriter(System.out));
    }

    @Test
    public void testAddTable() throws IOException {
        MemoryTable mt = new MemoryTable();
        mt.setName("MyTable");
        mt.getInfo().put("table_metadata", "here");
        mt.getInfo().put("created", "today");
        mt.setColumns(new String[]{"c1", "c2", "c3", "c4"});
        mt.getColumnInfo(1).put("unit", "C");
        mt.getColumnInfo(2).put("unit", "F");
        mt.getColumnInfo(3).put("unit", "");
        mt.getColumnInfo(4).put("unit", "");

        mt.addRow(new Object[]{"1", "2", "3", "4"});
        mt.addRow(new Object[]{"1", "2", "3", "4"});

        CSTable t = DataIO.extractColumns(mt, "c2", "c3");

        Assert.assertEquals("MyTable", t.getName());
        Assert.assertEquals("c2", t.getColumnName(1));
        Assert.assertEquals("c3", t.getColumnName(2));

        Assert.assertEquals("today", t.getInfo().get("created"));

        Iterator<String[]> rows = t.rows().iterator();
        Assert.assertNotNull(rows);
        Assert.assertTrue(rows.hasNext());
        Assert.assertArrayEquals(new String[]{"1", "2", "3"}, rows.next());
        Assert.assertTrue(rows.hasNext());
        Assert.assertArrayEquals(new String[]{"2", "2", "3"}, rows.next());
        Assert.assertFalse(rows.hasNext());
    }

    @Test
    public void testTableMerge() throws IOException {
        CSTable t = DataIO.table(r, "Table");
        CSTable t1 = DataIO.table(r, "Other");

        CSTable res = DataIO.merge(t, t1, "new Table");
//         DataIO.print(res, new PrintWriter(System.out));
        Assert.assertEquals("new Table", res.getName());
        Assert.assertEquals(4, res.getColumnCount());
        Assert.assertEquals("temp", res.getColumnName(1));
        Assert.assertEquals("precip", res.getColumnName(2));
        Assert.assertEquals("solrad", res.getColumnName(3));
        Assert.assertEquals("tmin", res.getColumnName(4));
        Assert.assertNotNull(res.getInfo());

        Assert.assertEquals("this", res.getInfo().get("description"));
        Assert.assertEquals(4, res.getInfo().size());

        Assert.assertEquals("F", res.getColumnInfo(1).get("unit"));
        Assert.assertEquals(1, res.getColumnInfo(1).size());
        Assert.assertEquals("mm", res.getColumnInfo(2).get("unit"));
        Assert.assertEquals(1, res.getColumnInfo(2).size());
        Assert.assertEquals("deg", res.getColumnInfo(3).get("unit"));
        Assert.assertEquals(1, res.getColumnInfo(3).size());
        Assert.assertEquals("F", res.getColumnInfo(4).get("unit"));
        Assert.assertEquals(1, res.getColumnInfo(4).size());

        Iterator<String[]> rows = res.rows().iterator();
        Assert.assertNotNull(rows);
        Assert.assertTrue(rows.hasNext());
        String[] r = rows.next();
        Assert.assertEquals(5, r.length);
        Assert.assertEquals("1", r[0]);
        Assert.assertEquals("2.4", r[1]);
        Assert.assertEquals("3.5", r[2]);
        Assert.assertEquals("1.4", r[3]);
        Assert.assertEquals("0.5", r[4]);
    }

    @Test
    public void testCSVNamesplit() {
        String[] s = DataIO.parseCsvFilename("c:/test/here/abc.csv/table/col");
        Assert.assertEquals(3, s.length);
        Assert.assertEquals("c:/test/here/abc.csv", s[0]);
        Assert.assertEquals("table", s[1]);
        Assert.assertEquals("col", s[2]);

        s = DataIO.parseCsvFilename("c:/test/here/abc.csv/table");
        Assert.assertEquals(2, s.length);
        Assert.assertEquals("c:/test/here/abc.csv", s[0]);
        Assert.assertEquals("table", s[1]);

        s = DataIO.parseCsvFilename("c:/test/here/abc.csv");
        Assert.assertEquals(1, s.length);
        Assert.assertEquals("c:/test/here/abc.csv", s[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWroneProp() throws Exception {
        CSProperties p = DataIO.properties(r, "notThere");
    }

    @Test
    public void testProp() throws Exception {
        CSProperties p = DataIO.properties(r, "hello");

        Assert.assertEquals("olaf", p.getInfo().get("by"));
        Assert.assertEquals("1.234", p.get("temp"));
        Assert.assertEquals("20", p.getInfo("temp").get("dim"));

        Assert.assertEquals(null, p.get("empty"));
        Assert.assertTrue(p.containsKey("empty"));
        Assert.assertEquals("1", p.getInfo("empty").get("index"));
        Assert.assertTrue(p.getInfo("empty").containsKey("public"));
        Assert.assertTrue(p.getInfo("empty").containsKey("required"));

        Assert.assertEquals(3, p.keySet().size());
        Assert.assertEquals(3, p.values().size());
    }

    @Test
    public void testPropSet2() throws Exception {
        CSProperties p = DataIO.properties(r, "set1");
        Assert.assertEquals(11, p.keySet().size());
        Assert.assertEquals(11, p.values().size());
    }

    @Test
    public void testPropSetMerge() throws Exception {
        Reader[] reader = open("proptest1.csp", "proptest2.csp");
        CSProperties p = DataIO.properties(reader, "parameter");

        Assert.assertEquals(5, p.keySet().size());
        Assert.assertEquals(5, p.values().size());
//        DataIO.print(p, new PrintWriter(System.out));
    }

    @Test
    public void testtoProp() throws IOException {
        CSTable t = DataIO.table(r, "Table");

        // convert to properties
        CSProperties p = DataIO.toProperties(t);
//        DataIO.print(p, new PrintWriter(System.out));

        Object precip = p.get("Table$precip");
        Assert.assertNotNull(precip);
        Assert.assertTrue(p.getInfo("Table$precip").containsKey("unit"));
        Assert.assertTrue(p.getInfo("Table$precip").containsKey("len"));
        Assert.assertTrue(p.getInfo("Table$precip").containsKey("bound"));

        Object temp = p.get("Table$temp");
        Assert.assertNotNull(temp);
        Assert.assertTrue(p.getInfo("Table$temp").containsKey("unit"));
        Assert.assertTrue(p.getInfo("Table$temp").containsKey("len"));
        Assert.assertTrue(p.getInfo("Table$temp").containsKey("bound"));

        double[] te = Conversions.convert(temp, double[].class);
        Assert.assertNotNull(te);
        Assert.assertEquals(5, te.length);
        Assert.assertArrayEquals(new double[]{2.4, 2.4, 4.7, 4.8, 4.9}, te, 0.0d);

        double[] pr = Conversions.convert(precip, double[].class);
        Assert.assertNotNull(pr);
        Assert.assertEquals(5, pr.length);
        Assert.assertArrayEquals(new double[]{3.5, 2.5, 4.1, 4.2, 4.3}, pr, 0.0d);
    }

    @Test
    public void testtoPropToTable() throws IOException {
        CSTable t = DataIO.table(r, "Table");
        Assert.assertNotNull(t);

//        DataIO.print(t, new PrintWriter(System.out));
        // convert to properties
        CSProperties p = DataIO.toProperties(t);
//        DataIO.print(p, new PrintWriter(System.out));

        t = DataIO.toTable(p, "Table");
//        DataIO.print(t, new PrintWriter(System.out));
        Assert.assertNotNull(t);
        Iterator<String[]> rows = t.rows().iterator();
        Assert.assertNotNull(rows);
        Assert.assertTrue(rows.hasNext());
        String[] row = rows.next();
        Assert.assertEquals(3, row.length);
        Assert.assertArrayEquals(new String[]{"1", "2.4", "3.5"}, row);
        Assert.assertTrue(rows.hasNext());
        row = rows.next();
        Assert.assertEquals(3, row.length);
        Assert.assertArrayEquals(new String[]{"2", "2.4", "2.5"}, row);
        Assert.assertTrue(rows.hasNext());
        row = rows.next();
        Assert.assertEquals(3, row.length);
        Assert.assertArrayEquals(new String[]{"3", "4.7", "4.1"}, row);
        Assert.assertTrue(rows.hasNext());
    }
}
