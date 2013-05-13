--
-- Display how many BroadcastProviders (BP) have signed up in last week, Since an arbitrary date.
--

select count(*) from PROVIDERS where CREATE_TIME > date_add(now(),interval - 7 day);
