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
        │               │   ├── model
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
</details>

<details>
  <summary>데이터베이스 모델링 (ERD)</summary>

  *내용 입력*
  
</details>


<p align="right">(<a href="#약톡톡-백엔드-ytt-back-end">back to top</a>)</p>

## 상세 기능

*내용 입력*
