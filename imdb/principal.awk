#!/bin/awk -f

BEGIN {
    FS="\t";
    OFS="\t";
}

NR!=1 {
    sub(/\\N/, "", $4)
    sub(/\\N/, "", $5)
    print $3, $1, $4, $5;
}
