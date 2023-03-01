calibration_start "2006-04-01"     // calibration start date
rounds 1                           // calibration rounds, default 1
summary_file "luca_data.txt"
trace_file "luca_upccw_tracer.txt"

// step definitions

step{
  parameter {
    soilMaxDPS(lower:50, upper:80, calib_strategy:MEAN)
    soilMaxInfSummer(lower:120, upper:220, calib_strategy:MEAN)
    soilMaxInfWinter(lower:100, upper:200, calib_strategy:MEAN)
    soilMaxInfSnow(lower:20, upper:100, calib_strategy:MEAN)
    soilPolRed(lower:15, upper:30, calib_strategy:MEAN)
    soilMaxPerc(lower:2, upper:6, calib_strategy:MEAN)
    soilOutLPS(lower:1, upper:1.9, calib_strategy:MEAN)
    soilLatVertLPS(lower:3, upper:9, calib_strategy:MEAN)
    infil_conc_factor(lower:0.5, upper:1, calib_strategy:MEAN)
    FCAdaptation(lower:0.5, upper:0.7, calib_strategy:MEAN)
    ACAdaptation(lower:1.3, upper:1.5, calib_strategy:MEAN)
    flowRouteTA(lower:1, upper:2, calib_strategy:MEAN)
    baseTemp(lower:1, upper:3, calib_strategy:MEAN)
    t_factor(lower:0.4, upper:2, calib_strategy:MEAN)
    r_factor(lower:0.1, upper:2, calib_strategy:MEAN)
    snow_trs(lower:1, upper:7, calib_strategy:MEAN)
    snow_trans(lower:2, upper:6, calib_strategy:MEAN)
    snowCritDens(lower:0.1, upper:1, calib_strategy:MEAN)
    }

  objfunc(method:NS, timestep:DAILY) {
    sim(file:"Catchment_HRU.csv", table:"output", column:"catchmentSimRunoff")
    obs(file:"$oms_prj/data/cedarcreek/upper/orun.csv", table:"Observed",column:"orun[1]")
  }
}
 