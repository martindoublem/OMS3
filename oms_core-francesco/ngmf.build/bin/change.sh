#!/bin/bash

change () {
  perl -0777 -pe "s{^/\*.*?\*/}{}s" $1 > /tmp/a
  cat ../license-header.txt /tmp/a > $1
}

change $1
echo  $1
