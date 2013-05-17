--
-- Diagnosing problems with messages:

--
/*
Given a message externalId and a broadcast provider uri return:

all MESSAGE_POINT_ENVELOPE  
all the BROADCAST_MESSAGE_FRAGMENTS
Look at the error messages and we need to figure out what happened with the bad messages.

*/


select MPE.*,bmf.*
from MESSAGE_POINT_ENVELOPE mpe,BROADCAST_MESSAGES bm,PROVIDERS p,BROADCAST_MESSAGE_FRAGMENTS bmf
where mpe.EXTERNAL_ID = 1
and p.PROVIDER_URL = 4
and mpe.BROADCAST_MESSAGE=bm.ID
and bmf.FRAGMENT_OWNER = bm.ID
and bm.OWNING_BP=P.ID




