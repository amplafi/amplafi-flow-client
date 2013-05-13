--
-- Display the list of user emails (and everything) that have posted in the last 7 days
--
select distinct USER_EMAILS.*,
                BROADCAST_MESSAGES.*,
                PROVIDERS.*,
                ROLES.*
from BROADCAST_MESSAGES,
     PROVIDERS,
     ROLES,
     USERS,
     USER_EMAILS
where 
      PROVIDERS.ID = BROADCAST_MESSAGES.OWNING_BP 
and
      BROADCAST_MESSAGES.PUB_DATE > date_add(now(),interval - 7 day) 
and   BROADCAST_MESSAGES.PUB_DATE < now()
and   ROLES.OWNING_BP = PROVIDERS.ID
and   ROLES.USER = USERS.ID
and   USER_EMAILS.USER = USERS.ID
group by USER_EMAILS.ID;

/*
select distinct USER_EMAILS.*,
                BROADCAST_MESSAGES.*,
                PROVIDERS.*,
                ROLES.*
from BROADCAST_MESSAGES,
     PROVIDERS,
     ROLES,
     USERS,
     USER_EMAILS
where 
      PROVIDERS.ID = BROADCAST_MESSAGES.OWNING_BP
and
      BROADCAST_MESSAGES.PUB_DATE > date_add(now(),interval - 7 day) 
and   BROADCAST_MESSAGES.PUB_DATE < now()
and   ROLES.OWNING_BP = PROVIDERS.ID
and   ROLES.USER = USERS.ID
and   USER_EMAILS.USER = USERS.ID
group by USER_EMAILS.ID;
*/
