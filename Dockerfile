FROM eclipse-temurin:21-jre-jammy

ENV APPLICATION_SECRET="changeme-this-is-a-very-long-secret-key-that-should-be-changed"
ENV PLAY_HTTP_SECRET_KEY="changeme-this-is-a-very-long-secret-key-that-should-be-changed"

COPY target/universal/stage /app
WORKDIR /app

EXPOSE 9000

CMD ["sh", "-c", "bin/ludo -Dplay.http.secret.key=${PLAY_HTTP_SECRET_KEY} -Dapplication.secret=${APPLICATION_SECRET}"]