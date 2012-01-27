
-- 	This query shows patient ids with the corresponding number of active prescriptions


select p.patientId, count(pr.id) as c

from patient p, prescription pr

where pr.current = 'T'

and p.id = pr.patient

group by p.patientId

order by c desc


