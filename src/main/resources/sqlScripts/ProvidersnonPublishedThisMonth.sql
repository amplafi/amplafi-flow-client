--
-- Display BroadcastProviders have not published content at all in last month.
--


select PROVIDERS.* 
from PROVIDERS
where  PROVIDERS.ID not in (select distinct OWNING_BP
                           from BROADCAST_MESSAGES 
                           where
                                BROADCAST_MESSAGES.PUB_DATE > date_add(now(),interval - 30 day)
                           and  BROADCAST_MESSAGES.PUB_DATE < now() );
