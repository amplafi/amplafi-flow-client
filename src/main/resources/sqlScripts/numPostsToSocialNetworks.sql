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
        BROADCAST_PROVIDER_URL.ROOT_URL,
        MESSAGE_POINTS.URL,
        BROADCAST_MESSAGES.PUBLIC_URL
from  BROADCAST_PROVIDER_URL,
      PROVIDERS,
      BROADCAST_MESSAGES,
      MESSAGE_POINT_ENVELOPE,
      MESSAGE_POINTS
where PROVIDERS.PROVIDER_URL = BROADCAST_PROVIDER_URL.ID
and BROADCAST_MESSAGES.OWNING_BP = PROVIDERS.ID
and MESSAGE_POINT_ENVELOPE.BROADCAST_MESSAGE = BROADCAST_MESSAGES.ID
and MESSAGE_POINT_ENVELOPE.MESSAGE_POINT = MESSAGE_POINTS.ID
and MESSAGE_POINTS.POINT_TYPE = "MEP"
Group by BROADCAST_PROVIDER_URL.ROOT_URL;
 
