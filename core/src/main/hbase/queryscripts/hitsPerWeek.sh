#!/bin/bash

#./hitsPerDay.sh 201349 [index4]

REQUEST=$2

if [ -n "$REQUEST" ]; then
	./scancols.sh wslog_request $2 "weekly:$1"
else
	./scanrows.sh wslog_request "weekly:$1"
fi
