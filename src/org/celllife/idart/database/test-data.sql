INSERT INTO accumulateddrugs (id, withpackage, pillcount) VALUES (0, 1, 1);
INSERT INTO accumulateddrugs (id, withpackage, pillcount) VALUES (1, 1, 2);
INSERT INTO accumulateddrugs (id, withpackage, pillcount) VALUES (2, 1, 0);
INSERT INTO accumulateddrugs (id, withpackage, pillcount) VALUES (3, 2, 4);
INSERT INTO accumulateddrugs (id, withpackage, pillcount) VALUES (4, 2, 5);
INSERT INTO accumulateddrugs (id, withpackage, pillcount) VALUES (5, 2, 3);

INSERT INTO appointment (id, appointmentdate, modified, patient, active) VALUES (0, '2007-08-28 00:00:00', 'T', 3, true);
INSERT INTO appointment (id, appointmentdate, modified, patient, active) VALUES (1, '2007-09-26 00:00:00', 'T', 3, true);
INSERT INTO appointment (id, appointmentdate, modified, patient, active) VALUES (2, '2007-10-26 00:00:00', 'T', 3, true);
INSERT INTO appointment (id, appointmentdate, modified, patient, active) VALUES (3, '2008-04-23 12:48:29.112', 'T', 1, true);

INSERT INTO doctor (id, emailaddress, firstname, lastname, mobileno, modified, telephoneno, active) VALUES (1, '', 'John', 'Smith', '', 'T', '', true);

INSERT INTO episode (id, startdate, stopdate, startreason, stopreason, startnotes, stopnotes, patient, index) VALUES (0, '2008-03-26 12:14:00.484', NULL, 'New Patient', NULL, '', NULL, 2, 0);
INSERT INTO episode (id, startdate, stopdate, startreason, stopreason, startnotes, stopnotes, patient, index) VALUES (1, '2008-03-26 12:15:51.793', NULL, 'New Patient', NULL, '', NULL, 0, 0);
INSERT INTO episode (id, startdate, stopdate, startreason, stopreason, startnotes, stopnotes, patient, index) VALUES (2, '2006-03-22 00:00:00', '2006-07-26 00:00:00', 'Transferred In', 'Transferred Out', 'start notes', 'stop notes', 1, 0);
INSERT INTO episode (id, startdate, stopdate, startreason, stopreason, startnotes, stopnotes, patient, index) VALUES (3, '2007-01-09 00:00:00', '2007-03-22 00:00:00', 'New Patient', 'Lost to Follow Up', '', '', 3, 0);
INSERT INTO episode (id, startdate, stopdate, startreason, stopreason, startnotes, stopnotes, patient, index) VALUES (4, '2007-06-20 00:00:00', NULL, 'Transferred In', NULL, '', NULL, 3, 1);

INSERT INTO package (id, dateleft, datereceived, modified, packageid, packdate, pickupdate, prescription, weekssupply, datereturned, stockreturned, packagereturned, reasonforpackagereturn) VALUES (0, '2007-07-31 00:00:00', '2007-07-31 00:00:00', 'T', '070627A-123-1', '2007-07-31 00:00:00', '2007-07-31 00:00:00', 13, 4, NULL, false, false, NULL);
INSERT INTO package (id, dateleft, datereceived, modified, packageid, packdate, pickupdate, prescription, weekssupply, datereturned, stockreturned, packagereturned, reasonforpackagereturn) VALUES (1, '2007-08-29 00:00:00', '2007-08-29 00:00:00', 'T', '070627A-123-2', '2007-08-29 00:00:00', '2007-08-29 00:00:00', 13, 4, NULL, false, false, NULL);
INSERT INTO package (id, dateleft, datereceived, modified, packageid, packdate, pickupdate, prescription, weekssupply, datereturned, stockreturned, packagereturned, reasonforpackagereturn) VALUES (2, '2007-09-28 00:00:00', '2007-09-28 00:00:00', 'T', '070627A-123-3', '2007-09-28 00:00:00', '2007-09-28 00:00:00', 13, 4, NULL, false, false, NULL);

