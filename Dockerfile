# ==========================================
# 1. ビルドステージ (Maven + JDK 25)
# ==========================================
FROM maven:3.9.9-eclipse-temurin-25-alpine AS builder

WORKDIR /app

# 依存関係のキャッシュを有効化するため、pom.xml を先にコピーしてダウンロード
COPY pom.xml .
RUN mvn dependency:go-offline -B

# ソースコードをコピーしてビルドを実行（テストをスキップして高速化）
COPY src ./src
RUN mvn clean package -DskipTests

# ==========================================
# 2. 実行ステージ (JRE 25)
# ==========================================
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# セキュリティ対策：ルート以外の一般ユーザーを作成して実行
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# ビルドステージから作成された JAR ファイルをコピー
COPY --from=builder /app/target/*.jar app.jar

# ポート番号の明示
EXPOSE 8080

# アプリケーションの起動コマンド
ENTRYPOINT ["java", "-jar", "app.jar"]
