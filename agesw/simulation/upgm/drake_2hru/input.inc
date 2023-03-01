parameter (file:"$oms_prj/data/upgm/drake_2hru/drake.csv")

// HRU and reach files

parameter (file:"${data}/hrus.csv")
parameter (file:"${data}/reach.csv")
parameter (file:"${data}/landuse.csv")
parameter (file:"${data}/hgeo.csv") 

// cropping and management files

parameter (file:"${data}/fert.csv")
parameter (file:"${data}/till.csv")
parameter (file:"${data}/crop.csv")
parameter (file:"${data}/crop_upgm.csv")     
parameter (file:"${data}/management.csv")

// soil properties parameter file

parameter (file:"${data}/soils_hor_swc.csv")

parameter {
	params RAWPARAM

	routingFile     "$data/topo.csv"     // watershed topology (routing) file  

  // crop rotation files
  
  rotFile         "$data/croprotation.csv"
  hruRotFile      "$data/hrus_rot.csv"

	// climate files

	dataFileTmin    "$data/tmin.csv"
	dataFileTmax    "$data/tmax.csv"
	dataFileHum     "$data/rhum.csv"
	dataFileSol     "$data/sunh.csv"
	dataFilePrecip  "$data/precip.csv"
	dataFileWind    "$data/wind.csv"

	humidity        "rel"      // "abs" or "rel"
	solar           "srad"     // "sunh" or "srad"

  // parameter over-ride file
   
  // hruOverrideFile "$data/hru_override.csv" // hru parameter over-ride file
  
}