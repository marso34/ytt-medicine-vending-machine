# 약톡톡 백엔드 (ytt Back-End) 

<!-- ABOUT THE PROJECT -->
## 프로젝트 개요

*내용 입력*

<br>

### 개발 환경



### 개발 기술



<p align="right">(<a href="#프로젝트-개요">back to top</a>)</p>

## 프로젝트 구조 (Architecture)


<p align="right">(<a href="#프로젝트-개요">back to top</a>)</p>

## 메인 기능
📌 회원가입, 로그인

📌 JWT 토큰기반 사용자 인증

📌 동시성 제어를 통한 주문 처리 

📌 STOMP 웹소켓을 활용한 유저-서버, 서버-자판기 양방향 통신

📌 자판기 관리 (조회, 생성, 수정, 삭제)

📌 공공데이터 기반 약품 정보 관리

📌 자판기 관리자 시스템

📌 입고 기록 관리

📌 자판기 즐겨찾기 기능
## 상세 기능

<details>
  <summary>Auth 관련 기능</summary>

![image](https://github.com/user-attachments/assets/488c4028-a617-4f74-89ac-d7d7881d8062)
![image](https://github.com/user-attachments/assets/4296f5be-ad54-4c5f-bba7-d7f6c48e2b42)
1. Sign Up API : 이메일, 비밀번호, 이름, 핸드폰 번호 데이터를 중복 확인 및 정규식 표현 형식 설정으로 비교 후 해시 함수 기능을 사용하여 저장
2. Sign In API : 이메일, 비밀번호를 Spring Security Filter 거쳐 인증 및 JWT 토큰(Authroization, refresh) 발행
3. password API : 사용자의 비밀번호를 해쉬 함수 기능을 사용하여 비교 및 변경
4. logout API : 사용자의 refresh 토큰 삭제를 통한 로그아웃 기능 구현
5. reissue API : 사용자의 refresh 토큰으로 Authorization 토큰을 새로 발급
</details>

<details>
  <summary>User 관련 기능</summary>

![image](https://github.com/user-attachments/assets/0d21897a-8f3a-4185-af5e-86894fdaca8f)
1. Mypage API : JWT를 검증하여 로그인된 사용자의 정보(유저번호, 이메일, 이름, 폰번호, role)를 반환
</details>

<details>
  <summary>Order 관련 기능</summary>

![image](https://github.com/user-attachments/assets/1e6f57f2-0794-424d-aa7a-001a379ea844)
1. Orders API : 주문 목록 조회 (사용자 주문 목록, 상세 주문 목록, 자판기 주문 목록, 모든 주문 목록)
2. Create API : 재고조회 및 자판기 상태 여부 체크 후 사용자의 주문 아이템들로 조합된 주문리스트를 생성, STOMP 형식으로 해당 자판기에게 주문 요청을 전달.
3. Cancle API : 주문 상태가 처리중인 상태라면 주문을 취소, 자판기에 주문 취소 알림.
4. Store API : 자판기에서 처리된 결과를 전달 받아 주문 상태를 업데이트 및 주문을 구독중인 사용자에게 알림.
5. Complete API : 사용자가 자판기에서 약을 수령하면 주문 상태를 완료로 업데이트.
</details>

<details>
  <summary>VendingMachine 관련 기능</summary>

![image](https://github.com/user-attachments/assets/3b9b3818-5452-41a0-b9a7-040046aa0a71)
1. {machineId} API : 관리자가 자판기 운영 상태 변경
2. [POST]vending-machine API : 설정 위치에 자판기를 등록
3. {machineId}/medicine API : 자판기에 약, 수량 추가
4. [GET]vending-machine API : 자판기 조회(범위 내 자판기 조회, 특정 자판기의 특정 약 조회, 자판기 Id로 조회, 특정 자판기의 전체 재고 조회, 약품이 포홤된 주변 자판기 조회, 모든 자판기 조회)

</details>

<details>
  <summary>Medicine 관련 기능</summary>

![image](https://github.com/user-attachments/assets/6c051ae7-a20d-4825-80e6-8797a5dc58bf)
1. [POST]medicine API : 공공데이터에서 이름으로 약품 정보를 찾아서 저장
2. [GET]medicine API : 약품 이름과 제조사, 성분명으로 약품 리스트를 조회
3. medicine/detail API : 약 id 또는 productCode로 약품을 상세 조회
4. medicine/all API : 모든 약품 리스트를 조회
</details>

<details>
  <summary>Managemnet 관련 기능</summary>

![image](https://github.com/user-attachments/assets/70cec844-2dab-4d2b-960e-507d495a3bd7)
1. [DELETE]management/{machineId} API : 특정 자판기의 관리자를 삭제
2. [GET]management/{machineId} API : 특정 자판기의 관리자 리스트 조회
3. [GET]management API : 로그인된 관리자의 관리하는 자판기 리스트 조회
4. [POST]management/{machineId} API : 특정 자판기에 관리자를 추가
</details>

<details>
  <summary>Inbound 관련 기능</summary>

![image](https://github.com/user-attachments/assets/04491ff7-4a78-4f15-af55-524ebcdac735)
1. [GET]inbound/{machineId} API : 입고 기록 조회 API, medicineId 또는 productCode로 특정 약 입고 기록 조회 가능, 없으면 전체 입고 기록 조회
2. [POST]inbound/{machineId} API : 특정 자판기에 약, 수량 입고를 요청
</details>

<details>
  <summary>Favorite 관련 기능</summary>

![image](https://github.com/user-attachments/assets/d6ba98f9-b381-47e2-a47a-025a83d40225)
1. [DELETE]favorites/{machineId} API : 즐겨찾기 리스트에서 해당 자판기를 삭제
2. [GET]favorites API : 사용자의 즐겨찾기 자판기 리스트 조회
3. [POST]favorites/{machineId} API : 즐겨찾기 리스트에 해당 자판기 추가

