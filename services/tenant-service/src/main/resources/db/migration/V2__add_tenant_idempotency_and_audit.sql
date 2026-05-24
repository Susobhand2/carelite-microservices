ALTER TABLE master.tenants
ADD COLUMN IF NOT EXISTS request_hash VARCHAR(64);

ALTER TABLE master.tenants
ADD COLUMN IF NOT EXISTS created_by VARCHAR(120);

ALTER TABLE master.tenants
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(120);

UPDATE master.tenants
SET request_hash = 'legacy'
WHERE request_hash IS NULL;

UPDATE master.tenants
SET created_by = 'system'
WHERE created_by IS NULL;

UPDATE master.tenants
SET updated_by = 'system'
WHERE updated_by IS NULL;

ALTER TABLE master.tenants
ALTER COLUMN request_hash SET NOT NULL;

ALTER TABLE master.tenants
ALTER COLUMN created_by SET NOT NULL;

ALTER TABLE master.tenants
ALTER COLUMN updated_by SET NOT NULL;