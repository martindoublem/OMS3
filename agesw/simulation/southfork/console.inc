 analysis(title:"southfork") {

  def f = "%last/Catchment.csv"    
  def fn = "%last/Catchment Reach.csv"
  def fnp = "%last/Catchment Nitrogen Pool.csv"
  def fcg = "%last/Catchment Crop Growth.csv"
  def fsed = "%last/Catchment Sediment.csv"
  def orun = "${data}/obs_data00_12.csv"

	timeseries(title:"Runoff m3/s", view:COMBINED) {
		x(file:fn, column:"time")
		y(file:fn, column:"catchmentSimRunoff")
        y(file:orun, column:"orun[1]")
	}
	timeseries(title:"Precip mm/m2", view:STACKED) {
		x(file:f, column:"time")
		y(file:f, column:"precip")
		y(file:f, column:"netRain")
		y(file:f, column:"netSnow")
	}
	timeseries(title:"NO3-N mg/l", view:COMBINED) {
		x(file:f, column:"time")
		y(file:fn, column:"catchmentSimRunoff_NO3_N")
		y(file:orun, column:"no3_n[2]")
		y(file:orun, column:"no3_n[3]")
	}
	timeseries(title:"NO3-N-Load kg/ha", view:COMBINED) {
		x(file:f, column:"time")
		y(file:fn, column:"catchmentNO3_N_Load_kg_ha")
		y(file:orun, column:"no3_n_load[4]")
		y(file:orun, column:"no3_n_load_ntrax[4]")
	}	
	timeseries(title:"Snow cm") {
		x(file:f, column:"time")
		y(file:f, column:"snowTotSWE")
		y(file:f, column:"snowMelt")
		y(file:f, column:"snowDepth")
	}
	timeseries(title:"ET", view:STACKED) {
		x(file:f, column:"time")
		y(file:f, column:"potET")
		y(file:f, column:"actET")
		y(file:f, column:"rhum")
		y(file:f, column:"tmean")
	}
	timeseries(title:"Interception") {
		x(file:f, column:"time")
		y(file:f, column:"interception")
		y(file:f, column:"throughfall")
		y(file:f, column:"intercStorage")
	}
	timeseries(title:"Rad", view:STACKED) {
		x(file:f, column:"time")
		y(file:f, column:"solRad")
		y(file:f, column:"netRad")
	}
	timeseries(title:"Soil" , view:STACKED) {
		x(file:f, column:"time")
		y(file:f, column:"soilSatMPS")
		y(file:f, column:"soilSatLPS")
		y(file:f, column:"soilActMPS")
		y(file:f, column:"soilActLPS")
		y(file:f, column:"soilSat")
		y(file:f, column:"precip")
	}
	timeseries(title:"RD/RG", view:STACKED) {
		x(file:fn, column:"time")
		y(file:fn, column:"catchmentRD1_w")
		y(file:fn, column:"catchmentRD2_w")
		y(file:fn, column:"catchmentRG1_w")
		y(file:fn, column:"catchmentRG2_w")
		y(file:f, column:"percolation")
	}
	timeseries(title:"act RD/RG", view:STACKED) {
		x(file:f, column:"time")
		y(file:f, column:"actRG1")
		y(file:f, column:"actRG2")
	}

	timeseries(title:"N Pools" , view:STACKED) {
		x(file:fnp, column:"time")
		y(file:fnp, column:"sNO3_Pool")
		y(file:fnp, column:"sNH4_Pool")
		y(file:fnp, column:"sNResiduePool")
	}
	timeseries(title:"Biomass" , view:STACKED) {
		x(file:fcg, column:"time")
		y(file:fcg, column:"BioAct")
		y(file:fcg, column:"BioNAct")
		y(file:fcg, column:"FPHUact")
	}
	timeseries(title:"Stress" , view:STACKED) {
		x(file:fcg, column:"time")
		y(file:fcg, column:"nstrs")
		y(file:fcg, column:"wstrs")
		y(file:fcg, column:"tstrs")
	}
	timeseries(title:"Erosion" , view:STACKED) {
		x(file:fsed, column:"time")
		y(file:fsed, column:"gensed")
		y(file:fsed, column:"outsed")
		y(file:fsed, column:"catchmentSed")
		y(file:fsed, column:"precip")
		y(file:fsed, column:"outRD1")
	}
	timeseries(title:"Erosion obs-sim" , view:COMBINED) {
		x(file:fsed, column:"time")
		y(file:fsed, column:"catchmentSed_mg_l")
		y(file:orun, column:"sedim[1]")                
	}
  timeseries(title:"Erosion obs-sim Load kg_ha" , view:COMBINED) {
		x(file:fsed, column:"time")
		y(file:fsed, column:"catchmentSed_Load_kg_ha")
		y(file:orun, column:"sedim_load[2]")                
	}
}
