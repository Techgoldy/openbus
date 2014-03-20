#!/bin/bash

# ./hbaseReqRows.sh wslog_request daily:20131105

TABLE=$1
DATE=$2

exec hbase shell << EOF
scan '${TABLE}'	, {COLUMNS => ['${DATE}']}
EOF
