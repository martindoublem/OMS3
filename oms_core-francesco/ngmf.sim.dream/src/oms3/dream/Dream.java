/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oms3.dream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;

import oms3.annotations.Execute;
import oms3.annotations.Finalize;
import oms3.annotations.Initialize;
import oms3.ComponentAccess;
import oms3.ComponentException;
import lib.*;
import ngmf.util.UnifiedParams;
import oms3.dsl.AbstractSimulation;
import oms3.dsl.Buildable;
import oms3.dsl.Output;
import oms3.dsl.Param;
import oms3.dsl.Params;
import oms3.dsl.cosu.ObjFunc;

/**
 * DREAM
 * 
 * @author gf,od,rs
 */
public class Dream extends AbstractSimulation {

    // Verbosity Level (Default = 0)"
    int verbose = 0;
    // The Upper and Lower Parameter Bounds")
    Params params;
    // Number of Differential Evolution pairs")
    int MCMCParam_DEpairs;
    // Number of parameters for Markov Chain Monte Carlo")
    int MCMCParam_N;
    // Number of Markov Chains / Sequences.")
    int MCMCParam_Seq;
    // Number of steps in the walk")
    int MCMCParam_Steps;
    // Crossover values used to generate proposals (geometric series).")
    int MCMCParam_nCR;
    // Maximum number of function evaluations.")
    int MCMCParam_nDraw;
    // The limit of the time series considered in computation.")
    int Extra_MaxT;
    // Every Tth sample is collected.")
    int Extra_T;
    // Number of iterations before collecting data")
    int Warmup;
    // Kurtosis Parameter: Bayesian Inference Scheme.")
    double MCMCParam_Gamma = Double.NaN;
    // Random Error for Ergodicity.")
    double MCMCParam_ErgodicErr = Double.NaN;
    // Define start index for density parameters computation.")
    double Extra_DRscale = Double.NaN;
    // Area of the basin.")
    double Area = Double.NaN;
    // The first value of measured discharge")
    double Q0 = Double.NaN;
    // Define the Sigma")
    double Measurement_Sigma = Double.NaN;
    // Give the parameter ranges (minimum values).")
    double[] ParamRange_Min;
    // Give the parameter ranges (maximum values).")
    double[] ParamRange_Max;
    // Path of ETP input data file.")
    String ETP_File;
    // Path of Precipitation input data file.")
    String Precip_File;
    // Path of Measurement Data file.")
    String Meas_Data_File;
    // Path of the R-Statistic output file.")
    String Rstat_File;
    // Path of the Sequences output file.")
    String Seq_File;
    // Type of test to detect outlier chains.")
    String MCMCParam_OutlierTest;
    // Adaptive tuning of crossover values.")
    String Extra_pCR;
    // Adaptive tuning of crossover values.")
    String Extra_ReducedSampleCollection;
    // Type of initial sampling.")
    String Extra_InitPop;
    // Define the boundary handling.")
    String Extra_BoundHandling;
    // TRUE=yes, FALSE=no")
    boolean Restart;
    // Save in memory or not.")
    boolean Extra_SaveInMemory;
    // Delayed Rejection: Define likelihood function-Sum of Squared Error. TRUE=yes, FALSE=no")
    boolean Extra_DR;
    // Define likelihood function -- Sum of Squared Error.")
    double Option = Double.NaN;
    // Defines an Objective Function")
    ObjFunc of;
    // The resulting Matrix from the Model.")
    lib.Matrix Matrix_Model;
    // Input Matrix from Model")
    lib.Matrix modPredMatrix;
    // DREAM output to HyMod.Cmax")
    double Out_Cmax;
    // DREAM output to HyMod.b")
    double Out_b;
    // DREAM output to HyMod.Alpha")
    double Out_Alpha;
    // DREAM output to HyMod.Rs")
    double Out_Rs;
    // DREAM output to HyMod.Rq")
    double Out_Rq;
    // DREAM output to HyMod.Precip")
    lib.Matrix Out_Precip;
    // DREAM output to HyMod.ETP")
    lib.Matrix Out_ETP;
    // DREAM output to HyMod.Area")
    double Out_Area;
    // DREAM output to HyMod.Timestep")
    double Out_Timestep = 24.0;
    // DREAM output to HyMod.Q0")
    double Out_Q0;
    ModelExecution exec;
    /*
     * Internal Parameters
     */
    //Potential ETP vector to the Model.
    private double[] Extra_ETP;
    //Precipitation vector to the Model
    private double[] Extra_Precip;
    //Define the measured streamflow data.
    private double[] Measurement_Data;
    //Number of elements in the measurement data
    private int Measurement_N;
    private int Iter;
    private int iLoc;
    private int teller;
    private int genNum;
    private int Count;
    private int updatedOutlier;
    private int[] DEversion;
    private double MCMCParam_Wb;
    private double MCMCParam_Cb;
    private double[] sigmArray;
    private double[] log_p_CD;
    private double[] pCR;
    private double[] oldP;
    private double[] log_p_old;
    private double[] deltaTotal;
    private double[] accept;
    private double[] lCR_out_AdaptpCR;
    private double[] pCR_out_AdaptpCR;
    private double[] lCR;
    private double[] Observed_Data;
    private double[] Simulated_Data;
    private double[][] hist_logp;
    //This should be the population
    private double[][] X;
    //Seems to hold density values for X
    private double[][] p_CD;
    //Crossover probability
    private double[][] outputCR;
    private double[][] oldX;
    private double[][] CR;
    private double[][] Table_JumpRate;
    private double[][] newX;
    private double[][] newGen;
    //Acceptance rate
    private double[][] outAccRate;
    //Outlier Chains
    private double[][] outputOutlier;
    private double[][] outputRstat;
    private double[][][] sequences;
    //Output path for RStat.txt & Sequences.txt
    private RandomStream randomstream;
    //String representing the Observed Data
    private String obs_data;
    //String representing Simulated Data
    private String sim_data;

    @Override
    public Buildable create(Object name, Object value) {
        if (name.equals("MCMCParam_N")) {
            MCMCParam_N = (Integer) value;
        } else if (name.equals("MCMCParam_Seq")) {
            MCMCParam_Seq = (Integer) value;
        } else if (name.equals("MCMCParam_nDraw")) {
            MCMCParam_nDraw = (Integer) value;
        } else if (name.equals("MCMCParam_nCR")) {
            MCMCParam_nCR = (Integer) value;
        } else if (name.equals("MCMCParam_Gamma")) {
            MCMCParam_Gamma = (Integer) value;
        } else if (name.equals("MCMCParam_DEpairs")) {
            MCMCParam_DEpairs = (Integer) value;
        } else if (name.equals("MCMCParam_Steps")) {
            MCMCParam_Steps = (Integer) value;
        } else if (name.equals("MCMCParam_ErgodicErr")) {
            MCMCParam_ErgodicErr = (Integer) value;
        } else if (name.equals("MCMCParam_OutlierTest")) {
            MCMCParam_OutlierTest = (String) value;
        } else if (name.equals("Extra_pCR")) {
            Extra_pCR = (String) value;
        } else if (name.equals("Extra_ReducedSampleCollection")) {
            Extra_ReducedSampleCollection = (String) value;
        } else if (name.equals("Extra_T")) {
            Extra_T = (Integer) value;
        } else if (name.equals("Extra_InitPop")) {
            Extra_InitPop = (String) value;
        } else if (name.equals("ParamRange_Min")) {
            ParamRange_Min = (double[]) value;
        } else if (name.equals("ParamRange_Max")) {
            ParamRange_Max = (double[]) value;
        } else if (name.equals("Extra_BoundHandling")) {
            Extra_BoundHandling = (String) value;
        } else if (name.equals("Extra_MaxT")) {
            Extra_MaxT = (Integer) value;
        } else if (name.equals("Extra_DR")) {
            Extra_DR = (Boolean) value;
        } else if (name.equals("Extra_DRscale")) {
            Extra_DRscale = (Integer) value;
        } else if (name.equals("Restart")) {
            Restart = (Boolean) value;
        } else if (name.equals("Warmup")) {
            Warmup = (Integer) value;
        } else if (name.equals("ETP_File")) {
            ETP_File = (String) value;
        } else if (name.equals("Precip_File")) {
            Precip_File = (String) value;
        } else if (name.equals("Meas_Data_File")) {
            Meas_Data_File = (String) value;
        } else if (name.equals("Measurement_Sigma")) {
            Measurement_Sigma = (Double) value;
        } else if (name.equals("Option")) {
            Option = (Integer) value;
        } else if (name.equals("Extra_SaveInMemory")) {
            Extra_SaveInMemory = (Boolean) value;
        } else if (name.equals("Area")) {
            Area = (Double) value;
        } else if (name.equals("Q0")) {
            Q0 = (Double) value;
        } else if (name.equals(Params.DSL_NAME)) {
            params = new Params();
            return params;
        } else if (name.equals("verbose")) {
            verbose = (Integer) value;
        } else if (name.equals(ObjFunc.DSL_NAME)) {
            of = new ObjFunc();
            return of;
        } else {
            return super.create(name, value);
        }
        return LEAF;
    }

