CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE SCHEMA IF NOT EXISTS auth;

--------------------------------------------------------------------------------------------------------
-- Permission Table
CREATE TABLE auth.permissions
(
    permission_id           BIGSERIAL PRIMARY KEY,
    permission_name         VARCHAR(255) NOT NULL,
    permission_description  VARCHAR(500),

    created_at              TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at              TIMESTAMP WITH TIME ZONE NOT NULL,

    created_by              UUID,
    updated_by              UUID,

    version                 BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uk_permission UNIQUE (permission_name)
);

CREATE INDEX idx_permission
    ON auth.permissions(permission_name);

--------------------------------------------------------------------------------------------------------

-- Role Table
CREATE TABLE auth.roles
(
    role_id                 BIGSERIAL PRIMARY KEY,
    role_name               VARCHAR(255) NOT NULL,
    role_description        VARCHAR(500),

    created_at              TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at              TIMESTAMP WITH TIME ZONE NOT NULL,

    created_by              UUID,
    updated_by              UUID,

    version                 BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uk_role UNIQUE (role_name)
);

CREATE INDEX idx_role
    ON auth.roles(role_name);

--------------------------------------------------------------------------------------------------------

-- Role-Permission Table
CREATE TABLE auth.role_permissions
(
    role_permission_id          BIGSERIAL PRIMARY KEY,
    role_id                     BIGINT NOT NULL,
    permission_id               BIGINT NOT NULL,

    created_at                  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at                  TIMESTAMP WITH TIME ZONE NOT NULL,

    created_by                  UUID,
    updated_by                  UUID,

    version                     BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_role_permission_role
        FOREIGN KEY(role_id)
            REFERENCES auth.roles(role_id)
            ON DELETE CASCADE,

    CONSTRAINT fk_role_permission_permission
        FOREIGN KEY(permission_id)
            REFERENCES auth.permissions(permission_id)
            ON DELETE CASCADE,

    CONSTRAINT uk_role_permission
        UNIQUE(role_id,permission_id)
);

CREATE INDEX idx_role_permission_role
    ON auth.role_permissions(role_id);

CREATE INDEX idx_role_permission_permission
    ON auth.role_permissions(permission_id);

--------------------------------------------------------------------------------------------------------

-- User Table
CREATE TABLE auth.users
(
    user_id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username                    VARCHAR(255) NOT NULL,
    password                    VARCHAR(255),
    email                       VARCHAR(255) NOT NULL,
    attempts                    INT NOT NULL DEFAULT 0,
    mfa_enabled                 BOOLEAN NOT NULL DEFAULT FALSE,
    oauth2_id                   VARCHAR(255),
    provider                    VARCHAR(30) NOT NULL DEFAULT 'LOCAL',
    enabled                     BOOLEAN NOT NULL DEFAULT FALSE,
    deleted                     BOOLEAN NOT NULL DEFAULT FALSE,
    account_non_expired         BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked          BOOLEAN NOT NULL DEFAULT TRUE,
    credentials_non_expired     BOOLEAN NOT NULL DEFAULT TRUE,
    scheduled_deletion_at       TIMESTAMP WITH TIME ZONE,
    locked_until                TIMESTAMP WITH TIME ZONE,

    created_at                  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at                  TIMESTAMP WITH TIME ZONE NOT NULL,

    created_by                  UUID,
    updated_by                  UUID,

    version                     BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uk_username UNIQUE (username),
    CONSTRAINT uk_email UNIQUE (email),

    CONSTRAINT chk_provider
        CHECK (provider IN ('LOCAL', 'GOOGLE', 'GITHUB'))
);

CREATE INDEX idx_username
    ON auth.users(username);

CREATE INDEX idx_email
    ON auth.users(email);

--------------------------------------------------------------------------------------------------------

-- User-Profile Table
CREATE TABLE auth.user_profiles
(
    user_id         UUID PRIMARY KEY,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    dob             DATE,
    gender          VARCHAR(20),
    address         VARCHAR(500),

    created_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL,

    created_by      UUID,
    updated_by      UUID,

    version         BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_user_profile_user
        FOREIGN KEY (user_id)
            REFERENCES auth.users (user_id)
            ON DELETE CASCADE,

    CONSTRAINT chk_gender
        CHECK (gender IN ('MALE', 'FEMALE', 'PREFER_NOT_TO_SAY'))
);

CREATE INDEX idx_user_profile_full_name
    ON auth.user_profiles (first_name, last_name);

CREATE INDEX idx_user_profile_dob
    ON auth.user_profiles (dob);

CREATE INDEX idx_user_profile_gender
    ON auth.user_profiles (gender);

--------------------------------------------------------------------------------------------------------

