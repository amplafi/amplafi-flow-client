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

/*
-----------------------------------------------

//not finished yet

Select *, count(*) from 
(Select ProviderSignup.cohort,
        ProviderSignup.year,
        ProviderSignup.week,
        DATEDIFF(PostsDate.post_date,ProviderSignup.CREATE_TIME ) as post_day,
        ProviderSignup.CREATE_TIME,
        ProviderSignup.provider_id,
        PostsDate.post_date
From 
    (select (year(CREATE_TIME) * 52) + week( CREATE_TIME) cohort,
        week( CREATE_TIME) as week,
        PROVIDERS.id provider_id,
         CREATE_TIME,
        year(CREATE_TIME) as year
    from PROVIDERS) ProviderSignup
join
    (select bm.owning_bp provider_id,
        bm.PUB_DATE post_date
    from broadcast_messages bm) PostsDate
on  ProviderSignup.provider_id = PostsDate.provider_id
and DATEDIFF(PostsDate.post_date,ProviderSignup.CREATE_TIME ) < 7) as CohortPosts
group by cohort

Select count(*) from broadcast_messages bm where DATEDIFF(bm.pub_date,"2012-08-28 23:00:00" ) < 7


-------------------------------------------------------------------------------------------------------------------------

Select *, count(*) from 
(Select ProviderSignup.cohort ,
        ProviderSignup.year,
        ProviderSignup.week,
        DATEDIFF(PostsDate.post_date,ProviderSignup.CREATE_TIME ) as post_day,
        ProviderSignup.CREATE_TIME,
        ProviderSignup.provider_id,
        PostsDate.post_date
From 
    (select (year(CREATE_TIME) * 52) + week( CREATE_TIME) cohort,
        week( CREATE_TIME) as week,
        PROVIDERS.id provider_id,
         CREATE_TIME,
        year(CREATE_TIME) as year
    from PROVIDERS) ProviderSignup
join
    (select bm.owning_bp provider_id,
        bm.PUB_DATE post_date
    from broadcast_messages bm) PostsDate
on  ProviderSignup.provider_id = PostsDate.provider_id
and DATEDIFF(PostsDate.post_date,ProviderSignup.CREATE_TIME ) < 7) as CohortPosts
group by cohort, provider_id, post_day


-------------------------------------------------------------------------------------------------------------------------
select day1.cohort, day1.year, day1.week, day1.day1min, day1.day1avg, day1.day1max, day2.day2min, day2.day2avg, day2.day2max from
    (select cohort, year, week, min(post_count) "day1min", avg(post_count)"day1avg", max(post_count) "day1max", null "day2min", null "day2avg", null "day2max" from
        (Select *, count(*) post_count from 
            (Select ProviderSignup.cohort ,
                    ProviderSignup.year,
                    ProviderSignup.week,
                    DATEDIFF(PostsDate.post_date,ProviderSignup.CREATE_TIME ) as post_day,
                    ProviderSignup.CREATE_TIME,
                    ProviderSignup.provider_id,
                    PostsDate.post_date
            From 
                (select (year(CREATE_TIME) * 52) + week( CREATE_TIME) cohort,
                    week( CREATE_TIME) as week,
                    PROVIDERS.id provider_id,
                     CREATE_TIME,
                    year(CREATE_TIME) as year
                from PROVIDERS) ProviderSignup
            join
                (select bm.owning_bp provider_id,
                    bm.PUB_DATE post_date
                from broadcast_messages bm) PostsDate
            on  ProviderSignup.provider_id = PostsDate.provider_id
            and DATEDIFF(PostsDate.post_date,ProviderSignup.CREATE_TIME ) < 7) as CohortPosts
            group by cohort, provider_id, post_day) post_counts

        where
            post_day = 0
        group by cohort ) day1,
    (select cohort, year, week, null "day1min", null "day1avg", null "day1max", min(post_count) "day2min", avg(post_count)"day2avg", max(post_count) "day2max" from
        (Select *, count(*) post_count from 
        (Select ProviderSignup.cohort ,
                ProviderSignup.year,
                ProviderSignup.week,
                DATEDIFF(PostsDate.post_date,ProviderSignup.CREATE_TIME ) as post_day,
                ProviderSignup.CREATE_TIME,
                ProviderSignup.provider_id,
                PostsDate.post_date
        From 
            (select (year(CREATE_TIME) * 52) + week( CREATE_TIME) cohort,
                week( CREATE_TIME) as week,
                PROVIDERS.id provider_id,
                 CREATE_TIME,
                year(CREATE_TIME) as year
            from PROVIDERS) ProviderSignup
        join
            (select bm.owning_bp provider_id,
                bm.PUB_DATE post_date
            from broadcast_messages bm) PostsDate
        on  ProviderSignup.provider_id = PostsDate.provider_id
        and DATEDIFF(PostsDate.post_date,ProviderSignup.CREATE_TIME ) < 7) as CohortPosts
        group by cohort, provider_id, post_day) post_counts

    where
        post_day = 1
    group by cohort) day2
where day1.cohort = day2.cohort




-------------------------------------------------------------------------------------------------------------------------
select day1.cohort, day1.year, day1.week, day1.day1min, day1.day1avg, day1.day1max, day2.day2min, day2.day2avg, day2.day2max, day3.day3min, day3.day3avg, day3.day3max ,day4.day4min, day4.day4avg, day4.day4max ,day5.day5min, day5.day5avg, day5.day5max ,day6.day6min, day6.day6avg, day6.day6max ,day7.day7min, day7.day7avg, day7.day7max  from
    ( select cohort, year, week, min(post_count) "day1min", avg(post_count) "day1avg", max(post_count) "day1max", null "day2min", null "day2avg", null "day2max", null "day3min", null "day3avg", null "day3max",null "day4min", null "day4avg", null "day4max",null "day5min", null "day5avg", null "day5max",null "day6min", null "day6avg", null "day6max",null "day7min", null "day7avg", null "day7max" from
        (Select *, count(*) post_count from 
            (Select ProviderSignup.cohort ,
                    ProviderSignup.year,
                    ProviderSignup.week,
                    DATEDIFF(PostsDate.post_date,ProviderSignup.CREATE_TIME ) as post_day,
                    ProviderSignup.CREATE_TIME,
                    ProviderSignup.provider_id,
                    PostsDate.post_date
            From 
                (select (year(CREATE_TIME) * 52) + week( CREATE_TIME) cohort,
                    week( CREATE_TIME) as week,
                    PROVIDERS.id provider_id,
                     CREATE_TIME,
                    year(CREATE_TIME) as year
                from PROVIDERS) ProviderSignup
            join
                (select bm.owning_bp provider_id,
                    bm.PUB_DATE post_date
                from broadcast_messages bm) PostsDate
            on  ProviderSignup.provider_id = PostsDate.provider_id
            and DATEDIFF(PostsDate.post_date,ProviderSignup.CREATE_TIME ) < 7) as CohortPosts
            group by cohort, provider_id, post_day) post_counts

        where
            post_day = 0
        group by cohort ) day1
LEFT OUTER JOIN 
    (select cohort, year, week, null "day1min", null "day1avg", null "day1max", min(post_count) "day2min", avg(post_count) "day2avg", max(post_count) "day2max", null "day3min", null "day3avg", null "day3max",null "day4min", null "day4avg", null "day4max",null "day5min", null "day5avg", null "day5max",null "day6min", null "day6avg", null "day6max",null "day7min", null "day7avg", null "day7max" from
        (Select *, count(*) post_count from 
        (Select ProviderSignup.cohort ,
                ProviderSignup.year,
                ProviderSignup.week,
                DATEDIFF(PostsDate.post_date,ProviderSignup.CREATE_TIME ) as post_day,
                ProviderSignup.CREATE_TIME,
                ProviderSignup.provider_id,
                PostsDate.post_date
        From 
            (select (year(CREATE_TIME) * 52) + week( CREATE_TIME) cohort,
                week( CREATE_TIME) as week,
                PROVIDERS.id provider_id,
                 CREATE_TIME,
                year(CREATE_TIME) as year
            from PROVIDERS) ProviderSignup
        join
            (select bm.owning_bp provider_id,
                bm.PUB_DATE post_date
            from broadcast_messages bm) PostsDate
        on  ProviderSignup.provider_id = PostsDate.provider_id
        and DATEDIFF(PostsDate.post_date,ProviderSignup.CREATE_TIME ) < 7) as CohortPosts
        group by cohort, provider_id, post_day) post_counts

    where
        post_day = 1
    group by cohort) day2
ON day1.cohort = day2.cohort
LEFT OUTER JOIN     
    (select cohort, year, week, null "day1min", null "day1avg", null "day1max", min(post_count) "day2min", avg(post_count) "day2avg", max(post_count) "day2max", null "day3min", null "day3avg", null "day3max",null "day4min", null "day4avg", null "day4max",null "day5min", null "day5avg", null "day5max",null "day6min", null "day6avg", null "day6max",null "day7min", null "day7avg", null "day7max" from
        (Select *, count(*) post_count from 
        (Select ProviderSignup.cohort ,
                ProviderSignup.year,
                ProviderSignup.week,
                DATEDIFF(PostsDate.post_date,ProviderSignup.CREATE_TIME ) as post_day,
                ProviderSignup.CREATE_TIME,
                ProviderSignup.provider_id,
                PostsDate.post_date
        From 
            (select (year(CREATE_TIME) * 52) + week( CREATE_TIME) cohort,
                week( CREATE_TIME) as week,
                PROVIDERS.id provider_id,
                 CREATE_TIME,
                year(CREATE_TIME) as year
            from PROVIDERS) ProviderSignup
        join
            (select bm.owning_bp provider_id,
                bm.PUB_DATE post_date
            from broadcast_messages bm) PostsDate
        on  ProviderSignup.provider_id = PostsDate.provider_id
        and DATEDIFF(PostsDate.post_date,ProviderSignup.CREATE_TIME ) < 7) as CohortPosts
        group by cohort, provider_id, post_day) post_counts

    where
        post_day = 2
    group by cohort) day3
ON day2.cohort = day3.cohort
LEFT OUTER JOIN     
    (select cohort, year, week, null "day1min", null "day1avg", null "day1max", min(post_count) "day2min", avg(post_count) "day2avg", max(post_count) "day2max", null "day3min", null "day3avg", null "day3max",null "day4min", null "day4avg", null "day4max",null "day5min", null "day5avg", null "day5max",null "day6min", null "day6avg", null "day6max",null "day7min", null "day7avg", null "day7max" from
        (Select *, count(*) post_count from 
        (Select ProviderSignup.cohort ,
                ProviderSignup.year,
                ProviderSignup.week,
                DATEDIFF(PostsDate.post_date,ProviderSignup.CREATE_TIME ) as post_day,
                ProviderSignup.CREATE_TIME,
                ProviderSignup.provider_id,
                PostsDate.post_date
        From 
            (select (year(CREATE_TIME) * 52) + week( CREATE_TIME) cohort,
                week( CREATE_TIME) as week,
                PROVIDERS.id provider_id,
                 CREATE_TIME,
                year(CREATE_TIME) as year
            from PROVIDERS) ProviderSignup
        join
            (select bm.owning_bp provider_id,
                bm.PUB_DATE post_date
            from broadcast_messages bm) PostsDate
        on  ProviderSignup.provider_id = PostsDate.provider_id
        and DATEDIFF(PostsDate.post_date,ProviderSignup.CREATE_TIME ) < 7) as CohortPosts
        group by cohort, provider_id, post_day) post_counts

    where
        post_day = 3
    group by cohort) day4
ON day3.cohort = day4.cohort
LEFT OUTER JOIN     
    (select cohort, year, week, null "day1min", null "day1avg", null "day1max", min(post_count) "day2min", avg(post_count) "day2avg", max(post_count) "day2max", null "day3min", null "day3avg", null "day3max",null "day4min", null "day4avg", null "day4max",null "day5min", null "day5avg", null "day5max",null "day6min", null "day6avg", null "day6max",null "day7min", null "day7avg", null "day7max" from
        (Select *, count(*) post_count from 
        (Select ProviderSignup.cohort ,
                ProviderSignup.year,
                ProviderSignup.week,
                DATEDIFF(PostsDate.post_date,ProviderSignup.CREATE_TIME ) as post_day,
                ProviderSignup.CREATE_TIME,
                ProviderSignup.provider_id,
                PostsDate.post_date
        From 
            (select (year(CREATE_TIME) * 52) + week( CREATE_TIME) cohort,
                week( CREATE_TIME) as week,
                PROVIDERS.id provider_id,
                 CREATE_TIME,
                year(CREATE_TIME) as year
            from PROVIDERS) ProviderSignup
        join
            (select bm.owning_bp provider_id,
                bm.PUB_DATE post_date
            from broadcast_messages bm) PostsDate
        on  ProviderSignup.provider_id = PostsDate.provider_id
        and DATEDIFF(PostsDate.post_date,ProviderSignup.CREATE_TIME ) < 7) as CohortPosts
        group by cohort, provider_id, post_day) post_counts

    where
        post_day = 4
    group by cohort) day5
ON day4.cohort = day5.cohort
LEFT OUTER JOIN     
    (select cohort, year, week, null "day1min", null "day1avg", null "day1max", min(post_count) "day2min", avg(post_count) "day2avg", max(post_count) "day2max", null "day3min", null "day3avg", null "day3max",null "day4min", null "day4avg", null "day4max",null "day5min", null "day5avg", null "day5max",null "day6min", null "day6avg", null "day6max",null "day7min", null "day7avg", null "day7max" from
        (Select *, count(*) post_count from 
        (Select ProviderSignup.cohort ,
                ProviderSignup.year,
                ProviderSignup.week,
                DATEDIFF(PostsDate.post_date,ProviderSignup.CREATE_TIME ) as post_day,
                ProviderSignup.CREATE_TIME,
                ProviderSignup.provider_id,
                PostsDate.post_date
        From 
            (select (year(CREATE_TIME) * 52) + week( CREATE_TIME) cohort,
                week( CREATE_TIME) as week,
                PROVIDERS.id provider_id,
                 CREATE_TIME,
                year(CREATE_TIME) as year
            from PROVIDERS) ProviderSignup
        join
            (select bm.owning_bp provider_id,
                bm.PUB_DATE post_date
            from broadcast_messages bm) PostsDate
        on  ProviderSignup.provider_id = PostsDate.provider_id
        and DATEDIFF(PostsDate.post_date,ProviderSignup.CREATE_TIME ) < 7) as CohortPosts
        group by cohort, provider_id, post_day) post_counts

    where
        post_day = 5
    group by cohort) day6
ON day5.cohort = day6.cohort    
LEFT OUTER JOIN     
    (select cohort, year, week, null "day1min", null "day1avg", null "day1max", min(post_count) "day2min", avg(post_count) "day2avg", max(post_count) "day2max", null "day3min", null "day3avg", null "day3max",null "day4min", null "day4avg", null "day4max",null "day5min", null "day5avg", null "day5max",null "day6min", null "day6avg", null "day6max",null "day7min", null "day7avg", null "day7max" from
        (Select *, count(*) post_count from 
        (Select ProviderSignup.cohort ,
                ProviderSignup.year,
                ProviderSignup.week,
                DATEDIFF(PostsDate.post_date,ProviderSignup.CREATE_TIME ) as post_day,
                ProviderSignup.CREATE_TIME,
                ProviderSignup.provider_id,
                PostsDate.post_date
        From 
            (select (year(CREATE_TIME) * 52) + week( CREATE_TIME) cohort,
                week( CREATE_TIME) as week,
                PROVIDERS.id provider_id,
                 CREATE_TIME,
                year(CREATE_TIME) as year
            from PROVIDERS) ProviderSignup
        join
            (select bm.owning_bp provider_id,
                bm.PUB_DATE post_date
            from broadcast_messages bm) PostsDate
        on  ProviderSignup.provider_id = PostsDate.provider_id
        and DATEDIFF(PostsDate.post_date,ProviderSignup.CREATE_TIME ) < 7) as CohortPosts
        group by cohort, provider_id, post_day) post_counts

    where
        post_day = 6
    group by cohort) day7
ON day6.cohort = day7.cohort    


===============================================================================================================================
*/
select day1.cohort, day1.year, day1.week, day1.day1min, day1.day1avg, day1.day1max, day2.day2min, day2.day2avg, day2.day2max, day3.day3min, day3.day3avg, day3.day3max ,day4.day4min, day4.day4avg, day4.day4max ,day5.day5min, day5.day5avg, day5.day5max from
    ( select cohort, year, week, min(post_count) "day1min", avg(post_count) "day1avg", max(post_count) "day1max", null "day2min", null "day2avg", null "day2max", null "day3min", null "day3avg", null "day3max",null "day4min", null "day4avg", null "day4max",null "day5min", null "day5avg", null "day5max" from
        (Select *, count(*) post_count from 
            (Select ProviderSignup.cohort ,
                    ProviderSignup.year,
                    ProviderSignup.week,
                    DATEDIFF(PostsDate.post_date,ProviderSignup.CREATE_TIME ) as post_day,
                    ProviderSignup.CREATE_TIME,
                    ProviderSignup.provider_id,
                    PostsDate.post_date
            From 
                (select (year(CREATE_TIME) * 52) + week( CREATE_TIME) cohort,
                    week( CREATE_TIME) as week,
                    PROVIDERS.ID provider_id,
                     CREATE_TIME,
                    year(CREATE_TIME) as year
                from PROVIDERS) ProviderSignup
            join
                (select bm.OWNING_BP provider_id,
                    bm.PUB_DATE post_date
                from BROADCAST_MESSAGES bm) PostsDate
            on  ProviderSignup.provider_id = PostsDate.provider_id
            and DATEDIFF(PostsDate.post_date,ProviderSignup.CREATE_TIME ) < 7) as CohortPosts
            group by cohort, provider_id, post_day) post_counts

        where
            post_day = 0
        group by cohort ) day1
