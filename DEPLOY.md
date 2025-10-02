# EXE Team Management Backend

## 🚀 Deploy trên Render

### Bước 1: Cấu hình Environment Variables
Trên Render Dashboard, thêm các environment variables sau:

```
DB_HOST=sjc1.clusters.zeabur.com
DB_PORT=31933
DB_NAME=zeabur
DB_USERNAME=root
DB_PASSWORD=Jw1sKV7jm8ZqTyU05DfG6eS42Wu3xvl9

FCM_PROJECT_ID=exe-platform
FCM_PRIVATE_KEY_ID=42a34b4db374a1ac1f096d8f70edd219877c9712
FCM_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----
MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCPrmyyhXWsvc9c
n46YL65UkV8o/aMIqT7fG1jSVExnyyxLeBKRnrA8nU/lAkyvKJzGNUlI7kdk9y2H
q5IanloJpfr2Of0Szlt01phNVZ4vFMEo9DCj1P8cv3+PQRAzp1hJ8TK0k9y+u0In
7hi6F7RSoHLVbpJIlsAtlusGBDetMI0sWYWblFR6cjlsvNzJXwbPBKbQ5wf9qtxx
cUCUnwu8fxTVxdyZNkiUZaLeOC11zI/dE1eYu3qYLtZIi88UKYo8GUHSI8a394vo
j/VBUeEuMyETxNj6Tec/XBiH+O5b0JNGjh5SN7M41PTGe73Qt50oWjTZ+yh80015
EzlC8y05AgMBAAECggEAFjYhbRaXGqlsGzY6SboDQeUIODK/h5j8VGqqmDc6aYg9
1HvfKP5GYdRy06T/nEtTswttRRY62LeiZPkHuNA2fIvZaEOOJSBcAIulFz1vWvlj
I/Td6RHGD1hmPIvTGJ0b17BvBX3fL+gMYdZbobcE5AoUPnzdpdWSJ2oPcopn3gCq
XqQFUaQC2IxSs7HyqrMWpXVAR70DcM4Yjic9pbSn7PrMQZLAY8Ff5lC5BzVp+sv0
5XBp8Agt+KQTmz2jnmIKIqybvspB0zTXiNaIDw/B9imHFdFBsLGjV5ZifOkGphq/
FjrWEkMjcmzJluz6EWHfw+5nu5DH7BkfJ7Zt5kPelQKBgQDEArti4r3lPQgf5mFG
q6EioS7suSuTSAOj/6MVAbii3kiYkxttNcselHnTV13VU/inpMlr4wpXwuxJzX69
1nZ9AEdxyLy93R5WGn72NLiHmEj8zGCA7ro5+/NavLlswaEPb5vhoVAvRirsHZEb
5xJJSMQTzL+QB9Yl8c0xllJ09QKBgQC7p7weGAWA5Qi8AzR0nOky6GGOVAPe11w6
QFQrI9z8z5Btrw978+8sthPdbRSlb3KLE7lTYX6s0l2SxVI3vjxL2XzpuxvB26i+
5nE+/QnRnL/+gbCndwQTauXZcvKMzJeg36JZkf1SssLL4+93F7r907GA02fkKf3F
/Y119Z8MtQKBgF1B43/n22uO/yeWR+AE1pSQVbAhZb8cO0cy4JelCwDKe4XEL/9k
4bA5+kB4R9ewrRSuDXs8OXaJ8jwm/k86E4PjtxpG5K3832seDhv793PkG43Te3K8
onPmEOGx+xLoiUW711Ghm3Hyk4dlhd3dsdCWQ1wcRWQ0pknFYthd6ftlAoGAYkdX
o18rSrUo3ObEyyn+i/XeWd8wY73ghHV6X1mZ/CFs37TmZCF+Kql1P+tH+9ihYatD
zSJraHGIJj0HcUBFbegKnpKq192Fw+0GdFmrkccJWm5rqVkQnmbG2Bp2Yz0nUE6v
RT/LL0uF76iBmpqoCRWcif6uE3XWfaHsO1IXKV0CgYEAtQ9HXrIT/ArhjfMYBKFa
Kpw+L6LoTwFkJmehc3Pk4f/q5FEYKcYttBX7zNEbAIRvCadF8wdbLp/WmiNZRl8A
5USgXO5yttwqK1+2TSnIIeIEvCDu5Pm1cPvNrpsGrsuE+HQOlTzjvC7XdDh6tei4
PQ+rMGh6AIMpZCzYZYyFgbc=
-----END PRIVATE KEY-----"
FCM_CLIENT_EMAIL=firebase-adminsdk-fbsvc@exe-platform.iam.gserviceaccount.com
FCM_CLIENT_ID=100691038453426225172
FCM_AUTH_URI=https://accounts.google.com/o/oauth2/auth
FCM_TOKEN_URI=https://oauth2.googleapis.com/token
FCM_AUTH_PROVIDER_CERT_URL=https://www.googleapis.com/oauth2/v1/certs
FCM_CLIENT_CERT_URL=https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-fbsvc%40exe-platform.iam.gserviceaccount.com
```

### Bước 2: Deploy
1. Connect repository với Render
2. Render sẽ tự động detect `render.yaml`
3. Thêm environment variables ở trên vào Render Dashboard
4. Deploy!

## 🔒 Bảo mật
- Tất cả thông tin nhạy cảm được lưu trong Render Environment Variables
- File `.env` không được commit lên Git
- Chỉ có file `.env.example` được public để hướng dẫn

## 🏃‍♂️ Chạy local
```bash
# Copy và cấu hình environment
cp .env.example .env
# Chỉnh sửa .env với thông tin thật

# Chạy ứng dụng
mvn spring-boot:run
```