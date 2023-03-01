/*
 * DefaultSoilProperties.java
 *
 * Created on September 20, 2005, 12:09 PM
 */
package soilproperties;

/**
 * This Class will determine the DEFAULT soil properties based on soil texture or
 * through using a texture code.
 *
 * This is a singleton class, so you must use access points to obtains instances
 * of this class
 *
 * Public Access Points:
 *   getDefaultSoilProperties()
 *
 *   getDefaultSoilProperties(int iTex)
 *
 *   getDefaultSoilProperties(double sand, double silt, double clay)
 *
 * @author KenRojasW
 * @oms.version 1.0
 */
public class DefaultSoilProperties {

    /**
     * Codes can be:
     *     0- sand
     *     1- loamy sand
     *     2- sandy loam
     *     3- loam
     *     4- silty loam
     *     5- silt
     *     6- sandy clay loam
     *     7- clay loam
     *     8- silty clay loam
     *     9- sandy clay
     *     10- silty clay
     *     11- clay
     */
    private int iTex;
    private double psand;
    private double psilt;
    private double pclay;
    private double bd;
    //
    private double satThetaCurveAdj = 0.0f;
    private double satSlopeCondCurve = 0.0f;
    //
    private static final String SoilNames[] = {"sand", "loamy sand", "sandy loam", "loam", "silty loam", "silt", "sandy clay loam", "clay loam", "silty clay loam", "sandy clay", "silty clay", "clay"};
    private static final double[] rhob = {1.492, 1.492, 1.450, 1.423, 1.322, 1.322, 1.595, 1.420, 1.402, 1.511, 1.381, 1.391, 1.387};
    private static final double[] bubblingPressure = {7.2600, 8.6900, 14.660, 11.150, 20.760, 20.760, 28.080, 25.890, 32.560, 29.170, 34.190, 37.300, 50.000};
    private static final double[] lamda = {0.592, 0.474, 0.322, 0.220, 0.211, 0.211, 0.250, 0.194, 0.151, 0.168, 0.127, 0.131, 0.535};
    private static final double[] khUnSatSlope = {3.776, 3.422, 2.966, 2.660, 2.633, 2.633, 2.750, 2.582, 2.453, 2.480, 2.381, 2.393, 3.540};
    private static final double[] ksat = {21.000, 6.1100, 2.5900, 1.3200, 0.6800, 0.6800, 0.4300, 0.2300, 0.1500, 0.1200, 0.0900, 0.0600, 21.000};
    private static final double[] wcResidual = {0.020, 0.035, 0.041, 0.027, 0.015, 0.015, 0.068, 0.075, 0.040, 0.109, 0.056, 0.090, 0.020};
    private static final double[] wcSaturated = {0.437, 0.437, 0.453, 0.463, 0.501, 0.501, 0.398, 0.464, 0.471, 0.430, 0.479, 0.475, 0.555};
    private static final double[] wc333 = {0.0633, 0.1064, 0.1917, 0.2335, 0.2855, 0.2855, 0.2458, 0.3119, 0.3433, 0.3222, 0.3728, 0.3789, 0.2855};
    private static final double[] wc15000 = {0.0245, 0.0467, 0.0852, 0.1164, 0.1362, 0.1362, 0.1366, 0.1882, 0.2107, 0.2215, 0.2513, 0.2655, 0.1362};
    private static final double[] wc100 = {0.1083, 0.1613, 0.2630, 0.2961, 0.3638, .3638, 0.3082, 0.3743, 0.4038, 0.3699, 0.4251, 0.4283, 0.3638};
    private static final double[] khUnSatIntercept = {37421.03, 9985.430, 7448.230, 805.9800, 1998.790, 1998.790, 4135.810, 1024.320, 770.3800, 515.4900, 404.0700, 346.1400, 8716.600};
    //
    private static DefaultSoilProperties instance = null;
    //

    private DefaultSoilProperties() {
    }

    /**
     * default constructor with no texture information supplied. The user will
     * have to use set... methods to establish what soil type is used to index
     * the other public methods for soil properties.
     */
    public static synchronized DefaultSoilProperties getInstance() {
        if (instance == null) {
            instance = new DefaultSoilProperties();
        }
        return instance;
    }

    private int DetermineTextureCode(double psand, double psilt, double pclay) {
        if (psand >= 85.0f && (psilt + 1.5f * pclay) <= 15.0f) {
            return (0);
        } else if (psand >= 85.0f && psand <= 90.0f && (psilt + 1.5f * pclay) >= 15.0f) {
            return (1);
        } else if (psand >= 70.0f && psand <= 85.0f && (psilt + 2.0f * pclay) <= 30.0f) {
            return (1);
        } else if (psand >= 52.0f && pclay <= 20.0f && (psilt + 2.0f * pclay) > 30.0f) {
            return (2);
        } else if (psand >= 43.0f && psand <= 52.0f && (pclay < 7.0f && psilt < 50.0f)) {
            return (2);
        } else if (psand < 52.0f && pclay >= 7.0f && (pclay <= 27.0f && psilt >= 28.0f && psilt <= 50.0f)) {
            return (3);
        } else if (psilt >= 50.0f && pclay >= 12.0f && pclay <= 27.0f) {
            return (4);
        } else if (psilt >= 50.0f && psilt <= 80.0f && (pclay < 12.0f)) {
            return (4);
        } else if (psilt >= 80.0f && pclay < 12.0f) {
            return (5);
        } else if (psand >= 45.0f && pclay >= 20.0f && (pclay <= 35.0f && psilt < 28.0f)) {
            return (6);
        } else if (psand >= 20.0f && psand <= 45.0f && pclay >= 27.0f && pclay <= 40.0f) {
            return (7);
        } else if (pclay >= 27.0f && pclay <= 40.0f && psand < 20.0f) {
            return (8);
        } else if (pclay >= 35.0f && psand >= 45.0f) {
            return (9);
        } else if (pclay >= 40.0f && psilt >= 40.0f) {
            return (10);
        } else if (pclay >= 40.0f && psilt < 40.0f && psand < 45.0f) {
            return (11);
        } else {
            return (12);
        }
    }

