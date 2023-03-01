package soilproperties;

import oms3.ComponentAccess;
import oms3.annotations.*;

/**
 *
 * @author  KenRojasW, od
 */
public class BCParamInit  {
    
    @Role(Role.PARAMETER)
    @In public double[] soilLayerDepth;
    @Role(Role.PARAMETER)
    @In public double[] soilLayerSand;
    @Role(Role.PARAMETER)
    @In public double[] soilLayerSilt;
    @Role(Role.PARAMETER)
    @In public double[] soilLayerClay;
    @Role(Role.PARAMETER)
    @In public double[] soilLayerBulkDensity;
    @Role(Role.PARAMETER)
    @In public double wcFieldSaturationFraction;
    
    @Out public double[] brookscoreyBubblingPressure;
    @Out public double[] brookscoreyLamda;
    @Out public double[] brookscoreySatAdjThetaCurve;
    @Out public double[] brookscoreySatSlopeCondCurve;
    @Out public double[] brookscoreyUnSatInterceptCondCurve;
    @Out public double[] brookscoreyUnSatSlopeCondCurve;
    @Out public double[] wcResidual;
    @Out public double[] wcSaturation;
    @Out public double[] wcWiltingPoint;
    @Out public double[] waterConductivitySaturated;
    @Out public double[] waterConductivity;
    @Out public double[] headTensiometric;
    @Out public double[] wc;
    @Out public double[] wcFieldCapacity100;
    @Out public double[] wcFieldCapacity333;
    @Out public double[] soilLayerPorosity;
    @Out public double[] soilLayerThickness;

    int len; // soildepth
    DefaultSoilProperties dsp;
//    BCHydraulicFunctions bchf;

    private static final double FC_PRESSURE_100CM = 100.0; //tension of water [cm] column at 1/10 bar
    private static final double FC_PRESSURE_333CM = 333.0; //tension of water [cm] column at 1/3 bar
    private static final double WILTINGPOINTPRESSURE_20BAR = 20000.0; // tension of water [cm] column at 15 bar
    
    void init() {
        len = soilLayerDepth.length;  //have to have at least soil layers defined to get initial number of layers
        
        _allocArrays(soilLayerDepth.length);
        //determine default soil properties from basicFour (sand, silt, clay, bulk density)
        dsp = DefaultSoilProperties.getInstance();
        for (int i = 0; i < len; i++) {
            soilLayerThickness[i] = (i == 0) ? soilLayerDepth[i] : (soilLayerDepth[i] - soilLayerDepth[i - 1]);
            dsp.setTextureFractions(soilLayerSand[i], soilLayerSilt[i], soilLayerClay[i]);
            dsp.setBulkDensity(soilLayerBulkDensity[i]);
            
            _setBCValues(i);
            _adjustBCValues(i);
        }

        //      initialize head, theta, and conductivities
        for (int i = 0; i < len; i++) {
            wcWiltingPoint[i] = calcWaterContents(-WILTINGPOINTPRESSURE_20BAR, i);
            if (wc[i] >= wcSaturation[i] || wc[i] < wcWiltingPoint[i]) { //if wc's are outside bounds then try head values to initialize
                wc[i] = calcWaterContents(headTensiometric[i], i);
            } else {  //wc's are in bounds so initialize using wc's
                headTensiometric[i] = calcTensiometricHeads(wc[i], headTensiometric[i], i);
            }
            waterConductivity[i] = calcConductivities(headTensiometric[i], i);
        }
        
       // original section below
//        //      initialize head, theta, and conductivities
//        bchf = BCHydraulicFunctions.getBCHydraulicFunctionsInstance();
//        for (int i = 0; i < len; i++) {
//            wcWiltingPoint[i] = bchf.calcWaterContents(-__20BARWILTINGPOINTPRESSURE, i);
//            if (wc[i] >= wcSaturation[i] || wc[i] < wcWiltingPoint[i]) { //if wc's are outside bounds then try head values to initialize
//                wc[i] = bchf.calcWaterContents(headTensiometric[i], i);
//            } else {  //wc's are in bounds so initialize using wc's
//                headTensiometric[i] = bchf.calcTensiometricHeads(wc[i], headTensiometric[i], i);
//            }
//            waterConductivity[i] = bchf.calcConductivities(headTensiometric[i], i);
//        }
    }
    
    @Execute
    public void execute() throws Exception {
        System.out.println("called................");
        if (brookscoreyBubblingPressure == null) {
            init();
        }
        System.out.println(ComponentAccess.dump(this));
    }
    
    private void _allocArrays(int len) {
        brookscoreyBubblingPressure = new double[len];
        brookscoreyLamda = new double[len];
        brookscoreySatAdjThetaCurve = new double[len];
        brookscoreySatSlopeCondCurve = new double[len];
        brookscoreyUnSatInterceptCondCurve = new double[len];
        brookscoreyUnSatSlopeCondCurve = new double[len];
        wcResidual = new double[len];
        wcSaturation = new double[len];
        wcWiltingPoint = new double[len];
        waterConductivitySaturated = new double[len];
        waterConductivity = new double[len];
        headTensiometric = new double[len];
        wc = new double[len];
        wcFieldCapacity100 = new double[len];
        wcFieldCapacity333 = new double[len];
        soilLayerPorosity = new double[len];
        soilLayerThickness = new double[len];
    }
    
