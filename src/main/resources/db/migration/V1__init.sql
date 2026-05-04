-- V1__init.sql
-- Initial schema: users, categories, expenses

CREATE TABLE users (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE categories (
    id        SERIAL PRIMARY KEY,
    name      VARCHAR(50)  NOT NULL UNIQUE,
    icon      VARCHAR(50),
    is_system BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE expenses (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      UUID           NOT NULL REFERENCES users(id),
    amount       DECIMAL(12, 2) NOT NULL CHECK (amount > 0),
    category_id  INTEGER        NOT NULL REFERENCES categories(id),
    expense_date DATE           NOT NULL,
    description  VARCHAR(255),
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted_at   TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_expenses_user_id       ON expenses(user_id);
CREATE INDEX idx_expenses_user_date     ON expenses(user_id, expense_date DESC);
CREATE INDEX idx_expenses_user_category ON expenses(user_id, category_id);

INSERT INTO categories (name, icon, is_system) VALUES
    ('FOOD',          'utensils',    TRUE),
    ('TRANSPORT',     'car',         TRUE),
    ('BILLS',         'file-text',   TRUE),
    ('HOUSING',       'home',        TRUE),
    ('HEALTHCARE',    'heart-pulse', TRUE),
    ('EDUCATION',     'book-open',   TRUE),
    ('ENTERTAINMENT', 'film',        TRUE),
    ('CLOTHING',      'shirt',       TRUE),
    ('OTHERS',        'box',         TRUE);
