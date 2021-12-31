### Bulletin Board Rest API

stack

- SpringBoot
- JPA → Spring Data JPA로 업그레이드
- QueryDSL
- Spring Security
- H2

✱ TDD를 중점적으로

요구사항

모든 응답은 `ApiResult<T>`객체를 통해 이루어진다.  
이를 위해 `ApiUtil` 클래스를 구현하며 `success` 함수와 `fail` 함수를 구현한다. 두 함수의 리턴은 `ApiResult<T>`이고 `fail`함수는 ControllerAdvice에서만 사용된다.

```java
class ApiResult<T> {
    HttpStatus status;
    T response;
    ApiError error;
}

class ApiError {
    String message;
}
```

예외로 인한 응답은 ControllerAdvice로 한다.

회원에 대한 인증은 Jwt를 이용하며 인증이 필요한 서비스의 경우 인증이 되지 않을 시 UnAuthorized 예외를 발생시킨다. 인증 토큰은 request header에 포함시키며 이름은 `X-FM-AUTH`이며 형식은 `Bearer {token}`이다. 형식에 맞지 않으면 UnAuthorized 예외를 발생시킨다.

Swagger

모든 request body의 값들은 해당 entity에 대한 vaildation 확인

---

#### entity

1. users

   |    field     |     type     | null | key  | default |     extra      |                 desc                 |
   | :----------: | :----------: | :--: | :--: | :-----: | :------------: | :----------------------------------: |
   |      id      |     int      |  no  |  pk  |  null   | auto increment |                  -                   |
   |    email     | varchar(30)  |  no  |  pk  |  null   |       -        |            email 형식 id             |
   |   password   | varchar(100) |  no  |  -   |  null   |       -        | bcrypt 알고리즘을 사용한 해싱값 저장 |
   | display_name | varchar(10)  |  no  |  -   |  null   |       -        |            10자까지 가능             |
   |  create_at   |   datetime   |  no  |  -   |   now   |       -        |            사용자 등록일             |

2. posts

   |   field   |     type     | null | key  | default |     extra      |       desc        |
   | :-------: | :----------: | :--: | :--: | :-----: | :------------: | :---------------: |
   |    id     |     int      |  no  |  pk  |  null   | auto increment |         -         |
   |  writer   | varchar(30)  |  no  |  fk  |  null   |  ref users.id  |         -         |
   |   title   | varchar(100) |  no  |  -   |  null   |       -        |   최소 1자 이상   |
   |  content  |     text     |  no  |  -   |  null   |       -        |   최소 1자 이상   |
   |   views   |     int      |  no  |  -   |    0    |       -        |   포스트 조회수   |
   | create_at |   datetime   |  no  |  -   |   now   |       -        |   포스트 등록일   |
   | update_at |   datetime   |  no  |  -   |   now   |       -        | 포스트 업데이트일 |

3. comments

   |   field   |    type     | null | key  | default |     extra      |        desc        |
   | :-------: | :---------: | :--: | :--: | :-----: | :------------: | :----------------: |
   |    id     |     int     |  no  |  pk  |  null   | auto increment |         -          |
   |  post_id  |     int     |  no  |  fk  |  null   |  ref posts.id  | 등록된 포스트의 id |
   |  writer   | varchar(30) |  no  |  fk  |  null   |  ref users.id  | comment 작성자 id  |
   |  content  |    text     |  no  |  -   |  null   |       -        |         -          |
   | create_at |  datetime   |  no  | now  |    -    | comment 등록일 |                    |

#### APIs

🔑 : 인증된 사용자만

