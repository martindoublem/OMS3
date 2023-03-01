 analysis(title:"upgm_drake_2hru_gen") {

  def f = "%last/Catchment Mass Balance.csv"
  def fn = "%last/Catchment Reach.csv"
  def fnp = "%last/Catchment Nitrogen Pool.csv"
  def fcg = "%last/Catchment Crop Growth.csv"
  def fsed = "%last/Catchment Sediment.csv"
  //def fupgm = "%last/HRU UPGM.csv"
  //def fcg1 = "%last/HRU CropGrowth.csv"
  def fcg1 = "%last/HRU Crop Growth_23.csv"
  def fcg2 = "%last/HRU Crop Growth_24.csv"
  def fupgm1 = "%last/HRU UPGM_23.csv"     
  def fupgm2 = "%last/HRU UPGM_24.csv"    
   
	timeseries(title:"Runoff") {
		x(file:fn, column:"time")
		y(file:fn, column:"catchmentSimRunoff")
	}
	timeseries(title:"Precip", view:STACKED) {
		x(file:f, column:"time")
		y(file:f, column:"precip")
		y(file:f, column:"netRain")
		y(file:f, column:"netSnow")
	}
	timeseries(title:"Snow") {
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

	timeseries(title:"Biomass HRU23" , view:STACKED) {
		x(file:fcg1, column:"time")
	//	y(file:fcg1, column:"BioagAct")
		//y(file:fcg1, column:"BioNAct")
		y(file:fcg1, column:"FPHUact")
	}
	
	timeseries(title:"Biomass HRU24" , view:STACKED) { 
		x(file:fcg2, column:"time")                       
	//	y(file:fcg2, column:"BioagAct")                     
	//	y(file:fcg2, column:"BioNAct")                    
		y(file:fcg2, column:"FPHUact")                    
	} 
		                                                 
	timeseries(title:"Stress HRU23" , view:STACKED) {
		x(file:fcg1, column:"time")
		y(file:fcg1, column:"nstrs")
		y(file:fcg1, column:"wstrs")
		y(file:fcg1, column:"tstrs")
	}
	
	timeseries(title:"Stress HRU24" , view:STACKED) {  
		x(file:fcg2, column:"time")                       
		y(file:fcg2, column:"nstrs")                      
		y(file:fcg2, column:"wstrs")                      
		y(file:fcg2, column:"tstrs")                      
	}
	                                                  
	timeseries(title:"Erosion" , view:STACKED) {
		x(file:fsed, column:"time")
		y(file:fsed, column:"gensed")
		y(file:fsed, column:"outsed")
		y(file:fsed, column:"catchmentSed")
		y(file:fsed, column:"precip")
		y(file:fsed, column:"outRD1")

	}
	timeseries(title:"UPGM HRU23" , view:STACKED) {
		x(file:fupgm1, column:"time")
		y(file:fupgm1, column:"LAI")
		y(file:fupgm1, column:"BioAct")
		y(file:fupgm1, column:"root") 
		y(file:fupgm1, column:"CanHeightAct")
		y(file:fupgm1, column:"PHUact")
	  //y(file:fupgm1, column:"grainf")
  }
	
	timeseries(title:"UPGM HRU24" , view:STACKED) {  	
		x(file:fupgm2, column:"time")                   	
		y(file:fupgm2, column:"LAI")                    	
		y(file:fupgm2, column:"BioAct")                 	
		y(file:fupgm2, column:"root")                   	
		y(file:fupgm2, column:"CanHeightAct")           	
		y(file:fupgm2, column:"PHUact")                 	
	  //y(file:fupgm2, column:"grainf")               	
  }                                              	
	  
	timeseries(title:"UPGM StandFlat HRU23" , view:STACKED) {   
    x(file:fupgm1, column:"time")                        
    y(file:fupgm1, column:"standstem")                   
    y(file:fupgm1, column:"standleaf")                   
    y(file:fupgm1, column:"standstore")                  
    y(file:fupgm1, column:"flatstem")                    
    y(file:fupgm1, column:"flatleaf")                    
    y(file:fupgm1, column:"flatstore")                   
  } 

	timeseries(title:"UPGM StandFlat HRU24" , view:STACKED) {  
    x(file:fupgm2, column:"time")                             
    y(file:fupgm2, column:"standstem")                        
    y(file:fupgm2, column:"standleaf")                        
    y(file:fupgm2, column:"standstore")                       
    y(file:fupgm2, column:"flatstem")                         
    y(file:fupgm2, column:"flatleaf")                         
    y(file:fupgm2, column:"flatstore")                        
  }                                                          
  
 // timeseries (title:"UPGM Biomass", view:STACKED) {
 // x(file:fcg1, column:"time") 
 // y(file:fcg1, column:"BioAct")
 // y(file:fcg1, column:"FPHUact")
 // y(file:fcg1, column:"BioYield")
 // y(file:fcg1, column:"NYield")
 // }
  
}                                                    