// main parameter file

parameter (file:"${data_param}.csv")

// spatial input files

parameter (file:"${data}/hrus.csv")
parameter (file:"${data}/reach.csv")
parameter (file:"${data}/landuse.csv")
parameter (file:"${data}/hgeo.csv")

// crop and management files

parameter (file:"${data}/fert.csv")
parameter (file:"${data}/till.csv")
parameter (file:"${data}/crop.csv")
parameter (file:"${data}/management.csv")

// soils input file 

parameter (file:"${data}/soils_hor.csv")

parameter {

	params       RAWPARAM

  rotFile      "$data/croprotation.csv"
  hruRotFile   "$data/hrus_rot.csv"
  routingFile  "$data/routing.csv"    

	// climate

  dataFileTmin     "$data/tmin.csv"
  dataFileTmax     "$data/tmax.csv"
  dataFileHum      "$data/rhum.csv"
  dataFileSol      "$data/solrad.csv"
  dataFilePrecip   "$data/rain.csv"
  dataFileWind     "$data/wind.csv"

  humidity   "abs"    // "abs" or "rel"
  solar      "sunh"   // "sunh" or "srad"

 // parameter over-ride file
   
 // hruOverrideFile "$data/hru_override.csv" // hru parameter over-ride file
  
}