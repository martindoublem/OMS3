
Collection of OMS3 examples.
==============================================

The simulation example number always corresponds to component source packages, 
data folder, and output folder. Start always with the simulation script, then
drill down to the corresponding component source package with the same name

ex00.sim - Classic 'Hello World'.
  passing in a message to an '@In' field of a component; 
  the component just prints the message out.

ex01.sim - Two connected components.
  Parameter passes from 'sim' file into 'Comp1', 'Comp1' modifies it and passes
  it further to 'Comp2' which prints it out.

ex02.sim - Passing a 'first class' object as a parameter into a component.
  The 'Point' class is a POJO that gets instantiated in the sim script
  and passed to to the component. The component then prints it.

ex03.sim - Connecting 'non-connectable' components via Conversions.
  Demonstrating the use of the 'ConversionProvider' SPI to provide
  generic conversion of objects exchanged by components. Without such a 
  conversion support, @In/@Out connect of incompatible types would fail.

ex04.sim - Three components in chain,@In/@Out pass through.
  Data pass is passed via @In/@Out; Same as example 2 but one more 
  component in the execution line. The component in the middle uses 
  an @In/@Out annotated field that is used for passing  the data 
  through the same object.

ex05.sim - Simple Iteration example. 
  The 'IterationControl' class counts down from 'count' to zero, providing 
  the loop variable 'i' as output. 'i' is not consumed anywhere.
  
ex06.sim - Time Iteration example. 
  The control component iterates over time, It can be parameterized
  with 4 parameters, allowing almost any regular time step. The current time
  is output of the control component and consumed in 'TimeConsumerComponent'

ex07.sim - Component '@Name' alias.
  Use a '@Name' alias instead of a full qualified Java package name for 
  component class declaration.

ex08.sim - Selective Component Logging.
  Use a logger in your component and control the level from a 'sim' script.
  The logging element allows to assign different log levels to packages and
  components.

ex09.sim - An OMS test suite for a component.
  Use the 'tests' element to define a test suite, where individual
  tests are defines with the 'test' element. See example 'test' elements
  for variants.

ex10.sim - Three components connected in a simplified way. 
  All connection alternatives are given in the 'sim' file. 'Comp1' produces 
  output that is consumed by both, 'Comp2' and 'Comp3' at the same time. 
  Hence, execution order of Comp2 and Comp3 is random and parallel. 
  Execute this multiple times and watch the order of execution.

ex11.sim - Direct Feedback example.
  There are two components in a feedback loop, bouncing 'fb' back and forth
  until it reached its "convergence" (greater than 10). fb is declared @In/@Out
  in both components. Note that there can be a indirect feedbacks too, 
  means: data flow goes through multiple components until it 'comes back'.

ex12.sim - External Annotation using a 'CompInfo' class.
  The 'ComputePOJOCompInfo' class carries the annotations for the 'ComputePOJO'
  class, that has no annotations at all. This shows the separation of an annotated
  class and a 'legacy' class; the legacy class can be even compiled.

ex13.sim - Default values for @In annotated fields.
  An '@In' field can be left unconnected. In this case it takes its default
  value as specified within the component.
  
ex14.sim - Parameter passing from sim file to a component with or without conversion.
  OMS converts values for parameter if needed. Parameter can be provided as first
  class objects, or as string representation. Files, Dates, Time, and others
  are more descriptive as strings, rather than objects.

ex15.sim - @Initialize/@Execute/@Finalize life cycle example.
  the 'TimeConsumerComponent' is implementing in addition to Execute also the 
  Initialize and Finalize method. Both methods are executed __once__ at the
  beginning and end of the simulation. 
  IMPORTANT: Within the @Initialize method, NEVER assume that an @In field
             has a valid value or is set. This is only the case within @Execute (and 
             @Finalize)


Building the examples and running them
--------------------------------------
To build the example jar file, just type

  <oms3.prj.examples-basic folder>$ ant

To run for a single example, e.g. 'ex09.sim', type:

  <oms3.prj.examples folder>$ ant -Dfile=simulation/ex09.sim run

 or

  open the OMS console version 3.1rc5 or later and let
  it point to that oms3.prj.example folder.

 
Notes
-----------------
-Adjust the file .oms/project.properties if needed.


odavid@colostate.edu