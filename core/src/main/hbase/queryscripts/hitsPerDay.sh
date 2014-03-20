#!/bin/bash

#./hitsPerDay.sh 20131021 [index4]

REQUEST=$2

if [ -n "$REQUEST" ]; then
	./scancols.sh wslog_request $2 "daily:$1"
else
	./scanrows.sh wslog_request "daily:$1"
fi
