CREATE TABLE blocked_extension (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    space_id    BIGINT      NOT NULL,
    name        VARCHAR(20) NOT NULL,
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_blocked_extension_space_name UNIQUE (space_id, name)
);
