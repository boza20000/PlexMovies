1. You understand how schema changes are described and tracked
 Schemas and changes are tracked in a table databasechangelog where every change is logged 
with its cache (md5 caching used) so no changes could be done to past chainsets. 
Аlso every log contains information about the change like author, timestamp, md5sum and etc..
They could be rolled back in case we want, and they will be deleted in reverse chronological order, deleting all logs for them in the changelog table.

2. You understand how changes are applied automatically at startup
 On startup liquibase acquires a lock of the database or waits for it. When acquired it locks the db and starts executing its chainsets.
It checks if they have been run already if they have not been it executes them.

3. You understand rollback and why applied changes are immutable
All changes get logged with their md5 cache sum. So if you try to change old chainset liquibase will know the cache is different
and will throw exception. It could roll back changes by reading in reverse chronological order the logs and running inverse sql statements 
after which it will remove the chainsets from the logs. There three main strategies for rolling back changes 
- rolling back the n last changes 
- rolling back to a timestamp
- rolling back to a release checkpoint tag