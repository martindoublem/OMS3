/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interception;

import oms3.annotations.Execute;

/**
 *
 * @author od
 */
public class GreenAmpt {
    
    
    @Execute
    public void exec() {
    }

//C     CI   TIME SERIES ACCUMULATED WATER INFILTRATION [CM]
//C     CUMRO   CUMMULATIVE WATER RUNOFF [CM]
//C     DR   DEPTH OF RAIN IN CURRENT STEP
//C     DTIM   TIME STEP [HR]
//C     I   INDEX VARIABLE
//C     ISAT   INDICATES WHETHER PROFILE IS SATURATED 
//C           0=NO 
//C           1=YES [0..1]
//C     RO   WATER RUNOFF DURING A TIME INTERVAL [CM]
//C     RR   RANDOM ROUGHNESS [CM]
//C     RRD   AMOUNT OF RAIN IN THIS TIME STEP [CM]
//C     TR   REFERENCE SOIL TEMPERATURE [C]
//C     YESMAC   INDICATS PRESENCE OF MACROPORES =1 YES, =0 NOT
    void mixrunoff(double[] CI, double[] CUMRO, int I, int ID, int ISAT, double RO,
            double RR, double RRD, double[] TR, int YESMAC) {
        double DR, DTIM;

        // UNSATURATED/PARTIALLY SATURATED CONDITION
        if (I == 0) {
            DR = CI[I];
            RRD = RR * TR[I];
            DTIM = TR[I];
        } else {
            DR = CI[I] - CI[I - 1];
            RRD = RR * (TR[I] - TR[I - 1]);
            DTIM = TR[I] - TR[I - 1];
        }

        RO = RRD - DR;
        if (RO <= 1.0E-13) {
            RO = 0.0;
        }
        // SETUP ACCUMULATORS
        if (I <= 0) {
            CUMRO[I] = 0.0;
        } else {
            CUMRO[I] = CUMRO[I - 1];
        }

        // IF THERE IS RUNOFF THEN MIX CHEMICALS
        if (RO > 0.0) {
            // ACCUMULATE RUNOFF TOTALS
            if (ISAT == 1 || (ISAT != 1 && YESMAC == 0)) {
                CUMRO[I] = CUMRO[I] + RO;
            }
        }
    }

    /*
    C    -------------------------------------------------------------------------
    C     IT2H   RELATED INFILTRATION INDEX TO HORIZON NUMBER [1..MXNODT]
    C     ITRANS   INFILTRATION INDEX TO BE TRANSLATED
    C     JT   INDEX VARIABLE
    C     NDXN2H   INDEX FOR NUMERICAL LAYERS TO HORIZONS, 
    C           IE WHICH HORIZON IS NUMERICAL LAYER IN. [1..MXNOD]
    C     NDXT2N   INDEX FOR GREEN-AMPT GRID TO NUMERICAL LAYER,   
    C           IE WHICH NUMERICAL LAYER IS G-A GRID IN. [1..MXNODT]
    C    -------------------------------------------------------------------------
     */
    int IT2H(int itrans, double[] dhb) {
        int t = 0;
        for (int i = 0; i < dhb.length; i++) {
            if (dhb[i] != 0) {
                t = i;
            }
            if (itrans <= dhb[i]) {
                break;
            }
        }
        return t;
    }
}
