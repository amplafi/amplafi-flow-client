--
-- Display the list of user emails that have never used the system
--

select user_emails.*,
       providers.*,
       roles.*
from providers,
     roles,
     users,
     user_emails
where  roles.OWNING_BP = providers.ID
and    roles.USER = users.ID
and    user_emails.USER = users.ID
and    providers.ID not in (select distinct OWNING_BP
                            from broadcast_messages);




/*
select USER_EMAILS.*,
       PROVIDERS.*,
       ROLES.*
from PROVIDERS,
     ROLES,
     USERS,
     USER_EMAILS
where  ROLES.OWNING_BP = PROVIDERS.ID
and    ROLES.USER = USERS.ID
and    USER_EMAILS.USER = USERS.ID
and    PROVIDERS.ID not in (select distinct OWNING_BP
                            from BROADCAST_MESSAGES);
*/
