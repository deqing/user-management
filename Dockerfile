# 使用带有 Gradle 的 JDK 镜像（可缓存依赖）
FROM eclipse-temurin:17-jdk-jammy

# 设置工作目录
WORKDIR /app

# 1. 复制 Gradle 相关文件（利用缓存层）
COPY gradlew .
COPY gradlew.bat .
COPY gradle/ gradle/
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src/ src/

# 2. 构建项目（生成 JAR）
RUN chmod +x gradlew  # 确保 gradlew 可执行
RUN ./gradlew clean build -x test  # 跳过测试

# 3. 最终阶段：只保留 JAR 文件

WORKDIR /app

# 从 builder 阶段复制生成的 JAR
COPY ./build/libs/user-management-0.0.1-SNAPSHOT.jar ./app.jar

# 暴露端口
EXPOSE 19001

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]
