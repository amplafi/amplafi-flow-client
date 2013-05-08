--
-- Count of posts sent to each social network. 
--
/*
select count(*)  as "Count", "Facebook Posts" as "Social Network" from MESSAGE_POINTS where POINT_TYPE = "MEP" and URL LIKE "%facebook%"
union  select count(*) ,  "Twitter_Posts" as "Social Network" from MESSAGE_POINTS where POINT_TYPE = "MEP" and URL LIKE "%twitter%"
union  select count(*) , "Tumblr Posts" as "Social Network" from MESSAGE_POINTS where POINT_TYPE = "MEP" and URL LIKE "%tumblr%"
union  select count(*) ,  "Stripe Posts" as "Social Network" from MESSAGE_POINTS where POINT_TYPE = "MEP" and URL LIKE "%stripe%";
*/

 
 

select  COUNT(*),
        broadcast_provider_url.ROOT_URL,
        message_points.URL,
        broadcast_messages.PUBLIC_URL
from  broadcast_provider_url,
      providers,
      broadcast_messages,
      message_point_envelope,
      message_points
where providers.PROVIDER_URL = broadcast_provider_url.ID
and broadcast_messages.OWNING_BP = providers.ID
and message_point_envelope.BROADCAST_MESSAGE = broadcast_messages.ID
and message_point_envelope.MESSAGE_POINT = message_points.ID
and message_points.POINT_TYPE = "MEP"
Group by broadcast_provider_url.ROOT_URL;
 