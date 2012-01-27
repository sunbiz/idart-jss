--This query shows all drugs in the regimens--

select  regimen.regimenname as "Drug Group Name",
 	regimen.druggroup as "Regimen",
 	drug.name as "Drug",
 	form.actionlanguage1 as "Take",
 	regimendrugs.amtpertime  as "Amount",
 	form.formlanguage1 as "Unit",   
 	regimendrugs.timesperday as "Times Per Day",
 	regimendrugs.regimendrugsindex as "Index"
 	
from regimen, regimendrugs, drug, form
 	
where regimen.id = regimendrugs.regimen 
      and regimendrugs.drug = drug.id and
      drug.form = form.id
 	
order by regimen.regimenname, regimendrugs.regimendrugsindex