#!/bin/bash

#./sessionsPerDay.sh 20131126 [0000z2ur1hruUUG-MhpsITK9JY_:0]

SESSION=$2

if [ -n "$SESSION" ]; then
	./scancols.sh wslog_session $2 "daily:$1"
else
	./scanrows.sh wslog_session "daily:$1"
fi
