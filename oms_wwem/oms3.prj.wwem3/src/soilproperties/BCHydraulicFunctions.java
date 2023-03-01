/*
 * BCHydraulicFunctions.java
 *
 * Created on December 7, 2005, 5:53 PM
 */

package soilproperties;

import oms3.annotations.*;
import static oms3.annotations.Role.*;



/** This component updates the values for tensiometric head (cm), theta (cc/cc),
 * and <code>hydaulic conductivity</code> (cm/hr); as described by the Brook & Corey (1964)
 * relationships (with slight modifications).  It will balance these values for a
 * single soil layer.
 * <p>
 * Pertenent hydaulic properties must be supplied for the layer as well as the
 * current head value.  Theta and conductivity are then determined based on this
 * head value.  A field saturation fraction will limit the theta values and the
 * heads will be adjusted with this limit applied.<br>
 * <p>
 * An adjustment to the original B/C equations are is applied for head values
 * between bubbling pressure (hb) and 10*hb.  If the head is in this range then the
 * original (non-adjusted due to tillage).  These original values are stored in the
 * orig_hyd_prop variable during the initialization phase, and can be later applied
 * by setting the reset variable to true.
 * <p>
 * @oms.author KenRojasW
 * @oms.science agronomy
 * @oms.temporalScale hour
 * @oms.spatialScale 1-d
 */
public class BCHydraulicFunctions   {
    
    /**  These are the local and original hydraulic values stored during the
     * initialization phase.
     */
    private int len=0;
    private boolean setupDone = false;
    private double[] orgwcSaturation;
    private double[] orgbrookscoreySatAdjThetaCurve;
    private double[] orgbrookscoreyBubblingPressure;
    private double[] orgbrookscoreyUnSatInterceptCondCurve;
    private double[] orgbrookscoreyLamda;
    private double[] orgwcResidual;
    private double[] orgbrookscoreyUnSatSlopeCondCurve;
    
    @In public double[] wcWiltingPoint;
    @In public double[] wcSaturation;
    @In public double[] brookscoreySatAdjThetaCurve;
    @In public double[] brookscoreyBubblingPressure;
    @In public double[] waterConductivitySaturated;
    @In public double[] brookscoreyUnSatInterceptCondCurve;
    @In public double[] brookscoreyLamda;
    @In public double[] wcResidual;
    @In public double[] brookscoreySatSlopeCondCurve;
    @In public double[] brookscoreyUnSatSlopeCondCurve;
    @In public double wcFieldSaturationFraction;
    @In public double[] soilLayerDepth;
    //
    private static final double __20BARWILTINGPOINTPRESSURE = -20000.0f; // tension of water [cm] column at 15 bar
    
    private static BCHydraulicFunctions instance = null;
    
    public static synchronized BCHydraulicFunctions getBCHydraulicFunctionsInstance() {
        if (instance == null) {
            instance = new BCHydraulicFunctions();
        }
        return instance;
    }

    public void setup() {
        setupDone = true;
        setupArrays();
    }

    /**
     * Determines the tensiometric head values from water content values.  The theta
     * values are first determined from the current head then bounded by a field
     * saturation percentage.  This keeps them in balance.
     *
     * @return head - tensiometric head values (cm)
     * @param theta
     * @param head
     * @param index1
     */
    public double calcTensiometricHeads(double theta, double head, int index1) {
        if (!setupDone) {
            setup();
        }
        double thead = head;
        double ttheta = Math.max(theta, wcWiltingPoint[index1]);

        if (ttheta - (wcSaturation[index1] - brookscoreySatAdjThetaCurve[index1]
                * brookscoreyBubblingPressure[index1]) >= -1.0e-10) {
            if (brookscoreySatAdjThetaCurve[index1] != 0.0 && thead <= 0.0) {
                thead = -1.0 * (wcSaturation[index1] - ttheta) / brookscoreySatAdjThetaCurve[index1];
            } else if (thead < -brookscoreyBubblingPressure[index1]) {
                thead = -brookscoreyBubblingPressure[index1];
            }
        } else {
            double ThetaAtTenXBP = calcBetaOrig(index1) * Math.pow(10.0 * orgbrookscoreyBubblingPressure[index1], -orgbrookscoreyLamda[index1]) + orgwcResidual[index1];
            if (ttheta <= ThetaAtTenXBP) {
                thead = -1.0 * Math.pow((ttheta - orgwcResidual[index1]) / calcBetaOrig(index1), -1.0 / orgbrookscoreyLamda[index1]);
            } else if (ttheta < wcSaturation[index1]) {
                thead = -1.0 * Math.pow((ttheta - wcResidual[index1]) / calcBeta(index1), -1.0 / brookscoreyLamda[index1]);
            }
        }
        return (Math.abs(thead) <= 1.0e-12) ? 0.0 : thead;
    }