INSERT INTO packageddrugs (id, amount, stock, parentpackage, modified, packageddrugsindex) VALUES (0, 60, 17, 0, 'T', 0);
INSERT INTO packageddrugs (id, amount, stock, parentpackage, modified, packageddrugsindex) VALUES (1, 60, 10, 0, 'T', 1);
INSERT INTO packageddrugs (id, amount, stock, parentpackage, modified, packageddrugsindex) VALUES (2, 60, 11, 0, 'T', 2);
INSERT INTO packageddrugs (id, amount, stock, parentpackage, modified, packageddrugsindex) VALUES (3, 60, 17, 1, 'T', 0);
INSERT INTO packageddrugs (id, amount, stock, parentpackage, modified, packageddrugsindex) VALUES (4, 60, 10, 1, 'T', 1);
INSERT INTO packageddrugs (id, amount, stock, parentpackage, modified, packageddrugsindex) VALUES (5, 60, 11, 1, 'T', 2);
INSERT INTO packageddrugs (id, amount, stock, parentpackage, modified, packageddrugsindex) VALUES (6, 60, 17, 2, 'T', 0);
INSERT INTO packageddrugs (id, amount, stock, parentpackage, modified, packageddrugsindex) VALUES (7, 60, 10, 2, 'T', 1);
INSERT INTO packageddrugs (id, amount, stock, parentpackage, modified, packageddrugsindex) VALUES (8, 60, 11, 2, 'T', 2);

INSERT INTO patient (id, accountstatus, cellphone, dateofbirth, clinic, firstnames, homephone, idnum, lastname, modified, patientid, province, sex, workphone, address1, address2, address3, nextofkinname, nextofkinphone, race) VALUES (0, true, '0820000000', '2004-06-03 00:00:00', 2, 'Joe', '', '8000000000000', 'Soap', 'T', '123456789', '', 'M', '0530000000', 'Address Line 1', 'Address Line 2', 'Address Line 3', 'Next Of Kin Name', '0820000000', NULL);
INSERT INTO patient (id, accountstatus, cellphone, dateofbirth, clinic, firstnames, homephone, idnum, lastname, modified, patientid, province, sex, workphone, address1, address2, address3, nextofkinname, nextofkinphone, race) VALUES (1, false, '0820000000', '1909-12-31 00:00:00', 2, 'Joe', '', '8000000000001', 'Soap', 'T', '1', '', 'M', '0530000000', 'Address Line 1', 'Address Line 2', 'Address Line 3', 'Next Of Kin Name', '0820000000', NULL);
INSERT INTO patient (id, accountstatus, cellphone, dateofbirth, clinic, firstnames, homephone, idnum, lastname, modified, patientid, province, sex, workphone, address1, address2, address3, nextofkinname, nextofkinphone, race) VALUES (2, true, '0820000000', '1980-06-04 00:00:00', 2, 'Jane', '021 456 9856', '8000000000002', 'Soap', 'T', 'ABC123', 'Western Cape', 'F', '0530000000', 'Address Line 1', 'Address Line 2', 'Address Line 3', 'Next Of Kin Name', '0820000000', NULL);
INSERT INTO patient (id, accountstatus, cellphone, dateofbirth, clinic, firstnames, homephone, idnum, lastname, modified, patientid, province, sex, workphone, address1, address2, address3, nextofkinname, nextofkinphone, race) VALUES (3, true, '0820000000', '1985-08-20 00:00:00', 2, 'Jane', '', '8000000000003', 'Soap', 'T', '123', '', 'F', '0530000000', 'Address Line 1', 'Address Line 2', 'Address Line 3', 'Next Of Kin Name', '0820000000', NULL);

INSERT INTO patientattribute (id, value, patient, type_id) VALUES (0, '07 Oct 2004', 1, 1);
INSERT INTO patientattribute (id, value, patient, type_id) VALUES (1, '26 Mar 2008', 0, 1);

INSERT INTO pillcount (id, accum, previouspackage, dateofcount, drug) VALUES (0, 2, 0, '2008-03-26 12:46:35.814', 11);
INSERT INTO pillcount (id, accum, previouspackage, dateofcount, drug) VALUES (1, 2, 0, '2008-03-26 12:46:35.814', 10);
INSERT INTO pillcount (id, accum, previouspackage, dateofcount, drug) VALUES (2, 3, 0, '2008-03-26 12:46:35.814', 17);
INSERT INTO pillcount (id, accum, previouspackage, dateofcount, drug) VALUES (3, 3, 1, '2008-03-26 12:47:06.088', 17);
INSERT INTO pillcount (id, accum, previouspackage, dateofcount, drug) VALUES (4, 2, 1, '2008-03-26 12:47:06.088', 10);
INSERT INTO pillcount (id, accum, previouspackage, dateofcount, drug) VALUES (5, 2, 1, '2008-03-26 12:47:06.088', 11);

