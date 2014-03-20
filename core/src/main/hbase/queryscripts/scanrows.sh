#!/bin/bash

# ./scanrows.sh apache_request daily:20131105


./hbaseReqRows.sh $1 $2 > out
./formatRows

