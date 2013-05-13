--
-- the distributions of links in the content.
--

select bmf1.c1 as sbfNum,
       bmf2.c2 as nonSbfNum,
       bmf1.c1/bmf2.c2 as ratio 
from (select count(*) c1 
      from BROADCAST_MESSAGE_FRAGMENTS 
      where FRAGMENT_TYPE='SBF') bmf1,
     (select count(*) c2 
      from BROADCAST_MESSAGE_FRAGMENTS 
      where FRAGMENT_TYPE<>'SBF') bmf2;