INSERT INTO pregnancy (id, confirmdate, enddate, patient, modified) VALUES (0, '2007-01-03 12:19:59.20', '2008-03-26 12:42:48.439', 3, 'T');

INSERT INTO prescribeddrugs (id, amtpertime, drug, prescription, timesperday, modified, prescribeddrugsindex) VALUES (0, 1, 17, 0, 2, 'T', 0);
INSERT INTO prescribeddrugs (id, amtpertime, drug, prescription, timesperday, modified, prescribeddrugsindex) VALUES (1, 1, 10, 0, 2, 'T', 1);
INSERT INTO prescribeddrugs (id, amtpertime, drug, prescription, timesperday, modified, prescribeddrugsindex) VALUES (2, 1, 11, 0, 2, 'T', 2);
INSERT INTO prescribeddrugs (id, amtpertime, drug, prescription, timesperday, modified, prescribeddrugsindex) VALUES (3, 1, 9, 1, 1, 'T', 0);
INSERT INTO prescribeddrugs (id, amtpertime, drug, prescription, timesperday, modified, prescribeddrugsindex) VALUES (4, 1, 12, 1, 1, 'T', 1);
INSERT INTO prescribeddrugs (id, amtpertime, drug, prescription, timesperday, modified, prescribeddrugsindex) VALUES (5, 1, 15, 1, 1, 'T', 2);

INSERT INTO prescription (id, clinicalstage, current, date, doctor, duration, modified, patient, prescriptionid, weight, reasonforupdate, notes, enddate) VALUES (0, 2, 'T', '2007-06-27 00:00:00', 1, 4, 'T', 3, '070627A-123', 0, 'Initial', '', NULL);
INSERT INTO prescription (id, clinicalstage, current, date, doctor, duration, modified, patient, prescriptionid, weight, reasonforupdate, notes, enddate) VALUES (1, 0, 'T', '2008-03-26 00:00:00', 1, 4, 'T', 0, '080326A-123456789', 0, 'Initial', '', NULL);

INSERT INTO regimen (id, modified, notes, regimenname, druggroup) VALUES (0, 'T', '', '1A-30', '1A');
INSERT INTO regimen (id, modified, notes, regimenname, druggroup) VALUES (1, 'T', '', '1B-30', '1B');

INSERT INTO regimendrugs (id, amtpertime, drug, modified, regimen, timesperday, notes, regimendrugsindex) VALUES (1, 1, 17, 'T', 0, 2, NULL, 0);
INSERT INTO regimendrugs (id, amtpertime, drug, modified, regimen, timesperday, notes, regimendrugsindex) VALUES (2, 1, 10, 'T', 0, 2, NULL, 1);
INSERT INTO regimendrugs (id, amtpertime, drug, modified, regimen, timesperday, notes, regimendrugsindex) VALUES (3, 1, 8, 'T', 0, 1, NULL, 2);
INSERT INTO regimendrugs (id, amtpertime, drug, modified, regimen, timesperday, notes, regimendrugsindex) VALUES (4, 1, 17, 'T', 1, 2, NULL, 0);
INSERT INTO regimendrugs (id, amtpertime, drug, modified, regimen, timesperday, notes, regimendrugsindex) VALUES (5, 1, 10, 'T', 1, 2, NULL, 1);
INSERT INTO regimendrugs (id, amtpertime, drug, modified, regimen, timesperday, notes, regimendrugsindex) VALUES (6, 1, 11, 'T', 1, 2, NULL, 2);

