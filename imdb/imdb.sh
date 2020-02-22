#!/bin/sh

if [ "$1" != -f ] ; then
    echo "usage: $0 -f"
    echo "This downloads IMDB datasets and imports into neo4j."
    echo "Before running this script:"
    echo "  1. stop neo4j server"
    echo "  2. rm -R the entire neo4j database (e.g., /var/lib/neo4j/data/databases/graph.db )"
    echo "You may need SUDO for these commands, and for running this script"
    echo "After running: sudo chown -R neo4j: /var/lib/neo4j/data/databases/graph.db"

    exit
fi

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
        cat $1.tsv | awk -c -f $2.awk | python3 add_uuid.py >$2.tsv
    fi
}

get name.basics name
get title.basics title
get title.principals principal
