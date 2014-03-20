#!/bin/bash

# ./scancols.sh apache_request index4 daily:20131021


./hbaseReq.sh $1 $2 $3> out
./formatCols

