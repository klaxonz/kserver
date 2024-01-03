FROM openjdk:8-jre-alpine

RUN apk add --no-cache ffmpeg && \
    wget https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp -O /usr/local/bin/yt-dlp && \
    chmod a+rx /usr/local/bin/yt-dlp

COPY build/libs/*.jar app.jar

VOLUME /app/download/

EXPOSE 9090
ENTRYPOINT ["java","-jar","/app.jar"]
