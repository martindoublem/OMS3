#!/bin/bash

REPO_URL=http://svn.javaforge.com/svn/oms
OMS_VERSION=`grep 'oms.version=' ../nbproject/ngmf.properties | awk -F= '{ print $2 }'`

echo tagging:  $OMS_VERSION
svn -m "tagging OMS version $OMS_VERSION" copy $REPO_URL/trunk $REPO_URL/tags/$OMS_VERSION

