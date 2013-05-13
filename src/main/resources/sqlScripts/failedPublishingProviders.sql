--
-- Display had problems publishing providers. 
--

select distinct PROVIDERS.*
from CALLBACKS,
     AUDIT_API_REQUEST,
     PROVIDERS
where CALLBACKS.LOGON_USER = AUDIT_API_REQUEST.LOGON_USER
and PROVIDERS.ID = CALLBACKS.LOGON_BROADCAST_PROVIDER
and AUDIT_API_REQUEST.HTTP_STATUS_CODE not in(0,200)
and AUDIT_API_REQUEST.USER_EMAIL is not null;

/*
select distinct providers.*
from providers 
INNER JOIN callbacks
on providers.ID = callbacks.LOGON_BROADCAST_PROVIDER
and callbacks.LOGON_USER in (select audit_api_request.LOGON_USER
                             from audit_api_request 
                             where audit_api_request.HTTP_STATUS_CODE <> 0
                             and audit_api_request.USER_EMAIL is not null);
*/
