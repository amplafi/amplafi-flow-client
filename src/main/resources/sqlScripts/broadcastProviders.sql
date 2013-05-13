--
-- Display BPs have activated, have published content by week.

--

/*
columns:

1.Group by Week of BPs CreateDate
2.Number of BPs
3.average number, min, max posts created in first day, second day after BPs signed up 
4.average number, min, max created in first week, second, third, fourth, second month

Example:


Cohort              BPs       First day       Second day ......


28 April 2013     10        ( 0, 2.3, 5 )  (1, 3.7, 6)
5 May 2013        15      (0, 2.4, 7 )     (2, 4.5, 9) 




 if a BP signed up on between 28 April 2013 and 4 May 2013 they are in the 28 April 2013 cohort. The times are relative to the BP's create date. 


So if a BP signed up at midnight 4 May 2013 the first day is  4 May 2013. If a BP signed up at midnight at 29 April 2013 the first day is 29 april 2013. Because both are in the 28 April-4 May 2013 cohort. 

*/


select week( CREATE_TIME) as cohort, count(*) as BPsCounts, 
     CREATE_TIME, year(CREATE_TIME) as year,
     DATE_SUB(  DATE_ADD(MAKEDATE( year(CREATE_TIME), 1),
        INTERVAL week( CREATE_TIME) WEEK),
        INTERVAL WEEKDAY(    DATE_ADD(MAKEDATE( year(CREATE_TIME), 1), INTERVAL week( CREATE_TIME) WEEK)
     ) -1 DAY) as Tuesday
  
from Providers
Group by cohort Order by CREATE_TIME ;

