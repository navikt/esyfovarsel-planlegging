FROM navikt/java:14
LABEL org.opencontainers.image.source=https://github.com/navikt/esyfovarsel-planlegging
COPY build/libs/*.jar app.jar