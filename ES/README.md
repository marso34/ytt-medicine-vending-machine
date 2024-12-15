# 약톡톡 자판기
## 간단한 설명 (개요)

라즈베리파이를 활용하여 주문을 처리할 수 있는 실물 크기 자판기 제작
 * 주문 요청
	- 자판기에서 터치 모니터를 활용한 직접 주문 처리
	- 앱을 통한 특정 자판기에 주문 요청시 주문 처리 

 * 주문 처리: 배출 로직에 따라 배출 후 보관함 로직에 따라 분류되어 보관
	- 내부 서버 flask에서 동작 및 처리

 * 물품 수령 : qr코드를 이용한 찾기
	- 앱 주문 : 4개의 보관 칸을 사용
	- 자판기 직접 주문 : 1개의 보관 칸을 사용

## 개발 환경

- 하드웨어 : 라즈베리파이
- 운영체제 : Raspberry Pi OS
- 데이터베이스 : MariaDB
- 서버: Flask (Python 기반)
<br>

* 프론트엔드
	- HTML: 자판기 화면의 주문 인터페이스 개발
	- CSS: 화면의 레이아웃 및 스타일링 구성
	- JavaScript (Script): 주문 데이터를 Flask 서버로 전송
* 백엔드
	- Flask (Python): 라즈베리파이 내부 서버에서 주문 데이터를 수집 및 처리
	- MariaDB: 주문 데이터와 보관함 상태를 관리하는 데이터베이스
	- STOMP 프로토콜: 외부 STOMP 서버와 연결하여 WebSocket을 통해 주문 데이터를 구독 및 처리
* 자판기 동작 제어
	- Python: 자판기 내부 모든 동작 로직(모터 제어, 보관함 로직 등)을 처리

### 개발 기술
* 프론트엔드
	- HTML
 	- CSS
 	- JavaScript
* 백엔드
  	- Flask
  	- pymysql
 	- websocket
 	- json
  	- requests
* 모터 제어 및 하드웨어 동작
  	- RPi.GPIO
 	- threading
 	- subprocess
  	- os
* QR코드 처리 미 카메라 제어
  	- cv2(OpenCV)
 	- pyzbar
 	- Picamera2
* 기타 유틸리티 및 로깅
  	- logging
 	- datetime
 	- sys

## 아키텍처 (프로젝트 구조)
```
/home/capstone/
├── Desktop/
│   └── index.html              # 자판기 메인 화면 (프론트엔드 파일)
├── order/
    ├── moter/                  # 자판기 내부 모터 동작 관련 파일
    │   ├── print/              # 약 배출 모터 로직
    │   │   ├── step_m1.py      # 1번 모터 제어
    │   │   ├── step_m2.py      # 2번 모터 제어
    │   │   ├── step_m3.py      # 3번 모터 제어
    │   │   └── step_m4.py      # 4번 모터 제어
    │   ├── storage_in/         # 보관함 선택 로직
    │   │   ├── 1_close.py      # 1번 보관함 닫기
    │   │   ├── 1_open.py       # 1번 보관함 열기
    │   │   ├── 2_close.py      # 2번 보관함 닫기
    │   │   ├── 2_open.py       # 2번 보관함 열기
    │   │   ├── 3_close.py      # 3번 보관함 닫기
    │   │   ├── 3_open.py       # 3번 보관함 열기
    │   │   ├── 4_close.py      # 4번 보관함 닫기
    │   │   └── 4_open.py       # 4번 보관함 열기
    │   ├── storage_out/        # 물품 찾는 로직
    │   │   ├── 1_out.py        # 1번 보관함 출고
    │   │   ├── 2_out.py        # 2번 보관함 출고
    │   │   ├── 3_out.py        # 3번 보관함 출고
    │   │   └── 4_out.py        # 4번 보관함 출고
    │   ├── moter.txt           # 모터 핀 정보
    │   └── play_check.py       # 주문 완료 알림
    ├── app.py                  # Flask 서버 실행 파일
    ├── cam.py                  # QR 코드 처리 로직
    ├── db.txt                  # 데이터베이스 정보
    ├── order.html              # 주문 화면 (프론트엔드 파일)
    ├── play.py                 # 주문 처리 로직 (Python)
    └── specific_task.py        # 물품 수령 로직 (Python)
```
## 주요 기능 흐름
 1. 주문 데이터 전송
	- 사용자가 자판기에서 주문 (HTML + JavaScript → Flask 서버로 데이터 전송)

 2. 주문 데이터 외부 전송
	- Flask 서버가 STOMP 프로토콜을 통해 외부 STOMP 서버로 주문 데이터를 전송

 3. 응답 수신 및 자판기 동작
	- STOMP 서버로부터 응답 데이터를 수신
	- 수신한 데이터를 Flask 서버가 내부 Python 로직으로 전달하여 자판기 동작 수행

