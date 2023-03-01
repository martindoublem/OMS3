parameter {

// idset_hru is a ";"" separator list, e.g., "435;441;476" will select HRUs with IDs 435, 441, and 476
// leave blank (i.e., "") to use all HRU IDs

idSet_hru                ""

// idset_reach uses actual (negative) reach IDs, i.e., "-87;-91;-95" will select reaches with IDs -87, -91, and -95
// leave blank (i.e., "") to use all stream reach IDs

idSet_reach              ""
//idSet_reach            "-87;-91;-95"
                                          
outFile_hru              "HRU.csv"
attrSet_hru              "ID;precip;tmean;rhum;wind;rain;snow;solRad;netRad;netRainOut;netSnowOut;irrigation_amount;interception;intercStorage;throughfall;snowDepth;snowTotSWE;snowMelt;potET;actET;soilSat;soilSatMPS;soilSatLPS;soilActMPS;soilActLPS;percolation;inRD1;inRD2;inRG1;inRG2;actRG1;actRG2;outRD1;outRD2;outRG1;outRG2;balanceMPSstart;balanceMPSend;balanceLPSstart;balanceLPSend;balanceDPSstart;balanceDPSend;balanceIn;balanceOut;balance" 
attrSet_hru_w            "rain;snow;potET;actET;netRainOut;netSnowOut;throughfall;irrigation_amount;percolation;interception;intercStorage;snowTotSWE;snowMelt;soilActMPS;soilActLPS;inRD1;inRD2;inRG1;inRG2;actRG1;actRG2;outRD1;outRD2;outRG1;outRG2;balance;balanceIn;balanceMPSstart;balanceMPSend;balanceLPSstart;balanceLPSend;balanceDPSstart;balanceDPSend;balanceOut;"

outFile_hru_crop         "HRU Crop Growth.csv"
attrSet_hru_crop         "ID;zrootd;BioAct;BioNAct;FPHUact;PHUact;nstrs;wstrs;tstrs;nmin;NYield;BioYield;Addresidue_pool;Addresidue_pooln;cropid;nfert"
attrSet_hru_crop_w       "-"

outFile_hru_layer        "HRU Layer.csv"
attrSet_hru_layer        "ID;swc_h;outRD2_h;aEP_h;NO3_N;InterflowN"
attrSet_hru_w_layer      "outRD2_h;aEP_h;"

outFile_hru_n_mb         "HRU Nitrogen Mass Balance.csv"
attrSet_hru_n_mb         "ID;SurfaceN_in;SurfaceNabs;sinterflowN_in;sinterflowNabs;PercoNabs;Denit_trans;Volati_trans;actnup;SurfaceN;sinterflowN;sum_Ninput;PercoN;NitrateBalance"
attrSet_hru_n_mb_w       "-"

outFile_hru_n_pool       "HRU Nitrogen Pool.csv"
attrSet_hru_n_pool       "ID;sNO3_Pool;sNH4_Pool;sN_activ_pool;sN_stable_pool;sNResiduePool"
attrSet_hru_n_pool_w     "-"

outFile_hru_sediment     "HRU Sediment.csv"
attrSet_hru_sediment     "ID;precip;tmean;outRD1;insed;gensed;outsed;sedpool"
attrSet_hru_w_sediment   "outRD1;insed;gensed;outsed;sedpool;"

outFile_hru_crop_upgm  "HRU UPGM.csv"
//outFile_hru_crop_upgm    "-"
attrSet_hru_crop_upgm    "ID;Addresidue_pool;Addresidue_pooln;BioYield;LAI;PHUact;BioAct;BioagAct;BioOpt_delta;deltabiomass;CanHeightAct;zrootd;HarvIndex;FPHUact;wstrs;tstrs;standleaf;standstem;standstore;flatleaf;flatstem;flatstore;grainf;root"
attrSet_hru_crop_upgm_w  "-"

outFile_catch            "Catchment.csv"
attrSet_catch            "precip;tmean;rhum;wind;rain;snow;solRad;netRad;netRain;netSnow;irrigation_amount;interception;intercStorage;throughfall;snowDepth;snowTotSWE;snowMelt;potET;actET;soilSat;soilSatMPS;soilSatLPS;soilActMPS;soilActLPS;percolation;inRD1;inRD2;inRG1;inRG2;actRG1;actRG2;outRD1;outRD2;outRG1;outRG2;balanceMPSstart;balanceMPSend;balanceLPSstart;balanceLPSend;balanceDPSstart;balanceDPSend;balanceIn;balanceOut;balance" 

outFile_catch_crop       "Catchment Crop Growth.csv"
attrSet_catch_crop       "zrootd;BioAct;BioNAct;FPHUact;PHUact;nstrs;wstrs;tstrs;nmin;NYield;BioYield;Addresidue_pool;Addresidue_pooln;cropid;nfert"

outFile_catch_n_mb       "Catchment Nitrogen Mass Balance.csv"
attrSet_catch_n_mb       "SurfaceN_in;SurfaceNabs;sinterflowN_in;sinterflowNabs;PercoNabs;Denit_trans;Volati_trans;actnup;SurfaceN;sinterflowN;sum_Ninput;PercoN;NitrateBalance"

outFile_catch_n_pool     "Catchment Nitrogen Pool.csv"
attrSet_catch_n_pool     "sNO3_Pool;sNH4_Pool;sN_activ_pool;sN_stable_pool;sNResiduePool"

outFile_catch_reach      "Catchment Reach.csv"
attrSet_catch_reach      "catchmentSimRunoff;catchmentSimRunoffN;catchmentSimRunoff_NO3;catchmentSimRunoff_NO3_N;catchmentNO3_N_Load_kg_ha;catchmentRD1_w;catchmentRD2_w;catchmentRG1_w;catchmentRG2_w;catchmentNRD1_w;catchmentNRD2_w;catchmentNRG1_w;catchmentNRG2_w;DeepsinkW;DeepsinkN"

outFile_catch_sediment   "Catchment Sediment.csv"
attrSet_catch_sediment   "precip;tmean;outRD1;insed;gensed;outsed;sedpool;catchmentSed;catchmentSed_mg_l;catchmentSed_Load_kg_ha;"

outFile_catch_crop_upgm  "Catchment UPGM.csv"
// outFile_catch_crop_upgm  "-"
attrSet_catch_crop_upgm  "BioNAct"

outFile_reach            "Reach.csv"
attrSet_reach            "ID;simRunoff;actRD1;actRD2;actRG1;actRG2;outRD1;outRD2;outRG1;outRG2"
attrSet_reach_w          "-"  
}
