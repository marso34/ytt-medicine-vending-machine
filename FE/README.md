# 약톡톡 프론트엔드 (ytt Front-End)

<!-- ABOUT THE PROJECT -->
## 프로젝트 개요

### 개발 환경 
- IDE: Android Studio 버전 11 이상상
- 프로그래밍 언어: Kotlin 
- SDK: compileSdk 34, minSdk 30 
- 버전 관리 도구: Git 

### 개발 기술

- 지도 API: 네이버 지도를 사용하기 위해 Naver Cloud API를 적용하여 사용자에게 위치 기반 서비스를 제공합니다. 이는 사용자 경험을 극대화하고, 실시간 정보 제공을 가능하게 합니다.
- UI 설계: XML 레이아웃 파일을 사용하여 직관적이고 반응형 사용자 인터페이스를 디자인합니다. 사용자 피드백을 반영하여 지속적으로 개선해 나갑니다.
- 네트워킹: Retrofit 라이브러리를 이용해 RESTful API를 호출하고, 원활한 데이터 통신을 구현합니다. 이로 인해 사용자에게 신속하고 정확한 정보를 제공합니다.
- 데이터 관리: SharedPreference를 사용하여 사용자 설정 및 간단한 데이터를 안전하게 저장합니다. 이를 통해 앱의 성능을 최적화하고, 사용자의 편리함을 증대시킵니다.
- 암호화: HashKey로 비밀번호를 암호화하여 외부 유출 방지 및 보안을 강화합니다. 사용자 데이터 보호를 최우선으로 생각하여 설계하였습니다.
- 서버 엔드포인트 주소 관리: local.properties 파일을 통해 서버 주소 관리하여 보안성을 높이고 유연성을 제공합니다. 이를 통해 개발 및 배포 환경에 맞춰 쉽게 조정할 수 있습니다.
```
NAVERMAP_CLIENT_ID ="your_API_ID"
BaseUrl = "your server url"
SubUrl = "your websocket url"
```
<p align="right">(<a href="#프로젝트-개요">back to top</a>)</p>

## 프로젝트 구조 (Architecture)
- 아키텍처 (프로젝트 전체 구조): MVVM 아키텍처를 적용하여 각 컴포넌트는 medicine, vendingmachine, user, order 패키지에 따라 명확한 책임을 가지고 있습니다.

- Model: medicine, vendingmachine, user, order 패키지 내의 데이터 및 비즈니스 로직을 처리하며, 애플리케이션의 핵심 기능을 담당합니다.
- 예를 들어, Medicine 모델은 약의 정보와 재고를 관리하고, VendingMachine 모델은 자판기의 위치와 운영 상태를 처리합니다.

- View: 사용자 인터페이스는 Activity와 Fragment로 구성되어 있으며, 각 패키지에 맞는 UI 요소들을 포함하여 사용자 경험을 극대화합니다.
- User 패키지의 UI는 로그인 및 회원 가입을 쉽게 할 수 있도록 설계되었으며, Medicine과 VendingMachine의 UI는 사용자가 원하는 정보를 손쉽게 조회하고 선택할 수 있도록 구성되어 있습니다.

- ViewModel: 각 패키지에 대한 ViewModel이 존재하여 UI와 Model 간의 데이터 연결 및 비즈니스 로직 처리를 담당합니다.
- UserViewModel은 로그인 및 사용자 정보 관리를, VendingMachineViewModel은 자판기 데이터 제공을, MedicineViewModel은 약 정보 조회를, OrderViewModel은 주문 상태 관리를 담당합니다.


