//analysis(title:"arti_catch") {
analysis(title:"upgm_drake_1hru_gen") {


def f = "%last/Catchment Mass Balance.csv"
def fn = "%last/Catchment Reach.csv"
def fnp = "%last/Catchment Nitrogen Pool.csv"
def fcg = "%last/Catchment Crop Growth.csv"
def fsed = "%last/Catchment Sediment.csv"
//def fcg1 = "%last/HRU Crop Growth 23.csv"
//def fcg2 = "%last/HRU Crop Growth 24.csv"
//def fupgm1 = "%last/HRU UPGM 23.csv"
//def fupgm2 = "%last/HRU UPGM 24.csv"
def fupgm = "%last/HRU UPGM.csv"

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
//for 1 HRU
timeseries(title:"Crop Growth BiomassLAI" , view:STACKED) {
x(file:fcg, column:"time")
y(file:fcg, column:"BioAct")
y(file:fcg, column:"BioYield")
y(file:fcg, column:"LAI")
y(file:fcg, column:"frLAImxAct") 
y(file:fcg, column:"NYield")    
y(file:fcg, column:"zrootd")   
y(file:fcg, column:"HarvIndex")
}

timeseries(title:"Crop Growth NHeight" , view:STACKED) {
x(file:fcg, column:"time")
y(file:fcg, column:"FPHUact")
//y(file:fcg, column:"NYield")
y(file:fcg, column:"BioNAct")
y(file:fcg, column:"nmin")
//y(file:fcg, column:"zrootd")
y(file:fcg, column:"CanHeightAct")
}            

timeseries(title:"Crop Growth HeightStress" , view:STACKED) {    
x(file:fcg, column:"time")                                         
y(file:fcg, column:"FPHUact")                                      
y(file:fcg, column:"CanHeightAct")                                 
y(file:fupgm, column:"CanHeightAct")                                      
//y(file:fupgm, column:"aczht")                                      
y(file:fcg, column:"HarvIndex")                                    
y(file:fcg, column:"nstrs")                                        
y(file:fcg, column:"wstrs")                                        
y(file:fcg, column:"tstrs")                                        
}                                                                   

timeseries(title:"UPGM StandFlat" , view:STACKED) {     
x(file:fupgm, column:"time")                              
y(file:fupgm, column:"standstem")                      
y(file:fupgm, column:"standleaf")                      
y(file:fupgm, column:"standstore")                     
y(file:fupgm, column:"flatstem")                       
y(file:fupgm, column:"flatleaf")                       
y(file:fupgm, column:"flatstore")                      
}                                                          

timeseries(title:"UPGM HeightLeaves" , view:STACKED) {  
x(file:fupgm, column:"time")                              
//y(file:fupgm, column:"aczht")                             
y(file:fupgm, column:"CanHeightAct")                             
y(file:fupgm, column:"LAI")                          
//y(file:fupgm, column:"ln")                                
y(file:fupgm, column:"PHUact")                          
y(file:fupgm, column:"grainf")                          
//y(file:fupgm, column:"pdiam")                             
}                                                          

//"Addresidue_pool;BioYield;LAI;PHUact;BioAct;BioagAct;BioOpt_delta;deltabiomass;CanHeightAct;zrootd;HarvIndex;FPHUact;tstrs;standleaf;standstem;standstore;flatleaf;flatstem;flatstore;grainf;root            

timeseries(title:"UPGM Stems" , view:STACKED) {    
x(file:fupgm, column:"time")                         
//y(file:fupgm, column:"acdayap")                      
//y(file:fupgm, column:"acdstm")                       
y(file:fupgm, column:"bg_stem_sum")                  
y(file:fupgm, column:"temp_stem")                    
//y(file:fupgm, column:"acxstmrep")                    
y(file:fupgm, column:"stem_propor")                  
}                                                     

//timeseries(title:"UPGM Roots" , view:STACKED) {  
//x(file:fupgm, column:"time")                       
//y(file:fupgm, column:"zrootd")                     
//y(file:fupgm, column:"root_store_sum")             
//y(file:fupgm, column:"root_fiber_sum")             
//y(file:fupgm, column:"temp_fiber")                 
//y(file:fupgm, column:"temp_store")                 
//y(file:fupgm, column:"temp_sai")                   
//}                                              

//timeseries(title:"Crop Growth 23 BiomassLAI" , view:STACKED) {   
//x(file:fcg1, column:"time")                                      
//y(file:fcg1, column:"BioAct")                                    
//y(file:fcg1, column:"BioYield")                                  
//y(file:fcg1, column:"NYield")                                    
//y(file:fcg1, column:"LAI")                                       
//y(file:fcg1, column:"frLAImxAct")                                
//y(file:fcg1, column:"zrootd")                                    
//y(file:fcg1, column:"HarvIndex")                                 
//}                                                                
//timeseries(title:"Crop Growth 23 HeightStress" , view:STACKED) {
//x(file:fcg1, column:"time")
//y(file:fcg1, column:"FPHUact")
//y(file:fcg1, column:"CanHeightAct")
//y(file:fupgm1, column:"canht")
//y(file:fupgm1, column:"aczht")
//y(file:fcg1, column:"HarvIndex")
//y(file:fcg1, column:"nstrs")
//y(file:fcg1, column:"wstrs")
//y(file:fcg1, column:"tstrs")
//}
//timeseries(title:"Crop Growth 24 BiomassLAI" , view:STACKED) {
//x(file:fcg2, column:"time")
//y(file:fcg2, column:"BioAct")
//y(file:fcg2, column:"BioYield")
//y(file:fcg2, column:"NYield")
//y(file:fcg2, column:"LAI")
//y(file:fcg2, column:"frLAImxAct")
//y(file:fcg2, column:"zrootd")
//y(file:fcg2, column:"HarvIndex")
//}
//timeseries(title:"Crop Growth 24 HeightStress" , view:STACKED) {
//x(file:fcg2, column:"time")
//y(file:fcg2, column:"FPHUact")
//y(file:fcg1, column:"CanHeightAct")
//y(file:fupgm2, column:"canht")
//y(file:fupgm2, column:"aczht")
//y(file:fcg2, column:"HarvIndex")
//y(file:fcg2, column:"nstrs")
//y(file:fcg2, column:"wstrs")
//y(file:fcg2, column:"tstrs")
//}
//timeseries(title:"UPGM 23 StandFlat" , view:STACKED) {
//x(file:fupgm1, column:"time")
//y(file:fupgm1, column:"acmstandstem")
//y(file:fupgm1, column:"acmstandleaf")
//y(file:fupgm1, column:"acmstandstore")
//y(file:fupgm1, column:"acmflatstem")
//y(file:fupgm1, column:"acmflatleaf")
//y(file:fupgm1, column:"acmflatstore")
//}
//timeseries(title:"UPGM 23 HeightLeaves" , view:STACKED) {
//x(file:fupgm1, column:"time")
//y(file:fupgm1, column:"aczht")
//y(file:fupgm1, column:"canht")
//y(file:fupgm1, column:"trad_lai")
//y(file:fupgm1, column:"ln")
//y(file:fupgm1, column:"acthucum")
//y(file:fupgm1, column:"acgrainf")
//y(file:fupgm1, column:"pdiam")
//}
//timeseries(title:"UPGM 23 Stems" , view:STACKED) {
//x(file:fupgm1, column:"time")
//y(file:fupgm1, column:"acdayap")
//y(file:fupgm1, column:"acdstm")
//y(file:fupgm1, column:"bg_stem_sum")
//y(file:fupgm1, column:"temp_stem")
//y(file:fupgm1, column:"acxstmrep")
//y(file:fupgm1, column:"stem_propor")
//}
//timeseries(title:"UPGM 23 Roots" , view:STACKED) {
//x(file:fupgm1, column:"time")
//y(file:fupgm1, column:"aczrtd")
//y(file:fupgm1, column:"root_store_sum")
//y(file:fupgm1, column:"root_fiber_sum")
//y(file:fupgm1, column:"temp_fiber")
//y(file:fupgm1, column:"temp_store")
//y(file:fupgm1, column:"temp_sai")
//}
//timeseries(title:"UPGM 24 StandFlat" , view:STACKED) {
//x(file:fupgm2, column:"time")
//y(file:fupgm2, column:"acmstandstem")
//y(file:fupgm2, column:"acmstandleaf")
//y(file:fupgm2, column:"acmstandstore")
//y(file:fupgm2, column:"acmflatstem")
//y(file:fupgm2, column:"acmflatleaf")
//y(file:fupgm2, column:"acmflatstore")
//}
//timeseries(title:"UPGM 24 HeightLeaves" , view:STACKED) {
//x(file:fupgm2, column:"time")
//y(file:fupgm2, column:"aczht")
//y(file:fupgm2, column:"canht")
//y(file:fupgm2, column:"trad_lai")
//y(file:fupgm2, column:"ln")
//y(file:fupgm2, column:"acthucum")
//y(file:fupgm2, column:"acgrainf")
//y(file:fupgm2, column:"pdiam")
//}
//timeseries(title:"UPGM_24_Stems" , view:STACKED) {
//x(file:fupgm2, column:"time")
//y(file:fupgm2, column:"acdayap")
//y(file:fupgm2, column:"acdstm")
//y(file:fupgm2, column:"bg_stem_sum")
//y(file:fupgm2, column:"temp_stem")
//y(file:fupgm2, column:"acxstmrep")
//y(file:fupgm2, column:"stem_propor")
//}
//timeseries(title:"UPGM 24 Roots" , view:STACKED) {
//x(file:fupgm2, column:"time")
//y(file:fupgm2, column:"aczrtd")
//y(file:fupgm2, column:"root_store_sum")
//y(file:fupgm2, column:"root_fiber_sum")
//y(file:fupgm2, column:"temp_fiber")
//y(file:fupgm2, column:"temp_store")
//y(file:fupgm2, column:"temp_sai")
//}

}