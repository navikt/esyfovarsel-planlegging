CREATE TABLE PLANLAGTE_VARSLER (
  uuid                     UUID               PRIMARY KEY,
  varseldato              DATE               NOT NULL,
  type                     VARCHAR(100)       NOT NULL,
  arbeidstakerFnr          VARCHAR(11)        NOT NULL,
  orgnummer                VARCHAR(9)         NOT NULL,
  opprettet                TIMESTAMP          NOT NULL,
  sist_endret              TIMESTAMP          NOT NULL
);
