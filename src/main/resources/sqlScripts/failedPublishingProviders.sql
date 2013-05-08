--
-- Display had problems publishing providers. 
--

select distinct providers.*
from callbacks,
     audit_api_request,
     providers
where callbacks.LOGON_USER = audit_api_request.LOGON_USER
and providers.ID = callbacks.LOGON_BROADCAST_PROVIDER
and audit_api_request.HTTP_STATUS_CODE <> 0
and audit_api_request.USER_EMAIL is not null;