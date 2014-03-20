#!/bin/bash

# ./hbaseReq.sh wslog_request index4 daily:20131021

TABLE=$1
ROW=$2
DATE=$3

exec hbase shell << EOF
get '${TABLE}', '${ROW}', {COLUMNS => ['${DATE}']}
EOF
