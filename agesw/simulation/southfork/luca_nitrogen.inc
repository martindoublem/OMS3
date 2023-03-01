calibration_start "2010-01-01"         // calibration start date
start_month_of_year 1
rounds 1                               // calibration rounds, default 1
//model_stdout false
summary_file "southfork_lucaNdata.txt"
trace_file "trace_of_N.txt"

// step definitions

step()

{  

parameter {

//denitfac(lower:0.6, upper:1.0, calib_strategy:INDIVIDUAL)
//Beta_min(lower:0.001, upper:0.003, calib_strategy:INDIVIDUAL)
//Beta_trans(lower:1.0E-6, upper:1.0E-4, calib_strategy:INDIVIDUAL)
//Beta_Ndist(lower:1, upper:55, calib_strategy:INDIVIDUAL)
//Beta_NO3(lower:0.01, upper:1.0, calib_strategy:INDIVIDUAL)
//Beta_rsd(lower:0.02, upper:0.1, calib_strategy:INDIVIDUAL)
//deposition_factor(lower:0, upper:0.05, calib_strategy:INDIVIDUAL)
N_delay_RG1(lower:0, upper:5, calib_strategy:INDIVIDUAL)
N_delay_RG2(lower:0, upper:5, calib_strategy:INDIVIDUAL)
N_concRG1(lower:0, upper:2, calib_strategy:INDIVIDUAL)
N_concRG2(lower:0, upper:5, calib_strategy:INDIVIDUAL)
theta_nit(lower:0.001, upper:0.6, calib_strategy:INDIVIDUAL)
soilSatLPS_factor(lower:0.001, upper:0.5, calib_strategy:INDIVIDUAL)
no3_cons_tile(lower:1.0E-6, upper:1.0E-3, calib_strategy:INDIVIDUAL)
}

objfunc(method:NS, timestep:DAILY) {
    sim(file:"Catchment_Reach.csv", table:"output", column:"catchmentSimRunoff_NO3_N")
    obs(file:"$oms_prj/data/southfork/obs_data.csv", table:"obs",column:"no3_n[2]")
}
}
