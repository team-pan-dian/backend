# API Docs

## login system

- [POST] `/api/login`
    - `curl -X POST -F name={User Name} -F password={User Password} http://{Host Name}/api/login`
    - ```json
        {
          "error": false,
          "data": "JWT",
          "errorMessage": ""
        }
      ```
    - error
      - UnprocessableEntity: 遺失參數
      - Forbidden: 資訊錯誤
- [POST] `/api/sign-up`
    - `curl -X POST -F name={User Name} -F password={User Password} http://{Host Name}/api/sign-up`
    - ```json
      {
        "error": false,
        "data": "ok",
        "errorMessage": ""
      }
      ```
    - error
        - UnprocessableEntity: 遺失參數
        - Forbidden: 資訊錯誤
    
## video system

- [GET] `/api/class`
    - `curl -X GET http://{Host Name}/api/class/{Class Id}`
    - ```json
      {
          "error": false,
          "data": "ok",
          "errorMessage": ""
      }
      ```