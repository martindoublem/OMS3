
Building OMS
===========================================================
Just a few steps are required to build OMS from source.

Installation Requirements:
   JDK 1.6+  (http://www.oracle.com/technetwork/java/javase/downloads/index.html)
   ANT 1.8+  (http://ant.apache.org)


Building OMS:
   $ cd <DIR>/ngmf.build/bin
   $ ant all

 after successful build there will be in <DIR>/ngmf.build/dist:

   oms<version>-console.zip
   oms<version>-src.zip

Test the console and installing the OMS jar file:
  $ cd <DIR>/ngmf.build/bin
  $ ant run

-------
http://oms.javaforge.com
