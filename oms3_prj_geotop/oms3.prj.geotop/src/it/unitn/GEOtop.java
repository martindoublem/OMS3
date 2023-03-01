/*
 * $Id$
 * 
 * This file is part of the Object Modeling System (OMS),
 * 2007-2012, Olaf David and others, Colorado State University.
 *
 * OMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 2.1.
 *
 * OMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OMS.  If not, see <http://www.gnu.org/licenses/lgpl.txt>.
 */
package it.unitn;

import au.com.bytecode.opencsv.CSVWriter;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import oms3.Conversions;
import oms3.annotations.*;
import oms3.util.ProcessComponent;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import utils.BinUtils;
import utils.Processes;

/**
 * GeoTop component.
 *
 * @author od, gf
 */
public class GEOtop {

    // in
    @Description("The rain corrector factor.")
    @Role(Role.PARAMETER)
    @In
    public Double rainCorrFactor = null;
    @Description("The rain corrector factor.")
    @Role(Role.PARAMETER)
    @In
    public Boolean createInputMaps = null;
    @Description("The path to the point output file")
    @Role(Role.PARAMETER)
    @In
    public String pointOutputFile = null;
    @Description("folder structure that contains the input data")
    @Role(Role.PARAMETER)
    @In
    public String dataFolder;
    @Description("Runtime and output")
    @Role(Role.PARAMETER + Role.OUTPUT)
    @In
    public File runFolder;
    //*******************************
    //******* CONFIGURATION *********
    //*******************************
    @Description("Time step of the energy and water balance expressed in Seconds")
    @Role(Role.PARAMETER)
    @In
    public Integer timeStepEnergyAndWater;
    @Description("The name of the folder containing input maps")
    @Role(Role.PARAMETER)
    @In
    public String inputMapFolder;
    @Description("The simulation start date in the DD/MM/YYYY hh:mm format")
    @Role(Role.PARAMETER)
    @In
    public String initDateDDMMYYYYhhmm;
    @Description("The simulation end date in the DD/MM/YYYY hh:mm format")
    @Role(Role.PARAMETER)
    @In
    public String endDateDDMMYYYYhhmm;
    @Description("The Energy budget flag: 0 turned off and 1 turned on")
    @Role(Role.PARAMETER)
    @In
    public Integer energyBalance;
    @Description("The water budget flag: 0 turned off and 1 turned on")
    @Role(Role.PARAMETER)
    @In
    public Integer waterBalance;
    @Description("Point Simulation: 0 means 3D simulation, 1 means 1D simulation")
    @Role(Role.PARAMETER)
    @In
    public Double pointSim;
    //*******************************
    //********* GEOGRAPHY ***********
    //*******************************
    @Description("Geography: latitude of the centroid of the study area expressed in degree")
    @Role(Role.PARAMETER)
    @In
    public Double latitude;
    @Description("Geography: longitude of the centroid of the study area expressed in degree")
    @Role(Role.PARAMETER)
    @In
    public Double longitude;
    //*******************************
    //****** METEO STATIONS *********
    //*******************************
    @Description("Number of meteo stations")
    @Role(Role.PARAMETER)
    @In
    public Integer numberOfMeteoStations;
    @Description("Vector of the the meteo stations X coordinates.")
    @Role(Role.PARAMETER)
    @In
    public double[] meteoStationCoordinateX;
    @Description("Vector of the the meteo stations Y coordinates: the order of the values in the vector correspond to the order in the meteo file name.")
    @Role(Role.PARAMETER)
    @In
    public double[] meteoStationCoordinateY;
    @Description("Vector of the the meteo stations elevations: the order of the values in the vector correspond to the order in the meteo file name.")
    @Role(Role.PARAMETER)
    @In
    public double[] meteoStationSkyViewFactor;
    @Description("Vector of the the meteo stations skyview factor: the order of the values in the vector correspond to the order in the meteo file name.")
    @Role(Role.PARAMETER)
    @In
    public double[] meteoStationElevation;
    @Description("Vector of the the meteo stations latitudes: the order of the values in the vector correspond to the order in the meteo file name.")
    @Role(Role.PARAMETER)
    @In
    public double[] meteoStationLatitude;
    @Description("Vector of the the meteo stations longitudes: the order of the values in the vector correspond to the order in the meteo file name.")
    @Role(Role.PARAMETER)
    @In
    public double[] meteoStationLongitude;
    @Description("Vector of the meteo stations standard times: the order of the values in the vector correspond to the order in the meteo file name.")
    @Role(Role.PARAMETER)
    @In
    public double[] meteoStationStandardTime;
    @Description("Vector of the meteo stations wind velocity sensor height: the order of the values in the vector correspond to the order in the meteo file name.")
    @Role(Role.PARAMETER)
    @In
    public double[] meteoStationWindVelocitySensorHeight;
    @Description("Vector of the meteo stations air temperature sensor height: the order of the values in the vector correspond to the order in the meteo file name.")
    @Role(Role.PARAMETER)
    @In
    public double[] meteoStationTemperatureSensorHeight;
    @Description("Path to the meteo stations measuremts folder.")
    @Role(Role.PARAMETER)
    @In
    public String meteoFile;
    @Description("Output Soil Maps.")
    @Role(Role.PARAMETER)
    @In
    public Double outputSoilMaps;
    @Description("freeDrainageAtBottom.")
    @Role(Role.PARAMETER)
    @In
    public Integer freeDrainageAtBottom;
    @Description("freeDrainageAtLateralBorder.")
    @Role(Role.PARAMETER)
    @In
    public Integer freeDrainageAtLateralBorder;
    @Description("channelDepression.")
    @Role(Role.PARAMETER)
    @In
    public Double channelDepression;
    @Description("normalHydrConductivityBedrock.")
    @Role(Role.PARAMETER)
    @In
    public double[] normalHydrConductivityBedrock;
    @Description("nVanGenuchtenBedrock.")
    @Role(Role.PARAMETER)
    @In
    public double[] nVanGenuchtenBedrock;
    @Description("alphaVanGenuchtenBedrock.")
    @Role(Role.PARAMETER)
    @In
    public double[] alphaVanGenuchtenBedrock;
    @Description("thetaResBedrock.")
    @Role(Role.PARAMETER)
    @In
    public double[] thetaResBedrock;
    @Description("thetaSatBedrock.")
    @Role(Role.PARAMETER)
    @In
    public double[] thetaSatBedrock;
    @Description("lateralHydrConductivityBedrock.")
    @Role(Role.PARAMETER)
    @In
    public double[] lateralHydrConductivityBedrock;
    //!*******************************
    //!#######  HEADER ##########
    //!*******************************
    @Description("Header of the Date DDMMYYYYhhmm of the Meteo file.")
    @Role(Role.PARAMETER)
    @In
    public String headerDateDDMMYYYYhhmmMeteo;
    @Description("Header of the precipitation in the Meteo file.")
    @Role(Role.PARAMETER)
    @In
    public String headerIPrec;
    @Description("Header of the wind velocity in the Meteo file.")
    @Role(Role.PARAMETER)
    @In
    public String headerWindVelocity;
    @Description("Header of the wind direction in the Meteo file.")
    @Role(Role.PARAMETER)
    @In
    public String headerWindDirection;
    @Description("Header of the relative humidity in the Meteo file.")
    @Role(Role.PARAMETER)
    @In
    public String headerRH;
    @Description("Header of the air temperature in the Meteo file.")
    @Role(Role.PARAMETER)
    @In
    public String headerAirTemp;
    @Description("Header of the SW global radiation in the Meteo file.")
    @Role(Role.PARAMETER)
    @In
    public String headerSWglobal;
    @Description("Header of the cloud SW transmissivity in the Meteo file.")
    @Role(Role.PARAMETER)
    @In
    public String headerCloudSWTransmissivity;
    //!*******************************
    //!******* SIM. POINTS ***********
    //!*******************************
    @Description("The path to the points file where the output are printed out.")
    @Role(Role.PARAMETER)
    @In
    public String pointFile;
    @Description("Header of the point ID in the Point file.")
    @Role(Role.PARAMETER)
    @In
    public String headerPointID;
    @Description("Header of the point X coordinate in the Point file.")
    @Role(Role.PARAMETER)
    @In
    public String headerCoordinatePointX;
    @Description("Header of the point Y coordinate in the Point file.")
    @Role(Role.PARAMETER)
    @In
    public String headerCoordinatePointY;
    //*******************************
    //******* INPUT MAPS ************
    //*******************************
    @Description("Path to dem .asc file.")
    @Role(Role.PARAMETER)
    @In
    public String pathToDemFile;
    @Description("Path to land cover .asc file.")
    @Role(Role.PARAMETER)
    @In
    public String pathToLandCoverMapFile;
    @Description("Path to sky-view factor .asc file.")
    @Role(Role.PARAMETER)
    @In
    public String pathToSkyViewFactorMapFile;
    @Description("Path to Bedrock Depth .asc file.")
    @Role(Role.PARAMETER)
    @In
    public String pathToBedrockDepthMapFile;
    @Description("Path to slope .asc file.")
    @Role(Role.PARAMETER)
    @In
    public String pathToSlopeMapFile;
    @Description("Path to aspect .asc file.")
    @Role(Role.PARAMETER)
    @In
    public String pathToAspectMapFile;
    @Description("Path to soil .asc file.")
    @Role(Role.PARAMETER)
    @In
    public String pathToSoilMapFile;
    @Description("Path to river network .asc file.")
    @Role(Role.PARAMETER)
    @In
    public String pathToRiverNetwork;
    @Description("Path to curvature .asc file.")
    @Role(Role.PARAMETER)
    @In
    public String pathToCurvaturesMapFile;
    @Description("Path to curvature .asc file.")
    @Role(Role.PARAMETER)
    @In
    public String pathToDischargeFile;
    @Description("Path to curvature .asc file.")
    @Role(Role.PARAMETER)
    @In
    public String pathToMeasurement;
    @Description("Path to curvature .asc file.")
    @Role(Role.PARAMETER)
    @Out
    public double[] measured;
    @Description("Path to curvature .asc file.")
    @Role(Role.PARAMETER)
    @In
    public int dimMeasured;
    //*******************************
    //******** LAND COVER  **********
    //*******************************
    @Description("The number of land cover classes in the Landcover map")
    @Role(Role.PARAMETER)
    @In
    public Integer numLandCoverTypes;
    @Description("Vector of the soil roughness: the order of the values in the vector correspond to the order of the Land Cover classes.")
    @Role(Role.PARAMETER)
    @In
    public double[] soilRoughness;
    @Description("Vector of the soil roughness: the order of the values in the vector correspond to the order of the Land Cover classes.")
    @Role(Role.PARAMETER)
    @In
    public double[] thresSnowSoilRough;
    @Description("Vector of the vegetation height: the order of the values in the vector correspond to the order of the Land Cover classes.")
    @Role(Role.PARAMETER)
    @In
    public double[] vegHeight;
    @Description("Vector of the vegetation height: the order of the values in the vector correspond to the order of the Land Cover classes.")
    @Role(Role.PARAMETER)
    @In
    public double[] thresSnowVegUp;
    @Description("Vector of the vegetation height: the order of the values in the vector correspond to the order of the Land Cover classes.")
    @Role(Role.PARAMETER)
    @In
    public double[] thresSnowVegDown;
    @Description("Vector of the vegetation height: the order of the values in the vector correspond to the order of the Land Cover classes.")
    @Role(Role.PARAMETER)
    @In
    public double[] LSAI;
    @Description("Vector of the canopy fraction: the order of the values in the vector correspond to the order of the Land Cover classes.")
    @Role(Role.PARAMETER)
    @In
    public double[] canopyFraction;
    @Description("Vector of the decay coefficient canopy: the order of the values in the vector correspond to the order of the Land Cover classes.")
    @Role(Role.PARAMETER)
    @In
    public double[] decayCoeffCanopy;
    @Description("Vector of the vegetation snow burying: the order of the values in the vector correspond to the order of the Land Cover classes.")
    @Role(Role.PARAMETER)
    @In
    public double[] vegSnowBurying;
    @Description("Vector of the root depth: the order of the values in the vector correspond to the order of the Land Cover classes.")
    @Role(Role.PARAMETER)
    @In
    public double[] rootDepth;
    @Description("Vector of the minimal stomatal resistance: the order of the values in the vector correspond to the order of the Land Cover classes.")
    @Role(Role.PARAMETER)
    @In
    public double[] minStomatalRes;
    @Description("Vector of the vegetation reflected vis: the order of the values in the vector correspond to the order of the Land Cover classes.")
    @Role(Role.PARAMETER)
    @In
    public double[] vegReflectVis;
    @Description("Vector of the vegetation reflected nir: the order of the values in the vector correspond to the order of the Land Cover classes.")
    @Role(Role.PARAMETER)
    @In
    public double[] vegReflNIR;
    @Description("Vector of the vegetation trasmitted vis: the order of the values in the vector correspond to the order of the Land Cover classes.")
    @Role(Role.PARAMETER)
    @In
    public double[] vegTransVis;
    @Description("Vector of the vegetation trasmitted nir: the order of the values in the vector correspond to the order of the Land Cover classes.")
    @Role(Role.PARAMETER)
    @In
    public double[] vegTransNIR;
    @Description("Vector of the leaf angles: the order of the values in the vector correspond to the order of the Land Cover classes.")
    @Role(Role.PARAMETER)
    @In
    public double[] leafAngles;
    @Description("Vector of the canopy density surface: the order of the values in the vector correspond to the order of the Land Cover classes.")
    @Role(Role.PARAMETER)
    @In
    public double[] canDensSurface;
    @Description("Vector of the soil albedo vis dry: the order of the values in the vector correspond to the order of the Land Cover classes.")
    @Role(Role.PARAMETER)
    @In
    public double[] soilAlbVisDry;
    @Description("Vector of the soil albedo nir dry: the order of the values in the vector correspond to the order of the Land Cover classes.")
    @Role(Role.PARAMETER)
    @In
    public double[] soilAlbNIRDry;
    @Description("Vector of the soil albedo vis wet: the order of the values in the vector correspond to the order of the Land Cover classes.")
    @Role(Role.PARAMETER)
    @In
    public double[] soilAlbVisWet;
    @Description("Vector of the soil albedo nir wet: the order of the values in the vector correspond to the order of the Land Cover classes.")
    @Role(Role.PARAMETER)
    @In
    public double[] soilAlbNIRWet;
    @Description("Vector of the soilemissivity: the order of the values in the vector correspond to the order of the Land Cover classes.")
    @Role(Role.PARAMETER)
    @In
    public double[] soilEmissiv;
    @Description("Vector of the Surface Flow ResLand: the order of the values in the vector correspond to the order of the Land Cover classes.")
    @Role(Role.PARAMETER)
    @In
    public double[] surFlowResLand;
    //*******************************
    //*******  SOIL TYPE ************
    //*******************************
    @Description("The path wher soil txt soil have to be created.")
    @Role(Role.PARAMETER)
    @In
    public String soilParFile;
    @Description("The number of the soil layer types.")
    @Role(Role.PARAMETER)
    @In
    public Integer soilLayerTypes;
    @Description("Vector of the thermal conductivity of the solid soils: the order of the values in the vector correspond to the order of the soil txt files.")
    @Role(Role.PARAMETER)
    @In
    public double[] thermalConductivitySoilSolids;
    @Description("Vector of the Initial Soil Pressure: the order of the values in the vector correspond to the order of the soil txt files.")
    @Role(Role.PARAMETER)
    @In
    public double[] initSoilPressure;
    @Description("Vector of the Initial Soil Pressure: the order of the values in the vector correspond to the order of the soil txt files.")
    @Role(Role.PARAMETER)
    @In
    public double[] initSoilPressureBedrock;
    @Description("Vector of the Initial Soil Temperature: the order of the values in the vector correspond to the order of the soil txt files.")
    @Role(Role.PARAMETER)
    @In
    public double[] initSoilTemp;
    @Description("Vector of the Initial Soil Temperature of the Bedrock: the order of the values in the vector correspond to the order of the soil txt files.")
    @Role(Role.PARAMETER)
    @In
    public double[] initSoilTempBedrock;
    @Description("Vector of the thermal capacity of the solid soils: the order of the values in the vector correspond to the order of the soil txt files.")
    @Role(Role.PARAMETER)
    @In
    public double[] thermalCapacitySoilSolids;
    @Description("Vector of the Lateral hydraulic condictivity of the soils: the order of the values in the vector correspond to the order of the soil txt files.")
    @Role(Role.PARAMETER)
    @In
    public double[] kh;
    @Description("Vector of the residual water content of the soils: the order of the values in the vector correspond to the order of the soil txt files.")
    @Role(Role.PARAMETER)
    @In
    public double[] res;
    @Description("Vector of the saturated water content of the soils: the order of the values in the vector correspond to the order of the soil txt files.")
    @Role(Role.PARAMETER)
    @In
    public double[] sat;
    @Description("Vector of the field capacity of the soils: the order of the values in the vector correspond to the order of the soil txt files.")
    @Role(Role.PARAMETER)
    @In
    public double[] fc;
    @Description("Vector of the a van genuchten SWRC of the soils: the order of the values in the vector correspond to the order of the soil txt files.")
    @Role(Role.PARAMETER)
    @In
    public double[] a;
    @Description("Vector of the n van genuchten SWRC of the soils: the order of the values in the vector correspond to the order of the soil txt files.")
    @Role(Role.PARAMETER)
    @In
    public double[] n;
    @Description("Vector of the Specific Storativity of the soils: the order of the values in the vector correspond to the order of the soil txt files.")
    @Role(Role.PARAMETER)
    @In
    public double[] ss;
    @Description("Vector of the vertical layer depht of the soils: the order of the values in the vector correspond to the order of the soil txt files.")
    @Role(Role.PARAMETER)
    @In
    public double[] dz;
    @Description("Vector of the Normal Hydraulic Conductivity of the soils: the order of the values in the vector correspond to the order of the soil txt files.")
    @Role(Role.PARAMETER)
    @In
    public double[] kv;
    @Description("The soil layer number.")
    @Role(Role.PARAMETER)
    @In
    public Integer soilLayerNumber;
    @Description("Header of the dz field in the soil file.")
    @Role(Role.PARAMETER)
    @In
    public String headerSoilDz;
    @Description("Header of the Lateral Hydr Conductivity in the soil file.")
    @Role(Role.PARAMETER)
    @In
    public String headerLateralHydrConductivity;
    @Description("Header of the Normal Hydr Conductivity in the soil file.")
    @Role(Role.PARAMETER)
    @In
    public String headerNormalHydrConductivity;
    @Description("Header of the Theta Res in the soil file.")
    @Role(Role.PARAMETER)
    @In
    public String headerThetaRes;
    @Description("Header of the Field Capacity in the soil file.")
    @Role(Role.PARAMETER)
    @In
    public String headerFieldCapacity;
    @Description("Header of the Theta Sat in the soil file.")
    @Role(Role.PARAMETER)
    @In
    public String headerThetaSat;
    @Description("Header of the Alpha in the soil file.")
    @Role(Role.PARAMETER)
    @In
    public String headerAlpha;
    @Description("Header of the N in the soil file.")
    @Role(Role.PARAMETER)
    @In
    public String headerN;
    @Description("Header of the Specific Storativity in the soil file.")
    @Role(Role.PARAMETER)
    @In
    public String headerSpecificStorativity;
    @Description("Header of the Specific Storativity in the soil file.")
    @Role(Role.PARAMETER)
    @In
    public double[] initWaterTableHeightOverTopoSurface;
    //!*******************************
    //!******* NUMERICS **********
    //!*******************************    
    @Description("Heat Equation Tolerance.")
    @Role(Role.PARAMETER)
    @In
    public Double heatEqTol;
    @Description("Heat Equation Max Iteration Number.")
    @Role(Role.PARAMETER)
    @In
    public Double heatEqMaxIter;
    @Description("Outputs folder path.")
    @Role(Role.PARAMETER)
    @Out
    public String outputTabsFolder;
    //!*******************************
    //!**** OUTPUT TIME SERIES *******
    //!******************************* 
    @Description("Dt Plot Discharge: 0-1.")
    @Role(Role.PARAMETER)
    @In
    public Double dtPlotDischarge;
    @Description("Date Point:0-1.")
    @Role(Role.PARAMETER)
    @In
    public Integer datePoint;
    @Description("Dt Plot Point:0-1.")
    @Role(Role.PARAMETER)
    @In
    public Integer dtPlotPoint;
    @Description("Soil Liq Water Press Profile File.")
    @Role(Role.PARAMETER)
    @In
    public String soilLiqWaterPressProfileFile;
    @Description("Soil Liq Content Profile File.")
    @Role(Role.PARAMETER)
    @In
    public String soilLiqContentProfileFile;
    @Description("Soil Liq Content Profile File.")
    @Role(Role.PARAMETER)
    @In
    public String waterTableDepthMapFile;

