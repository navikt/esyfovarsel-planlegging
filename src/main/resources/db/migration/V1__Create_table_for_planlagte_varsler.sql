CREATE TABLE PLANLAGTE_VARSLER (
  uuid                     UUID               PRIMARY KEY,
  varsel_dato              DATE               NOT NULL,
  mottaker_fnr             VARCHAR(11)        NOT NULL,
  type                     VARCHAR(100)       NOT NULL,
  data                     CLOB,
  opprettet                TIMESTAMP          NOT NULL,
  sist_endret              TIMESTAMP          NOT NULL
);
