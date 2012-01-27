-- This query sets the dateLeft, pickupDate and dateReceived dates to the packDate --
update package
set dateleft = packdate, pickupdate = packdate,  datereceived = packdate
where dateleft is null
and pickupdate is null
and datereceived is null;