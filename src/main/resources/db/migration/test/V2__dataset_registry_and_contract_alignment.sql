-- V2: aligns the schema with openapi_v2.yaml (2.1.0) and the dataset registry of ER v1.2.
--     Together with the changes below this is ER version 1.3 (see PR notes).
-- Identical copy lives in db/migration/test. Always change both folders together.
-- Existing rows (if any) are back-filled before NOT NULL / CHECK constraints are added.

-- ---------------------------------------------------------------- USERS
-- Contract: register/login use email + displayName, there is no username.
ALTER TABLE users ADD COLUMN display_name VARCHAR(100);
UPDATE users SET display_name = LEFT(username, 100) WHERE display_name IS NULL;
ALTER TABLE users ALTER COLUMN display_name SET NOT NULL;
ALTER TABLE users DROP COLUMN username;           -- also drops its UNIQUE constraint

-- ---------------------------------------------------------------- DATASETS
ALTER TABLE datasets ADD COLUMN dataset_key VARCHAR(100);
UPDATE datasets SET dataset_key = 'dataset_' || id WHERE dataset_key IS NULL;
ALTER TABLE datasets ALTER COLUMN dataset_key SET NOT NULL;
ALTER TABLE datasets
    ADD CONSTRAINT uq_datasets_workspace_key UNIQUE (workspace_id, dataset_key);

-- ---------------------------------------------------------------- DATASET_COLUMNS
ALTER TABLE dataset_columns ADD COLUMN label VARCHAR(150);
UPDATE dataset_columns SET label = column_name WHERE label IS NULL;
ALTER TABLE dataset_columns ALTER COLUMN label SET NOT NULL;
ALTER TABLE dataset_columns ADD COLUMN groupable  BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE dataset_columns ADD COLUMN filterable BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE dataset_columns SET data_type = 'NUMBER' WHERE data_type IN ('DECIMAL', 'INTEGER');
ALTER TABLE dataset_columns
    ADD CONSTRAINT ck_dataset_columns_data_type CHECK (data_type IN ('NUMBER', 'STRING', 'DATE'));

-- ---------------------------------------------------------------- DASHBOARDS
ALTER TABLE dashboards ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- ---------------------------------------------------------------- WIDGET_CONFIGS
-- MetricQuery.filters (JSON array of {field, operator, value}) and WidgetPosition (x, y, w, h).
ALTER TABLE widget_configs ADD COLUMN filters_json TEXT;
ALTER TABLE widget_configs ADD COLUMN position_x INTEGER;
ALTER TABLE widget_configs ADD COLUMN position_y INTEGER;
ALTER TABLE widget_configs ADD COLUMN position_w INTEGER;
ALTER TABLE widget_configs ADD COLUMN position_h INTEGER;
ALTER TABLE widget_configs ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- Contract: metricField and aggregation are required.
ALTER TABLE widget_configs ALTER COLUMN metric_column_key SET NOT NULL;
ALTER TABLE widget_configs ALTER COLUMN aggregation SET NOT NULL;
ALTER TABLE widget_configs
    ADD CONSTRAINT ck_widget_configs_chart_type CHECK (chart_type IN ('BAR', 'LINE', 'KPI'));
ALTER TABLE widget_configs
    ADD CONSTRAINT ck_widget_configs_aggregation CHECK (aggregation IN ('SUM', 'COUNT', 'AVG', 'MIN', 'MAX'));

-- ---------------------------------------------------------------- WORKSPACE_MEMBERS
ALTER TABLE workspace_members
    ADD CONSTRAINT ck_workspace_members_role CHECK (role IN ('ADMIN', 'EDITOR', 'VIEWER'));

-- ---------------------------------------------------------------- DELETE BEHAVIOUR
-- DELETE /workspaces/{id} removes everything the workspace owns in one statement.
-- widget_configs -> datasets stays NO ACTION on purpose: deleting a dataset alone must not silently drop widgets
-- (it is still allowed inside a workspace delete, because the widgets are gone by the end of the statement).
ALTER TABLE workspace_members DROP CONSTRAINT fk_workspace_members_workspace;
ALTER TABLE workspace_members
    ADD CONSTRAINT fk_workspace_members_workspace
        FOREIGN KEY (workspace_id) REFERENCES workspaces (id) ON DELETE CASCADE;

ALTER TABLE datasets DROP CONSTRAINT fk_datasets_workspace;
ALTER TABLE datasets
    ADD CONSTRAINT fk_datasets_workspace
        FOREIGN KEY (workspace_id) REFERENCES workspaces (id) ON DELETE CASCADE;

ALTER TABLE dataset_columns DROP CONSTRAINT fk_dataset_columns_dataset;
ALTER TABLE dataset_columns
    ADD CONSTRAINT fk_dataset_columns_dataset
        FOREIGN KEY (dataset_id) REFERENCES datasets (id) ON DELETE CASCADE;

ALTER TABLE sales_records DROP CONSTRAINT fk_sales_records_dataset;
ALTER TABLE sales_records
    ADD CONSTRAINT fk_sales_records_dataset
        FOREIGN KEY (dataset_id) REFERENCES datasets (id) ON DELETE CASCADE;

ALTER TABLE dashboards DROP CONSTRAINT fk_dashboards_workspace;
ALTER TABLE dashboards
    ADD CONSTRAINT fk_dashboards_workspace
        FOREIGN KEY (workspace_id) REFERENCES workspaces (id) ON DELETE CASCADE;

ALTER TABLE widget_configs DROP CONSTRAINT fk_widget_configs_dashboard;
ALTER TABLE widget_configs
    ADD CONSTRAINT fk_widget_configs_dashboard
        FOREIGN KEY (workspace_id, dashboard_id) REFERENCES dashboards (workspace_id, id) ON DELETE CASCADE;

ALTER TABLE widget_configs DROP CONSTRAINT fk_widget_configs_workspace;
ALTER TABLE widget_configs
    ADD CONSTRAINT fk_widget_configs_workspace
        FOREIGN KEY (workspace_id) REFERENCES workspaces (id) ON DELETE CASCADE;

-- --------------------------------- INDEXES ---------------------------------------------

CREATE INDEX idx_sales_records_dataset_date   ON sales_records (dataset_id, sale_date);
