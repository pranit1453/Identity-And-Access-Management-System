CREATE SCHEMA IF NOT EXISTS ticket;

CREATE TABLE ticket.help_desk_ticket
(
    id          BIGSERIAL PRIMARY KEY,

    ticket_id   VARCHAR(50)  NOT NULL,
    username    VARCHAR(100) NOT NULL,
    category    VARCHAR(50)  NOT NULL,
    priority    VARCHAR(20)  NOT NULL,
    status      VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    email       VARCHAR(30)  NOT NULL,

    summary     TEXT         NOT NULL,

    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE,

    version     BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uk_help_desk_ticket_ticket_id
        UNIQUE (ticket_id)
);

CREATE INDEX idx_help_desk_ticket_username
    ON ticket.help_desk_ticket (username);

CREATE INDEX idx_help_desk_ticket_username_status
    ON ticket.help_desk_ticket (username, status);

CREATE INDEX idx_help_desk_ticket_category
    ON ticket.help_desk_ticket (category);

CREATE INDEX idx_help_desk_ticket_created_at
    ON ticket.help_desk_ticket (created_at);