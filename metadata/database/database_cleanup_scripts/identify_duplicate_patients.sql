-- This query identifies all patientId's that occur more than once in the patient table

select patientId, count(patientId) as c
from patient 
group by patientId having count(patientId) > 1;

-------------------------------------------------

-- This query identifies all idnum's that occur more than once in the patient table

select idnum, count(idnum) as c
from patient 
group by patientId having count(idnum) > 1;
