#!/bin/bash

#./usersPerDay.sh 20131021 [user1]

USER=$2

if [ -n "$USER" ]; then
	./scancols.sh wslog_user $2 "daily:$1"
else
	./scanrows.sh wslog_user "daily:$1"
fi