LEFT OUTER JOIN 
    (select cohort, year, week, null "day1min", null "day1avg", null "day1max", min(post_count) "day2min", avg(post_count) "day2avg", max(post_count) "day2max", null "day3min", null "day3avg", null "day3max",null "day4min", null "day4avg", null "day4max",null "day5min", null "day5avg", null "day5max" from
        (Select *, count(*) post_count from 
        (Select ProviderSignup.cohort ,
                ProviderSignup.year,
                ProviderSignup.week,
                DATEDIFF(PostsDate.post_date,ProviderSignup.CREATE_TIME ) as post_day,
                ProviderSignup.CREATE_TIME,
                ProviderSignup.provider_id,
                PostsDate.post_date
        From 
            (select (year(CREATE_TIME) * 52) + week( CREATE_TIME) cohort,
                week( CREATE_TIME) as week,
                PROVIDERS.ID provider_id,
                 CREATE_TIME,
                year(CREATE_TIME) as year
            from PROVIDERS) ProviderSignup
        join
            (select bm.OWNING_BP provider_id,
                bm.PUB_DATE post_date
            from BROADCAST_MESSAGES bm) PostsDate
        on  ProviderSignup.provider_id = PostsDate.provider_id
        and DATEDIFF(PostsDate.post_date,ProviderSignup.CREATE_TIME ) < 7) as CohortPosts
        group by cohort, provider_id, post_day) post_counts

    where
        post_day = 1
    group by cohort) day2
