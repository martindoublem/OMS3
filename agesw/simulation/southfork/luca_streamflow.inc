calibration_start "2010-01-01"         // calibration start date
start_month_of_year 1
rounds 1                               // calibration rounds, default 1
//model_stdout false
summary_file "southfork_lucaNS_data.txt"
trace_file "trace_of_NS.txt"  

// step definitions

step()

{  

parameter {

//flowRouteTA(lower:0.1, upper:3, calib_strategy:INDIVIDUAL)
//FCAdaptation(lower:0.8, upper:3, calib_strategy:INDIVIDUAL)
//ACAdaptation(lower:0.8, upper:3, calib_strategy:INDIVIDUAL)

//snowCritDens(lower:0.4, upper:0.5, calib_strategy:INDIVIDUAL)
//snow_trans(lower:0, upper:5, calib_strategy:INDIVIDUAL)
//snow_trs(lower:-4, upper:4, calib_strategy:INDIVIDUAL)
//baseTemp(lower:-4, upper:4, calib_strategy:INDIVIDUAL)
//t_factor(lower:0.1, upper:5, calib_strategy:INDIVIDUAL)
//r_factor(lower:0.1, upper:5, calib_strategy:INDIVIDUAL)
//g_factor(lower:0.1, upper:5, calib_strategy:INDIVIDUAL)

//soilPolRed(lower:45, upper:60, calib_strategy:INDIVIDUAL)
//soilMaxPerc(lower:0.1, upper:20, calib_strategy:INDIVIDUAL)
//geoMaxPerc(lower:0.1, upper:10, calib_strategy:INDIVIDUAL)
//soilMaxDPS(lower:0, upper:5, calib_strategy:INDIVIDUAL)
//soilMaxInfWinter(lower:0.5, upper:3, calib_strategy:INDIVIDUAL)
//soilMaxInfSnow(lower:0, upper:0.09, calib_strategy:INDIVIDUAL)
//soilMaxInfSummer(lower:0.5, upper:3, calib_strategy:INDIVIDUAL)
//soilOutLPS(lower:0.1, upper:5, calib_strategy:INDIVIDUAL)
//soilLatVertLPS(lower:0.1, upper:5, calib_strategy:INDIVIDUAL)
//soilDistMPSLPS(lower:0.1, upper:10, calib_strategy:INDIVIDUAL)
//soilDiffMPSLPS(lower:0.1, upper:10, calib_strategy:INDIVIDUAL)
//soilConcRD1(lower:0.1, upper:5, calib_strategy:INDIVIDUAL)
//soilConcRD2 (lower:0.1, upper:5, calib_strategy:INDIVIDUAL)
//infil_conc_factor(lower:0.1, upper:1.0, calib_strategy:INDIVIDUAL)

//gwCapRise(lower:0, upper:0.5, calib_strategy:INDIVIDUAL)

//gwRG1Fact(lower:1.5, upper:5, calib_strategy:INDIVIDUAL)
//gwRG2Fact(lower:2, upper:15, calib_strategy:INDIVIDUAL)
//gwRG1RG2dist(lower:0, upper:10, calib_strategy:INDIVIDUAL)
//kf_ratio(lower:0, upper:120, calib_strategy:INDIVIDUAL)
musi_co1(lower:0.0, upper:2.0, calib_strategy:INDIVIDUAL)
musi_co2(lower:0.3, upper:1.0, calib_strategy:INDIVIDUAL)
musi_co3(lower:0.3, upper:1.0, calib_strategy:INDIVIDUAL)
musi_co4(lower:0.0, upper:1.0, calib_strategy:INDIVIDUAL)
}

objfunc(method:NS, timestep:DAILY) {
//sim(file:"Catchment_Reach.csv", table:"output", column:"catchmentSimRunoff")
//obs(file:"E:\\Holm\\agesw_aug\\data\\southfork\\obs_data.csv", table:"obs",column:"orun[1]")
sim(file:"Catchment_Reach.csv", table:"output", column:"catchmentSed_Load_kg_ha")
obs(file:"E:\\Holm\\agesw_aug\\data\\southfork\\obs_data00_12.csv", table:"obs",column:"sedim_load[2]")
}
}
