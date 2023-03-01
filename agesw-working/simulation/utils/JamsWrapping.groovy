/* 
 * OMS3 to JAMS wrapper generator.
 */

// list of jar files that contain the oms3 components
jars = ["/od/projects/oms3.prj.ages/dist/oms3.prj.ages.jar"]

// the target folder to store the generated source(s) in
src  = "/od/tmp"

// list of component classes as full qualifies class name (no extension)
comps = ["erosion.MusleMay"]

// perform the wrapping
oms3.JAMS.conv(jars, src, comps)
                
                

