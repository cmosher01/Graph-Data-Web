#!/bin/awk -f

BEGIN {
    FS="\t";
    OFS="\t";
}

NR!=1 {
    if ($5==0) { # if not an adult movie
        sub(/\\N/, "-1", $6)
        sub(/\\N/, "-1", $8)
        print $1, $2, $3, $6, $8;
    }
}
