-- fix episode inconsistencies. (atleast 1 episde and lastest 1 has no stop date = accntstatus true, and no episodes or all episodes have stopdates = accntstatus false)

update patient
set accountstatus = not accountstatus
where patient.id in 
(--get only patients with mismatching accountstatus and most recent episode
	select patient as patientid from
	(--get most recent episode for each patient
		select distinct on (e.patient) patient, startdate, stopdate, accountstatus, patientid from episode e, patient p
		where p.id = e.patient
		order by patient, startdate desc
	) as problemPPL
	where ((stopdate is null and accountstatus = false) 
	or (stopdate is not null and accountstatus = true))
)



-- Check that if a patient's account status is set to TRUE, that they have one, and only one open, current episode

select patient.patientid, patient.accountstatus,
episode.startreason, episode.startdate, episode.stopreason, episode.stopdate
from patient, episode
where accountstatus = true
and episode.stopdate is not null
and episode.patient = patient.id

-- Check that if a patient's account status is set to FALSE, that they do not have any current, open episodes

select patient.patientid, patient.accountstatus,
episode.startreason, episode.startdate, episode.stopreason, episode.stopdate
from patient, episode
where accountstatus = false
and episode.stopdate is null
and episode.patient = patient.id 


-- Reasons for episode start which are not in simple domain.

select patient.patientid, patient.accountstatus,
episode.startreason, episode.startdate, episode.stopreason, episode.stopdate
from patient, episode
where (accountstatus = true or accountstatus = false)
and (episode.stopdate is null or episode.stopdate is not null)
and episode.patient = patient.id 
and episode.startreason not in
(select value from simpledomain
where name = 'activation_reason');

-- Reasons for episode stop which are not in simple domain.

select patient.patientid, patient.accountstatus,
episode.startreason, episode.startdate, episode.stopreason, episode.stopdate
from patient, episode
where (accountstatus = true or accountstatus = false)
and (episode.stopdate is not null)
and episode.patient = patient.id 
and episode.stopreason not in
(select value from simpledomain
where name = 'deactivation_reason');

