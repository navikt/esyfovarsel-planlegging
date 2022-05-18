CREATE TABLE SYKMELDING_IDS (
    uuid            UUID       PRIMARY KEY,
    sykmelding_id   VARCHAR    NOT NULL,
    varsling_id     UUID       NOT NULL,
    CONSTRAINT fk_sykmelding_id
        FOREIGN KEY sykmelding_id
            REFERENCES PLANLAGT_VARSEL(sykmelding_id)
                ON DELETE CASCADE
);
