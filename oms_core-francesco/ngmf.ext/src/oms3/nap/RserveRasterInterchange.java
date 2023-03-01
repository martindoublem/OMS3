/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oms3.nap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Date;

/**
 *
 * @author sidereus
 */
public class RserveRasterInterchange {

    File genFile;

    public RserveRasterInterchange(File genDir) {
        this.genFile = new File(genDir.toString() + "/edu/colostate/engr/alm/");
        generateCoverageStack();
    }

    private void generateCoverageStack() {

        File file = null;

        try {
            file = new File(genFile.toString() + "/CoverageStack.java");
            file.getParentFile().mkdirs();

            PrintStream w = new PrintStream(file);

            w.println("// Generated at " + new Date());
            w.println("package edu.colostate.engr.alm;");
            w.println();
            w.println("import org.geotools.coverage.grid.GridCoverage2D;");
            w.println("import org.geotools.resources.coverage.CoverageUtilities;");
            w.println("import org.geotools.geometry.Envelope2D;");
            w.println("import org.opengis.referencing.crs.CoordinateReferenceSystem;");
            w.println();
            w.println("import java.awt.image.RenderedImage;");
            w.println("import java.util.Iterator;");
            w.println("import java.util.List;");
            w.println();
            w.println("public class CoverageStack implements Iterable<GridCoverage2D> {");
            w.println();
            w.println("    private List<GridCoverage2D> coverages;");
            w.println("    private Double nodata = Double.NaN;");
            w.println("    private Envelope2D envelope;");
            w.println("    private CoordinateReferenceSystem crs;");
            w.println("    private RenderedImage image;");
            w.println();
            w.println("    public CoverageStack(List<GridCoverage2D> coverages) {");
            w.println("        this.coverages = coverages;");
            w.println("        GridCoverage2D tmpGrid = coverages.get(0);");
            w.println("        this.nodata = CoverageUtilities.getNoDataProperty(tmpGrid).getAsSingleValue();");
            w.println("        this.envelope = tmpGrid.getEnvelope2D();");
            w.println("        this.crs = tmpGrid.getCoordinateReferenceSystem2D();");
            w.println("        this.image = tmpGrid.getRenderedImage();");
            w.println("    }");
            w.println();
            w.println("    public void add(GridCoverage2D coverage) {");
            w.println("        coverages.add(coverage);");
            w.println("    }");
            w.println();
            w.println("    public Double getNoData() {");
            w.println("        return nodata;");
            w.println("    }");
            w.println();
            w.println("    public int getLayersNumber() {");
            w.println("        return coverages.size();");
            w.println("    }");
            w.println();
            w.println("    public Envelope2D getEnvelope() {");
            w.println("        return envelope;");
            w.println("    }");
            w.println();
            w.println("    public CoordinateReferenceSystem getCrs() {");
            w.println("        return crs;");
            w.println("    }");
            w.println();
            w.println("    public RenderedImage getImage() {");
            w.println("        return image;");
            w.println("    }");
            w.println();
            w.println("    public List<GridCoverage2D> getCoverages() {");
            w.println("        return coverages;");
            w.println("    }");
            w.println();
            w.println("    @Override");
            w.println("    public Iterator<GridCoverage2D> iterator() {");
            w.println("        return coverages.iterator();");
            w.println("    }");
            w.println();
            w.println("}");

        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void generateJava2RFiles() {
        j2rAbstractClass();
        j2rFactory();
        j2rRaster();
        j2rRasterStack();
        j2rRasterList();
    }

    public void generateR2JavaFiles() {
        r2jRasterTransfer();
    }

    private void j2rAbstractClass() {

        File file = null;

        try {
            file = new File(genFile.toString() + "/J2RrasterTransfer.java");
            file.getParentFile().mkdirs();

            PrintStream w = new PrintStream(file);

            w.println("// Generated at " + new Date());
            w.println("package edu.colostate.engr.alm;");
            w.println("import org.geotools.geometry.Envelope2D;");
            w.println("import org.geotools.referencing.CRS;");
            w.println("import org.opengis.referencing.FactoryException;");
            w.println("import org.opengis.referencing.crs.CoordinateReferenceSystem;");
            w.println("import org.rosuda.REngine.REXP;");
            w.println("import org.rosuda.REngine.REXPDouble;");
            w.println("import org.rosuda.REngine.REXPInteger;");
            w.println("import org.rosuda.REngine.Rserve.RConnection;");
            w.println();
            w.println("import java.awt.image.Raster;");
            w.println("import java.awt.image.RenderedImage;");
            w.println("import java.util.LinkedList;");
            w.println("import java.util.List;");
            w.println();
            w.println("public abstract class J2RrasterTransfer {");
            w.println();
            w.println("    private double xLowerCorner;");
            w.println("    private double yLowerCorner;");
            w.println("    private double xUpperCorner;");
            w.println("    private double yUpperCorner;");
            w.println("    private Double nodata = Double.NaN;");
            w.println("    private String projection;");
            w.println("    private int ncols;");
            w.println("    private int nrows;");
            w.println("    private LinkedList<String> names = new LinkedList<>();");
            w.println("    private LinkedList<double[]> data = new LinkedList<>();");
            w.println();
            w.println("    protected int layerNumbers;");
            w.println();
            w.println("    protected void setNoDataValue(Double nodata) {");
            w.println("        this.nodata = nodata;");
            w.println("    }");
            w.println();
            w.println("    protected void setCornersCoordinates(Envelope2D envelope) {");
            w.println("        double[] upperCorner = envelope.getUpperCorner().getCoordinate();");
            w.println("        double[] lowerCorner = envelope.getLowerCorner().getCoordinate();");
            w.println("        xLowerCorner = lowerCorner[0];");
            w.println("        yLowerCorner = lowerCorner[1];");
            w.println("        xUpperCorner = upperCorner[0];");
            w.println("        yUpperCorner = upperCorner[1];");
            w.println("    }");
            w.println();
            w.println("    protected void setProjection(CoordinateReferenceSystem crs) {");
            w.println("        String epsg = crs.getName().getCodeSpace();");
            w.println("        Integer val = null;");
            w.println("        try {");
            w.println("            val = CRS.lookupEpsgCode(crs, true);");
            w.println("        } catch (FactoryException e) {");
            w.println("            e.printStackTrace();");
            w.println("        }");
            w.println("        projection = \"+init=\" + epsg.toLowerCase() + \":\" + val;");
            w.println("    }");
            w.println();
            w.println("    protected void setRowsColumnsDim(RenderedImage image) {");
            w.println("        ncols = image.getWidth();");
            w.println("        nrows = image.getHeight();");
            w.println("    }");
            w.println();
            w.println("    protected void addDataLayer(Raster raster) {");
            w.println("        int xmin = raster.getMinX();");
            w.println("        int ymin = raster.getMinY();");
            w.println("        double[] dat = raster.getSamples(xmin, ymin, ncols, nrows, 0, new double[ncols*nrows]);");
            w.println("        data.add(dat);");
            w.println("    }");
            w.println();
            w.println("    protected void setName(String name) {");
            w.println("        names.add(name);");
            w.println("    }");
            w.println();
            w.println("    private REXPDouble getXlowerCorner() {");
            w.println("        return new REXPDouble(xLowerCorner);");
            w.println("    }");
            w.println();
            w.println("    private REXPDouble getYlowerCorner() {");
            w.println("        return new REXPDouble(yLowerCorner);");
            w.println("    };");
            w.println();
            w.println("    private REXPDouble getXupperCorner() {");
            w.println("        return new REXPDouble(xUpperCorner);");
            w.println("    }");
            w.println();
            w.println("    private REXPDouble getYupperCorner() {");
            w.println("        return new REXPDouble(yUpperCorner);");
            w.println("    }");
            w.println();
            w.println("    private String getProjection() {");
            w.println("        return projection;");
            w.println("    }");
            w.println();
            w.println("    private REXPInteger getNrows() {");
            w.println("        return new REXPInteger(nrows);");
            w.println("    }");
            w.println();
            w.println("    private REXPInteger getNcols() {");
            w.println("        return new REXPInteger(ncols);");
            w.println("    }");
            w.println();
            w.println("    private double[] getDataPerLayer(int i) {");
            w.println("        return data.remove();");
            w.println("    }");
            w.println();
            w.println("    private String getNamePerLayer() {");
            w.println("        return names.remove();");
            w.println("    }");
            w.println();
            w.println("    private REXPDouble getNoData() {");
            w.println("        return new REXPDouble(nodata);");
            w.println("    }");
            w.println();
            w.println("    protected void rasterTransfer(RConnection c, int layer, String name) {");
            w.println("        try {");
            w.println("            c.assign(\"tmpproj\", getProjection());");
            w.println("            c.assign(\"ncols\", getNcols());");
            w.println("            c.assign(\"nrows\", getNrows());");
            w.println("            c.assign(\"xmin\", getXlowerCorner());");
            w.println("            c.assign(\"ymin\", getYlowerCorner());");
            w.println("            c.assign(\"xmax\", getXupperCorner());");
            w.println("            c.assign(\"ymax\", getYupperCorner());");
            w.println("            c.assign(\"val\", getDataPerLayer(layer));");
            w.println("            c.assign(\"tmpName\", getNamePerLayer());");
            w.println("            c.assign(\"nodataVal\", getNoData());");
            w.println("            c.voidEval(name + \" <- raster(nrows=nrows, ncols=ncols,crs=tmpproj,xmn=xmin,xmx=xmax,ymn=ymin,ymx=ymax,vals=val)\");");
            w.println("            c.voidEval(name + \" <- calc(\" + name + \", fun=function(x){ x[x == nodataVal] <- NA; return(x) })\");");
            w.println("            c.voidEval(\"names(\" + name + \") <- tmpName\");");
            w.println("        } catch (org.rosuda.REngine.REngineException e) {");
            w.println("            throw new RuntimeException(e);");
            w.println("        }");
            w.println("    }");
            w.println();
            w.println("    abstract public void transfer(RConnection c, String varName);");
            w.println();
            w.println("}");

        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void j2rFactory() {

        File file = null;

        try {
            file = new File(genFile.toString() + "/J2Rfactory.java");
            file.getParentFile().mkdirs();

            PrintStream w = new PrintStream(file);

            w.println("// Generated at " + new Date());
            w.println("package edu.colostate.engr.alm;");
            w.println();
            w.println("import org.geotools.coverage.grid.GridCoverage2D;");
            w.println();
            w.println("import java.util.List;");
            w.println();
            w.println("public class J2Rfactory {");
            w.println();
            w.println("    public static J2RrasterTransfer createTranslator(Object object) {");
            w.println("        if (object instanceof CoverageStack)");
            w.println("            return new J2RrasterStack((CoverageStack) object);");
            w.println("        else if (object instanceof GridCoverage2D)");
            w.println("            return new J2Rraster((GridCoverage2D) object);");
            w.println("        else if (object instanceof List<?>)");
            w.println("            return new J2RrasterList((List<GridCoverage2D>) object);");
            w.println("        else");
            w.println("            throw new UnsupportedOperationException(\"Object must be CoverageStack or GridCoverage2D\");");
            w.println("    }");
            w.println();
            w.println("}");

        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void j2rRaster() {

        File file = null;

        try {
            file = new File(genFile.toString() + "/J2Rraster.java");
            file.getParentFile().mkdirs();

            PrintStream w = new PrintStream(file);

            w.println("// Generated at " + new Date());
            w.println("package edu.colostate.engr.alm;");
            w.println();
            w.println("import org.geotools.coverage.grid.GridCoverage2D;");
            w.println("import org.geotools.resources.coverage.CoverageUtilities;");
            w.println("import org.rosuda.REngine.Rserve.RConnection;");
            w.println();
            w.println("public class J2Rraster extends J2RrasterTransfer {");
            w.println();
            w.println("    public J2Rraster(GridCoverage2D gc) {");
            w.println("        layerNumbers = 1;");
            w.println("        setNoDataValue(CoverageUtilities.getNoDataProperty(gc).getAsSingleValue());");
            w.println("        setCornersCoordinates(gc.getEnvelope2D());");
            w.println("        setProjection(gc.getCoordinateReferenceSystem2D());");
            w.println("        setRowsColumnsDim(gc.getRenderedImage());");
            w.println("        addDataLayer(gc.getRenderedImage().getData());");
            w.println("        setName(gc.getName().toString());");
            w.println("    }");
            w.println();
            w.println("    @Override");
            w.println("    public void transfer(RConnection c, String varName) {");
            w.println("        rasterTransfer(c, 0, varName);");
            w.println("    }");
            w.println();
            w.println("}");

        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void j2rRasterStack() {

        File file = null;

        try {
            file = new File(genFile.toString() + "/J2RrasterStack.java");
            file.getParentFile().mkdirs();

            PrintStream w = new PrintStream(file);

            w.println("// Generated at " + new Date());
            w.println("package edu.colostate.engr.alm;");
            w.println();
            w.println("import org.geotools.coverage.grid.GridCoverage2D;");
            w.println("import org.rosuda.REngine.REXPGenericVector;");
            w.println("import org.rosuda.REngine.RList;");
            w.println("import org.rosuda.REngine.Rserve.RConnection;");
            w.println("import org.rosuda.REngine.Rserve.RserveException;");
            w.println();
            w.println("public class J2RrasterStack extends J2RrasterTransfer {");
            w.println();
            w.println("    public J2RrasterStack(CoverageStack cs) {");
            w.println("        layerNumbers = cs.getLayersNumber();");
            w.println();
            w.println("        setNoDataValue(cs.getNoData());");
            w.println("        setCornersCoordinates(cs.getEnvelope());");
            w.println("        setProjection(cs.getCrs());");
            w.println("        setRowsColumnsDim(cs.getImage());");
            w.println("        for (GridCoverage2D gc : cs.getCoverages()) {");
            w.println("            addDataLayer(gc.getRenderedImage().getData());");
            w.println("            setName(gc.getName().toString());");
            w.println("        }");
            w.println("    }");
            w.println();
            w.println("    @Override");
            w.println("    public void transfer(RConnection c, String varName) {");
            w.println("        try {");
            w.println("            c.voidEval(varName + \" <- stack()\");");
            w.println("            for (int layer=0; layer < layerNumbers; layer++) {");
            w.println("                String tmpName = \"name_\" + layer;");
            w.println("                rasterTransfer(c, layer, tmpName);");
            w.println("                c.voidEval(varName + \" <- stack(\" + varName + \", \" + tmpName + \")\");");
            w.println("            }");
            w.println();
            w.println("        } catch (RserveException e) {");
            w.println("            throw new RuntimeException(e);");
            w.println("        }");
            w.println("    }");
            w.println();
            w.println("}");

        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void j2rRasterList() {

        File file = null;

        try {
            file = new File(genFile.toString() + "/J2RrasterList.java");
            file.getParentFile().mkdirs();

            PrintStream w = new PrintStream(file);

            w.println("// Generated at " + new Date());
            w.println("package edu.colostate.engr.alm;");
            w.println();
            w.println("import org.geotools.coverage.grid.GridCoverage2D;");
            w.println("import org.geotools.resources.coverage.CoverageUtilities;");
            w.println("import org.rosuda.REngine.Rserve.RConnection;");
            w.println("import org.rosuda.REngine.Rserve.RserveException;");
            w.println();
            w.println("import java.util.List;");
            w.println();
            w.println("public class J2RrasterList extends J2RrasterTransfer {");
            w.println();
            w.println("    public J2RrasterList(List<GridCoverage2D> gclist) {");
            w.println("        layerNumbers = gclist.size();");
            w.println("        GridCoverage2D tmpMap = gclist.get(0);");
            w.println("        setNoDataValue(CoverageUtilities.getNoDataProperty(tmpMap).getAsSingleValue());");
            w.println("        setCornersCoordinates(tmpMap.getEnvelope2D());");
            w.println("        setProjection(tmpMap.getCoordinateReferenceSystem2D());");
            w.println("        setRowsColumnsDim(tmpMap.getRenderedImage());");
            w.println("        for (GridCoverage2D gc : gclist) {");
            w.println("            addDataLayer(gc.getRenderedImage().getData());");
            w.println("            setName(gc.getName().toString());");
            w.println("        }");
            w.println("    }");
            w.println();
            w.println("    @Override");
            w.println("    public void transfer(RConnection c, String varName) {");
            w.println("        try {");
            w.println("            System.out.println(\"Transferring List of Raster\");");
            w.println("            c.voidEval(varName + \" <- c()\");");
            w.println("            for (int layer=0; layer < layerNumbers; layer++) {");
            w.println("                String tmpName = \"name_\" + layer;");
            w.println("                rasterTransfer(c, layer, tmpName);");
            w.println("                c.voidEval(varName + \" <- c(\" + varName + \", \" + tmpName + \")\");");
            w.println("            }");
            w.println("            System.out.println(\"Done!\");");
            w.println();
            w.println("        } catch (RserveException e) {");
            w.println("            throw new RuntimeException(e);");
            w.println("        }");
            w.println("    }");
            w.println();
            w.println("}");

        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void r2jRasterTransfer() {

        File file = null;

        try {
            file = new File(genFile.toString() + "/R2JrasterTransfer.java");
            file.getParentFile().mkdirs();

            PrintStream w = new PrintStream(file);

            w.println("// Generated at " + new Date());
            w.println("package edu.colostate.engr.alm;");
            w.println();
            w.println("import it.geosolutions.jaiext.JAIExt;");
            w.println("import org.geotools.coverage.grid.GridCoverage2D;");
            w.println("import org.geotools.coverage.grid.GridCoverageFactory;");
            w.println("import org.geotools.coverage.processing.CoverageProcessor;");
            w.println("import org.geotools.data.ows.CRSEnvelope;");
            w.println("import it.geosolutions.jaiext.range.Range;");
            w.println("import it.geosolutions.jaiext.range.RangeFactory;");
            w.println("import org.geotools.coverage.grid.GridCoverageFactory;");
            w.println("import org.geotools.image.ImageWorker;");
            w.println("import org.rosuda.REngine.REXP;");
            w.println("import org.rosuda.REngine.REXPDouble;");
            w.println("import org.rosuda.REngine.REXPMismatchException;");
            w.println("import org.rosuda.REngine.RList;");
            w.println();
            w.println("import java.util.LinkedList;");
            w.println("import java.util.List;");
            w.println();
            w.println("public class R2JrasterTransfer {");
            w.println();
            w.println("    static {");
            w.println("        JAIExt.initJAIEXT();");
            w.println("    }");
            w.println();
            w.println("    private GridCoverage2D outgc;");
            w.println("    private CoverageStack outcs;");
            w.println();
            w.println("    public R2JrasterTransfer(REXP raster) {");
            w.println("        CRSEnvelope envelope = getEnvelope(raster);");
            w.println("        retrieveData(raster, envelope);");
            w.println("    }");
            w.println();
            w.println("    private CRSEnvelope getEnvelope(REXP raster) {");
            w.println("        String epsg = getProjection(raster);");
            w.println();
            w.println("        double xLowerCorner;");
            w.println("        double yLowerCorner;");
            w.println("        double xUpperCorner;");
            w.println("        double yUpperCorner;");
            w.println();
            w.println("        REXP extent = raster.getAttribute(\"extent\");");
            w.println("        try {");
            w.println("            xLowerCorner = extent.getAttribute(\"xmin\").asDouble();");
            w.println("            yLowerCorner = extent.getAttribute(\"ymin\").asDouble();");
            w.println("            xUpperCorner = extent.getAttribute(\"xmax\").asDouble();");
            w.println("            yUpperCorner = extent.getAttribute(\"ymax\").asDouble();");
            w.println();
            w.println("            return new CRSEnvelope(epsg, xLowerCorner, yLowerCorner, xUpperCorner, yUpperCorner);");
            w.println();
            w.println("        } catch (REXPMismatchException e) {");
            w.println("            throw new RuntimeException(e.getCause());");
            w.println("        }");
            w.println("    }");
            w.println();
            w.println("    private String getProjection(REXP raster) {");
            w.println("        String projection = null;");
            w.println("        try {");
            w.println("            projection = raster.getAttribute(\"crs\").getAttribute(\"projargs\").asString();");
            w.println("        } catch (REXPMismatchException e) {");
            w.println("            e.printStackTrace();");
            w.println("        }");
            w.println();
            w.println("        String[] strProjection= projection.split(\"\\\\s+\");");
            w.println("        String epsg = null;");
            w.println("        for (int i=0; i<strProjection.length; i++) {");
            w.println("            if (strProjection[i].contains(\"epsg\")) {");
            w.println("                return epsg = strProjection[i].substring(strProjection[i].lastIndexOf(\"=\") + 1);");
            w.println("            }");
            w.println("        }");
            w.println("        if (epsg == null) {");
            w.println("            String msg = \"Provided projection: \" + projection;");
            w.println("            msg += \"\\n no epsg in crs: not supported yet\";");
            w.println("            throw new NullPointerException(msg);");
            w.println("        }");
            w.println();
            w.println("        return epsg;");
            w.println();
            w.println("    }");
            w.println();
            w.println("    private void retrieveData(REXP raster, CRSEnvelope envelope) {");
            w.println("        try {");
            w.println("            int ncols = raster.getAttribute(\"ncols\").asInteger();");
            w.println("            int nrows = raster.getAttribute(\"nrows\").asInteger();");
            w.println("            if (raster.hasAttribute(\"layers\")) {");
            w.println("                REXP layers = raster.getAttribute(\"layers\");");
            w.println("                int layersNumber = layers.asList().size();");
            w.println("                RList tmpLayer = layers.asList();");
            w.println("                List<GridCoverage2D> list = new LinkedList<GridCoverage2D>();");
            w.println("                for (int i=0; i<layersNumber; i++) {");
            w.println("                    String name = tmpLayer.at(i).getAttribute(\"data\").getAttribute(\"names\").asString();");
            w.println("                    double[] tmpdata = tmpLayer.at(i).getAttribute(\"data\").getAttribute(\"values\").asDoubles();");
            w.println("                    GridCoverage2D tmpgrid = buildRaster(tmpdata, ncols, nrows, envelope, name);");
            w.println("                    list.add(tmpgrid);");
            w.println("                }");
            w.println("                outcs = new CoverageStack(list);");
            w.println("            } else {");
            w.println("                String name = raster.getAttribute(\"data\").getAttribute(\"names\").asString();");
            w.println("                double[] data = raster.getAttribute(\"data\").getAttribute(\"values\").asDoubles();");
            w.println("                outgc = buildRaster(data, ncols, nrows, envelope, name);");
            w.println("            }");
            w.println("        } catch (REXPMismatchException e) {");
            w.println("           throw new RuntimeException(e.getCause());");
            w.println("        }");
            w.println("    }");
            w.println();
            w.println("    private GridCoverage2D setNoData(GridCoverage2D gc) {");
            w.println("        Range nodata = RangeFactory.create(-9999.0, -9999.0);");
            w.println("        ImageWorker w = new ImageWorker(gc.getRenderedImage());");
            w.println("        w.setNoData(nodata);");
            w.println("        GridCoverageFactory factory = new GridCoverageFactory();");
            w.println("        return factory.create(gc.getName().toString(), w.getRenderedImage(), gc.getEnvelope2D());");
            w.println("    }");
            w.println();
            w.println("    private float[][] vector2matrix(double[] layerValues, int ncols, int nrows) {");
            w.println("        int cont = 0;");
            w.println("        float[][] tmpMatrix = new float[nrows][ncols];");
            w.println("        for (int i=0; i<nrows; i++) {");
            w.println("            for (int j=0; j<ncols; j++) {");
            w.println("                if (REXPDouble.isNA(layerValues[cont])) {");
            w.println("                    tmpMatrix[i][j] = -9999.0f;");
            w.println("                } else {");
            w.println("                    tmpMatrix[i][j] = (float) layerValues[cont];");
            w.println("                }");
            w.println("                cont++;");
            w.println("            }");
            w.println("        }");
            w.println("        return tmpMatrix;");
            w.println("    }");
            w.println();
            w.println("    private GridCoverage2D buildRaster(double[] data, int ncols, int nrows, CRSEnvelope envelope, String name) {");
            w.println("        GridCoverageFactory gcfactory = new GridCoverageFactory();");
            w.println("        return setNoData(gcfactory.create(name, vector2matrix(data, ncols, nrows), envelope));");
            w.println("    }");
            w.println();
            w.println("    public GridCoverage2D getGridCoverage2D() {");
            w.println("        if (outgc != null) {");
            w.println("            return outgc;");
            w.println("        } else {");
            w.println("            String msg = \"No GridCoverage to transform from R to Java\";");
            w.println("            throw new NullPointerException(msg);");
            w.println("        }");
            w.println("    }");
            w.println();
            w.println("    public CoverageStack getCoverageStack() {");
            w.println("        if (outcs != null) {");
            w.println("            return outcs;");
            w.println("        } else {");
            w.println("            String msg = \"No CoverageStack to transform from R to Java\";");
            w.println("            throw new NullPointerException(msg);");
            w.println("        }");
            w.println("    }");
            w.println();
            w.println("}");

        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
