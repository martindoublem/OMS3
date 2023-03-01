#!/bin/bash

TEMPLATE="../../oms3.prj.template/.oms/project.xml"
PRJ="oms3.prj.examples oms3.prj.nlFire oms3.prj.helloworld oms3.prj.hym oms3.prj.prms oms3.prj.thornthwaite oms3.prj.ages-w oms3.prj.dp oms3.prj.csm"

echo "copy '$TEMPLATE' to ..."
for i in $PRJ 
do
 echo "  $i/.oms/project.xml"
 cp $TEMPLATE  ../../$i/.oms/project.xml
done;