## 상세 기술
1. 모터 동작 제어 (moter)
	* 주요 라이브러리:
		- RPi.GPIO: 라즈베리파이 GPIO 핀 제어
		- time.sleep: 대기 시간 설정
		- threading: 병렬 처리를 통한 비동기 작업 수행
	* 기능 설명:
		- 약 배출 및 보관함 제어와 같은 하드웨어 동작 로직을 구현
		- 모터 핀 초기화, 동작 명령, 스레드를 활용한 비동기적 모터 작동 처리
2. Flask 내부 서버 및 외부 서버 연결 (app.py)
	* 주요 라이브러리:
		- Flask, request, jsonify, render_template: RESTful API 및 웹 서버 구현
		- flask_cors: CORS 설정을 통해 프론트엔드와의 데이터 송수신 허용
		- pymysql: MariaDB와 연결하여 데이터 처리
		- websocket, json, threading: STOMP 서버와의 실시간 통신 구현
		- logging: 서버 로그 기록
		- subprocess, requests, os: 외부 명령 실행 및 HTTP 요청 처리
	* 기능 설명:
		- 프론트엔드에서 주문 데이터를 수신하여 외부 STOMP 서버와 연결
		- MariaDB를 통해 주문 데이터 저장 및 상태 확인
		- 주문 처리와 연동된 Flask 기반 웹 서버 구현
3. QR 코드 인식 (cam.py)
	* 주요 라이브러리:
		- cv2 (OpenCV): 카메라를 통해 QR 코드 이미지 처리
		- pyzbar: QR 코드 디코딩
		- Picamera2: 라즈베리파이 카메라 제어
		- pymysql: 주문 데이터와 QR 코드 매칭을 위한 DB 연동
		- subprocess, os, requests: 외부 명령 실행 및 데이터 전송
		- time: 시간 측정 및 대기
	* 기능 설명:
		- 카메라로 QR 코드를 스캔하여 주문 데이터와 매칭
		- DB에서 QR 코드 정보를 조회하고 주문 처리 로직으로 전송
4. 주문 데이터 처리 (play.py)
	* 주요 라이브러리:
		- json: 주문 데이터 처리
		- pymysql: DB에서 주문 상태 조회 및 갱신
		- subprocess, os, sys: 외부 명령 실행 및 시스템 제어
		- requests: HTTP 요청을 통한 데이터 송수신
		- datetime, time.sleep: 시간 기반 데이터 처리 및 대기
	* 기능 설명:
		- STOMP 서버에서 받은 데이터를 처리하여 주문 상태를 갱신
		- 주문 데이터를 DB에 저장하고, 자판기 동작 로직과 연동
5. 물품 수령 처리 (specific_task.py)
	* 주요 라이브러리:
		- time.sleep: 물품 수령 대기 시간 구현
		- subprocess, os, sys: 시스템 명령을 실행하여 물품 수령 로직 수행
	* 기능 설명:
		- 자판기 보관함에서 사용자에게 물품을 전달
		- 외부 명령 및 시스템 제어를 통해 하드웨어 동작과 연계
