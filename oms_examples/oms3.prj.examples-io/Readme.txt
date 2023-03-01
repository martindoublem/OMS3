
Collection of OMS3 examples.  (IO)
==============================================

The simulation example number always corresponds to component source packages, 
data folder, and output folder. Start always with the simulation script, then
drill down to the corresponding component source package with the same name


ex16.sim - File parameter and writing of output.
  Demonstrates the use of '$oms_prj' within a sim file; passes a file name from
  a sim file to a file object in a component; the component iterates and 
  closes the file in @Finalize. Note: the use of initialization at the
  beginning of @Execute. This is the *right* way of doing it, do _not_ use the
  @Initialize method for the same purpose.

ex17.sim - Use an outputstrategy element to 'version' output.
  The same component as in ex16, however the sim file has now an additional
  'ouputstrategy' element taking over the management of the output folder.
  It is set to 'NUMBERED': each subsequent simulation run will create a new
  folder in output/ex17, that will receive the output files. No previous
  output will be overwritten. SIMPLE will put the output always into 
  the same folder, TIME will use an ISO date as the folder name, NUMBERED
  just a number.


Building the examples and running them
--------------------------------------
To build the example jar file, just type

  <oms3.prj.examples folder>$ ant

To run for a single example, e.g. 'ex09.sim', type:

  <oms3.prj.examples folder>$ ant -Dfile=simulation/ex09.sim run
 or
  open the OMS console version 3.1rc5 or later and let
  it point to that oms3.prj.example folder.

 
Notes
-----------------
-Adjust the file .oms/project.properties if needed.



odavid@colostate.edu