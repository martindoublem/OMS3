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

theta_nit(lower:0.001, upper:0.9, calib_strategy:INDIVIDUAL)
}

objfunc(method:NS, timestep:DAILY) {
    sim(file:"Catchment_Reach.csv", table:"output", column:"catchmentSimRunoff_NO3_N")
    obs(file:"$oms_prj/data/southfork/obs_data.csv", table:"obs",column:"no3_n[1]")
}
}


