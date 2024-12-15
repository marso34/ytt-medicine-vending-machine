# 약톡톡 프론트엔드 (ytt Front-End)

<!-- ABOUT THE PROJECT -->
## 프로젝트 개요
<br>
### 개발 환경
<br>

- IDE: Android Studio
- 프로그래밍 언어: Kotlin
- SDK: compileSdk 34, minSdk 30
- 버전: Android 5.0 이상
- 기타 도구: Git (버전 관리)
 


### 개발 기술

- 지도 API: 네이버 지도를 사용하기 위해 Naver Cloud Api를 사용하여 지도 개발
- UI 설계: XML 레이아웃 파일을 사용하여 사용자 인터페이스 디자인
- 네트워킹: Retrofit 라이브러리를 이용한 API 호출
- 데이터 관리: SharedPreference를 사용하여 데이터 관리
- 암호화: HashKey로 암호화하여 비밀번호 외부 유출 방지
- 서버에서 사용되는 엔드포인트 주소를 local.properties에서 다음과 같이 사용
```
NAVERMAP_CLIENT_ID = aipi7vjj4g
BaseUrl = "http://13.125.128.15:8080"
SubUrl = "ws://13.125.128.15:8080"
```
<p align="right">(<a href="#프로젝트-개요">back to top</a>)</p>

## 프로젝트 구조 (Architecture)
- 아키텍처 (프로젝트 전체 구조)
- MVVM 아키텍처: Model-View-ViewModel 패턴을 적용하여 코드의 유지보수성과 테스트 용이성을 향상
- Model: 데이터 및 비즈니스 로직 처리
- View: UI 구성 요소 (Activity, Fragment)
- ViewModel: UI와 Model 간의 데이터 연결 및 비즈니스 로직 처리

