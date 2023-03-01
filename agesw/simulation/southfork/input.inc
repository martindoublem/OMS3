parameter (file:"${data}.csv")              // main parameter file

// HRU and reach files 

parameter (file:"${data}/hrus.csv")         // hru parameter file
parameter (file:"${data}/reach.csv")        // stream reach parameter file

parameter (file:"${data}/landuse.csv")      // landuse parameter file
parameter (file:"${data}/hgeo.csv")         // hydrogeology parameter file

// cropping and management files

parameter (file:"${data}/fert.csv")         // fertilizer operations file
parameter (file:"${data}/till.csv")         // tillage operations file
parameter (file:"${data}/crop.csv")         // crop parameter file
parameter (file:"${data}/management_00_12.csv")   // management operations file

//parameter (file:"${data}/irrigation.csv") //irrigation file
parameter (file:"${data}/soils_hor.csv")    // soil properties parameter file

parameter {
   params RAWPARAM
   
   routingFile "$data/routing.csv"          // watershed topology (routing) file
   //manIrriFile "$data/man_irri.csv"       // linkage between irrigation ID (IID) to management ID (MID)

   // crop rotation files
   
   rotFile     "$data/croprotation.csv"     // crop rotation parameter file
   hruRotFile  "$data/hrus_rot_00_12.csv"         // hru rotation parameter file
   
   // climate files

   dataFilePrecip "$data/precip.csv"        // precipitation climate file
   dataFileTmin   "$data/tmin.csv"          // minimum temperature climate file
   dataFileTmax   "$data/tmax.csv"          // maximum temperature climate file
   dataFileHum    "$data/rhum.csv"          // humidity (absolute or relative) climate file
   dataFileSol    "$data/solrad.csv"        // radiation (solar or sunlight hours) climate file
   dataFileWind   "$data/wind.csv"          // wind speed climate file

   humidity   "rel"                         // absolute ("abs") or relative ("rel") humidity
   solar      "srad"                        // sunlight hours ("sunh") or solar radiation ("srad")
} 
