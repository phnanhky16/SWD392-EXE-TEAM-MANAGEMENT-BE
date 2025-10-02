# 🚨 Error Response Format

Khi có lỗi xảy ra, API sẽ trả về format sau:

## 📋 **Cấu trúc Error Response:**

```json
{
  "status": <HTTP_STATUS_CODE>,
  "message": "<ERROR_MESSAGE>", 
  "data": null // hoặc chi tiết lỗi
}
```

## 🔍 **Các loại lỗi:**

### 1️⃣ **App Exception (Business Logic Error)**
```json
{
  "status": 404,
  "message": "User does not exist",
  "data": null
}
```

### 2️⃣ **Validation Error**
```json
{
  "status": 400,
  "message": "Validation failed",
  "data": {
    "email": "Email has invalid format",
    "fullName": "Full name is required"
  }
}
```

### 3️⃣ **Authentication Error**
```json
{
  "status": 401,
  "message": "Invalid Google token",
  "data": null
}
```

### 4️⃣ **Authorization Error**
```json
{
  "status": 403,
  "message": "You do not have permission", 
  "data": null
}
```

### 5️⃣ **Server Error**
```json
{
  "status": 500,
  "message": "Internal server error",
  "data": null
}
```

### 6️⃣ **Bad Request**
```json
{
  "status": 400,
  "message": "Invalid argument provided",
  "data": null
}
```

## 📝 **Ví dụ thực tế:**

### ✅ **Success Response:**
```json
{
  "status": 200,
  "message": "Get user successfully",
  "data": {
    "id": 1,
    "email": "user@example.com",
    "fullName": "John Doe"
  }
}
```

### ❌ **Error Response:**
```json
{
  "status": 404,
  "message": "User does not exist",
  "data": null
}
```

## 🎯 **HTTP Status Codes:**
- **200**: Success (GET, PUT, PATCH, DELETE)
- **201**: Created (POST)
- **400**: Bad Request
- **401**: Unauthorized
- **403**: Forbidden
- **404**: Not Found
- **500**: Internal Server Error