ON day1.cohort = day2.cohort
LEFT OUTER JOIN     
    (select cohort, year, week, null "day1min", null "day1avg", null "day1max", min(post_count) "day2min", avg(post_count) "day2avg", max(post_count) "day2max", null "day3min", null "day3avg", null "day3max",null "day4min", null "day4avg", null "day4max",null "day5min", null "day5avg", null "day5max" from
        (Select *, count(*) post_count from 
        (Select ProviderSignup.cohort ,
                ProviderSignup.year,
                ProviderSignup.week,
                DATEDIFF(PostsDate.post_date,ProviderSignup.CREATE_TIME ) as post_day,
                ProviderSignup.CREATE_TIME,
                ProviderSignup.provider_id,
                PostsDate.post_date
        From 
                (select (year(CREATE_TIME) * 52) + week( CREATE_TIME) cohort,
                    week( CREATE_TIME) as week,
                    PROVIDERS.ID provider_id,
                     CREATE_TIME,
                    year(CREATE_TIME) as year
                from PROVIDERS) ProviderSignup
            join
                (select bm.OWNING_BP provider_id,
                    bm.PUB_DATE post_date
                from BROADCAST_MESSAGES bm) PostsDate
        on  ProviderSignup.provider_id = PostsDate.provider_id
        and DATEDIFF(PostsDate.post_date,ProviderSignup.CREATE_TIME ) < 7) as CohortPosts
        group by cohort, provider_id, post_day) post_counts

    where
        post_day = 2
    group by cohort) day3