#### Model
![image](https://github.com/user-attachments/assets/7e9645f4-512f-4ea9-b3a2-2f4e47434111)       
User: 사용자 정보를 관리하며, 로그인 및 회원 가입 시 필요한 인증 로직을 포함합니다. 사용자의 상태(로그인 여부 등)를 저장하고, 개인 정보를 안전하게 처리합니다.
VendingMachine: 자판기 정보를 저장하고, 자판기 위치 및 운영 상태를 처리합니다. 사용자가 가까운 자판기를 조회할 수 있도록 데이터를 제공합니다.
Medicine: 자판기에서 판매되는 약의 정보를 관리합니다. 약의 이름, 가격, 이미지, 재고 상태 등을 포함하여 사용자가 쉽게 약을 선택할 수 있도록 합니다.
Order: 사용자가 주문한 약의 정보를 관리합니다. 주문 내역, 수량, 가격 등의 데이터를 처리하여 사용자가 주문 상태를 확인할 수 있게 합니다.

#### network
![image](https://github.com/user-attachments/assets/ce91867b-d9fc-44c1-ab6e-0df281f43d83)      
- ApiService에는 서버 경로가 정의되어 있으며, RetrofitAPI에는 Retrofit 관련 라이브러리 파일이 저장되어 있습니다.
- 이외에도 토큰 및 웹소켓 관련 클래스가 포함되어 있어 실시간 데이터 통신이 가능합니다. 네트워크 오류 처리를 통해 안정적인 데이터 통신을 보장합니다.

#### repository  
![image](https://github.com/user-attachments/assets/756bd6c5-f9da-4cc2-8180-91b672959aa2)      
- Repository: auth, medicine, order, vendingmachine에 관련된 메서드들을 관리하여 데이터 접근을 통합적으로 처리합니다. 이를 통해 데이터 관리의 일관성을 유지합니다.
- 각 Repository는 필요한 데이터 소스에 따라 적절한 메서드를 호출하여 필요한 정보를 효율적으로 가져옵니다.
- 
#### View
![image](https://github.com/user-attachments/assets/3ca8dd91-915a-478a-aea5-91764b6ee56a)      
- User Interface (UI): Activity와 Fragment로 구성되어 있으며, 사용자와의 상호작용을 최적화하도록 설계되었습니다.
- 예를 들어, 자판기 목록 화면은 지도와 리스트 형태로 자판기 정보를 제공하여 사용자가 쉽게 탐색할 수 있습니다. 약 조회 화면은 그리드 형태로 약의 정보를 표시하여 사용자가 선택하기 쉽게 구성되어 있습니다.
- Navigation: 사용자 경험을 고려하여 화면 간의 전환을 매끄럽게 처리합니다. 버튼 클릭이나 탭 선택 시 적절한 Fragment로 이동하도록 설계되어 있습니다.
  
#### ViewModel
![image](https://github.com/user-attachments/assets/d65d18c6-9eef-4809-918e-f071d3a6ead3)      
- ViewModel: 각 모델에 대한 ViewModel이 존재하여 UI와 데이터 간의 연결을 효과적으로 관리합니다.
- UserViewModel은 로그인 및 사용자 정보 처리를 담당하며, VendingMachineViewModel은 자판기 데이터를 제공하여 사용자가 쉽게 접근할 수 있도록 합니다.
- MedicineViewModel은 약 정보 조회를 담당하여 사용자가 필요한 약을 빠르게 찾을 수 있도록 지원합니다.

## 상세 기능

##### 회원 로그인 기능
<img src="https://github.com/user-attachments/assets/cd87700e-2b30-4828-8987-0aea0196a8a0" width="300" height="600">     
<br>

- 사용자는 회원 가입 시 등록한 아이디와 비밀번호를 이용하여 손쉽게 로그인할 수 있습니다.
- 로그인 과정에서 발생하는 오류 메시지를 통해 사용자 경험을 개선할 수 있도록 하였습니다.
- 로그인 후 사용자는 개인화된 서비스를 제공받을 수 있습니다.

#### 자판기 조회 기능
<img src="https://github.com/user-attachments/assets/eb4aaf23-96d0-4244-99a2-f73e8e6055c8" width="300" height="600">      
<br>

- 사용자는 자신의 현재 위치를 기반으로 가까운 자판기를 검색하고 목록을 조회할 수 있습니다.
- UI는 지도상에 자판기의 위치를 표시하고, 하단 바텀 시트에서 자판기 목록을 그리드 형태로 제공하여 쉽게 접근할 수 있도록 설계되었습니다. 
- 지도와 리스트 뷰 간의 전환이 원활하여 사용자 편의성이 높습니다.

##### 약 조회 기능
<img src="https://github.com/user-attachments/assets/b0bc1ba0-ec6f-4458-8bc2-811ea0f0ba7c" width="300" height="600">
<br>

- 사용자는 선택한 자판기에서 판매 중인 약 목록을 손쉽게 조회할 수 있습니다.
- UI는 약 목록을 그리드 형태로 표시하며, 각 약의 이미지와 가격, 상품 코드가 함께 나타나 사용자에게 유용한 정보를 제공합니다. 
- 검색 기능을 통해 원하는 약을 더욱 쉽게 찾을 수 있습니다.

#### 즐겨찾기 기능
<img src="https://github.com/user-attachments/assets/69156160-657a-4afd-817c-33eb1d57fa7e" width="300" height="600">
<img src="https://github.com/user-attachments/assets/16498e49-a7fc-4878-8972-ffabd6c2c501" width="300" height="600">
<br>
       
- 사용자는 자판기 목록에서 하트를 눌러 즐겨찾기에 추가할 수 있으며, 즐겨찾기한 자판기는 별도의 탭에서 쉽게 확인할 수 있습니다.
- 이 기능은 사용자가 자주 이용하는 자판기를 빠르게 찾을 수 있도록 도와줍니다.
- 즐겨찾기 목록은 사용자의 선호에 따라 쉽게 관리할 수 있습니다.

#### 약 선택 및 약 상세 기능 설명 기능
<img src="https://github.com/user-attachments/assets/cd87700e-2b30-4828-8987-0aea0196a8a0" width="300" height="600">   
<br>
      
- 사용자가 원하는 약을 선택하면 해당 약에 대한 상세 정보를 제공하며, 원하는 수량을 조절하여 구매 목록에 추가할 수 있습니다.
- UI는 선택된 약이 구매할 목록에 추가되는 모습을 직관적으로 보여주어 사용자 편의성을 높입니다. 상세 정보 페이지에는 복용 방법 및 주의 사항도 포함되어 있습니다.

#### 약 주문하기 및 취소하기 기능  
<img src="https://github.com/user-attachments/assets/318d0f59-c60e-4598-9b9b-215622aef3db" width="300" height="600">   
<br>

- 사용자가 선택한 약을 주문할 수 있는 기능을 제공하며, 주문한 약의 이름, 가격, 수량을 한눈에 확인할 수 있습니다.
- 주문하기 버튼을 누르면 자판기에 입력할 수 있는 QR 코드가 생성되며, 필요 시 주문 취소도 가능합니다. 주문 내역은 사용자가 쉽게 관리할 수 있도록 구성되어 있습니다.

#### 주문 실패 시 사유 확인 기능
<img src="https://github.com/user-attachments/assets/0074f543-654d-4fc5-942e-ee6fb3bfd89b" width="300" height="600">    
<br>

- 주문이 실패했을 경우, 사용자는 그 이유를 다이얼로그로 확인할 수 있습니다.      
이를 통해 사용자는 문제를 빠르게 인지하고 해결할 수 있습니다.

#### 주문 목록 확인 기능  
<img src="https://github.com/user-attachments/assets/46fc9ba5-1a40-447f-86e4-61621863b314" width="300" height="600">  
<br>

- 사용자는 자신의 주문 목록을 전체적으로 확인할 수 있으며, 주문 내역을 쉽게 관리할 수 있습니다.
- 각 주문 항목에 대한 상세 정보도 제공하여, 사용자 경험을 더욱 향상시킵니다.

#### 사용자 정보 확인, 로그아웃 기능
<img src="https://github.com/user-attachments/assets/ce60f22c-0f15-47a1-8c10-85932cf0402f" width="300" height="600">   
<br>

- 로그인한 사용자는 자신의 이름 정보를 확인할 수 있으며, 로그아웃 기능을 통해 안전하게 계정을 종료할 수 있습니다.
- 이 기능은 사용자 개인정보 보호를 위해 필수적인 요소입니다. 로그아웃 후에도 사용자의 세션 정보가 안전하게 관리됩니다.
