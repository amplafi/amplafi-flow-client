--
-- Display the list of user emails that have not posted this week
--

select user_emails.*,
           	    providers.*,
                roles.*
from providers,
    roles,
    users,
    user_emails
where  roles.OWNING_BP = providers.ID
and   roles.USER = users.ID
and   user_emails.USER = users.ID
and   providers.ID not in (select distinct OWNING_BP
                           from broadcast_messages 
                           where
                                broadcast_messages.PUB_DATE > date_add(now(),interval - 7 day)
                           and  broadcast_messages.PUB_DATE < now() )
group by user_emails.ID;



/*
select USER_EMAILS.*,
       PROVIDERS.*,
       ROLES.*
from PROVIDERS,
    ROLES,
    USERS,
    USER_EMAILS
where  ROLES.OWNING_BP = PROVIDERS.ID
and   ROLES.USER = USERS.ID
and   USER_EMAILS.USER = USERS.ID
and   PROVIDERS.ID not in (select distinct OWNING_BP
                           from BROADCAST_MESSAGES 
                           where
                                BROADCAST_MESSAGES.PUB_DATE > date_add(now(),interval - 7 day)
                           and  BROADCAST_MESSAGES.PUB_DATE < now() )
group by USER_EMAILS.ID;
*/