ON day2.cohort = day3.cohort
LEFT OUTER JOIN     
    (select cohort, year, week, null "day1min", null "day1avg", null "day1max", min(post_count) "day2min", avg(post_count) "day2avg", max(post_count) "day2max", null "day3min", null "day3avg", null "day3max",null "day4min", null "day4avg", null "day4max",null "day5min", null "day5avg", null "day5max" from
        (Select *, count(*) post_count from 
        (Select ProviderSignup.cohort ,
                ProviderSignup.year,
                ProviderSignup.week,
                DATEDIFF(PostsDate.post_date,ProviderSignup.CREATE_TIME ) as post_day,
                ProviderSignup.CREATE_TIME,
                ProviderSignup.provider_id,
                PostsDate.post_date
        From 
                (select (year(CREATE_TIME) * 52) + week( CREATE_TIME) cohort,
                    week( CREATE_TIME) as week,
                    PROVIDERS.ID provider_id,
                     CREATE_TIME,
                    year(CREATE_TIME) as year
                from PROVIDERS) ProviderSignup
            join
                (select bm.OWNING_BP provider_id,
                    bm.PUB_DATE post_date
                from BROADCAST_MESSAGES bm) PostsDate
        on  ProviderSignup.provider_id = PostsDate.provider_id
        and DATEDIFF(PostsDate.post_date,ProviderSignup.CREATE_TIME ) < 7) as CohortPosts
        group by cohort, provider_id, post_day) post_counts

    where
        post_day = 3
    group by cohort) day4
