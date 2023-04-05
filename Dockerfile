FROM openjdk:8-jdk-alpine
COPY build/libs/*.jar app.jar

RUN apk add --no-cache ffmpeg
RUN wget https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp -O /usr/local/bin/yt-dlp && \
    chmod a+rx /usr/local/bin/yt-dlp

EXPOSE 9001
ENTRYPOINT ["java","-jar","/app.jar"]
