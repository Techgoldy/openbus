#!/bin/bash

#./sessionsPerWeek.sh 201349 [0000z2ur1hruUUG-MhpsITK9JY_:0]

SESSION=$2

if [ -n "$SESSION" ]; then
	./scancols.sh wslog_session $2 "weekly:$1"
else
	./scanrows.sh wslog_session "weekly:$1"
fi