1. users
    1. 사용자 등록
        - POST /api/v1/users
        - 등록 성공시 `Created`
        - 중복된 id일 경우 `DuplicateException`으로 `BadRequest`
        - RequestBody
            ```json
            {
                "id" : "email 형식",
                "password" : "password",
                "confirmPassword" : "confirm password",
                "displayName" : "닉네임"
            }
            ```

    2. email 중복 체크
        - POST /api/v1/users/check/email
        - RequestBody
            ```json
            {"email" : "email@example.com"}
            ```
        - `성공시 status = ok, response = duplicate ? false : true`

    3. displayName 중복 체크
        - POST /api/v1/users/check/displayname
        - RequestBody
            ```json
            {"displayName" : "display name"}
            ```
        - `성공시 status = ok, response = duplicate ? false : true`

    4. 🔑 사용자 삭제
        - DELETE /api/v1/users
        - Request Body
            ```json
            {"password" : "user pwd"}
            ```
        - 비밀번호 틀릴시 `BadRequest`
        - 삭제 성공시 `NoContent`

    5. 로그인
        - POST /api/v1/users/login
        - Request Body
            ```json
            {
                "id" : "email 형식",
                "password" : "password"
            }
            ```
        - 실패시 `NotFound`
        - 성공시 `200` + response body
            ```json
            {
                "id" : "id",
                "displayName" : "닉네임",
                "createAt" : "yyyy-MM-dd hh:mm:ss"
            }

2. posts

    1. 🔑 포스트 등록

        - POST /api/v1/posts

        - Request Body

          ```json
          {
              "title" : "제목",
              "content" : "내용"
          }
          ```

        - 제목과 내용은 모두 포함해야하며 1자 이상

    2. 여러 포스트 조회

        - GET /api/v1/posts

        - RequestParam: offset & size

            - offset → default: 0
            - size → deafult: 1000

        - 컨트롤러에 argument를 Pageable 객체로 받는다. 이를 위해 `HandlerMethodArgumentResolver`를 구현한다.

            - offset과 size가 주어지지 않으면 default 값으로 생성한다.

        - ApiResult의 response는 List로 반환하며 결과값이 없으면 null 반환

        - List의 각 객체는 다음과 같다.

          ```json
          {
              "title" : "제목",
              "writer" : "작성자",
              "views" : "조회수",
              "commentsCount" : "댓글수",
              "createAt" : "등록일",
              "updateAt" : "수정일"
          }
          ```

    3. 포스트 상세 조회

        - GET /api/v1/posts/{id}

        - 성공시 `200` + response body

          ```json
          {
              "title" : "제목",
              "writer" : "작성자",
              "content" : "내용",
              "views" : "조회수",
              "createAt" : "등록일",
              "updateAt" : "수정일",
              "comments" : [
                  {
                      "id" : "comment id",
                      "writer" : "댓글 작성자",
                      "content" : "댓글 내용",
                      "createAt" : "댓글 등록일"
                  }
              ]
          }
          ```

        - 조회 성공시 조회수 하나를 높인다.

        - id에 해당하는 포스트를 찾을 수 없을 경우 `404`

    4. 🔑 포스트 삭제

        - DELETE /api/v1/posts/{id}
        - 삭제시 포스트에 연관된 comments도 함께 삭제
        - 성공시 `NoContent`
        - id에 해당하는 포스트를 찾을 수 없을 경우 `404`

    5. 포스트 검색

        - GET /api/v1/posts/search
        - Request Param: q
        - 제목과 내용에 query가 포함된 포스트를 조회해 반환한다.
        - response는 여러 포스트 조회 기능과 같다.

3. comments

    1. 댓글 등록
    2. 포스트별 댓글 조회

### dev sequence

1. project setup ✔︎
2. ApiResult 구현 ✔︎
3. users 기능
    1. user entity ✔︎
    2. 사용자 등록 ✔︎
    3. 로그인
4. JWT 설정
5. posts 기능
    1. posts entity
    2. 여러 포스트 조회
    3. 포스트 상세 조회
    4. 포스트 등록
    5. 포스트 삭제
    6. 포스트 검색
6. comments 기능
    1. 포스트별 댓글 조회
    2. 댓글 등록

### devlog
- 21.12.23 project setup
- 21.12.24 ApiUtil 구현
- 21.12.25~26 User Entity & Repository.save() 구현
- 21.12.27
    - EncryptUtil 구현
    - user service: register, findByEmail, findByDisplayName 추가
    - user repository: findByEmail, findByDisplayName 추가
    - exception: PasswordNotMatch, EmailDuplicate, DisplayNameDuplicate 예외 추가
- 21.12.30
    - ControllerAdvice 추가
    - UserController 추가
    - ? extends CustomException 코드 수정
    - DTO: UserInfo 추가 및 UserRegisterRequest 수정
- 21.12.31
    - UserController: doubleCheckEmail, doubleCheckDisplayName 추가
    - DTO: EmailCheckRequest, DisplayNameCheckRequest 추가