    // out
    @Execute
    public void execute() throws Exception {

	System.out.println("output " + runFolder);

	if (pathToMeasurement != null) {
	    int cont_portate = 0;
	    measured = new double[dimMeasured];
	    //lettura portate//
	    try {

		InputStreamReader reader = new InputStreamReader(System.in);
		BufferedReader myInput = new BufferedReader(reader);
		String str = new String();
		str = pathToMeasurement;

		FileInputStream fstream = new FileInputStream(str);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;

		double aa = 0;
		while ((strLine = br.readLine()) != null) {
		    aa = Double.parseDouble(strLine);
		    measured[cont_portate] = aa;

		    cont_portate = cont_portate + 1;
		    in.close();
		}

	    } catch (Exception e) {
		//System.err.println("Errore: " + e.getMessage());
	    }

	    //fine lettura portate//
	    //lettura etp//


	}

	// check for valid input data.
	if (dataFolder == null || !new File(dataFolder).exists()) {
	    throw new IllegalArgumentException("Invalid data folder: " + dataFolder);
	}
	FileUtils.copyDirectory(
		new File(dataFolder), runFolder);

	outputTabsFolder = runFolder.toString().concat("/output_tabs/");
	System.out.print(outputTabsFolder);
	// 1. create geotop.inpts
	// change the parameter values as needed
	Map<String, Object> params = new HashMap<String, Object>();

	//!*******************************
	//!******* CONFIGURATION *********
	//!*******************************
	params.put(
		"TimeStepEnergyAndWater", timeStepEnergyAndWater);
	params.put(
		"InitDateDDMMYYYYhhmm", initDateDDMMYYYYhhmm);

	params.put(
		"EndDateDDMMYYYYhhmm", endDateDDMMYYYYhhmm);

	params.put(
		"EnergyBalance", energyBalance);
	params.put(
		"WaterBalance", waterBalance);

	//!*******************************
	//!********* GEOGRAPHY ***********
	//!*******************************
	params.put(
		"Latitude", latitude);
	params.put(
		"Longitude", longitude);


	//!*******************************
	//!****** METEO STATIONS *********
	//!*******************************

	params.put(
		"NumberOfMeteoStations", numberOfMeteoStations);
	params.put(
		"MeteoStationCoordinateX", meteoStationCoordinateX);

	params.put(
		"MeteoStationCoordinateY", meteoStationCoordinateY);

	params.put(
		"MeteoStationElevation", meteoStationElevation);

	params.put(
		"MeteoStationLatitude", meteoStationLatitude);

	params.put(
		"MeteoStationLongitude", meteoStationLongitude);

	params.put(
		"MeteoStationStandardTime", meteoStationStandardTime);

	params.put(
		"MeteoStationWindVelocitySensorHeight", meteoStationWindVelocitySensorHeight);

	params.put(
		"MeteoStationTemperatureSensorHeight", meteoStationTemperatureSensorHeight);

	params.put(
		"MeteoFile", meteoFile);
	params.put(
		"MeteoStationSkyViewFactor", meteoStationSkyViewFactor);

	params.put(
		"OutputSoilMaps", outputSoilMaps);

	params.put(
		"FreeDrainageAtBottom", freeDrainageAtBottom);

	params.put(
		"FreeDrainageAtLateralBorder", freeDrainageAtLateralBorder);
	params.put(
		"ChannelDepression", channelDepression);
	params.put(
		"NormalHydrConductivityBedrock", normalHydrConductivityBedrock);
	params.put(
		"LateralHydrConductivityBedrock", lateralHydrConductivityBedrock);
	params.put(
		"ThetaSatBedrock", thetaSatBedrock);
	params.put(
		"ThetaResBedrock", thetaResBedrock);
	params.put(
		"AlphaVanGenuchtenBedrock", alphaVanGenuchtenBedrock);
	params.put(
		"NVanGenuchtenBedrock", nVanGenuchtenBedrock);


	//!*******************************
	//!#######  HEADER ##########
	//!*******************************

	params.put(
		"HeaderDateDDMMYYYYhhmmMeteo", headerDateDDMMYYYYhhmmMeteo);
	params.put(
		"HeaderIPrec", headerIPrec);
	params.put(
		"HeaderWindVelocity", headerWindVelocity);

	params.put(
		"HeaderWindDirection", headerWindDirection);

	params.put(
		"HeaderRH", headerRH);

	params.put(
		"HeaderAirTemp", headerAirTemp);

	params.put(
		"HeaderSWglobal", headerSWglobal);

	params.put(
		"HeaderCloudSWTransmissivity", headerCloudSWTransmissivity);




	//!*******************************
	//!******* SIM. POINTS ***********
	//!*******************************

	params.put(
		"PointFile", pointFile);
	params.put(
		"HeaderPointID", headerPointID);
	params.put(
		"HeaderCoordinatePointX", headerCoordinatePointX);
	params.put(
		"HeaderCoordinatePointY", headerCoordinatePointY);

	//!*******************************  
	//!******* INPUT MAPS ************
	//!*******************************

	params.put(
		"DemFile", pathToDemFile);
	params.put(
		"LandCoverMapFile", pathToLandCoverMapFile);
	params.put(
		"SkyViewFactorMapFile", pathToSkyViewFactorMapFile);
	params.put("SlopeMapFile", pathToSlopeMapFile);

	params.put(
		"AspectMapFile", pathToAspectMapFile);

	params.put(
		"SoilMapFile", pathToSoilMapFile);
	params.put(
		"BedrockDepthMapFile", pathToBedrockDepthMapFile);
	params.put(
		"DischargeFile", pathToDischargeFile);


	//params.put("CurvaturesMapFile", pathToCurvaturesMapFile);
	params.put("RiverNetwork", pathToRiverNetwork);

	//!*******************************
	//!******** LAND COVER  **********
	//!*******************************

	params.put(
		"NumLandCoverTypes", numLandCoverTypes);
	params.put(
		"SoilRoughness", soilRoughness);
	params.put(
		"SoilAlbVisDry", soilAlbVisDry);
	params.put(
		"SoilAlbNIRDry", soilAlbNIRDry);
	params.put(
		"SoilAlbVisWet", soilAlbVisWet);
	params.put(
		"SoilAlbNIRWet", soilAlbNIRWet);
	params.put(
		"SoilEmissiv", soilEmissiv);
	params.put(
		"SurFlowResLand", surFlowResLand);

	params.put(
		"VegHeight", vegHeight);


	params.put(
		"LSAI", LSAI);
	params.put(
		"CanopyFraction", canopyFraction);
	params.put(
		"RootDepth", rootDepth);
	params.put(
		"VegReflectVis", vegReflectVis);
	params.put(
		"VegReflNIR", vegReflNIR);
	params.put(
		"VegTransVis", vegTransVis);
	params.put(
		"VegTransNIR", vegTransNIR);







	//!*******************************
	//!*******  SOIL TYPE ************
	//!*******************************

	params.put(
		"SoilParFile", soilParFile);
	params.put(
		"SoilLayerTypes", soilLayerTypes);
	params.put(
		"ThermalConductivitySoilSolids", thermalConductivitySoilSolids);
	params.put(
		"ThermalCapacitySoilSolids", thermalCapacitySoilSolids);
	params.put(
		"SoilLayerNumber", soilLayerNumber);
	params.put(
		"InitSoilPressure", initSoilPressure);

	params.put(
		"InitSoilTemp", initSoilTemp);

	params.put(
		"InitSoilTempBedrock", initSoilTempBedrock);

	params.put(
		"InitSoilPressureBedrock", initSoilPressureBedrock);



	params.put(
		"HeaderSoilDz", headerSoilDz);
	params.put(
		"HeaderLateralHydrConductivity", headerLateralHydrConductivity);
	params.put(
		"HeaderNormalHydrConductivity", headerNormalHydrConductivity);
	params.put(
		"HeaderThetaRes", headerThetaRes);
	params.put(
		"HeaderFieldCapacity", headerFieldCapacity);
	params.put(
		"HeaderThetaSat", headerThetaSat);
	params.put(
		"HeaderAlpha", headerAlpha);
	params.put(
		"HeaderN", headerN);
	params.put(
		"HeaderSpecificStorativity", headerSpecificStorativity);


	String matSoilPar[][] = new String[soilLayerNumber][9];
	for (int i = 0;
		i < dz.length;
		i++) {
	    matSoilPar[i][0] = String.valueOf(dz[i]);
	}
	for (int i = 0;
		i < kh.length;
		i++) {
	    matSoilPar[i][1] = String.valueOf(kh[i]);
	}
	for (int i = 0;
		i < kv.length;
		i++) {
	    matSoilPar[i][2] = String.valueOf(kv[i]);
	}
	for (int i = 0;
		i < res.length;
		i++) {
	    matSoilPar[i][3] = String.valueOf(res[i]);
	}
	for (int i = 0;
		i < fc.length;
		i++) {
	    matSoilPar[i][4] = String.valueOf(fc[i]);
	}
	for (int i = 0;
		i < sat.length;
		i++) {
	    matSoilPar[i][5] = String.valueOf(sat[i]);
	}
	for (int i = 0;
		i < a.length;
		i++) {
	    matSoilPar[i][6] = String.valueOf(a[i]);
	}
	for (int i = 0;
		i < n.length;
		i++) {
	    matSoilPar[i][7] = String.valueOf(n[i]);
	}
	for (int i = 0;
		i < ss.length;
		i++) {
	    matSoilPar[i][8] = String.valueOf(ss[i]);
	}
	File f = new File(runFolder, soilParFile + "0001.txt");

	if (!f.getParentFile()
		.exists()) {
	    f.getParentFile().mkdirs();
	}
	CSVWriter writer = new CSVWriter(new FileWriter(f), ',', CSVWriter.NO_QUOTE_CHARACTER);
	// feed in your array (or convert your data to an array)
	String e = (headerSoilDz + "," + headerLateralHydrConductivity + "," + headerNormalHydrConductivity + "," + headerThetaRes + "," + headerFieldCapacity + "," + headerThetaSat + "," + headerAlpha + "," + headerN + "," + headerSpecificStorativity);
	//String[] entries = "Dz,Kh,Kv,res,fc,sat,a,n,SS".split(",");
	String[] entries = e.split(",");

	writer.writeNext(entries);
	for (int i = 1;
		i < matSoilPar.length + 1; i++) {
	    entries = matSoilPar[i - 1];
	    writer.writeNext(entries);

	}

	writer.close();

	//!*******************************
	//!******* NUMERICS **********
	//!*******************************        
	params.put(
		"HeatEqTol", heatEqTol);
	params.put(
		"HeatEqMaxIter", heatEqMaxIter);


	//!*******************************
	//!**** OUTPUT TIME SERIES *******
	//!*******************************        

	params.put(
		"DtPlotDischarge", dtPlotDischarge);
	params.put(
		"DtPlotPoint", dtPlotPoint);
	params.put(
		"PointOutputFile", pointOutputFile);
	params.put(
		"SoilLiqWaterPressProfileFile", soilLiqWaterPressProfileFile);
	params.put(
		"SoilLiqContentProfileFile", soilLiqContentProfileFile);
	params.put(
		"WaterTableDepthMapFile", waterTableDepthMapFile);
	params.put(
		"InitWaterTableHeightOverTopoSurface", initWaterTableHeightOverTopoSurface);

	params.put(
		"DtPlotPoint", dtPlotPoint);
	params.put(
		"PointSim", pointSim);

	params.put(
		"DatePoint", datePoint);











	createGeoTopInput(params, new File(runFolder + "/geotop.inpts"));

	// 2. call geotop
	String binDir = System.getProperty("java.io.tmpdir");

	System.out.println(
		"BIN " + binDir);
//        File gt_exe = null;

	System.out.println(BinUtils.ARCH);

//        String meteoio = "";

//        gt_exe = BinUtils.unpackResource("/bin/" + BinUtils.ARCH + "/GEOtop145_CPP_SVN.exe", new File(binDir));

//        if ("lin_amd64".equals(BinUtils.ARCH)) {
//            meteoio = "libmeteoio.so.2";
//        } else if ("mac_x86_64".equals(BinUtils.ARCH)) {
//            meteoio = "libmeteoio.2.dylib";
//            BinUtils.unpackResource("/bin/" + BinUtils.ARCH + "/libz.1.dylib", new File(binDir));
//        }
	//gt_exe = BinUtils.unpackResource("/bin/" + BinUtils.ARCH + "/GEOtop145_CPP_SVN.exe", new File(binDir));
//        gt_exe = BinUtils.unpackResource("/bin/" + BinUtils.ARCH + "/GEOtop1.45", new File(binDir));
//        BinUtils.unpackResource("/bin/" + BinUtils.ARCH + "/" + meteoio, new File(binDir));

	Processes p = new Processes(new File("GEOtop1.45"));
	p.setWorkingDirectory(runFolder);
//        System.out.println("DYLD " + System.getenv("LD_FALLBACK_LIBRARY_PATH"));
//        System.out.println("DYLD " + System.getenv("DYLD_FALLBACK_FRAMEWORK_PATH"));
//        p.environment().put("LD_LIBRARY_PATH", gt_exe.getParent());
//        p.environment().put("DYLD_PRINT_LIBRARIES", "");
//        p.environment().put("DYLD_FALLBACK_LIBRARY_PATH", gt_exe.getParent() + ":$(HOME)/lib:/usr/local/lib:/lib:/usr/lib");
//        p.environment().put("DYLD_FALLBACK_FRAMEWORK_PATH", gt_exe.getParent());
	p.setArguments(".");

	int exitValue = p.exec();

//        System.out.println("Output :" + pc.stdout);
//        System.out.println();
//        System.out.println("Err :" + pc.stderr);

	// 3. parse outputs and return results.
	processOutput();
    }