    private void _setBCValues(int i) {
        brookscoreyBubblingPressure[i] = dsp.getBubblingPressure();
        brookscoreyLamda[i] = dsp.getLamda();
        brookscoreySatAdjThetaCurve[i] = dsp.getBCSatAdjThetaCurve();
        brookscoreySatSlopeCondCurve[i] = dsp.getBCSatSlopeCondCurve();
        brookscoreyUnSatInterceptCondCurve[i] = dsp.getBCUnSatInterceptCondCurve();
        brookscoreyUnSatSlopeCondCurve[i] = dsp.getBCUnSatSlopeCondCurve();
        waterConductivitySaturated[i] = dsp.getKsat();
        wcResidual[i] = dsp.getWcResidual();
        wcSaturation[i] = dsp.getWcSaturated();
    }
    
    private void _adjustBCValues(int i) {
        _shiftBubblingPressure(i);
        _adjustFieldCapacities(i);
        _adjustSatConductivity(i);
    }
    
    private void _adjustSatConductivity(int i){
        double c1=764.5*Math.pow((wcSaturation[i]-wcFieldCapacity333[i]), 3.29);
        double c2=c1*Math.pow(brookscoreyBubblingPressure[i],(brookscoreyUnSatSlopeCondCurve[i]-brookscoreySatSlopeCondCurve[i]));
        waterConductivitySaturated[i] = c1;
        brookscoreyUnSatInterceptCondCurve[i] = c2;
    }
    
    private void _adjustFieldCapacities(int i){
        if (wcFieldCapacity333[i] > 0.0) {
            wcFieldCapacity100[i] = _newFieldCapacity(FC_PRESSURE_100CM,i);
        } else if (wcFieldCapacity100[i] > 0.0) {
            wcFieldCapacity333[i] = _newFieldCapacity(FC_PRESSURE_333CM,i);
        } else {
            wcFieldCapacity333[i] = _newFieldCapacity(FC_PRESSURE_333CM,i);
            wcFieldCapacity100[i] = _newFieldCapacity(FC_PRESSURE_100CM,i);
        }
    }
    
    private double _newFieldCapacity(double refPress, int i) {
        if(brookscoreyBubblingPressure[i]<=refPress){
            return _thetaBeta(i)/Math.pow(refPress,brookscoreyLamda[i])+wcResidual[i];
        } else {
            return wcSaturation[i]-brookscoreySatAdjThetaCurve[i]*refPress;
        }
    }
    
    private void _shiftBubblingPressure(int i) {
        double alfa, fc;
        double rb2=(1.0-brookscoreySatAdjThetaCurve[i]*brookscoreyBubblingPressure[i])*Math.pow(brookscoreyBubblingPressure[i], brookscoreyLamda[i]);
        if (wcFieldCapacity333[i] > 0.0) {
            fc=_thetaProportion(wcFieldCapacity333[i],i);
            alfa=_newHref(fc,rb2,i)/FC_PRESSURE_333CM;
        } else if (wcFieldCapacity100[i] > 0.0) {
            fc=_thetaProportion(wcFieldCapacity100[i],i);
            alfa=_newHref(fc,rb2,i)/FC_PRESSURE_100CM;
        } else {
            fc=_thetaProportion(dsp.getReferenceWcSaturated(),i);
            alfa=Math.pow(fc, (-1.0/brookscoreyLamda[i]));
        }
        brookscoreyBubblingPressure[i] = brookscoreyBubblingPressure[i]/alfa;
        brookscoreySatAdjThetaCurve[i] = brookscoreySatAdjThetaCurve[i]*alfa*(wcSaturation[i]-wcResidual[i]);
    }
   
    private double _newHref(double fc, double rb2, int i) {
        double href = 0.0;
        if (fc >= 1.0) {
            href = brookscoreyBubblingPressure[i] * 0.5;
        } else {
            href = Math.pow((rb2 / fc), (1.0 / brookscoreyLamda[i]));
            if (href < brookscoreyBubblingPressure[i] && brookscoreySatAdjThetaCurve[i] > 0.0) {
                href = (1.0 - fc) / brookscoreySatAdjThetaCurve[i];
            }
        }
        return href;
    }

    private double _thetaProportion(double refTheta, int i) {
        return (refTheta - dsp.getWcResidual()) / (wcSaturation[i] - wcResidual[i]);
    }

    private double _thetaBeta(int i) {
        double tt = Math.pow(brookscoreyBubblingPressure[i], brookscoreyLamda[i]);
        return (wcSaturation[i] - wcResidual[i] - brookscoreySatAdjThetaCurve[i] * brookscoreyBubblingPressure[i]) * tt;
    }


    //////////////////////////////
    // Hydraulic Functions merge.

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
        double thead = Math.max(head, WILTINGPOINTPRESSURE_20BAR);
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
//        len = soilLayerDepth.length;
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

    private boolean setupDone = false;
    private double[] orgwcSaturation;
    private double[] orgbrookscoreySatAdjThetaCurve;
    private double[] orgbrookscoreyBubblingPressure;
    private double[] orgbrookscoreyUnSatInterceptCondCurve;
    private double[] orgbrookscoreyLamda;
    private double[] orgwcResidual;
    private double[] orgbrookscoreyUnSatSlopeCondCurve;
 
}
