# API Docs

## login system

- 預設老師帳號
    - name: `teacher`
    - password: `password`


- 登入 **[POST]** `/api/login`
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


- 註冊 **[POST]** `/api/sign-up`
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

- 所有課程 **[GET]** `/api/classes`
    - `curl -X GET -H "Authorization: Bearer {JWT}" http://{Host Name}/api/classes`
    - ```json5
        {
          "error":false,
          "data":[
            {"id":"1","name":"a","information":"w","img":"","type":"s"}
          ],
          "errorMessage":""
        }
      ```


- 課程資訊 **[GET]** `/api/class/{Class Id}`
    - `curl -X GET -H "Authorization: Bearer {JWT}" http://{Host Name}/api/class/{Class Id}`
    - ```json5
        {
            "error": false,
            "data": {
                "id": "1", // 課程id
                "name": "a", // 課程名稱
                "count": 0, // 課程數量
                "information": "w", //課程說明
                "videoList": [
                    {"id":1,"name":"aa","information":"asd","viewCount":0,"sequence":1,"classId":1,"fileName":"1626236154689YouTube.mp4"},
                    {"id":2,"name":"a","information":"w","viewCount":0,"sequence":2,"classId":1,"fileName":"1626236760981YouTube.mp4"}
                ], // 課程影片資訊清單,
                "img": "" // 課程封面
            },
            "errorMessage": ""
        }
      ```


- 新增課程 **[POST]** `/api/class`
    - `curl -X POST -H "Authorization: Bearer {JWT}" -F name={Class Name} -F info={Class Information} -F type={Class Type} http://{Host Name}/api/class`
    - ```json
      {
      "error": false,
      "data": "ok",
      "errorMessage": ""
      }
      ```
    - error
        - UnprocessableEntity: 缺少參數


- 刪除課程 **[DELETE]** `/api/class/{Class Id}`
    - `curl -X DELETE -H "Authorization: Bearer {JWT}" http://{Host Name}/api/class/{Class Id}`
    - ```json
      {
          "error": false,
          "data": "ok",
          "errorMessage": ""
      }
      ```
    - error
        - UnprocessableEntity: 缺少參數
        - BadRequestException: 沒這個class

- 修改課程資訊 **[PUT]** `/api/class/{Class Id}`
    - `curl -X PUT -H "Authorization: Bearer {JWT}" http://{Host Name}/api/class/{Class Id}/?name={revise Name?}&info={revise info?}`
    - ```json5
      {
          "error": false,
          "data": "ok",
          "errorMessage": ""
      }
      ```
    - error
        - UnprocessableEntity: 缺少參數
        - BadRequestException: 沒這個class


- 上傳影片到課程 **[POST]** `/api/class/{Class Id}/upload`
    - `curl -X POST -H "Authorization: Bearer {JWT}" -F upload=@"{PATH}" "http://{Host Name}/api/class/1?name={Video Name}&info={Video info}"`

- 影片資訊 **[GET]** `/api/class/{Class Id}/{Video Id}`
    - `curl -X GET -H "Authorization: Bearer {JWT}" http://{Host Nmae}/api/class/{Class Id}/{Video id}`
    - ```json5
      {
          "error": false,
          "data": "ok",
          "errorMessage": ""
      }
      ```
  - error
      - UnprocessableEntity: 缺少參數
      - BadRequestException: 沒這個video
    

- 更新課程影片資訊 **[PUT]** `/api/class/{Class Id}/{Video Id}`
    - `curl -X PUT -H "Authorization: Bearer {JWT}" http://{Host Name}/api/class/{Class Id}/{Video ID}/?name={revise Name}&info={revise info}`
    - ```json5
      {
          "error": false,
          "data": "ok",
          "errorMessage": ""
      }
      ```
    - error
        - UnprocessableEntity: 缺少參數
        - BadRequestException: 沒這個video
    

- 修改課程排序 **[PUT]** `/api/class/{Class Id}/{Video Id}/order`

[comment]: <> (`應該還有學生加入課程和退出ㄅ 還是這邊都是教師端`) // have those thing will be unfree