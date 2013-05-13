--
-- Display the list of user emails that have never used the system
--

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