    /**
     * Determines volumetric water content - theta (cc/cc) from tensiometric head (cm).
     * The head value is bounded by h > -20000 cm.
     * @return wc - volumetric water content (cc/cc)
     * @param head
     * @param index1
     */
    public double calcWaterContents(double head, int index1) {
        if (!setupDone) {
            setup();
        }
        double wc = wcSaturation[index1]; // default to sat wc
        double thead = Math.max(head, __20BARWILTINGPOINTPRESSURE);
        if (thead < 0.0) {
            double poshead = Math.abs(thead);
            if (thead > -brookscoreyBubblingPressure[index1]) {
                wc = wcSaturation[index1] + brookscoreySatAdjThetaCurve[index1] * poshead;  //on sat portion of unsat curve
            } else if (thead < -10.0 * brookscoreyBubblingPressure[index1]) {
                wc = calcBetaOrig(index1) * Math.pow(poshead, -orgbrookscoreyLamda[index1]) + orgwcResidual[index1];
            } else {
                wc = calcBeta(index1) * Math.pow(poshead, -brookscoreyLamda[index1]) + wcResidual[index1];
            }
        }
        return Math.min(wc, wcSaturation[index1] * wcFieldSaturationFraction);
    }

    /**
     * Determines the hydraulic conductivities (cm/hr) from tensiometric head (cm).
     * @return cond - conductivity value (cm/hr)
     * @param head
     * @param index1
     */
    public double calcConductivities(double head, int index1) {
        if (!setupDone) {
            setup();
        }
        double cond = waterConductivitySaturated[index1];  // default to ksat
        double thead = head;
        if (thead < 0) { //unsat soil so find unsat k
            double poshead = Math.abs(thead);
            if (thead >= -brookscoreyBubblingPressure[index1]) {
                cond = waterConductivitySaturated[index1] * Math.pow(poshead, -brookscoreySatSlopeCondCurve[index1]);
            } else {
                if (thead <= -brookscoreyBubblingPressure[index1] * 10.0) {
                    cond = orgbrookscoreyUnSatInterceptCondCurve[index1] * Math.pow(poshead, -orgbrookscoreyUnSatSlopeCondCurve[index1]);
                } else {
                    cond = brookscoreyUnSatInterceptCondCurve[index1] * Math.pow(poshead, -brookscoreyUnSatSlopeCondCurve[index1]);
                }
            }
        }
        return cond;
    }

    private void setupArrays() {
        len = soilLayerDepth.length;
        if (len > 0) {
            orgwcSaturation = new double[len];
            orgbrookscoreySatAdjThetaCurve = new double[len];
            orgbrookscoreyBubblingPressure = new double[len];
            orgbrookscoreyUnSatInterceptCondCurve = new double[len];
            orgbrookscoreyLamda = new double[len];
            orgwcResidual = new double[len];
            orgbrookscoreyUnSatSlopeCondCurve = new double[len];
            
            for (int i = 0; i < len; i++) {
                orgwcSaturation[i] = wcSaturation[i];
                orgbrookscoreySatAdjThetaCurve[i] = brookscoreySatAdjThetaCurve[i];
                orgbrookscoreyBubblingPressure[i] = brookscoreyBubblingPressure[i];
                orgbrookscoreyUnSatInterceptCondCurve[i] = brookscoreyUnSatInterceptCondCurve[i];
                orgbrookscoreyLamda[i] = brookscoreyLamda[i];
                orgwcResidual[i] = wcResidual[i];
                orgbrookscoreyUnSatSlopeCondCurve[i] = brookscoreyUnSatSlopeCondCurve[i];
            }
        }
    }

    /** Determines the B/C constant beta for application in the relationships.
     * @return the beta value
     * @param hydprop hydraulic properties
     * @see hyd definition
     */
    private double calcBeta(int index1) {
        double rt = Math.pow(brookscoreyBubblingPressure[index1], brookscoreyLamda[index1]);
        return (wcSaturation[index1] - wcResidual[index1] - brookscoreySatAdjThetaCurve[index1] * brookscoreyBubblingPressure[index1]) * rt;
    }

    /** Determines the B/C constant beta for application in the relationships.
     * @return the beta value
     * @param hydprop hydraulic properties
     * @see hyd definition
     */
    private double calcBetaOrig(int index1) {
        double rt = Math.pow(orgbrookscoreyBubblingPressure[index1], orgbrookscoreyLamda[index1]);
        return (orgwcSaturation[index1] - orgwcResidual[index1] - orgbrookscoreySatAdjThetaCurve[index1] * orgbrookscoreyBubblingPressure[index1]) * rt;
    }

}
