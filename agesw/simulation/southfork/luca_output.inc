parameter {

// idset_hru is a ";"" separator list, e.g., "435;441;476" will select HRUs with IDs 435, 441, and 476
// leave blank (i.e., "") to use all HRU IDs

idset_hru         ""
//idSet_hru       "435;441;476"

// idset_reach uses actual (negative) reach IDs, i.e., "-87;-91;-95" will select reaches with IDs -87, -91, and -95
// leave blank (i.e., "") to use all stream reach IDs

idSet_reach       ""
//idSet_reach     "-87;-91;-95"


//outFile_n         "-"
outFile_n         "Catchment_Reach.csv"
attrSet_n         "catchmentSimRunoff;catchmentSed_Load_kg_ha;"   

}
