--
-- Display BroadcastProviders have not published content at all in last month.
--


select providers.* 
from providers
where  providers.ID not in (select distinct OWNING_BP
                           from broadcast_messages 
                           where
                                broadcast_messages.PUB_DATE > date_add(now(),interval - 30 day)
                           and  broadcast_messages.PUB_DATE < now() );