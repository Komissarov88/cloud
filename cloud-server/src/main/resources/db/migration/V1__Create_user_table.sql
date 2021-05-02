CREATE TABLE public.user
(
    login      character varying(155) NOT NULL,
    password   character varying(32)  NOT NULL,
    spacelimit bigint                 NOT NULL,
    PRIMARY KEY (login)
)
    WITH (
        OIDS = FALSE
    );