    void processOutput() throws FileNotFoundException, IOException {
    }

    /**
     * Map the component parameter to a Geotop input file via a 'geotop.inpts'
     * template.
     *
     * @param params parameter hash map
     * @return
     */
    static private void createGeoTopInput(Map<String, Object> params, File file) throws Exception {
	Properties props = new Properties();
	props.put("resource.loader", "class");
	props.put("class.resource.loader.class",
		"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

	VelocityContext context = new VelocityContext();
	for (Iterator<String> it = params.keySet().iterator(); it.hasNext();) {
	    String key = it.next();
	    //System.out.println(key);
	    Object val = params.get(key);
	    System.out.println(key + "  " + val);
	    String str_val = null;
	    if (val != null) {
		str_val = Conversions.convert(val, String.class);
		str_val = str_val.replace("{", "").replace("}", "");
	    }


	    String k_final = "";
	    if (str_val == null) {
		k_final = "!" + key;
	    } else {

		k_final = key;
	    }
	    System.out.println(key + "  " + str_val);
	    context.put(key, str_val);
	    context.put(key + "_K", k_final);
	}
	Writer writer = new FileWriter(file);
	VelocityEngine ve = new VelocityEngine();
	ve.init(props);
	ve.getTemplate("it/unitn/geotop.input.vm").merge(context, writer);
	writer.close();
    }
}
