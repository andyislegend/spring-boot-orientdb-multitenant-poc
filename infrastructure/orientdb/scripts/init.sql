CONNECT remote:localhost/databases/demodb root rootpwd;

-- Enabling Partitioned Graphs
ALTER CLASS V SUPERCLASS ORestricted;
ALTER CLASS E SUPERCLASS ORestricted;

-- Creating users for multi-tenant demo
CREATE USER client1 IDENTIFIED BY client1_pwd ROLE writer;
CREATE USER client2 IDENTIFIED BY client2_pwd ROLE writer;

-- Partition Country vertices among tenants
UPDATE Countries SET _allow = _allow || (SELECT @rid FROM OUser WHERE name = 'client1') WHERE Id <= 100;
UPDATE Countries SET _allow = _allow || (SELECT @rid FROM OUser WHERE name = 'client2') WHERE Id BETWEEN 101 AND 200;

-- Partition IsFromCountry edges among tenants
UPDATE IsFromCountry SET _allow = _allow || (SELECT @rid FROM OUser WHERE name = 'client1') WHERE in.Id <= 100;
UPDATE IsFromCountry SET _allow = _allow || (SELECT @rid FROM OUser WHERE name = 'client2') WHERE in.Id BETWEEN 101 AND 200;

-- Partition IsFromCountry's OUT references (Customer vertices) among tenants
UPDATE (select expand( out ) from IsFromCountry where in.Id <= 100) SET _allow = _allow || (SELECT @rid FROM OUser WHERE name = 'client1');
UPDATE (select expand( out ) from IsFromCountry where in.Id BETWEEN 101 AND 200) SET _allow = _allow || (SELECT @rid FROM OUser WHERE name = 'client2');

-- Partition Customer's OUT 'HasProfile' edges among tenants
UPDATE HasProfile SET _allow = _allow || (SELECT @rid FROM OUser WHERE name = 'client1') WHERE out.@rid IN (select @rid from (select expand( out ) from IsFromCountry where in.Id <= 100));
UPDATE HasProfile SET _allow = _allow || (SELECT @rid FROM OUser WHERE name = 'client2') WHERE out.@rid IN (select @rid from (select expand( out ) from IsFromCountry where in.Id BEtWEEN 101 AND 200));

-- Partition Profile vertices among tenants
UPDATE (select expand( out('HasProfile') ) from (select expand( out ) from IsFromCountry where in.Id <= 100)) SET _allow = _allow || (SELECT @rid FROM OUser WHERE name = 'client1');
UPDATE (select expand( out('HasProfile') ) from (select expand( out ) from IsFromCountry where in.Id BETWEEN 101 AND 200)) SET _allow = _allow || (SELECT @rid FROM OUser WHERE name = 'client2');

-- Update DB info
UPDATE DBInfo SET IsMultitenant = true;

DISCONNECT;