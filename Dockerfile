# ==========================================
# 1. ビルドステージ (Maven + JDK 21)
# ==========================================
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# マルチモジュール POM のコピー
COPY pom.xml ./
COPY ticket-system-backend/pom.xml ./ticket-system-backend/
COPY ticket-system-frontend/pom.xml ./ticket-system-frontend/

# バックエンドのソースコードをコピー
COPY ticket-system-backend/src ./ticket-system-backend/src

# バックエンドモジュールをビルド（テストをスキップして高速化）
RUN mvn clean package -DskipTests -pl ticket-system-backend -am

# ==========================================
# 2. 実行ステージ (JRE 21)
# ==========================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# セキュリティ対策：ルート以外の一般ユーザーを作成して実行
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# ビルドステージから生成された JAR ファイルをコピー
COPY --from=builder /app/ticket-system-backend/target/*.jar app.jar

# ポート番号（8081）
EXPOSE 8081

# アプリケーションの起動コマンド
ENTRYPOINT ["java", "-jar", "app.jar"]
