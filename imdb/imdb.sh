#!/bin/bash

set -e
set -u
set -o pipefail

p=/imdb

cd /tmp

apt-get update && apt-get install -y python3 curl

get() {
    echo "$1..."
    if [ ! -e $1.tsv.gz ] ; then
        echo "downloading $1.tsv.gz..."
        curl -o $1.tsv.gz https://datasets.imdbws.com/$1.tsv.gz
    fi
    if [ ! -e $1.tsv ] ; then
        echo "decompressing $1.tsv.gz..."
        cat $1.tsv.gz | gunzip -c >$1.tsv
    fi
    if [ ! -e $2.tsv ] ; then
        cat $1.tsv | awk -f $p/$2.awk | python3 $p/add_uuid.py >$2.tsv
    fi
}

get name.basics name
get title.basics title
get title.principals principal

neo4j-admin import \
    --delimiter='\09' \
    --quote=\| \
    --ignore-extra-columns=true \
    --ignore-missing-nodes=true \
    --nodes:Person=$p/name_head.tsv,name.tsv \
    --nodes:Movie=$p/title_head.tsv,title.tsv \
    --relationships:HAD_ROLE_IN=$p/principal_head.tsv,principal.tsv

chown -R neo4j: /data/databases
