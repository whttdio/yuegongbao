FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /build

COPY pom.xml ./
COPY yuegongbao-admin ./yuegongbao-admin
COPY yuegongbao-framework ./yuegongbao-framework
COPY yuegongbao-system ./yuegongbao-system
COPY yuegongbao-business ./yuegongbao-business
COPY yuegongbao-quartz ./yuegongbao-quartz
COPY yuegongbao-generator ./yuegongbao-generator
COPY yuegongbao-common ./yuegongbao-common

RUN mvn -pl yuegongbao-admin -am clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy

ENV TZ=Asia/Shanghai
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8

WORKDIR /app

COPY --from=builder /build/yuegongbao-admin/target/yuegongbao-admin.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]

