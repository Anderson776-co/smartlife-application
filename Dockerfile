FROM eclipse-temurin:25-jre-alpine AS capas
WORKDIR /app
COPY target/smartlife.jar app.jar
RUN java -Djarmode=tools -jar app.jar extract --layers --launcher --destination extraido

FROM eclipse-temurin:25-jre-alpine
RUN addgroup -S smartlife && adduser -S smartlife -G smartlife
WORKDIR /app
COPY --from=capas /app/extraido/dependencies/ ./
COPY --from=capas /app/extraido/spring-boot-loader/ ./
COPY --from=capas /app/extraido/snapshot-dependencies/ ./
COPY --from=capas /app/extraido/application/ ./
USER smartlife
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "org.springframework.boot.loader.launch.JarLauncher"]
