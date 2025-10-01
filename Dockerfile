# Sử dụng OpenJDK 17 với Alpine Linux để tối ưu kích thước
FROM openjdk:17-jdk-alpine

# Thiết lập thư mục làm việc
WORKDIR /app

# Cài đặt Maven
RUN apk add --no-cache maven

# Sao chép pom.xml để tận dụng Docker layer caching
COPY pom.xml .

# Download dependencies trước để cache layer
RUN mvn dependency:go-offline -B

# Sao chép source code
COPY src ./src

# Build ứng dụng
RUN mvn clean package -DskipTests

# Tạo stage runtime nhẹ hơn
FROM openjdk:17-jre-alpine

WORKDIR /app

# Tạo user non-root để bảo mật
RUN addgroup -g 1001 -S spring && \
    adduser -S spring -u 1001

# Sao chép JAR file từ build stage
COPY --from=0 /app/target/team-management-*.jar app.jar

# Chuyển quyền sở hữu cho user spring
RUN chown spring:spring app.jar
USER spring:spring

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Command để chạy ứng dụng
CMD ["java", "-jar", "app.jar"]