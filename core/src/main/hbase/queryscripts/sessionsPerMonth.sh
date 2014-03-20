#!/bin/bash

#./sessionsPerMonth.sh 201311 [0000z2ur1hruUUG-MhpsITK9JY_:0]

SESSION=$2

if [ -n "$SESSION" ]; then
	./scancols.sh wslog_session $2 "monthly:$1"
else
	./scanrows.sh wslog_session "monthly:$1"
fi
