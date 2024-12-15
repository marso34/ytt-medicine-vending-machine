# 약톡톡 백엔드 (ytt Back-End) 

<!-- ABOUT THE PROJECT -->
## 프로젝트 개요

*내용 입력*

<br>

### 개발 환경

- Spring Boot 3.3.4
- JDK 17
- Gradle
- AWS - EC2 (`Ubuntu`), RDS (`MySQL`)
- IntelliJ IDEA

### 개발 기술

- Spring MVC
- Spring Data JPA
- Spring Security
- Swagger
- WebSocket
- JWT - Authentication, Authorization
- QueryDSL
- MySQL GIS
- Spring Actuator & Prometheus & Grafana


<p align="right">(<a href="#약톡톡-백엔드-ytt-back-end">back to top</a>)</p>

## 프로젝트 구조 (Architecture)

*내용 입력*

<br>

<details>
  <summary>패키지 구조</summary>
  
```
└── src  
    └── main  
        ├── java  
        │   └── com  
        │       └── example  
        │           └── ytt  
        │               ├── App.java  
        │               ├── domain
        │               │   ├── favorite
        │               │   ├── inventory
        │               │   ├── management
        │               │   ├── medicine
        │               │   ├── model        # 공통 Enum, Embeddable 등
        │               │   ├── order
        │               │   ├── user
        │               │   └── vendingmachine  
        │               └── global  
        │               │   ├── common 
        │               │   ├── config 
        │               │   ├── error 
        │               │   └── util
        │               └──YttApplication
        └── resources
            ├── application.yml
            ├── application-common.yml
            ├── application-local.yml  
            ├── application-prod.yml  
            └── application-secret.yml
```

- 도메인 상세
```
└── vendingmachine
    ├── controller
    ├── domain      # Entity, Enum, Embeddable 등
    ├── dto
    ├── exception
    ├── repository
    └── service
```

</details>

<details>
  <summary>데이터베이스 모델링 (ERD)</summary>

  *내용 입력*
  
</details>
<details>
  <summary>Getting Started</summary>

  `application-common.yml` - 로깅 등 공통 환경 설정  
  `application-local.yml` - 로컬 개발을 위한 H2 DB 설정  
  `application-prod.yml` - 운영 환경 설정. 운영 DB(MySQL) 설정  
  `application-secret.yml` - jwt, 공공데이터 키 설정
  ```
  jwt:
    secret: {{your_jwt_key}}
    expiration:
      authorization; {{your_expiration}}
      refresh: {{your_refresh_expiration}}

  open-api: # 의약품 허가정보 기준
    url:
      base-url: http://apis.data.go.kr/1471000/DrugPrdtPrmsnInfoService06
      dtl-url: /getDrugPrdtPrmsnDtlInq05
      list-url: /getDrugPrdtPrmsnInq06

    serviceKey: {{your_open_api_key}}
  ```

  H2 DB 사용 시 아래 sql 추가
  ```
  CREATE ALIAS IF NOT EXISTS H2GIS_SPATIAL FOR
  "org.h2gis.functions.factory.H2GISFunctions.load";
  
  CALL H2GIS_SPATIAL();
  ```

  *프로메테우스, 그라파나 설정 추가*
    
</details>


<p align="right">(<a href="#약톡톡-백엔드-ytt-back-end">back to top</a>)</p>

## 상세 기능

*내용 입력*
