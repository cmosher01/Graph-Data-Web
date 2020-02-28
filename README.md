# Graph-Data-Web

Copyright © 2019–2020, Christopher Alan Mosher, Shelton, Connecticut, USA, <cmosher01@gmail.com>.

[![License](https://img.shields.io/github/license/cmosher01/Graph-Data-Web.svg)](https://www.gnu.org/licenses/gpl.html)
[![Build Status](https://travis-ci.com/cmosher01/Graph-Data-Web.svg?branch=master)](https://travis-ci.com/cmosher01/Graph-Data-Web)
[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=CVSSQ2BWDCKQ2)

Web application for editing Neo4j graphs.

The data model is defined in OGM.

Requires sticky sessions.

![flow](./ui_flow.svg)

## Example dataset: IMDB

To download and import part of the IMDB dataset:

    docker-compose -f load.docker-compose.yml pull
    docker-compose -f load.docker-compose.yml up
    docker-compose -f load.docker-compose.yml down

To run the server:

    docker-compose pull
    docker-compose build
    docker-compose up -d

Use your browser to connect to `http://localhost:8080` and log in as:

    Username: neo4j
    Password: demo

In another tab, go to `http://localhost:7474` to use Neo4J browser:

    CALL db.indexes();

and wait for both indexes to be ONLINE.

Back in the first browser tab, click on `Movie` and search for a title.

To quit the server and keep the database:

    docker-compose down

Or, to quit the server and remove the entire database:

    docker-compose down -v
