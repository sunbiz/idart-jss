#!/bin/bash
# Calls go.sh, which Launches the Cell-Life Pharmacy Application.
# necessary for correct working directory when running the application

FILENAME="Patients.xls"
SHEETNAME="import"

./go.sh import $FILENAME $SHEETNAME
