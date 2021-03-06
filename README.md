# Triple-Club-Mileage-Service
![image](https://user-images.githubusercontent.com/97106584/176442990-e8da72ba-8703-41c0-ba84-07848ef2b5d5.png)


## 목차
* **[개발 환경](#개발-환경)**
* **[실행 방법](#실행-방법)**
* **[과제 설명](#과제-설명)**
  * [서비스 목적](#서비스-목적)
  * [API 명세서](#API-명세서)
* **[과제 진행 방식](#과제-진행-방식)**
  * [설계](#설계)
    * [Database](#Database)
    * [API Design](#API-Design)
    * [디자인 패턴](#디자인-패턴)
    * [SOLID 원칙 설계](#SOLID-원칙-설계)
* **[실행 결과](#실행-결과)**
  * [포인트 적립 API](#포인트-적립-API)
    * [ADD](#ADD)
    * [MOD](#MOD)
    * [DELETE](#DELETE)
  * [포인트 조회 API](#포인트-조회-API)


<br><hr><br>

# 개발 환경
* Java 8
* Spring Boot (v2.7.1)
* Spring Data JPA
* Lombok
* MySQL(v5.7)

<br><hr><br>

# 실행 방법
1. git clone
![image](https://user-images.githubusercontent.com/97106584/176458572-1aa5a62c-b037-4391-acb2-18ce3dcdc7d2.png)
2. 위의 properties에 적힌 내용과 같이 "mileage" database 생성 및 id/pw 설정 (mysql 5.7) 
3. project run
4. database table 생성 확인
5. api test 진행

<br><hr><br>


# 과제 설명
## 서비스 목적
사용자들이 장소에 리뷰를 작성할 때 포인트를 부여하고, 전체/개인에 대한 포인트 부여 히스토리와 개인별 누적 포인트를 관리하고자 한다.

<br>

## API 명세서
리뷰 작성이 이뤄질때마다 리뷰 작성 이벤트가 발생하고, **아래 API로 이벤트를 전달**한다.
### API Info
```Java
{
  "type": "REVIEW",
  "action": "ADD", /* "MOD", "DELETE" */
  "reviewId": "240a0658-dc5f-4878-9381-ebb7b2667772",
  "content": "좋아요!",
  "attachedPhotoIds": ["e4d1a64e-a531-46de-88d0-ff0ed70c0bb8", "afb0cef2-
  851d-4a50-bb07-9cc15cbdc332"],
  "userId": "3ede0ef2-92b7-4817-a5f3-0c575361f745",
  "placeId": "2e4baf1c-5acb-4efb-a1af-eddada31b00f"
}
```
* type: 미리 정의된 string 값을 가지고 있습니다. 리뷰 이벤트의 경우 "REVIEW" 로 옵니다.
* action: 리뷰 생성 이벤트의 경우 "ADD" , 수정 이벤트는 "MOD" , 삭제 이벤트는 "DELETE" 값을 가지고 있습니다.
* reviewId: UUID 포맷의 review id입니다. 어떤 리뷰에 대한 이벤트인지 가리키는 값입니다.
* content: 리뷰의 내용입니다.
* attachedPhotoIds: 리뷰에 첨부된 이미지들의 id 배열입니다.
* userId: 리뷰의 작성자 id입니다.
* placeId: 리뷰가 작성된 장소의 id입니다.

한 사용자는 장소마다 리뷰를 1개만 작성할 수 있고, 리뷰는 수정 또는 삭제할 수 있습니다. 리뷰 작성 보상 점수는 아래와 같습니다.

* 이동 점수
  * 1자 이상 텍스트 작성: 1점
  * 1장 이상 사진 첨부: 1점
* 보너스 점수
  * 특정 장소에 첫 리뷰 작성: 1점

<br><hr><br>

# 과제 진행 방식
## 설계

### **Database**
#### [Schema.sql DDL바로가기](https://github.com/anjeongkyun/MileageService/blob/master/schema.sql)

* **tb_event**
  * event의 값을 관리하는 테이블
* **tb_point**
  * 사용자의 point를 관리하는 테이블
  * point_id : 사용자 id 키 매핑 값
  * point : 사용자 point 값
* **tb_review**
  * review 내용 글자 수 & 사진 갯수 값을 관리하는 테이블
* **tb_place**
  * place 정보를 관리하는 테이블 (현재 과제 내용과는 벗어난다 생각하여 구현은 안 되어있지만 특정(special), 일반(normal)장소 구분하는 필드가 있어도 좋을 것 같습니다.)
* **tb_point_history**
  * event 발생 후 추가된 event에 매핑된 point의 이력을 관리하는 테이블
* **tb_place_history**
  * review를 작성할 때 이미 작성했던 장소인지 확인을 위한 이력 테이블

<br>

### **API Design**
|Method|URI|description|
|------|---|---|
|POST|/events|작성, 수정, 삭제(ADD,MOD,DELETE) API|
|GET|/points/{id}|포인트 조회 API|

<br>

### **디자인 패턴**
* Event Request 후 ADD/ MOD/ DELETE의 인스턴스 생성 부분(EventActionHandler)을 **팩토리 메서드 패턴을 적용**하여 **인스턴스 생성 확장성과 낮은 결합도를 고려**하여 설계

<br>

### **SOLID 원칙 설계**
* Event API 수신 시 Event Interface의 구현체(EventActionService)를 만들어 **자주 변경될 수 있는 모듈을 변동성이 낮은 구현체에 의존하도록 설계

<br><hr><br>

# 실행 결과
## 포인트 적립 API
### ADD
![image](https://user-images.githubusercontent.com/97106584/176436668-59531e54-b8d6-4d28-bed8-03248154f8bb.png)

<br>

### MOD
![image](https://user-images.githubusercontent.com/97106584/176437036-ba7d003d-ac09-49f8-b1de-a674415bc98f.png)

<br>

### DELETE
![image](https://user-images.githubusercontent.com/97106584/176437115-63286a9d-8fca-44dd-9f31-973ec0278a09.png)

<br>

## 포인트 조회 API

![image](https://user-images.githubusercontent.com/97106584/176437579-e23cae09-243d-4971-8e0b-90d2c696b188.png)
