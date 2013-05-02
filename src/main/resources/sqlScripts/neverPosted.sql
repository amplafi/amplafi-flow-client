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
where  roles.OWNING_BP = providers.id
and    roles.user = users.id
and    user_emails.user = users.id
and    providers.id not in (select distinct owning_bp
                            from broadcast_messages);
