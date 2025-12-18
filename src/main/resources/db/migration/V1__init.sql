-- UUID generator
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ----------------------------
-- Canonical categories
-- ----------------------------
CREATE TABLE IF NOT EXISTS categories (
                                          code TEXT PRIMARY KEY,
                                          name TEXT NOT NULL,
                                          description TEXT NULL,
                                          created_at TIMESTAMPTZ NOT NULL DEFAULT now()
    );

-- Seed canonical categories (safe to re-run)
INSERT INTO categories (code, name, description) VALUES
                                                     ('INCOME_SALARY', 'Salary', 'Salary/wages income'),
                                                     ('INCOME_BUSINESS', 'Business Income', 'Business/side-hustle income'),
                                                     ('INCOME_REFUND', 'Refund', 'Refunds/chargebacks'),
                                                     ('INCOME_OTHER', 'Other Income', 'Other income'),

                                                     ('HOUSING_RENT', 'Housing / Rent', 'Rent and housing payments'),
                                                     ('UTILITIES', 'Utilities', 'Power/water/waste utilities'),
                                                     ('INTERNET_AIRTIME', 'Internet & Airtime', 'Data, airtime, ISP charges'),
                                                     ('GROCERIES', 'Groceries', 'Supermarket and groceries'),
                                                     ('FOOD_DINING', 'Food & Dining', 'Restaurants, fast food, dining'),

                                                     ('TRANSPORT', 'Transport', 'Ride-hailing, fuel, public transport'),
                                                     ('TRAVEL', 'Travel', 'Flights, hotels, travel bookings'),

                                                     ('HEALTHCARE', 'Healthcare', 'Hospital, pharmacy, medical services'),
                                                     ('EDUCATION', 'Education', 'School fees, courses, learning'),

                                                     ('SHOPPING', 'Shopping', 'Retail shopping and e-commerce'),
                                                     ('ENTERTAINMENT', 'Entertainment', 'Movies, events, leisure'),
                                                     ('SUBSCRIPTIONS', 'Subscriptions', 'Recurring subscriptions (Netflix, iCloud, etc.)'),

                                                     ('FEES_CHARGES', 'Fees & Charges', 'Bank fees, stamp duty, service charges'),
                                                     ('TAXES', 'Taxes', 'Government taxes and levies'),
                                                     ('SAVINGS_INVESTMENTS', 'Savings & Investments', 'Savings, investments, brokerage'),

                                                     ('TRANSFER_INTERNAL', 'Internal Transfer', 'Transfers between your own accounts'),
                                                     ('TRANSFER_EXTERNAL', 'External Transfer', 'Transfers to other people/businesses'),

                                                     ('CASH_WITHDRAWAL', 'Cash Withdrawal', 'ATM or cash withdrawal'),
                                                     ('DONATIONS_GIFTS', 'Donations & Gifts', 'Gifts, charity, donations'),

                                                     ('UNCATEGORIZED', 'Uncategorized', 'Needs review / not classified')
    ON CONFLICT (code) DO NOTHING;

-- ----------------------------
-- Users + profile
-- ----------------------------
CREATE TABLE IF NOT EXISTS users (
                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email TEXT NOT NULL,
    display_name TEXT NULL,
    avatar_url TEXT NULL,

    auth_provider TEXT NOT NULL,          -- e.g. 'google'
    provider_subject TEXT NOT NULL,       -- e.g. Google 'sub'

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT uq_users_provider UNIQUE (auth_provider, provider_subject)
    );

CREATE TABLE IF NOT EXISTS user_profile (
                                            user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    time_zone TEXT NOT NULL,              -- IANA TZ, e.g. Africa/Lagos
    default_currency CHAR(3) NOT NULL,    -- e.g. NGN

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
    );

-- ----------------------------
-- Imports + raw messages
-- ----------------------------
CREATE TABLE IF NOT EXISTS import_batch (
                                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,

    source_type TEXT NOT NULL,            -- SMS | EMAIL | CSV | PDF
    original_filename TEXT NULL,

    status TEXT NOT NULL DEFAULT 'CREATED', -- CREATED | PARSED | FAILED

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT chk_import_source_type CHECK (source_type IN ('SMS','EMAIL','CSV','PDF')),
    CONSTRAINT chk_import_status CHECK (status IN ('CREATED','PARSED','FAILED'))
    );

