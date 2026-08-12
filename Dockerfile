# ============ 构建阶段 ============
FROM maven:3.8-openjdk-17 AS build
WORKDIR /app

# 先拷贝 pom 以利用依赖缓存
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 拷贝源码并打包
COPY src ./src
RUN mvn clean package -DskipTests -B

# ============ 运行阶段 ============
FROM openjdk:17-jdk-slim
WORKDIR /app

# 时区设置（与 Quartz 定时任务相关）
ENV TZ=Asia/Shanghai

COPY --from=build /app/target/ylgj-1.0-SNAPSHOT.jar app.jar

# 容器内默认连接 docker-compose 中的服务
ENV DB_HOST=mysql \
    DB_PORT=3306 \
    DB_NAME=ylgj \
    DB_USERNAME=root \
    DB_PASSWORD=123456 \
    REDIS_HOST=redis \
    REDIS_PORT=6379

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
