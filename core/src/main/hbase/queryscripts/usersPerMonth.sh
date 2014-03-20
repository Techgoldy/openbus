#!/bin/bash

#./usersPerMonth.sh 201310 [user1]

USER=$2

if [ -n "$USER" ]; then
	./scancols.sh wslog_user $2 "monthly:$1"
else
	./scanrows.sh wslog_user "monthly:$1"
fi
