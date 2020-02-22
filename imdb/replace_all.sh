#!/bin/sh

if [ "$1" != -f ] ; then
    echo "usage: $0 -f"
    echo "DANGER: This deletes neo4j database."
    echo "(Must run this script as root or use sudo)"
    exit
fi

set -x
set -e

me="$(readlink -f "$0")"
here="$(dirname "$me")"

systemctl stop neo4j
rm -R /var/lib/neo4j/data/databases/graph.db

neo4j-admin import \
    --delimiter='\09' \
    --quote=\| \
    --ignore-extra-columns=true \
    --ignore-missing-nodes=true \
    --nodes:Person=name_head.tsv,name.tsv \
    --nodes:Movie=title_head.tsv,title.tsv \
    --relationships:HAD_ROLE_IN=principal_head.tsv,principal.tsv

chown -R neo4j: /var/lib/neo4j/data/databases/graph.db
systemctl start neo4j
sleep 10
systemctl status neo4j
