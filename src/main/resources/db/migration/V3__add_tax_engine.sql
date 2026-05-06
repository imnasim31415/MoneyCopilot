CREATE TABLE taxpayer_profiles (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID NOT NULL UNIQUE REFERENCES users(id),
    category            VARCHAR(50) NOT NULL DEFAULT 'GENERAL_MALE',
    location_type       VARCHAR(50) NOT NULL DEFAULT 'DHAKA_CHATTOGRAM',
    date_of_birth       DATE,
    has_disabled_child  BOOLEAN NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE tax_configurations (
    id              SERIAL PRIMARY KEY,
    assessment_year VARCHAR(10) NOT NULL UNIQUE,
    is_active       BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE tax_thresholds (
    id                  SERIAL PRIMARY KEY,
    config_id           INTEGER NOT NULL REFERENCES tax_configurations(id),
    taxpayer_category   VARCHAR(50) NOT NULL,
    threshold_amount    DECIMAL(12, 2) NOT NULL,
    UNIQUE (config_id, taxpayer_category)
);

CREATE TABLE tax_slabs (
    id              SERIAL PRIMARY KEY,
    config_id       INTEGER NOT NULL REFERENCES tax_configurations(id),
    slab_order      INTEGER NOT NULL,
    income_up_to    DECIMAL(14, 2),
    rate_percentage DECIMAL(5, 2) NOT NULL,
    UNIQUE (config_id, slab_order)
);

CREATE TABLE minimum_tax_rules (
    id              SERIAL PRIMARY KEY,
    config_id       INTEGER NOT NULL REFERENCES tax_configurations(id),
    location_type   VARCHAR(50) NOT NULL,
    minimum_amount  DECIMAL(12, 2) NOT NULL,
    is_new_taxpayer BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE (config_id, location_type, is_new_taxpayer)
);

CREATE TABLE investments (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID NOT NULL REFERENCES users(id),
    fiscal_year VARCHAR(10) NOT NULL,
    category    VARCHAR(100) NOT NULL,
    amount      DECIMAL(12, 2) NOT NULL CHECK (amount > 0),
    description VARCHAR(255),
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted_at  TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_investments_user_fiscal ON investments(user_id, fiscal_year);

-- ── AY 2025-2026 (active) ──────────────────────────────────────────────────
INSERT INTO tax_configurations (assessment_year, is_active) VALUES ('2025-2026', TRUE);

INSERT INTO tax_thresholds (config_id, taxpayer_category, threshold_amount) VALUES
    (1, 'GENERAL_MALE',    350000.00),
    (1, 'FEMALE',          400000.00),
    (1, 'SENIOR_CITIZEN',  400000.00),
    (1, 'DISABLED',        475000.00),
    (1, 'THIRD_GENDER',    475000.00),
    (1, 'FREEDOM_FIGHTER', 500000.00);

INSERT INTO tax_slabs (config_id, slab_order, income_up_to, rate_percentage) VALUES
    (1, 1,   100000.00,  5.00),
    (1, 2,   400000.00, 10.00),
    (1, 3,   500000.00, 15.00),
    (1, 4,   500000.00, 20.00),
    (1, 5,  2000000.00, 25.00),
    (1, 6,        NULL, 30.00);

INSERT INTO minimum_tax_rules (config_id, location_type, minimum_amount, is_new_taxpayer) VALUES
    (1, 'DHAKA_CHATTOGRAM', 5000.00, FALSE),
    (1, 'OTHER_CITY_CORP',  4000.00, FALSE),
    (1, 'NON_CITY',         3000.00, FALSE);

-- ── AY 2026-2027 (proposed, inactive) ─────────────────────────────────────
INSERT INTO tax_configurations (assessment_year, is_active) VALUES ('2026-2027', FALSE);

INSERT INTO tax_thresholds (config_id, taxpayer_category, threshold_amount) VALUES
    (2, 'GENERAL_MALE',    375000.00),
    (2, 'FEMALE',          425000.00),
    (2, 'SENIOR_CITIZEN',  425000.00),
    (2, 'DISABLED',        500000.00),
    (2, 'THIRD_GENDER',    500000.00),
    (2, 'FREEDOM_FIGHTER', 525000.00);

INSERT INTO tax_slabs (config_id, slab_order, income_up_to, rate_percentage) VALUES
    (2, 1,   400000.00, 10.00),
    (2, 2,   500000.00, 15.00),
    (2, 3,   500000.00, 20.00),
    (2, 4,  2000000.00, 25.00),
    (2, 5,        NULL, 30.00);

INSERT INTO minimum_tax_rules (config_id, location_type, minimum_amount, is_new_taxpayer) VALUES
    (2, 'DHAKA_CHATTOGRAM', 5000.00, FALSE),
    (2, 'OTHER_CITY_CORP',  5000.00, FALSE),
    (2, 'NON_CITY',         5000.00, FALSE),
    (2, 'DHAKA_CHATTOGRAM', 1000.00, TRUE),
    (2, 'OTHER_CITY_CORP',  1000.00, TRUE),
    (2, 'NON_CITY',         1000.00, TRUE);