CREATE TABLE IF NOT EXISTS raw_message (
                                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    import_batch_id UUID NULL REFERENCES import_batch(id) ON DELETE SET NULL,

    source_type TEXT NOT NULL,            -- SMS | EMAIL | CSV | PDF
    received_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    raw_text TEXT NOT NULL,
    raw_hash TEXT NULL,                   -- sha256 or similar, computed by app (optional)

    metadata JSONB NOT NULL DEFAULT '{}'::jsonb,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT chk_raw_source_type CHECK (source_type IN ('SMS','EMAIL','CSV','PDF'))
    );

-- Unique per user when raw_hash is present (partial unique index)
CREATE UNIQUE INDEX IF NOT EXISTS uq_raw_message_user_hash
    ON raw_message (user_id, raw_hash)
    WHERE raw_hash IS NOT NULL;

-- ----------------------------
-- Merchants (user-scoped)
-- ----------------------------
CREATE TABLE IF NOT EXISTS merchants (
                                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,

    normalized_name TEXT NOT NULL,        -- e.g. 'JUMIA'
    display_name TEXT NULL,               -- e.g. 'Jumia'

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uq_merchants_user_normalized UNIQUE (user_id, normalized_name)
    );

-- ----------------------------
-- Transactions (normalized)
-- ----------------------------
CREATE TABLE IF NOT EXISTS transactions (
                                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    raw_message_id UUID NULL REFERENCES raw_message(id) ON DELETE SET NULL,

    direction TEXT NOT NULL,              -- DEBIT | CREDIT
    amount NUMERIC(19,2) NOT NULL CHECK (amount > 0),
    currency CHAR(3) NOT NULL,

    occurred_at TIMESTAMPTZ NOT NULL,
    occurred_at_source TEXT NOT NULL,     -- TRANSACTION_TIME | RECEIVED_AT

    description TEXT NULL,
    channel TEXT NULL,
    counterparty TEXT NULL,

    balance_after NUMERIC(19,2) NULL,
    reference TEXT NULL,

    merchant_id UUID NULL REFERENCES merchants(id) ON DELETE SET NULL,
    category_code TEXT NULL REFERENCES categories(code) ON DELETE SET NULL,

    dedupe_key TEXT NULL,                 -- computed by app (optional)

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT chk_tx_direction CHECK (direction IN ('DEBIT','CREDIT')),
    CONSTRAINT chk_tx_time_source CHECK (occurred_at_source IN ('TRANSACTION_TIME','RECEIVED_AT'))
    );

CREATE INDEX IF NOT EXISTS ix_transactions_user_occurred_at
    ON transactions (user_id, occurred_at DESC);

CREATE INDEX IF NOT EXISTS ix_transactions_user_category
    ON transactions (user_id, category_code);

CREATE INDEX IF NOT EXISTS ix_transactions_user_merchant
    ON transactions (user_id, merchant_id);

-- Unique per user when dedupe_key is present (partial unique index)
CREATE UNIQUE INDEX IF NOT EXISTS uq_transactions_user_dedupe
    ON transactions (user_id, dedupe_key)
    WHERE dedupe_key IS NOT NULL;

-- ----------------------------
-- Classification cache (rules/LLM)
-- ----------------------------
CREATE TABLE IF NOT EXISTS classification_cache (
                                                    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,

    signature TEXT NOT NULL,              -- stable hash/signature of normalized description/source
    merchant_normalized TEXT NULL,
    category_code TEXT NULL REFERENCES categories(code) ON DELETE SET NULL,

    confidence NUMERIC(4,3) NOT NULL DEFAULT 0.000,

    model TEXT NULL,                      -- e.g. 'gpt-...'
    prompt_version TEXT NULL,             -- e.g. 'v1'

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uq_classification_user_sig UNIQUE (user_id, signature)
    );