INSERT INTO stock (id, drug, batchnumber, datereceived, stockcenter, expirydate, modified, shelfnumber, unitsreceived, manufacturer, hasunitsremaining, unitprice) VALUES (1, 1, 'BATCH1', '2008-03-26 00:00:00', 1, '2010-03-26 00:00:00', 'F', '123', 100, 'manufacturer', 'T', NULL);
INSERT INTO stock (id, drug, batchnumber, datereceived, stockcenter, expirydate, modified, shelfnumber, unitsreceived, manufacturer, hasunitsremaining, unitprice) VALUES (2, 2, 'BATCH2', '2008-03-26 00:00:00', 1, '2010-03-26 00:00:00', 'F', '123', 100, 'manufacturer', 'T', NULL);
INSERT INTO stock (id, drug, batchnumber, datereceived, stockcenter, expirydate, modified, shelfnumber, unitsreceived, manufacturer, hasunitsremaining, unitprice) VALUES (3, 3, 'BATCH3', '2008-03-26 00:00:00', 1, '2010-03-26 00:00:00', 'F', '123', 100, 'manufacturer', 'T', NULL);
INSERT INTO stock (id, drug, batchnumber, datereceived, stockcenter, expirydate, modified, shelfnumber, unitsreceived, manufacturer, hasunitsremaining, unitprice) VALUES (4, 4, 'BATCH4', '2008-03-26 00:00:00', 1, '2010-03-26 00:00:00', 'F', '123', 100, 'manufacturer', 'T', NULL);
INSERT INTO stock (id, drug, batchnumber, datereceived, stockcenter, expirydate, modified, shelfnumber, unitsreceived, manufacturer, hasunitsremaining, unitprice) VALUES (5, 5, 'BATCH5', '2008-03-26 00:00:00', 1, '2010-03-26 00:00:00', 'F', '123', 100, 'manufacturer', 'T', NULL);
INSERT INTO stock (id, drug, batchnumber, datereceived, stockcenter, expirydate, modified, shelfnumber, unitsreceived, manufacturer, hasunitsremaining, unitprice) VALUES (6, 6, 'BATCH6', '2008-03-26 00:00:00', 1, '2010-03-26 00:00:00', 'F', '123', 100, 'manufacturer', 'T', NULL);
INSERT INTO stock (id, drug, batchnumber, datereceived, stockcenter, expirydate, modified, shelfnumber, unitsreceived, manufacturer, hasunitsremaining, unitprice) VALUES (7, 7, 'BATCH7', '2008-03-26 00:00:00', 1, '2010-03-26 00:00:00', 'F', '123', 100, 'manufacturer', 'T', NULL);
INSERT INTO stock (id, drug, batchnumber, datereceived, stockcenter, expirydate, modified, shelfnumber, unitsreceived, manufacturer, hasunitsremaining, unitprice) VALUES (8, 8, 'BATCH8', '2008-03-26 00:00:00', 1, '2010-03-26 00:00:00', 'F', '123', 100, 'manufacturer', 'T', NULL);
INSERT INTO stock (id, drug, batchnumber, datereceived, stockcenter, expirydate, modified, shelfnumber, unitsreceived, manufacturer, hasunitsremaining, unitprice) VALUES (9, 9, 'BATCH9', '2008-03-26 00:00:00', 1, '2010-03-26 00:00:00', 'F', '123', 100, 'manufacturer', 'T', NULL);
INSERT INTO stock (id, drug, batchnumber, datereceived, stockcenter, expirydate, modified, shelfnumber, unitsreceived, manufacturer, hasunitsremaining, unitprice) VALUES (10, 10, 'BATCH10', '2008-03-26 00:00:00', 1, '2010-03-26 00:00:00', 'F', '123', 100, 'manufacturer', 'T', NULL);
INSERT INTO stock (id, drug, batchnumber, datereceived, stockcenter, expirydate, modified, shelfnumber, unitsreceived, manufacturer, hasunitsremaining, unitprice) VALUES (11, 11, 'BATCH11', '2008-03-26 00:00:00', 1, '2010-03-26 00:00:00', 'F', '123', 100, 'manufacturer', 'T', NULL);
INSERT INTO stock (id, drug, batchnumber, datereceived, stockcenter, expirydate, modified, shelfnumber, unitsreceived, manufacturer, hasunitsremaining, unitprice) VALUES (12, 12, 'BATCH12', '2008-03-26 00:00:00', 1, '2010-03-26 00:00:00', 'F', '123', 100, 'manufacturer', 'T', NULL);
INSERT INTO stock (id, drug, batchnumber, datereceived, stockcenter, expirydate, modified, shelfnumber, unitsreceived, manufacturer, hasunitsremaining, unitprice) VALUES (13, 13, 'BATCH13', '2008-03-26 00:00:00', 1, '2010-03-26 00:00:00', 'F', '123', 100, 'manufacturer', 'T', NULL);
INSERT INTO stock (id, drug, batchnumber, datereceived, stockcenter, expirydate, modified, shelfnumber, unitsreceived, manufacturer, hasunitsremaining, unitprice) VALUES (14, 14, 'BATCH14', '2008-03-26 00:00:00', 1, '2010-03-26 00:00:00', 'F', '123', 100, 'manufacturer', 'T', NULL);
INSERT INTO stock (id, drug, batchnumber, datereceived, stockcenter, expirydate, modified, shelfnumber, unitsreceived, manufacturer, hasunitsremaining, unitprice) VALUES (15, 15, 'BATCH15', '2008-03-26 00:00:00', 1, '2010-03-26 00:00:00', 'F', '123', 100, 'manufacturer', 'T', NULL);
INSERT INTO stock (id, drug, batchnumber, datereceived, stockcenter, expirydate, modified, shelfnumber, unitsreceived, manufacturer, hasunitsremaining, unitprice) VALUES (16, 16, 'BATCH16', '2008-03-26 00:00:00', 1, '2010-03-26 00:00:00', 'F', '123', 100, 'manufacturer', 'T', NULL);
INSERT INTO stock (id, drug, batchnumber, datereceived, stockcenter, expirydate, modified, shelfnumber, unitsreceived, manufacturer, hasunitsremaining, unitprice) VALUES (17, 17, 'BATCH17', '2008-03-26 00:00:00', 1, '2010-03-26 00:00:00', 'F', '123', 100, 'manufacturer', 'T', NULL);
INSERT INTO stock (id, drug, batchnumber, datereceived, stockcenter, expirydate, modified, shelfnumber, unitsreceived, manufacturer, hasunitsremaining, unitprice) VALUES (18, 18, 'BATCH18', '2008-03-26 00:00:00', 1, '2010-03-26 00:00:00', 'F', '123', 100, 'manufacturer', 'T', NULL);
INSERT INTO stock (id, drug, batchnumber, datereceived, stockcenter, expirydate, modified, shelfnumber, unitsreceived, manufacturer, hasunitsremaining, unitprice) VALUES (19, 19, 'BATCH19', '2008-03-26 00:00:00', 1, '2010-03-26 00:00:00', 'F', '123', 100, 'manufacturer', 'T', NULL);
INSERT INTO stock (id, drug, batchnumber, datereceived, stockcenter, expirydate, modified, shelfnumber, unitsreceived, manufacturer, hasunitsremaining, unitprice) VALUES (20, 20, 'BATCH30', '2008-03-26 00:00:00', 1, '2010-03-26 00:00:00', 'F', '123', 100, 'manufacturer', 'T', NULL);
INSERT INTO stock (id, drug, batchnumber, datereceived, stockcenter, expirydate, modified, shelfnumber, unitsreceived, manufacturer, hasunitsremaining, unitprice) VALUES (21, 21, 'BATCH31', '2008-03-26 00:00:00', 1, '2010-03-26 00:00:00', 'F', '123', 100, 'manufacturer', 'T', NULL);
INSERT INTO stock (id, drug, batchnumber, datereceived, stockcenter, expirydate, modified, shelfnumber, unitsreceived, manufacturer, hasunitsremaining, unitprice) VALUES (22, 22, 'BATCH32', '2008-03-26 00:00:00', 1, '2010-03-26 00:00:00', 'F', '123', 100, 'manufacturer', 'T', NULL);
INSERT INTO stock (id, drug, batchnumber, datereceived, stockcenter, expirydate, modified, shelfnumber, unitsreceived, manufacturer, hasunitsremaining, unitprice) VALUES (23, 23, 'BATCH33', '2008-03-26 00:00:00', 1, '2010-03-26 00:00:00', 'F', '123', 100, 'manufacturer', 'T', NULL);
INSERT INTO stock (id, drug, batchnumber, datereceived, stockcenter, expirydate, modified, shelfnumber, unitsreceived, manufacturer, hasunitsremaining, unitprice) VALUES (24, 24, 'BATCH34', '2008-03-26 00:00:00', 1, '2010-03-26 00:00:00', 'F', '123', 100, 'manufacturer', 'T', NULL);
INSERT INTO stock (id, drug, batchnumber, datereceived, stockcenter, expirydate, modified, shelfnumber, unitsreceived, manufacturer, hasunitsremaining, unitprice) VALUES (25, 25, 'BATCH35', '2008-03-26 00:00:00', 1, '2010-03-26 00:00:00', 'F', '123', 100, 'manufacturer', 'T', NULL);
INSERT INTO stock (id, drug, batchnumber, datereceived, stockcenter, expirydate, modified, shelfnumber, unitsreceived, manufacturer, hasunitsremaining, unitprice) VALUES (26, 26, 'BATCH36', '2008-03-26 00:00:00', 1, '2010-03-26 00:00:00', 'F', '123', 100, 'manufacturer', 'T', NULL);