-- User-Role Table
CREATE TABLE auth.user_roles
(
    user_role_id       BIGSERIAL PRIMARY KEY,

    user_id            UUID NOT NULL,
    role_id            BIGINT NOT NULL,

    status             VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    created_at         TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at         TIMESTAMP WITH TIME ZONE NOT NULL,

    created_by         UUID,
    updated_by         UUID,

    version            BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_user_role_user
        FOREIGN KEY (user_id)
            REFERENCES auth.users (user_id)
            ON DELETE CASCADE,

    CONSTRAINT fk_user_role_role
        FOREIGN KEY (role_id)
            REFERENCES auth.roles (role_id)
            ON DELETE CASCADE,

    CONSTRAINT uk_user_role
        UNIQUE (user_id, role_id),

    CONSTRAINT chk_user_role_status
        CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE INDEX idx_user_role_user
    ON auth.user_roles (user_id);

CREATE INDEX idx_user_role_role
    ON auth.user_roles (role_id);

--------------------------------------------------------------------------------------------------------

-- User-Role-Permission Table
CREATE TABLE auth.user_role_permissions
(
    user_role_permission_id     BIGSERIAL PRIMARY KEY,
    user_role_id                BIGINT NOT NULL,
    permission_id               BIGINT NOT NULL,
    effect                      VARCHAR(20) NOT NULL,

    created_at                  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at                  TIMESTAMP WITH TIME ZONE NOT NULL,

    created_by                  UUID,
    updated_by                  UUID,

    version                     BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uk_user_role_permission
        UNIQUE (user_role_id, permission_id),

    CONSTRAINT fk_user_role_permission_user_role
        FOREIGN KEY (user_role_id)
            REFERENCES auth.user_roles (user_role_id)
            ON DELETE CASCADE,

    CONSTRAINT fk_user_role_permission_permission
        FOREIGN KEY (permission_id)
            REFERENCES auth.permissions (permission_id)
            ON DELETE CASCADE,

    CONSTRAINT chk_permission_effect
        CHECK (effect IN ('GRANT', 'REVOKE'))
);

CREATE INDEX idx_user_role_permission_user_role
    ON auth.user_role_permissions (user_role_id);

CREATE INDEX idx_user_role_permission_permission
    ON auth.user_role_permissions (permission_id);

--------------------------------------------------------------------------------------------------------

-- Refresh Token Table
CREATE TABLE auth.refresh_tokens
(
    refresh_token_id      BIGSERIAL PRIMARY KEY,

    jti                   VARCHAR(128) NOT NULL,
    session_id            VARCHAR(128) NOT NULL,
    user_id               UUID NOT NULL,

    expires_at            TIMESTAMP WITH TIME ZONE NOT NULL,

    revoked               BOOLEAN NOT NULL DEFAULT FALSE,

    replaced_by_token     VARCHAR(128),

    created_at            TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at            TIMESTAMP WITH TIME ZONE NOT NULL,

    created_by            UUID,
    updated_by            UUID,

    version               BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uk_refresh_token_jti
        UNIQUE (jti),

    CONSTRAINT fk_refresh_token_user
        FOREIGN KEY (user_id)
            REFERENCES auth.users (user_id)
            ON DELETE CASCADE
);

CREATE INDEX idx_refresh_token_user
    ON auth.refresh_tokens (user_id);

CREATE INDEX idx_refresh_token_session
    ON auth.refresh_tokens (session_id);

CREATE INDEX idx_refresh_token_expires_at
    ON auth.refresh_tokens (expires_at);

CREATE INDEX idx_refresh_token_revoked_expires
    ON auth.refresh_tokens (revoked, expires_at);

--------------------------------------------------------------------------------------------------------

CREATE TABLE auth.otp
(
    otp_id       BIGSERIAL PRIMARY KEY,

    challenge_id VARCHAR(36)  NOT NULL,
    email        VARCHAR(320) NOT NULL,
    otp_hash     VARCHAR(255) NOT NULL,

    purpose      VARCHAR(30)  NOT NULL,
    status       VARCHAR(20)  NOT NULL,

    expires_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    verified_at  TIMESTAMP WITH TIME ZONE,

    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by   UUID,
    updated_by   UUID,

    CONSTRAINT uk_otp_challenge_id
        UNIQUE (challenge_id)
);

CREATE INDEX idx_otp_email
    ON auth.otp (email);

CREATE INDEX idx_otp_email_purpose
    ON auth.otp (email, purpose);

CREATE INDEX idx_otp_status
    ON auth.otp (status);

CREATE INDEX idx_otp_expires_at
    ON auth.otp (expires_at);

--------------------------------------------------------------------------------------------------------