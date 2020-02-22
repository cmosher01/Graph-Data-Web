#!/bin/awk -f

BEGIN {
    FS="\t";
    OFS="\t";
}

NR!=1 {
    sub(/\\N/, "-1", $3)
    sub(/\\N/, "-1", $4)
    print $1, $2, $3, $4;
}
