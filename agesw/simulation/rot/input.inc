parameter (file:"${data}.csv")            // main parameter file

// HRU and reach files 

parameter (file:"${data}/hrus.csv")       // hru parameter file
parameter (file:"${data}/reach.csv")      // stream reach parameter file

parameter (file:"${data}/landuse.csv")    // landuse parameter file
parameter (file:"${data}/hgeo.csv")       // hydrogeology parameter file

// cropping and management files

parameter (file:"${data}/fert.csv")       // fertilizer operations file
parameter (file:"${data}/till.csv")       // tillage operations file
parameter (file:"${data}/crop.csv")       // crop parameter file
parameter (file:"${data}/management.csv") // management operations file

parameter (file:"${data}/soils_hor.csv")  // soil properties parameter file

parameter {
   params RAWPARAM
   
   routingFile "$data/routing.csv"        // watershed topology (routing) file 

   // crop rotation files
   
   rotFile     "$data/croprotation.csv"   // crop rotation parameter file
   hruRotFile  "$data/hrus_rot.csv"       // hru rotation parameter file
   
   // climate files

   dataFilePrecip "$data/precip.csv"      // precipitation climate file
   dataFileTmin   "$data/tmin.csv"        // minimum temperature climate file
   dataFileTmax   "$data/tmax.csv"        // maximum temperature climate file
   dataFileHum    "$data/ahum.csv"        // humidity (absolute or relative) climate file
   dataFileSol    "$data/sunh.csv"        // radiation (solar or sunlight hours) climate file
   dataFileWind   "$data/wind.csv"        // wind speed climate file

   humidity   "abs"   // absolute ("abs") or relative ("rel") humidity
   solar      "sunh"  // sunlight hours ("sunh") or solar radiation ("srad")

   // parameter over-ride file
   
   // hruOverrideFile "$data/hru_override.csv" // hru parameter over-ride file
   
}