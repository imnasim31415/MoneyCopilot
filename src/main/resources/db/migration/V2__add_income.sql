CREATE TABLE income_sources (
    id              SERIAL PRIMARY KEY,
    name            VARCHAR(50) NOT NULL UNIQUE,
    icon            VARCHAR(50),
    is_system       BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE incomes (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES users(id),
    amount          DECIMAL(12, 2) NOT NULL CHECK (amount > 0),
    source_id       INTEGER NOT NULL REFERENCES income_sources(id),
    income_date     DATE NOT NULL,
    description     VARCHAR(255),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_incomes_user_id ON incomes(user_id);
CREATE INDEX idx_incomes_user_date ON incomes(user_id, income_date DESC);

INSERT INTO income_sources (name, icon, is_system) VALUES
    ('SALARY', 'briefcase', TRUE),
    ('FREELANCE', 'laptop', TRUE),
    ('BUSINESS', 'building', TRUE),
    ('INVESTMENT', 'trending-up', TRUE),
    ('RENTAL', 'home', TRUE),
    ('GIFT', 'gift', TRUE),
    ('OTHERS', 'box', TRUE);
