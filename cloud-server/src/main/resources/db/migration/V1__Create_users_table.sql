CREATE TABLE public.user
(
    login character varying(155) NOT NULL,
    password character varying(32) NOT NULL,
    spacelimit integer NOT NULL DEFAULT 0,
    PRIMARY KEY (login)
)
    WITH (
        OIDS = FALSE
    );

insert into public.user (login, password, spacelimit)
values ('user', 'D8578EDF8458CE06FBC5BB76A58C5CA4', 1073741824); --user:qwerty