ON day3.cohort = day4.cohort
LEFT OUTER JOIN     
    (select cohort, year, week, null "day1min", null "day1avg", null "day1max", min(post_count) "day2min", avg(post_count) "day2avg", max(post_count) "day2max", null "day3min", null "day3avg", null "day3max",null "day4min", null "day4avg", null "day4max",null "day5min", null "day5avg", null "day5max" from
        (Select *, count(*) post_count from 
        (Select ProviderSignup.cohort ,
                ProviderSignup.year,
                ProviderSignup.week,
                DATEDIFF(PostsDate.post_date,ProviderSignup.CREATE_TIME ) as post_day,
                ProviderSignup.CREATE_TIME,
                ProviderSignup.provider_id,
                PostsDate.post_date
        From 
                (select (year(CREATE_TIME) * 52) + week( CREATE_TIME) cohort,
                    week( CREATE_TIME) as week,
                    PROVIDERS.ID provider_id,
                     CREATE_TIME,
                    year(CREATE_TIME) as year
                from PROVIDERS) ProviderSignup
            join
                (select bm.OWNING_BP provider_id,
                    bm.PUB_DATE post_date
                from BROADCAST_MESSAGES bm) PostsDate
        on  ProviderSignup.provider_id = PostsDate.provider_id
        and DATEDIFF(PostsDate.post_date,ProviderSignup.CREATE_TIME ) < 7) as CohortPosts
        group by cohort, provider_id, post_day) post_counts

    where
        post_day = 4
    group by cohort) day5
ON day4.cohort = day5.cohort









