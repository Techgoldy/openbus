#!/bin/bash

#./usersPerWeek.sh 201349 [user1]

USER=$2

if [ -n "$USER" ]; then
	./scancols.sh wslog_user $2 "weekly:$1"
else
	./scanrows.sh wslog_user "weekly:$1"
fi
