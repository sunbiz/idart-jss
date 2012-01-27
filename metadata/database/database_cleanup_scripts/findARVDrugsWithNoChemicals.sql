-- 2008-08-21 Tested on iDART 3.3.0
-- This script finds all drugs that are not marked as side treatment (ie. ARV drugs) that do not have any chemicalcompounds
select * from drug where sidetreatment = 'F'
and drug.id not in 
(
	select drug from chemicaldrugstrength as cds
);
