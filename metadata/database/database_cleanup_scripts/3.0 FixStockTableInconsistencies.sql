update stock 
set hasUnitsRemaining = 'F';

update stock 
set hasUnitsRemaining = 'T'
where stock.id in 

(select d.id
from

(select COALESCE(a.received - COALESCE(b.issued, 0) - COALESCE(c.adjusted, 0), 0) as bal, a.id
 
from (
select (sum(s.unitsreceived)*d.packsize) as received, s.id
from  stock as s, drug as d
where s.clinic = 78 
and s.drug = d.id
group by s.id, d.packsize
order by s.id

) as a

left outer join 

(select sum(pd.amount) as issued, s.id

from stock as s, packageddrugs as pd, package as p
where s.clinic = 78 
and pd.stock = s.id 
and pd.parentpackage = p.id 
group by s.id
order by s.id
) as b

on a.id = b.id 
left outer join

(select sum(sa.adjustedValue) as adjusted, s.id

from stock as s, stockAdjustment as sa
where s.clinic = 78 
and sa.stock = s.id 
group by s.id
order by s.id
) as c

on a.id = c.id
) as d

where d.bal > 0
group by id
order by id);

delete from stockLevel;