    @Override
    public Object run() throws Exception {
        //Set up the component
        if (getModelElement() == null) {
            throw new ComponentException("Missing 'Model'!");
        }
        if (of == null) {
            throw new ComponentException("Objective Function not found! " + ObjFunc.DSL_NAME);
        }
        if (params == null) {
            throw new ComponentException("Params not found! " + Params.DSL_NAME);
        }

        //Load observed and simulated data
        obs_data = of.getObserved().getData();
        sim_data = of.getSimulated().getData();
        if (obs_data == null || sim_data == null) {
            throw new ComponentException("No Objective Function data!");
        }
        //Send Sim & Obs data to ModelExecution and run model
        getModelElement().setOut2Out(new String[]{obs_data, sim_data});

        exec = new ModelExecution();

        if (verbose == 1) {
            System.out.println("Inputs: ");
            for (Param p : params.getParam()) {
                System.out.println(p.getName() + " " + p.getLower() + " ... " + p.getUpper());
            }
            //Outputs sim_data, obs_data, # of "clusters"(kmax), # of chains(numPart)
            //System.out.println( obs_data + " " + sim_data + " " + kmax + " " + MCMCParam_Seq );
        }

        int numParam = params.getParam().size();
        ParamRange_Min = new double[numParam];
        ParamRange_Max = new double[numParam];

        for (int i = 0; i < numParam; i++) {
            if (!(params.getParam().get(i).getValue() instanceof Number)) {
                throw new IllegalArgumentException(params.getParam().get(i).getName() + " is not a scalar.");
            }
        }
        for (int i = 0; i < numParam; i++) {
            ParamRange_Min[i] = params.getParam().get(i).getLower();
            ParamRange_Max[i] = params.getParam().get(i).getUpper();
        }
        // parameters matrix [numPar][numPart]
        //double matPar[][] = uniform( pParDowRange, pParUpRange, MCMCParam_Seq ) ;
        // check param bounds
        //double matParBounded[][] = ReflectBounds( matPar, pParUpRange, pParDowRange );

        randomstream = new MRG32k3a();
        updatedOutlier = 0;
        MCMCParam_Cb = 0;
        MCMCParam_Wb = 0;

        if (Restart == false) {
            initVariables();
        }
        //If not restarting, load data from files
        loadData();
        Measurement_N = Measurement_Data.length;
        /*
         * Step 1: Sample s points in the parameter space
         */
        double[][] sample = null;
        if (Extra_InitPop.equals("LHS_BASED")) {
            // Latin hypercube sampling when indicated
            sample = LHSU(ParamRange_Min, ParamRange_Max, MCMCParam_Seq);
        }

        /*
         * Step 2: Calculate posterior density associated with each value in x
         */
        //Calculate the cost of each chain
        X = new double[sample.length][sample[ 0].length + 2];
        for (int i = 0; i < sample.length; i++) {
            for (int j = 0; j < sample[ 0].length; j++) {
                X[ i][ j] = sample[ i][ j];
                X[ i][ sample[ 0].length] = p_CD[ i][ 0];
                X[ i][ sample[ 0].length + 1] = log_p_CD[ i];
            }
        }
        if (Extra_SaveInMemory == true) {
            sequences = InitSequences(X);
        }
        // Save N_CR in memory and initialize delta_tot
        outputCR[ 0][ 0] = Iter;
        for (int i = 1; i < pCR.length + 1; i++) {
            outputCR[ 0][ i] = pCR[ i - 1];
        }
        // Save history log density of individual chains
        double[] X_x_histlogp = new double[MCMCParam_Seq];
        for (int i = 0; i < X.length; i++) {
            X_x_histlogp[ i] = X[ i][ MCMCParam_N + 1];
        }
        hist_logp[ 0][ 0] = Iter;
        for (int i = 1; i < MCMCParam_Seq + 1; i++) {
            hist_logp[ 0][ i] = X_x_histlogp[ i - 1];
        }
        double[] rStat = Gelman(sequences);
        outputRstat[ 0][ 0] = Iter;
        for (int i = 1; i < outputRstat[ 0].length; i++) {
            outputRstat[ 0][ i] = rStat[ i - 1];
        }

        // Now start iteration ...
        while (Iter < MCMCParam_nDraw) {
            // Loop a number of times
            for (genNum = 0; genNum < MCMCParam_Steps; genNum++) {
                // Initialize DR properties

                // Define the current locations and associated posterior densities
                GetLocation();

                //Now generate candidate in each sequence using current point & members of X
                offde(X);// from here get x_new and the updated CR
                computeDensity(newX);
                double[] log_p_xnew = log_p_CD;
                double[] p_xnew = new double[p_CD.length];
                for (int i = 0; i < p_xnew.length; i++) {
                    p_xnew[ i] = p_CD[ i][ 0];
                }

                // Now apply the acceptance/rejectance rule for the chain itself
                metropolis(newX, p_xnew, log_p_xnew, oldX, oldP, log_p_old);

                // Define the location in the sequence
                if (Extra_SaveInMemory == true) {
                    // Define idx based on iloc
                    iLoc++;
                }

                // Now update the locations of the Sequences with the current locations
                double[][] newGenTraspose = new double[newGen[ 0].length][newGen.length];
                for (int i = 0; i < newGenTraspose.length; i++) {
                    for (int j = 0; j < newGenTraspose[ 0].length; j++) {
                        newGenTraspose[ i][ j] = newGen[ j][ i];
                    }
                }
                for (int i = 0; i < sequences[ 0].length; i++) {
                    for (int j = 0; j < sequences[ 0][ 0].length; j++) {
                        sequences[ iLoc - 1][ i][ j] = newGenTraspose[ i][ j];
                    }
                }

                // And update X using current members of Sequences
                X = newGen;
                newGen = new double[oldX.length][oldX[ 0].length + 2];
                if (Extra_pCR.equals("Update")) {
                    // Calculate the standard deviation of each dimension of X
                    double[] stdDev = new double[MCMCParam_N];
                    for (int i = 0; i < stdDev.length; i++) {
                        double[] column = new double[stdDev.length];
                        double sum = 0.0;
                        for (int j = 0; j < column.length; j++) {
                            column[ j] = X[ j][ i];
                            sum += column[ j];
                        }
                        stdDev[ i] = Math.sqrt(Descriptive.sampleVariance(new DoubleArrayList(column), sum / column.length));
                    }

                    double[] deltaNormX = new double[oldX.length];
                    for (int i = 0; i < deltaNormX.length; i++) {
                        double sumNorm = 0.0;
                        for (int j = 0; j < oldX[ 0].length; j++) {
                            sumNorm += (((oldX[ i][ j] - X[ i][ j]) / stdDev[ j])
                                    * ((oldX[ i][ j] - X[ i][ j]) / stdDev[ j]));
                        }
                        deltaNormX[ i] = sumNorm;
                    }

                    double[] arrCR = new double[MCMCParam_Seq];
                    for (int i = 0; i < arrCR.length; i++) {
                        arrCR[ i] = CR[ i][ genNum];
                    }
                    CalcDelta(deltaNormX, arrCR);
                }

                // Update hist_logp
                hist_logp[ Count][ 0] = Iter;
                for (int i = 0; i < MCMCParam_Seq; i++) {
                    hist_logp[ Count][ i + 1] = X[ i][ MCMCParam_N + 1];
                }

                // Save some important output -- Acceptance Rate
                outAccRate[ Count][ 0] = Iter;
                double sumAccept = 0.0;
                for (int i = 0; i < accept.length; i++) {
                    sumAccept += accept[i];
                }
                outAccRate[ Count][ 1] = 100 * sumAccept / MCMCParam_Seq;

                // Update Iteration and counter
                Iter += MCMCParam_Seq;
                Count++;
            }

            // Store important diagnostic info - Probability of indiv. crossover values
            outputCR[ teller][ 0] = Iter;
            for (int i = 1; i < pCR.length + 1; i++) {
                outputCR[ teller][ i] = pCR[ i - 1];
            }

            // Do this to get rounded iteration numbers
            if (teller == 1) {
                MCMCParam_Steps++;
            }
            double v = (int) (0.1 * MCMCParam_nDraw);
            if (Iter <= v) {
                if (Extra_pCR.equals("Update")) {
                    // Update pCR values
                    AdaptpCR(CR, deltaTotal, lCR);
                    pCR = pCR_out_AdaptpCR;
                    lCR = lCR_out_AdaptpCR;
                }
            } else {
                // See if there are any outlier chains, & remove them to current best value of X
                double[][] seq_x_MOC = new double[sequences[ 0].length][sequences[ 0][ 0].length];
                for (int i = 0; i < seq_x_MOC.length; i++) {
                    for (int j = 0; j < seq_x_MOC[0].length; j++) {
                        seq_x_MOC[ i][ j] = sequences[ iLoc][ i][ j];
                    }
                }
                double[][] hist_logp_x_ROC = new double[Count][MCMCParam_Seq + 1];
                for (int i = 0; i < hist_logp_x_ROC.length; i++) {
                    for (int j = 0; j < hist_logp_x_ROC[ 0].length; j++) {
                        hist_logp_x_ROC[ i][ j] = hist_logp[ i][ j];
                    }
                }
                RemoveOutChains(X, seq_x_MOC, hist_logp_x_ROC, Iter, outputOutlier);
                for (int i = 0; i < seq_x_MOC.length; i++) {
                    for (int j = 0; j < seq_x_MOC[ 0].length; j++) {
                        sequences[ iLoc][ i][ j] = seq_x_MOC[ i][ j];
                    }
                }
                for (int i = 0; i < hist_logp_x_ROC.length; i++) {
                    for (int j = 0; j < hist_logp_x_ROC[ 0].length; j++) {
                        hist_logp[ i][ j] = hist_logp_x_ROC[ i][ j];
                    }
                }
            }
            for (int i = 0; i < pCR.length; i++) {
                System.out.print(pCR[i] + " ");
            }
            System.out.println();
            if (Extra_pCR.equals("Update")) {
                CR = GenCR(pCR);
                for(int j = 0; j < pCR.length; j++) {
                    System.out.print(pCR[j] + " ");
                }
                System.out.println();
            }
            // Calculate Gelman and Rubin convergence diagnostic
            if (Extra_SaveInMemory == true) {
                int startLoc = Math.max(1, (int) (0.5 * iLoc + 0.5) - 1);
                int endLoc = iLoc;
                // Compute the R-statistic using 50% burn-in from Sequences
                double[][][] seq_x_GE = new double[endLoc - startLoc][MCMCParam_N][MCMCParam_Seq];
                int rowCount = 0;
                for (int i = startLoc; i < endLoc; i++) {
                    for (int j = 0; j < seq_x_GE[ 0].length; j++) {
                        for (int k = 0; k < seq_x_GE[ 0][ 0].length; k++) {
                            seq_x_GE[ rowCount][ j][ k] = sequences[ i][ j][ k];
                        }
                    }
                    rowCount++;
                }
                double[] rStat2 = Gelman(seq_x_GE);
                outputRstat[ teller][ 0] = Iter;
                for (int i = 1; i < outputRstat[0].length; i++) {
                    outputRstat[ teller][ i] = rStat2[ i - 1];
                }
            }
            System.out.println("Teller: " + teller);
            teller++;
        }
        /*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
         * Check these four post-processing methods. They seem to simply shrink
         * results by removing values of 0 before printing them. Is that intended? 
         !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/

        // Postprocess output from DREAM before returning arguments
        int[] zeroArray1 = new int[sequences.length];
        int countZero = 0;
        for (int i = 0; i < sequences.length; i++) {
            double sum = 0.0;
            for (int j = 0; j < sequences[ 0].length; j++) {
                sum += sequences[ i][ j][ 0];
            }
            if (sum == 0) {
                zeroArray1[ countZero] = i;
                countZero++;
            }
        }
        if (countZero > 0) {
            int[] zeroArray2 = new int[countZero];
            for (int i = 0; i < zeroArray2.length; i++) {
                zeroArray2[ i] = zeroArray1[ i];
            }
            double[][][] sequences1 = sequences;
            sequences = new double[zeroArray2[ 0] - 1][sequences[ 0].length][sequences[ 0][ 0].length];
            for (int i = 0; i < sequences.length; i++) {
                for (int j = 0; j < sequences[ 0].length; j++) {
                    for (int k = 0; k < sequences[ 0][ 0].length; k++) {
                        sequences[ i][ j][ k] = sequences1[ i][ j][ k];
                    }
                }
            }
        }

        // Postprocess output from DREAM before returning arguments
        int[] iiR = new int[outputRstat.length];
        int countR = 0;
        for (int i = 0; i < iiR.length; i++) {
            double sum = 0.0;
            for (int j = 0; j < outputRstat[ 0].length; j++) {
                sum += outputRstat[ i][ j];
            }
            if (sum == 0) {
                iiR[ countR] = i;
                countR++;
            }
        }
        if (countR > 0) {
            int[] iR = new int[countR];
            for (int i = 0; i < countZero; i++) {
                iR[ i] = iiR[ i];
            }
            double[][] output_R_stat1 = outputRstat;
            outputRstat = new double[iR[ 0] - 1][outputRstat[ 0].length];
            for (int i = 0; i < outputRstat.length; i++) {
                for (int j = 0; j < outputRstat[ 0].length; j++) {
                    outputRstat[ i][ j] = output_R_stat1[ i][ j];
                }
            }
        }
        // Postprocess output from DREAM before returning arguments
        int[] iiAR = new int[outAccRate.length];
        int countAR = 0;
        for (int i = 0; i < iiAR.length; i++) {
            double sum = 0.0;
            for (int j = 0; j < outAccRate[ 0].length; j++) {
                sum += outAccRate[ i][ j];
            }
            if (sum == 0) {
                iiAR[ countAR] = i;
                countAR++;
            }
        }
        if (countAR > 0) {
            int[] iAR = new int[countAR];
            for (int i = 0; i < iAR.length; i++) {
                iAR[ i] = iiAR[ i];
            }
            double[][] output_AR1 = outAccRate;
            outAccRate = new double[iAR[ 0] - 1][outAccRate[ 0].length];
            for (int i = 0; i < outAccRate.length; i++) {
                for (int j = 0; j < outAccRate[0].length; j++) {
                    outAccRate[ i][ j] = output_AR1[ i][ j];
                }
            }
        }

        // Postprocess output from DREAM before returning arguments
        int[] iiCR = new int[outputCR.length];
        int countCR = 0;
        for (int i = 0; i < iiCR.length; i++) {
            double sum = 0;
            for (int j = 0; j < outputCR[ 0].length; j++) {
                sum += outputCR[ i][ j];
            }
            if (sum == 0) {
                iiCR[ countCR] = i;
                countCR++;
            }
        }
        if (countCR > 0) {
            int[] iCR = new int[countCR];
            for (int i = 0; i < iCR.length; i++) {
                iCR[ i] = iiCR[ i];
            }
            double[][] output_CR1 = outputCR;
            outputCR = new double[iCR[ 0] - 1][outputCR[ 0].length];
            for (int i = 0; i < outputCR.length; i++) {
                for (int j = 0; j < outputCR[ 0].length; j++) {
                    outputCR[ i][ j] = output_CR1[ i][ j];
                }
            }
        }

        /*
         * Print Sequences.txt
         */
        FileWriter fw = null;
        try {
            fw = new FileWriter(Seq_File);
            BufferedWriter bw = new BufferedWriter(fw);
            for (int i = 0; i < sequences.length; i++) {
                for (int j = 0; j < sequences[ 0].length; j++) {
                    for (int k = 0; k < sequences[ 0][ 0].length; k++) {
                        bw.write(i + " ");
                        bw.write(j + " ");
                        bw.write(k + " ");
                        bw.write(" " + sequences[ i][ j][ k]);
                        bw.newLine();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error writing Output/Sequences.txt! " + e.getMessage());
        } finally {
            if (fw != null) {
                fw.close();
            }
        }

        /*
         * Print Rstat.txt
         */
        fw = null;
        try {
            fw = new FileWriter(Rstat_File);
            BufferedWriter bw = new BufferedWriter(fw);
            for (int i = 0; i < outputRstat.length; i++) {
                for (int j = 0; j < outputRstat[ 0].length; j++) {
                    bw.write(outputRstat[ i][ j] + " ");
                }
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing Output/Rstat.txt! " + e.getMessage());
        } finally {
            if (fw != null) {
                fw.close();
            }
        }

        return exec.getLastComponent();
    }

    public void initVariables() throws Exception {

        int maxDim = 30000;
        // Load the data from files.
        loadData();
        Measurement_N = Measurement_Data.length;
        // Set the global output variables
        Out_Area = Area;
        Out_Q0 = Q0;
        // Calculate the parameters in the exponential power density function of Box and Tiao (1973)
        double A1 = Math.exp(Num.lnGamma(3.0 * (1.0 + MCMCParam_Gamma) / 2.0));
        double A2 = Math.exp(Num.lnGamma((1.0 + MCMCParam_Gamma) / 2.0));
        MCMCParam_Cb = Math.pow((A1 / A2), 1 / (1 + MCMCParam_Gamma));
        MCMCParam_Wb = Math.sqrt(A1) / ((1 + MCMCParam_Gamma) * (Math.pow(A2, 1.5)));

        // Initialize the array that contains the history of the log_density of each chain
        hist_logp = new double[maxDim][MCMCParam_Seq + 1];

        // Initialize output information -- AR
        outAccRate = new double[maxDim][2];
        outAccRate[ 0][ 0] = MCMCParam_Seq - 1;
        outAccRate[ 0][ 1] = MCMCParam_Seq - 1;

        // Initialize output information -- Outlier chains
        outputOutlier = new double[maxDim][MCMCParam_Seq + 1];

        // Initialize output information -- R statistic
        outputRstat = new double[maxDim][MCMCParam_N + 1];

        if (Extra_pCR.equals("Update")) {
            // Calculate multinomial probabilities of each of the nCR CR values
            pCR = new double[MCMCParam_nCR];
            for (double cross : pCR) {
                cross = 1.0 / MCMCParam_nCR;
            }

            // Calculate the actual CR values based on p
            CR = GenCR(pCR);
            lCR = new double[MCMCParam_nCR];
        }

        // Initialize output information -- N_CR
        outputCR = new double[maxDim][pCR.length + 1];
        // Derive the number of elements in the output file
        int numElem = (int) ((MCMCParam_nDraw / MCMCParam_Seq) + 1.5);
        if (Extra_SaveInMemory == true) {
            // Initialize Sequences with zeros
            sequences = new double[(int) (1.25 * numElem + 0.5)][MCMCParam_N + 2][MCMCParam_Seq];
        }

        // Generate Table with JumpRates (dependent on number of dimensions and number of pairs)
        Table_JumpRate = new double[MCMCParam_N][MCMCParam_DEpairs];
        int pairCount;
        for (int i = 0; i < Table_JumpRate[0].length; i++) {
            pairCount = 1;
            for (int j = 0; j < Table_JumpRate.length; j++) {
                Table_JumpRate[ j][ i] = 2.38 / (Math.sqrt(2 * (i + 1) * pairCount));
                pairCount++;
            }
        }

        Measurement_N = Measurement_Data.length;
        sigmArray = new double[Measurement_N];
        for (int i = 0; i < sigmArray.length; i++) {
            sigmArray[i] = Measurement_Sigma;
        }
        // Initialize Iter and counter
        Iter = MCMCParam_Seq;
        Count = 1;
        iLoc = 1;
        teller = 1;
        // Change MCMCPar.steps to ensure to get nice iteration numbers in first loop
        MCMCParam_Steps--;
    }

    public double[][] GenCR(double[] pCR) throws Exception {
        double[][] CRRR = new double[MCMCParam_Seq][MCMCParam_Steps];
        Multrnd multi = new Multrnd();
        multi.numTrials = MCMCParam_Seq * MCMCParam_Steps;
        double sumPCR = 0;
        for (int i = 0; i < pCR.length; i++) {
            sumPCR += pCR[i];
        }
        for (int i = 0; i < pCR.length; i++) {
            pCR[i] /= sumPCR;
        }

        multi.probVector = pCR;
        multi.numSim = 1;
        multi.process();
        double[][] randDev = multi.multiRandDev;

        double[] LL = new double[randDev[ 0].length];
        double[] LL2 = new double[randDev[ 0].length + 1];
        LL2[ 0] = 0;
        for (int i = 0; i < LL.length; i++) {
            LL[ i] = randDev[ 0][ i];
            LL2[ i + 1] = LL[ i] + LL2[ i];
        }

        double[] randArr = new double[MCMCParam_Seq * MCMCParam_Steps];
        RandomPermutation.init(randArr, MCMCParam_Seq * MCMCParam_Steps);
        RandomPermutation.shuffle(randArr, randomstream);

        /*
         * There is a difference between lib.Matrix & oms3.dsl.cosu.Matrix. This code was intended for lib.Matrix
         */
        lib.Matrix LL3 = new lib.Matrix(randArr, 1);
        lib.Matrix CCR = new lib.Matrix(LL3.getRowDimension(), LL3.getColumnDimension());
        for (int i = 0; i < MCMCParam_nCR; i++) {
            int iStart = (int) LL2[ i] + 1;
            int iEnd = (int) LL2[ i + 1];
            int[] idx = new int[iEnd - iStart + 1];
            int iCount = 0;
            for (int j = iStart - 1; j < iEnd; j++) {
                idx[ iCount] = (int) randArr[ j];
                iCount++;
            }
            for (int j = 0; j < idx.length; j++) {
                if ((i + 1) / MCMCParam_nCR == 0) {
                    System.out.println(i + " Stop ");
                }
                CCR.set(0, idx[ j] - 1, (i + 1) / MCMCParam_nCR);
            }
        }
        int counter = 0;
        for (int i = 0; i < CRRR.length; i++) {
            for (int j = 0; j < CRRR[ 0].length; j++) {
                CRRR[ i][ j] = CCR.get(0, counter + j);
            }
            counter += MCMCParam_Steps;
        }
        return CRRR;
    }

    public double[][] GenCRok(double[] pCR) throws Exception {
        CR = new double[MCMCParam_Seq][MCMCParam_Steps];
        Multrnd multi = new Multrnd();
        multi.numTrials = MCMCParam_Seq * MCMCParam_Steps;
        multi.probVector = pCR;
        multi.numSim = 1;
        multi.process();
        double[][] randDev = multi.multiRandDev;
        double[] LL = new double[randDev[ 0].length];
        double[] LL2 = new double[randDev[ 0].length + 1];
        LL2[ 0] = 0;
        for (int i = 0; i < LL.length; i++) {
            LL[ i] = randDev[ 0][ i];
            LL2[ i + 1] = LL[ i] + LL2[ i];
        }
        double[] randArr = new double[MCMCParam_Seq * MCMCParam_Steps];
        RandomPermutation.init(randArr, MCMCParam_Seq * MCMCParam_Steps);
        RandomPermutation.shuffle(randArr, randomstream);
        lib.Matrix LL3 = new lib.Matrix(randArr, 1);
        lib.Matrix CCR = new lib.Matrix(LL3.getRowDimension(), LL3.getColumnDimension());
        for (int i = 0; i < MCMCParam_nCR; i++) {
            int iStart = (int) LL2[ i] + 1;
            int iEnd = (int) LL2[ i + 1];
            int[] idx = new int[iEnd - iStart + 1];
            int iCount = 0;
            for (int j = iStart - 1; j < iEnd; j++) {
                idx[ iCount] = (int) randArr[ j];
                iCount++;
            }
            for (int j = 0; j < idx.length; j++) {
                if ((i + 1) / MCMCParam_nCR == 0) {
                    System.out.println(i + " Stop ");
                }
                CCR.set(0, idx[ j] - 1, (i + 1) / MCMCParam_nCR);
            }
        }
        int iCount = 0;
        for (int i = 0; i < MCMCParam_Seq; i++) {
            for (int j = 0; j < MCMCParam_Steps; j++) {
                CR[ i][ j] = CCR.get(0, iCount + j);
            }
            iCount += MCMCParam_Steps;
        }
        return CR;
    }

    /*
     * Latin Hypercube sampling
     */
    public double[][] LHSU(double[] xMin, double[] xMax, int numSample) {
        double[][] rand = new double[numSample][xMin.length];
        for (int i = 0; i < rand.length; i++) {
            for (int j = 0; j < rand[0].length; j++ ) {
                rand[i][j] = Math.random();
            }
        }

        double[] randPerm;
        double[][] sampling = new double[numSample][xMin.length];
        for (int j = 0; j < sampling[ 0].length; j++) {
            randPerm = new double[numSample];
            RandomPermutation.init(randPerm, numSample);
            RandomPermutation.shuffle(randPerm, randomstream);
            for (int k = 0; k < sampling.length; k++) {
                sampling[ k][ j] = xMin[ j] + (randPerm[ k] - rand[ k][ j]) / numSample
                        * (xMax[ j] - xMin[ j]);
            }
        }
        return sampling;
    }


    /*
     * This function computes the density of each x value
     */
    public double[] computeDensity(double[][] x) throws Exception {
        // Sequential evaluation
        log_p_CD = new double[x.length];
        p_CD = new double[log_p_CD.length][2];

        /*
         * double[] ModelPred = new double[ Extra_MaxT ];
         * double[] ModPredVect = new double[ ModelPred.length ];
         * double[] ObservedVector = new double[ ModelPred.length ];
         */
        double[] objFunVal = new double[x.length];

        for (int i = 0; i < x.length; i++) {
            /*
             * if (ModelName.equals("HyMod")) {
             * HyMod test = new HyMod();
             * test.Precip = new lib.Matrix(Extra_Precip, Extra_MaxT);
             * test.ETP = new lib.Matrix(Extra_ETP, Extra_MaxT);
             * test.Cmax = x[i][0];
             * test.b = x[i][1];
             * test.alpha = x[i][2];
             * test.Rs = x[i][3];
             * test.Rq = x[i][4];
             * test.Area = Area;
             * test.timestep = 24.0;
             * test.Q0 = Q0;
             * test.process();
             * 
             * modPredMatrix = test.outHyMod;
             * 
             * for (int j = 0; j < modPredMatrix.getArray().length; j++) {
             * ModelPred[j] = modPredMatrix.get(j, 0);
             * }
             * }
             */
            //Prepare variables to be sent to Model
            Out_Cmax = x[ i][ 0];
            Out_b = x[ i][ 1];
            Out_Alpha = x[ i][ 2];
            Out_Rs = x[ i][ 3];
            Out_Rq = x[ i][ 4];
            Out_Precip = new lib.Matrix(Extra_Precip, Extra_MaxT);
            Out_ETP = new lib.Matrix(Extra_ETP, Extra_MaxT);

            /*
             * double[] err = new double[ Extra_MaxT - Warmup ];
             * int countErr = 0;
             * for( int j = Warmup; j < ModelPred.length; j++ ) {
             * ObservedVector[ countErr ] = Measurement_Data[ j - Warmup ];
             * ModPredVect[ countErr ] = ModelPred[ j - 1 ];
             * err[ j - Warmup ] = ObservedVector[ countErr ] - ModPredVect[ countErr ];
             * countErr++;
             * }
             *
             * ObjFunc obFunc = new ObjFunc();
             * ObjFunc.observed = ObservedVector;
             * obFunc.simulated = ModPredVect;
             * obFunc.process();
             * System.out.println("NSE = " + obFunc.outObFunNash + " FLS = " + obFunc.outObFunFsls + " PBIAS = " 
             * + obFunc.outObFunPBIAS + " RMSE = " + obFunc.outObFunRMSE);
             */

            Map<String, Object> paramMap = exec.getParameter();
            // new parameters
            List<Param> paramList = params.getParam();
            for (int k = 0; k < paramList.size(); k++) {
                String name = paramList.get(k).getName();
                paramMap.put(name.replace('.', '_'), x[i][k]);
            }

            Object comp = exec.execute();
            objFunVal[ i] = calcOF(comp, obs_data, sim_data);
            /*
             if( sigmArray.length == 1 ) {
             double sum = 0;
             for( int j = 0; j < err.length; j++ ) {
             sum += Math.pow( Math.abs( err[ j ] / sigmArray[ 0 ] ), 2.0 / 
             ( 1.0 + MCMCParam_Gamma ) );
             }
             log_p_CD[ i ] = Measurement_N * ( MCMCParam_Wb / MCMCParam_Cb ) - MCMCParam_Cb * sum;
             } else {
             double sum1 = 0.0;
             double sum2 = 0.0;
             for( int ll = 0; ll < err.length; ll++ ) {
             sum1 += Math.log( MCMCParam_Wb / sigmArray[ ll ] );
             sum2 += Math.pow( Math.abs( err[ ll ] / sigmArray[ ll ] ),
             2.0 / ( 1.0 + MCMCParam_Gamma ) );
             }
             log_p_CD[ i ] = sum1 - MCMCParam_Cb * sum2;
             }
             */
            p_CD[ i][ 0] = log_p_CD[ i];
            p_CD[ i][ 1] = i;
        }
        return objFunVal;
    }

    public double calcOF(Object comp, String observed, String simulated) throws Exception {
        Class c = comp.getClass();
        Observed_Data = (double[]) c.getField(observed.replace(',', '_')).get(comp);
        Simulated_Data = (double[]) c.getField(simulated.replace(',', '_')).get(comp);
        double of_val = of.getOF().calculate(Observed_Data, Simulated_Data, of.getInvalidData());
        if (Double.isNaN(of_val) || Double.isInfinite(of_val)) {
            return 10000;
        }
        return (of.getOF().positiveDirection()) ? (1 - of_val) : of_val;
    }

    /*
     * Initialize sequences
     */
    public double[][][] InitSequences(double[][] X) {
        for (int i = 0; i < MCMCParam_Seq; i++) {
            for (int j = 0; j < MCMCParam_N + 2; j++) {
                sequences[ 0][ j][ i] = X[ i][ j];
            }
        }
        return sequences;
    }

    /*
     * Extracts the current location and density of the chain
     */
    public void GetLocation() {
        // First get the current location
        oldX = new double[MCMCParam_Seq][MCMCParam_N];
        for (int i = 0; i < oldX.length; i++) {
            for (int j = 0; j < oldX[ 0].length; j++) {
                oldX[ i][ j] = X[ i][ j];
            }
        }
        // Then get the current density
        oldP = new double[MCMCParam_Seq];
        for (int i = 0; i < oldP.length; i++) {
            oldP[ i] = X[ i][ MCMCParam_N + 1];
        }
        log_p_old = new double[oldP.length];
        for (int i = 0; i < log_p_old.length; i++) {
            log_p_old[ i] = X[ i][ MCMCParam_N + 1];
        }
    }

    public void DEStrategy() {
        // Determine which sequences to evolve with what DE strategy
        // Determine probability of selecting a given number of pairs;
        double cumsum = 0.0;
        double[] cump_pair = new double[MCMCParam_DEpairs + 1];
        cump_pair[ 0] = 0;
        for (int i = 0; i < cump_pair.length - 1; i++) {
            cump_pair[ i + 1] = (1 / MCMCParam_DEpairs) + cumsum;
            cumsum += cump_pair[ i];
        }

        // Generate a random number between 0 and 1
        double[] randArr = new double[MCMCParam_Seq];
        for (int i = 0; i < randArr.length; i++) {
            randArr[i] = Math.random();
        }

        DEversion = new int[randArr.length];
        for (int i = 0; i < DEversion.length; i++) {
            int holder = 0;
            int[] tmpArr = new int[cump_pair.length];
            for (int j = 0; j < tmpArr.length; j++) {
                holder = 0;
                if (randArr[ i] > cump_pair[ j]) {
                    tmpArr[ j] = j;
                    holder = j;
                } else {
                    tmpArr[ j] = 0;
                }
            }
            DEversion[ i] = tmpArr[ holder] + 1;
        }
    }

    public double[][] ReflectBounds(double[][] newW, double[] ParRange_maxn, double[] ParRange_minn) {
        double[][] y = newW;
        for (int i = 0; i < newW.length; i++) {
            for (int j = 0; j < newW[ 0].length; j++) {
                y[ i][ j] = (y[ i][ j] < ParRange_minn[ j]) ? 2 * ParRange_minn[ j]
                        - y[ i][ j] : 2 * ParRange_maxn[ j] - y[ i][ j];
            }
        }
        for (int i = 0; i < newW.length; i++) {
            for (int j = 0; j < newW[0].length; j++) {
                y[ i][ j] = (y[ i][ j] < ParRange_minn[ j]) ? ParRange_minn[ j] + Math.random()
                        * (ParRange_maxn[ j] - ParRange_minn[ j])
                        : ParRange_minn[ j] + Math.random() * (ParRange_maxn[ j] - ParRange_minn[ j]);
            }
        }
        return y;
    }

    public void metropolis(double[][] xxxx, double[] px, double[] logpx, double[][] xold,
            double[] pxold, double[] logpxold) {

        // Metropolis rule for acceptance or rejection
        // Calculate the number of Chains
        int numChains = xxxx.length;

        // First set newgen to the old positions in X
        newGen = new double[xold.length][xold[ 0].length + 2];
        for (int i = 0; i < newGen.length; i++) {
            for (int j = 0; j < newGen[ 0].length; j++) {
                newGen[ i][ j] = xold[ i][ j];
            }
            newGen[ i][ xold[ 0].length] = pxold[ i];
            newGen[ i][ xold[ 0].length + 1] = logpxold[ i];
        }

        // And initialize accept with zeros
        accept = new double[numChains];
        double[] alpha = new double[px.length];
        for (int i = 0; i < alpha.length; i++) {
            alpha[ i] = Math.min(Math.exp(px[ i] - pxold[ i]), 1.0);
        }

        // Generate random numbers
        double[] randArr = new double[numChains];
        for (int i = 0; i < randArr.length; i++) {
            randArr[i] = Math.random();
        }

        // Find which alpha's are greater than Z
        int[] idxx = new int[px.length];
        int ccc = 0;
        for (int row = 0; row < randArr.length; row++) {
            if (alpha[ row] > randArr[ row]) {
                idxx[ ccc] = row;
                ccc++;
            }
        }
        int[] idx = new int[ccc];
        for (int c = 0; c < idx.length; c++) {
            idx[ c] = idxx[ c];
        }

        // And update these chains
        for (int i = 0; i < idx.length; i++) {
            for (int j = 0; j < xold[ 0].length; j++) {
                newGen[ idx[ i]][ j] = xxxx[ idx[ i]][ j];
            }
            newGen[ idx[ i]][ xold[ 0].length] = px[ idx[ i]];
            newGen[ idx[ i]][ xold[ 0].length + 1] = logpx[ idx[ i]];
            accept[ idx[ i]] = 1;
        }
    }

    void CalcDelta(double[] delta_normX_CD, double[] CR_vet_CD) {
        // Calculate total normalized Euclidean distance for each crossover
        // value
        // Derive sum_p2 for each different CR value
        deltaTotal = new double[MCMCParam_nCR];
        for (int zz = 0; zz < deltaTotal.length; zz++) {
            // Find which chains are updated with zz/MCMCPar.nCR
            int[] idx1 = new int[CR_vet_CD.length];
            int count = 0;
            for (int c = 0; c < idx1.length; c++) {
                if (CR_vet_CD[ c] == (zz + 1.0) / MCMCParam_nCR) {
                    idx1[ count] = c;
                    count++;
                }
            }
            int[] idx = new int[count];
            for (int c = 0; c < idx.length; c++) {
                idx[ c] = idx1[ c];
            }

            // Add the normalized squared distance tot the current delta_tot;
            double sumDeltaNormX = 0;
            for (int i = 0; i < idx.length; i++) {
                sumDeltaNormX += delta_normX_CD[ idx[ i]];
            }
            deltaTotal[ zz] += sumDeltaNormX;
        }
    }

    void AdaptpCR(double[][] CR_in, double[] delta_tot_in, double[] lCRold_in) {

        // Updates the probabilities of the various crossover values
        lCR_out_AdaptpCR = new double[lCRold_in.length];
        pCR_out_AdaptpCR = new double[pCR.length];
        double[] CR_vect = new double[CR_in.length * CR_in[ 0].length];
        for (int i = 0; i < CR_in[ 0].length; i++) {
            for (int j = 0; j < CR_in.length; j++) {
                CR_vect[ j + i * CR_in.length] = CR_in[ j][ i];
            }
        }
        // Determine lCR
        for (int i = 0; i < MCMCParam_nCR; i++) {
            // Determine how many times a particular CR value is used
            int countCR = 0;
            for (int j = 0; j < CR_vect.length; j++) {
                if (CR_vect[ j] == (double) ((i + 1.0) / MCMCParam_nCR)) {
                    countCR++;
                }
            }
            // This is used to weight delta_tot
            lCR_out_AdaptpCR[ i] = lCRold_in[ i] + countCR;
        }

        // Adapt pCR using information from averaged normalized jumping distance
        double sdeltatot = 0.0;
        for (int i = 0; i < delta_tot_in.length; i++) {
            sdeltatot += delta_tot_in[i];
        }
        double sumPCR = 0.0;
        for (int i = 0; i < pCR_out_AdaptpCR.length; i++) {
            pCR_out_AdaptpCR[ i] = MCMCParam_Seq
                    * (delta_tot_in[ i] / lCR_out_AdaptpCR[ i]) / sdeltatot;
            sumPCR += pCR_out_AdaptpCR[ i];
        }
        for (int i = 0; i < pCR_out_AdaptpCR.length; i++) {
            pCR_out_AdaptpCR[i] /= sumPCR;
        }
    }

    void RemoveOutChains(double[][] Mat_X, double[][] matrixSeq, double[][] hist_logp_inROC,
            int Iter_in_ROC, double[][] output_outlier_in_ROC) {

        // Finds outlier chains and removes them when needed
        // Determine the number of elements of L_density
        int iEnd = hist_logp_inROC.length;
        int iStart = (int) (0.5 * iEnd + 0.5);

        // Then determine the mean log density of the active chains
        double[] mean_hist_logp = new double[MCMCParam_Seq];
        int countLog = 0;
        for (int i = 1; i < mean_hist_logp.length + 1; i++) {
            double sum = 0;
            for (int j = iStart; j < iEnd; j++) {
                sum += hist_logp_inROC[ j][ i];
            }
            mean_hist_logp[ countLog] = sum / (iEnd - iStart);
            countLog++;
        }

        int[] chain_id = new int[1];
        int numID = 0;
        // Check whether any of these active chains are outlier chains
        if (MCMCParam_OutlierTest.equals("IQR_test")) {
            // Derive the upper and lower quantile of the data
            double[] pppp = {25, 75};
            double[] rrrrr = Percentile(mean_hist_logp, pppp);
            // Compute the upper range -- to detect outliers
            double UpperRange = rrrrr[ 0] - 2 * (rrrrr[ 1] - rrrrr[ 0]);

            // See whether there are any outlier chain
            int[] chain_id1 = new int[mean_hist_logp.length];
            int countChain = 0;
            for (int i = 0; i < chain_id1.length; i++) {
                if (mean_hist_logp[ i] < UpperRange) {
                    chain_id1[ countChain] = i;
                    countChain++;
                }
            }
            chain_id = new int[countChain];
            for (int i = 0; i < chain_id.length; i++) {
                chain_id[ i] = chain_id1[ i];
            }
            numID = chain_id.length;
        }
        if (numID != 0) {
            for (int i = 0; i < numID; i++) {
                // Draw random other chain -- cannot be the same as current chain
                double max_mean_hist_logp = mean_hist_logp[ 0];
                for (int j = 1; j < mean_hist_logp.length; j++) {
                    if (mean_hist_logp[ j] > max_mean_hist_logp) {
                        max_mean_hist_logp = mean_hist_logp[ j];
                    }
                }
                int[] r_idx1 = new int[hist_logp_inROC.length];
                int countChain = 0;
                for (int j = 0; j < mean_hist_logp.length; j++) {
                    if (mean_hist_logp[ j] == max_mean_hist_logp) {
                        r_idx1[ countChain] = j;
                        countChain++;
                    }
                }

                // Added -- update hist_logp -- chain will not be considered as an outlier chain then
                for (int j = 0; j < hist_logp_inROC.length; j++) {
                    hist_logp_inROC[ j][ chain_id[ i]] = hist_logp_inROC[ j][ r_idx1[ 0]];
                }

                // Jump outlier chain to r_idx -- Sequences
                for (int j = 0; j < MCMCParam_N + 2; j++) {
                    matrixSeq[ j][ chain_id[ i]] = Mat_X[ r_idx1[ 0]][ j];
                    Mat_X[ chain_id[ i]][ j] = Mat_X[ r_idx1[ 0]][ j];
                }
                output_outlier_in_ROC[ updatedOutlier][ 0] = Iter;
                output_outlier_in_ROC[ updatedOutlier][ i + 1] = chain_id[ i];
                if (output_outlier_in_ROC[ updatedOutlier][ i + 1] > 0) {
                    System.out.print("Outlier = " + output_outlier_in_ROC[ updatedOutlier][ 0]
                            + " " + output_outlier_in_ROC[ updatedOutlier][ i + 1]);
                }
                System.out.println();
                updatedOutlier++;
                // Jump outlier chain to r_idx -- X
            }
        }
    }

    double[] Gelman(double[][][] seq) {
        // Compute the dimensions of Sequences
        double[] rStat = new double[MCMCParam_N];
        double[][] meanSeq;
        if (seq.length < 10) {
            // Set the R-statistic to a large value
            for (int i = 0; i < rStat.length; i++) {
                rStat[i] = -2.0;
            }
        } else {
            // Step 1: Determine the sequence means
            double[][] meanSeq_tra = new double[seq[ 0].length][seq[ 0][ 0].length];
            double sum;
            meanSeq = new double[seq[ 0].length][seq[ 0][ 0].length];
            for (int i = 0; i < meanSeq_tra.length; i++) {
                for (int j = 0; j < meanSeq_tra[ 0].length; j++) {
                    sum = 0.0;
                    for (int k = 0; k < seq.length; k++) {
                        sum += seq[ k][ i][ j];
                    }
                    meanSeq_tra[ i][ j] = sum / seq.length;
                }
            }
            for (int i = 0; i < meanSeq.length; i++) {
                for (int j = 0; j < meanSeq[ 0].length; j++) {
                    meanSeq[ i][ j] = meanSeq_tra[ j][ i];
                }
            }

            // Step 1: Determine the variance between the sequence means
            double[] varArr = new double[seq[ 0].length];
            double[] avgArr = new double[seq[ 0].length];
            for (int i = 0; i < meanSeq[ 0].length; i++) {
                sum = 0.0;
                for (int j = 0; j < meanSeq.length; j++) {
                    sum += (meanSeq[ j][ i]);
                }
                avgArr[ i] = sum / meanSeq.length;
            }
            for (int i = 0; i < meanSeq[ 0].length; i++) {
                sum = 0.0;
                for (int j = 0; j < meanSeq.length; j++) {
                    sum += (meanSeq[ j][ i] - avgArr[ i]) * (meanSeq[ j][ i]
                            - avgArr[ i]);
                }
                varArr[ i] = (sum / (meanSeq.length - 1)) * (seq.length + 1);
            }

            // Step 2: Compute the variance of the various sequences
            double[][] seqVariance = new double[seq[ 0][ 0].length][seq[ 0].length];
            double[][] avg = new double[seq[ 0][ 0].length][seq[ 0].length];
            for (int i = 0; i < seq[0][0].length; i++) {
                for (int j = 0; j < seq[0].length; j++) {
                    sum = 0.0;
                    for (int k = 0; k < seq.length; k++) {
                        sum += seq[k][j][i];
                    }
                    avg[i][j] = sum / seq.length;
                }
            }
            for (int i = 0; i < seqVariance.length; i++) {
                for (int j = 0; j < seqVariance[ 0].length; j++) {
                    double sumSquare = 0;
                    for (int k = 0; k < seq.length; k++) {
                        sumSquare += (seq[ k][ j][ i] - avg[ i][ j])
                                * (seq[ k][ j][ i] - avg[ i][ j]);
                    }
                    seqVariance[ i][ j] = sumSquare / (seq.length - 1.0);
                }
            }

            // Step 2: Calculate the average of the within sequence variances
            double[] intraSeqVar = new double[seq[ 0].length];
            for (int i = 0; i < intraSeqVar.length; i++) {
                sum = 0.0;
                for (int j = 0; j < seq[ 0][ 0].length; j++) {
                    sum += seqVariance[ j][ i];
                }
                intraSeqVar[ i] = sum / seq[ 0][ 0].length;
            }

            // Step 4: Estimate the target variance
            for (int i = 0; i < intraSeqVar.length; i++) {
                double sigma2 = ((seq.length - 1.0) / seq.length) * intraSeqVar[ i]
                        + varArr[ i] / seq.length;
                rStat[ i] = Math.sqrt((seq[ 0][ 0].length + 1.0) / seq[ 0][ 0].length
                        * sigma2 / intraSeqVar[i] - (seq.length - 1.0) / seq[ 0][ 0].length
                        / seq.length);
            }
        }
        return rStat;
    }

    /*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     *Bubblesort is not very efficient, esp. for large sets. Investigate faster alt?
     *Merge Sort is worst case nlogn and stable
     !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/
//    public static double[] Bubblesort(double[] list) {
//        for (int pass = 1; pass < list.length; pass++) {
//            // Count how many times this next loop becomes shorter and shorter
//            for (int i = 0; i < list.length - pass; i++) {
//                if (list[ i] > list[ i + 1]) {
//                    // exchange elements
//                    double temp = list[ i];
//                    list[ i] = list[ i + 1];
//                    list[ i + 1] = temp;
//                }
//            }
//        }
//        return list;
//    }

    /*??????????????????????????????????????????????????????????????????????????
     * Need descriptive name for this. Is it altruistic differential evolution algo?
     ?????????????????????????????????????????????????????????????????????????*/
    public void offde(double[][] XXXX) {

        double[][] exponent = new double[MCMCParam_Seq][MCMCParam_N];
        double[][] deltaX = new double[MCMCParam_Seq][MCMCParam_N];
        NormalGen normGen = new NormalGen(randomstream);
        for (int i = 0; i < exponent.length; i++) {
            for (int j = 0; j < exponent[ 0].length; j++) {
                exponent[ i][ j] = 10E-6 * normGen.nextDouble();
            }
        }
        // If not a delayed rejection step --> generate proposal with DE
        if (Extra_DR == false) {
            // Determine which sequences to evolve with what DE strategy
            DEStrategy();
            int[][] tt = new int[MCMCParam_Seq - 1][MCMCParam_Seq];
            double[][] dummy = new double[MCMCParam_Seq - 1][MCMCParam_Seq];
            double[][] randDim2 = new double[MCMCParam_Seq - 1][MCMCParam_Seq];
            for (int i = 0; i < randDim2.length; i++) {
                for (int j = 0; j < randDim2[0].length; j++) {
                    randDim2[i][j] = Math.random();
                }
            }

            double[] randDim1 = new double[randDim2[ 0].length];
            for (int i = 0; i < dummy[ 0].length; i++) {
                for (int j = 0; j < randDim2.length; j++) {
                    randDim1[ j] = randDim2[ j][ i];
                }
                int[] countArr = new int[MCMCParam_Seq - 1];
                for (int j = 0; j < countArr.length; j++) {
                    countArr[ j] = j;
                }
                for (int j = 0; j < randDim2.length; j++) {
                    for (int k = j + 1; k < randDim2.length; k++) {
                        if (randDim1[ j] > randDim1[ k]) {
                            double temp = randDim1[ j];
                            randDim1[ j] = randDim1[ k];
                            randDim1[ k] = temp;
                            int temp2 = countArr[ j];
                            countArr[ j] = countArr[ k];
                            countArr[ k] = temp2;
                        }
                    }
                }
                for (int j = 0; j < dummy.length; j++) {
                    dummy[ j][ i] = randDim1[ j];
                    tt[ j][ i] = countArr[ j];
                }
            }

            // Generate uniform random numbers for each chain to determine which
            // dimension to update
            double[][] uRandNum = new double[MCMCParam_Seq][MCMCParam_N];
            for (int i = 0; i < uRandNum.length; i++) {
                for (int j = 0; j < uRandNum[0].length; j++) {
                    uRandNum[i][j] = Math.random();
                }
            }

            // Ergodicity for each individual chain
            double[][] noiseX = new double[MCMCParam_Seq][MCMCParam_N];
            for (int i = 0; i < noiseX.length; i++) {
                for (int j = 0; j < noiseX[0].length; j++) {
                    noiseX[i][j] = MCMCParam_ErgodicErr * (2.0 * Math.random() - 1.0);
                }
            }

            // Initialize the delta update to zero
            deltaX = new double[MCMCParam_Seq][MCMCParam_N];

            // Each chain evolves using information from other chains to create
            // offspring
            for (int i = 0; i < MCMCParam_Seq; i++) {
                // Define ii and remove current member as an option
                double[] ii = new double[MCMCParam_Seq];
                for (int j = 0; j < ii.length; j++) {
                    ii[j] = 1;
                }
                ii[ i] = 0;
                int[] idx = new int[MCMCParam_Seq];
                for (int j = 0; j < idx.length; j++) {
                    if (ii[ j] > 0) {
                        idx[ j] = j;
                    }
                }

                // randomly select two members of ii that have value == 1
                int[] vet_tt = new int[2 * DEversion[ i]];
                for (int j = 0; j < vet_tt.length; j++) {
                    vet_tt[ j] = tt[ j][ i];
                }
                int[] rr = new int[vet_tt.length];
                for (int j = 0; j < rr.length; j++) {
                    rr[ j] = idx[ vet_tt[ j]];
                }

                // --- WHICH DIMENSIONS TO UPDATE? DO SOMETHING WITH CROSSOVER
                int[] iTemp = new int[MCMCParam_N];
                int cont = 0;
                for (int j = 0; j < iTemp.length; j++) {
                    if (CR[ i][ genNum] < 0) {
                        System.out.println(CR[ i][ genNum]);
                    }
                    if (uRandNum[ i][ j] > 1.0 - CR[ i][ genNum]) {
                        iTemp[cont] = j;
                        cont++;
                    }
                }

                // Update at least one dimension
                int[] dimArr;
                if (cont == 0) {
                    int[] it = new int[MCMCParam_N];
                    RandomPermutation.init(it, MCMCParam_N);
                    RandomPermutation.shuffle(it, randomstream);
                    dimArr = new int[1];
                    //What happens when you subtract 1 from unintialized variable?
                    dimArr[ cont] = it[ 0] - 1;
                } else {
                    dimArr = new int[cont];
                    for (int k = 0; k < dimArr.length; k++) {
                        dimArr[ k] = iTemp[ k];
                    }
                }

                double rand = Math.random();
                double thresh = 4.0 / 5.0;
                if (rand < thresh) {
                    // Lookup Table
                    double jumpRate = Table_JumpRate[ dimArr.length - 1][ 0];
                    // Produce the difference of the pairs used for population evolution
                    int row1End = DEversion[ i];
                    int row2Start = DEversion[ i];
                    int row2End = 2 * DEversion[ i];
                    int[] rr1 = new int[row1End];
                    int[] rr2 = new int[row2End - row2Start];
                    for (int j = 0; j < row1End; j++) {
                        rr1[ j] = rr[ j];
                    }
                    int rowCount = 0;
                    for (int j = row2Start; j < row2End; j++) {
                        rr2[ rowCount] = rr[ j];
                        rowCount++;
                    }
                    double[][] X1 = new double[rr1.length][MCMCParam_N];
                    double[][] X2 = new double[rr2.length][MCMCParam_N];
                    for (int j = 0; j < X1.length; j++) {
                        for (int k = 0; k < X1[ 0].length; k++) {
                            X1[ j][ k] = XXXX[ rr1[ j]][ k];
                        }
                    }
                    for (int j = 0; j < X2.length; j++) {
                        for (int k = 0; k < X2[0].length; k++) {
                            X2[ j][ k] = XXXX[ rr2[ j]][ k];
                        }
                    }

                    double[] delta = new double[X1[ 0].length];
                    for (int j = 0; j < X1[ 0].length; j++) {
                        double sum = 0;
                        for (int k = 0; k < X1.length; k++) {
                            sum += (X1[ k][ j] - X2[ k][ j]);
                        }
                        delta[ j] = sum;
                    }

                    // Then fill update the dimension
                    for (int j = 0; j < dimArr.length; j++) {
                        deltaX[ i][ dimArr[ j]] = (1.0 + noiseX[ i][ dimArr[ j]])
                                * jumpRate * delta[ dimArr[ j]];
                    }
                } else {
                    // Set the JumpRate to 1 and overwrite CR and DEversion
                    double JumpRate = 1.0;
                    CR[ i][ genNum] = -1.0;

                    // Compute delta from one pair
                    for (int j = 0; j < deltaX[0].length; j++) {
                        deltaX[ i][ j] = JumpRate * (XXXX[ rr[ 0]][ j] - XXXX[ rr[ 1]][ j]);
                    }
                }
                double sumSquare = 0.0;
                for (int j = 0; j < deltaX[ 0].length; j++) {
                    sumSquare += (deltaX[ i][ j] * deltaX[ i][ j]);
                }
                if (sumSquare == 0.0) {
                    double[] arr1 = new double[oldX.length];
                    double[] arr2 = new double[oldX.length];
                    double[][] matrixCovar = new double[oldX.length][oldX[ 0].length];
                    double expo = Math.pow(10, -5.0);

                    for (int j = 0; j < oldX[ 0].length; j++) {
                        for (int k = 0; k < arr1.length; k++) {
                            arr1[ k] = oldX[ k][ j];
                        }
                        for (int k = 0; k < oldX[ 0].length; k++) {
                            for (int m = 0; m < arr2.length; m++) {
                                arr2[ m] = oldX[ m][ k];
                            }
                            matrixCovar[ j][ k] = (j == k) ? Descriptive.covariance(new DoubleArrayList(arr1), new DoubleArrayList(arr2))
                                    + expo : Descriptive.covariance(new DoubleArrayList(arr1),
                                    new DoubleArrayList(arr2));
                        }
                    }

                    CholeskyDecomposition choDecomp = new lib.Matrix(matrixCovar).chol();
                    NormalGen normalGen = new NormalGen(randomstream);
                    double root = Math.sqrt(MCMCParam_N);
                    double[][] R = new double[oldX.length][oldX[ 0].length];
                    for (int j = 0; j < R.length; j++) {
                        for (int k = 0; k < R[ 0].length; k++) {
                            R[j][k] = (2.38 / root) * choDecomp.getL().get(j, k);
                        }
                    }
                    for (int j = 0; j < MCMCParam_N; j++) {
                        double sum = 0.0;
                        for (int k = 0; k < R[ 0].length; k++) {
                            sum += (R[ j][ k] * normalGen.nextDouble());
                        }
                        deltaX[ i][ j] = sum;
                    }
                    System.out.println("End Cholesky decomposition");
                }
            }
        }

        double[][] newX1 = new double[oldX.length][oldX[ 0].length];
        for (int i = 0; i < newX1.length; i++) {
            for (int j = 0; j < newX1[0].length; j++) {
                newX1[ i][ j] = oldX[ i][ j] + deltaX[ i][ j] + exponent[ i][ j];
            }
        }
        newX = ReflectBounds(newX1, ParamRange_Max, ParamRange_Min);
    }

    public double[] Percentile(double[] vector, double values[]) {
        double[] result = new double[values.length];
//        double[] ordinal = Bubblesort(vector);
        java.util.Arrays.sort(vector);
        double[] ordinal = vector;

        double[] complete = new double[ordinal.length];
        for (int i = 0; i < complete.length; i++) {
            complete[ i] = 100 * (i + 0.5) / vector.length;
        }
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < complete.length; j++) {
                if (complete[ j] > values[ i]) {
                    result[ i] = ordinal[ j - 1] + ((ordinal[ j] - ordinal[ j - 1])
                            / (complete[ j] - complete[ j - 1])) * (values[ i] - complete[ j - 1]);
                }
            }
        }
        return result;
    }

    private void loadData() {
        //Load the Measurement Data
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(Meas_Data_File));
            String strLine;
            int elemCount = 0;
            ArrayList< Double> tmpList = new ArrayList< Double>();
            while ((strLine = br.readLine()) != null) {
                elemCount++;
                tmpList.add(Double.parseDouble(strLine));
            }
            Measurement_Data = new double[elemCount];
            for (int i = 0; i < Measurement_Data.length; i++) {
                Measurement_Data[ i] = (double) tmpList.get(i);
            }
        } catch (IOException e) {
            System.out.println("I/O error reading Measurement Data input file! " + e.getMessage());
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                System.out.println("I/O error closing Measurement Data input file! " + e.getMessage());
            }
        }
        //Load the ETP data
        br = null;
        try {
            br = new BufferedReader(new FileReader(ETP_File));
            String strLine;
            int elemCount = 0;
            ArrayList< Double> tmpList = new ArrayList< Double>();
            while ((strLine = br.readLine()) != null) {
                elemCount++;
                tmpList.add(Double.parseDouble(strLine));
            }
            Extra_ETP = new double[elemCount];
            for (int i = 0; i < Extra_ETP.length; i++) {
                Extra_ETP[ i] = (double) tmpList.get(i);
            }
        } catch (IOException e) {
            System.out.println("I/O error reading Evapotranspiration Data input file! " + e.getMessage());
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                System.out.println("I/O error closing Evapotranspiration Data input file! " + e.getMessage());
            }
        }
        //Load the Precipitation data
        br = null;
        try {
            br = new BufferedReader(new FileReader(Precip_File));
            String strLine;
            int elemCount = 0;
            ArrayList< Double> tmpList = new ArrayList< Double>();
            while ((strLine = br.readLine()) != null) {
                elemCount++;
                tmpList.add(Double.parseDouble(strLine));
            }
            Extra_Precip = new double[elemCount];
            for (int i = 0; i < Extra_Precip.length; i++) {
                Extra_Precip[i] = (double) tmpList.get(i);
            }
        } catch (IOException e) {
            System.out.println("I/O error reading Precipitation Data input file! " + e.getMessage());
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                System.out.println("I/O error closing Precipitation Data input file! " + e.getMessage());
            }
        }
    }

    // helper methods
    static double[][] uniform(double[] xmin, double[] xmax, int nsample) {
        double[][] s = new double[nsample][xmin.length];
        double[][] ran = new double[nsample][xmin.length];
        for (int row = 0; row < ran.length; row++) {
            for (int col = 0; col < ran[ 0].length; col++) {
                s[row][ col] = (xmax[ col] - xmin[ col]) * Math.random() + xmin[ col];
            }
        }
        return s;
    }

    private class ModelExecution {

        File folder;
        UnifiedParams parameters;
        Object comp;

        ModelExecution() throws Exception {
            folder = getOutputPath();
            folder.mkdirs();
            parameters = getModelElement().getParameter();
            Logger.getLogger("oms3.model").setLevel(Level.WARNING);
            String libPath = getModelElement().getLibpath();
            if (libPath != null) {
                System.setProperty("jna.library.path", libPath);
                if (log.isLoggable(Level.CONFIG)) {
                    log.config("Setting jna.library.path to " + libPath);
                }
            }
        }

        Object getLastComponent() {
            return comp;
        }

        Map< String, Object> getParameter() {
            return parameters.getParams();
        }

        Object execute() throws Exception {
            comp = getModelElement().newModelComponent();
            log.config("Init ...");
            ComponentAccess.callAnnotated(comp, Initialize.class, true);

            // setting the input data;
//            boolean success = ComponentAccess.setInputData(parameters, comp, log);
            boolean success = parameters.setInputData(comp, log);
            if (!success) {
                throw new RuntimeException("There are Parameter problems. Simulation exits.");
            }

            boolean adjusted = ComponentAccess.adjustOutputPath(folder, comp, log);
            for (Output e : getOut()) {
                e.setup(comp, folder, getName());
            }
            // execute phases and be done.
            log.config("Exec ...");
            ComponentAccess.callAnnotated(comp, Execute.class, false);
            log.config("Finalize ...");
            ComponentAccess.callAnnotated(comp, Finalize.class, true);
            for (Output e : getOut()) {
                e.done();
            }
            return comp;
        }
    }

}