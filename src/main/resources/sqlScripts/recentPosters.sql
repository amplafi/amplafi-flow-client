--
-- Display the list of user emails (and everything) that have posted in the last 7 days
--
/*select distinct user_emails.*,
                broadcast_messages.*,
                providers.*,
                roles.*
from broadcast_messages,
     providers,
     roles,
     users,
     user_emails
where 
      providers.id = broadcast_messages.owning_bp 
and
      broadcast_messages.pub_date > date_add(now(),interval - 7 day) 
and   broadcast_messages.pub_date < now()
and   roles.OWNING_BP = providers.id
and   roles.user = users.id
and   user_emails.user = users.id
group by user_emails.id;
*/

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