    /**
     * The user knows the fractions of sand, silt, and clay from a soils 
     * analysis.  These values must be entered as fractions of the
     * total, therefore they must sum up to equal 1.0, or they are not valid.
     * @param sand fraction of sand (<1.0)
     * @param silt fraction of silt (<1.0)
     * @param clay fraction of clay (<1.0)
     */
    public void setTextureFractions(double sand, double silt, double clay) {
        psand = (sand > 1.0) ? sand : sand * 100.0f;
        psilt = (silt > 1.0) ? silt : silt * 100.0f;
        pclay = (clay > 1.0) ? clay : clay * 100.0f;
        iTex = DetermineTextureCode(psand, psilt, pclay);
        bd = 0.0;
    }

    /**
     * sets the iTex texture code
     * @param iTex is the texture code used to index soil types
     * <B>See Also:</B> {@link #getTextureCode}
     */
    public void setTextureCode(int iTex) {
        this.iTex = iTex;
        bd = 0.0;
    }

    public void setBulkDensity(double bd) {
        this.bd = bd;
    }

    public int getTextureCode() {
        return iTex;
    }

    /**
     * provides texture class name which follows:
     * @return name of the current soil
     * <B>See Also:</B> {@link #getTextureCode}
     */
    public String getTextureClass() {
        return SoilNames[iTex];
    }

    /**
     * provides default bulk density (g/cc)
     * @return bulk density (g/cc)
     */
    public double getBulkDensity() {
        return (bd == 0.0) ? rhob[iTex] : bd;
    }

    /**
     * provides soil particle density (g/cc)
     * @return particle density (usually = 2.65 g/cc)
     */
    public double getParticleDensity() {
        return (2.65f);
    }

    /**
     * provides default porosity
     * @return porosity (unitless)
     */
    public double getPorosity() {
        return 1.0f - getBulkDensity() / getParticleDensity();
    }

    /**
     * provides default bubbling pressure (cm of water) use this value
     * for both theat-h and cond-h curves.
     * @return bubbling pressure (cm of water)
     */
    public double getBubblingPressure() {
        return bubblingPressure[iTex];
    }

    /**
     * provides default saturated conductivity (cm/hr)
     * @return conductivity (cm/hr)
     */
    public double getKsat() {
        return ksat[iTex];
    }

    /**
     * provides default lamda for theta-head curve
     * @return lamda value
     */
    public double getLamda() {
        return lamda[iTex];
    }

    /**
     * provides default adjustment of saturated portion of the of the theta/h curve
     * used in the brooks/corey equations
     * @return BC adjustment to saturated part of t/h curve
     */
    public double getBCSatAdjThetaCurve() {
        return satThetaCurveAdj;
    }

    /**
     * provides default residual water content (cc/cc)
     * @return residual water content (cc/cc)
     */
    public double getWcResidual() {
        return wcResidual[iTex];
    }

    /**
     * provides default saturated water content (cc/cc)
     * @return saturated water content (cc/cc)
     */
    public double getWcSaturated() {
        return getPorosity();
    }

    /**
     * provides default saturated water content (cc/cc)
     * @return saturated water content (cc/cc)
     */
    public double getReferenceWcSaturated() {
        return wcSaturated[iTex];
    }

    /**
     * provides default 1/3 bar (333 cm water) water content (cc/cc)
     * @return field capacity value at 1/3 bar tension (cc/cc)
     */
    public double getWc333cm() {
        return wc333[iTex];
    }

    /**
     * provides default 1/10 bar (100 cm of water) water content (cc/cc)
     * @return field capacity at 1/10 bar tension (cc/cc)
     */
    public double getWc100cm() {
        return wc100[iTex];
    }

    /**
     * provides default 15 bar (15000 cm of water) water content (cc/cc)
     * (WILTING POINT)
     * @return wilting point water content at 15 bar tension (cc/cc)
     */
    public double getWc15000cm() {
        return wc15000[iTex];
    }

    /**
     * provides default intercept of the unsaturated portion for the conductivity-head
     * relationship for use in the brooks/corey equations
     * @return conductivity curve intercept (cm/hr)
     */
    public double getBCUnSatInterceptCondCurve() {
        return khUnSatIntercept[iTex];
    }

    /**
     * provides default slope of the unsaturated portion of conductivity-head
     * relationship for use in the brooks/corey equations
     * @return conductivity curve intercept (cm/hr)
     */
    public double getBCUnSatSlopeCondCurve() {
        return khUnSatSlope[iTex];
    }

    /**
     * provides default slope of the saturated portion of conductivity-head
     * relationship for use in the brooks/corey equations
     * @return conductivity curve slope
     */
    public double getBCSatSlopeCondCurve() {
        return satSlopeCondCurve;
    }
}
