# API Docs

## login system

- 登入 [POST] `/api/login`
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
- 註冊 [POST] `/api/sign-up`
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

- 課程資訊 [GET] `/api/class/{Class Id}`
    - `curl -X GET http://{Host Name}/api/class/1`
    - ```json
      {
          "error": false,
          "data": "ok",
          "errorMessage": ""
      }
      ```
      
- 新增課程 [POST] `/api/class`
    - `curl -X POST -F name={Class Name} -F info={Class Information} http://{Host Name}/api/class`
    - ```json
      {
      "error": false,
      "data": "ok",
      "errorMessage": ""
      }
      ```
    - error
        - UnprocessableEntity: 缺少參數
    