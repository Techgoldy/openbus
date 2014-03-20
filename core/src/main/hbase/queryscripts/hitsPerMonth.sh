#!/bin/bash

#./hitsPerDay.sh 201310 [index4]

REQUEST=$2

if [ -n "$REQUEST" ]; then
	./scancols.sh wslog_request $2 "monthly:$1"
else
	./scanrows.sh wslog_request "monthly:$1"
fi