#### Model
![image](https://github.com/user-attachments/assets/c6886721-c1b0-4e91-9859-ffd60df6b158)
- user, medicine, vendingmachine, order로 나누어져있습니다.
#### network
![image](https://github.com/user-attachments/assets/ce91867b-d9fc-44c1-ab6e-0df281f43d83)
- ApiService에는 서버 경로, RetrofitAPI에는 Retrofit 관련 라이브러리 관련 파일이 저장되어 있습니다.
- 토큰, 웹소켓 관련 클래스가 포함되어 있습니다.

#### repository
![image](https://github.com/user-attachments/assets/756bd6c5-f9da-4cc2-8180-91b672959aa2)
- repository에는 auth, medicine, Order, Vendingmachine Repository로 관련 메서드들을 관리합니다.

#### View
![image](https://github.com/user-attachments/assets/3ca8dd91-915a-478a-aea5-91764b6ee56a)
- user, medicine, order, vendingmachine 과 하단 4개 탭과 관련한 Fragment로 구성되어 있습니다.

#### ViewModel
![image](https://github.com/user-attachments/assets/d65d18c6-9eef-4809-918e-f071d3a6ead3)
- UI와 Model 간의 데이터 연결 및 비즈니스 로직 처리를 진행하여, 
- 애플리케이션의 흐름을 관리합니다. 이를 통해 사용자 인터페이스의 반응성을 높입니다. ViewModel은 LiveData를 통해 UI를 자동으로 업데이트하여 사용자 경험을 향상시킵니다.


## 상세 기능

##### 회원 로그인 기능

![image](https://github.com/user-attachments/assets/fb0830c7-c9a9-4161-8b3d-296d5f5e9dc1)

- 사용자는 회원 가입 시 등록한 아이디와 비밀번호를 이용하여 손쉽게 로그인할 수 있습니다.
- 로그인 과정에서 발생하는 오류 메시지를 통해 사용자 경험을 개선할 수 있도록 하였습니다.
- 로그인 후 사용자는 개인화된 서비스를 제공받을 수 있습니다.

#### 자판기 조회 기능

![image](https://github.com/user-attachments/assets/f4c2dfa7-ea9e-4413-9622-803e409e1a9d)

- 사용자는 자신의 현재 위치를 기반으로 가까운 자판기를 검색하고 목록을 조회할 수 있습니다.
- UI는 지도상에 자판기의 위치를 표시하고, 하단 바텀 시트에서 자판기 목록을 그리드 형태로 제공하여 쉽게 접근할 수 있도록 설계되었습니다. 
- 지도와 리스트 뷰 간의 전환이 원활하여 사용자 편의성이 높습니다.


##### 약 조회 기능

![image](https://github.com/user-attachments/assets/10b06f97-9cb1-43ad-9883-8f580f31443d)

- 사용자는 선택한 자판기에서 판매 중인 약 목록을 손쉽게 조회할 수 있습니다.
- UI는 약 목록을 그리드 형태로 표시하며, 각 약의 이미지와 가격, 상품 코드가 함께 나타나 사용자에게 유용한 정보를 제공합니다. 
- 검색 기능을 통해 원하는 약을 더욱 쉽게 찾을 수 있습니다.

#### 즐겨찾기 기능
![image](https://github.com/user-attachments/assets/67814b99-8b01-4bd3-a2e4-9f4fce276512)
![image](https://github.com/user-attachments/assets/b2536a5c-9b53-47b1-b803-6f80fce09fd8)

- 사용자는 자판기 목록에서 하트를 눌러 즐겨찾기에 추가할 수 있으며, 즐겨찾기한 자판기는 별도의 탭에서 쉽게 확인할 수 있습니다.
- 이 기능은 사용자가 자주 이용하는 자판기를 빠르게 찾을 수 있도록 도와줍니다.
- 즐겨찾기 목록은 사용자의 선호에 따라 쉽게 관리할 수 있습니다.


#### 약 선택 및 약 상세 기능 설명 기능

![image](https://github.com/user-attachments/assets/1ef2417a-f9b4-4f72-a807-4cf9bbb97612)
- 사용자가 원하는 약을 선택하면 해당 약에 대한 상세 정보를 제공하며, 원하는 수량을 조절하여 구매 목록에 추가할 수 있습니다.
- UI는 선택된 약이 구매할 목록에 추가되는 모습을 직관적으로 보여주어 사용자 편의성을 높입니다. 상세 정보 페이지에는 복용 방법 및 주의 사항도 포함되어 있습니다.

#### 약 주문하기 및 취소하기 기능

![image](https://github.com/user-attachments/assets/193652aa-bb4d-4901-88cd-39955200bf69)
- 사용자가 선택한 약을 주문할 수 있는 기능을 제공하며, 주문한 약의 이름, 가격, 수량을 한눈에 확인할 수 있습니다.
- 주문하기 버튼을 누르면 자판기에 입력할 수 있는 QR 코드가 생성되며, 필요 시 주문 취소도 가능합니다. 주문 내역은 사용자가 쉽게 관리할 수 있도록 구성되어 있습니다.

#### 주문 실패 시 사유 확인 기능
![image](https://github.com/user-attachments/assets/6c61b66a-f923-4ebb-a10c-706fdcf52327)
- 주문이 실패했을 경우, 사용자는 그 이유를 다이얼로그로 확인할 수 있습니다.   
이를 통해 사용자는 문제를 빠르게 인지하고 해결할 수 있습니다.
- 
#### 주문 목록 확인 기능

![image](https://github.com/user-attachments/assets/61ab7178-897b-40bc-8c3f-2782fb40b7e2)
- 사용자는 자신의 주문 목록을 전체적으로 확인할 수 있으며, 주문 내역을 쉽게 관리할 수 있습니다.
- 각 주문 항목에 대한 상세 정보도 제공하여, 사용자 경험을 더욱 향상시킵니다.


#### 사용자 정보 확인, 로그아웃 기능

![image](https://github.com/user-attachments/assets/904fd24a-b5fc-4f0c-9556-5599ab95de77)
- 로그인한 사용자는 자신의 이름 정보를 확인할 수 있으며, 로그아웃 기능을 통해 안전하게 계정을 종료할 수 있습니다.
- 이 기능은 사용자 개인정보 보호를 위해 필수적인 요소입니다. 로그아웃 후에도 사용자의 세션 정보가 안전하게 관리됩니다.
