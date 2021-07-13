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
    - `curl -X GET http://{Host Name}/api/class/{Class Id}`
    - ```json5
        {
            "error": false,
            "data": {
                "id": "1", // 課程id
                "name": "a", // 課程名稱
                "count": 0, // 課程數量
                "information": "w", //課程說明
                "videoList": [] // 課程影片id清單
            },
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
    