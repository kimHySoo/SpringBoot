# Maven 핵심 개념 정리

## 1. Maven이란?

**빌드 도구(Build Tool)** — 라이브러리 의존성 관리 및 빌드 과정을 자동화하는 도구.
Spring과의 관계는 아래와 같다:

```
Maven/Gradle → Spring 라이브러리를 가져오는 도구
Spring       → 가져온 라이브러리로 DI, AOP, PSA 구현
```

---

## 2. Maven vs Docker

Maven과 Docker는 해결하는 영역이 다르다:

```
Maven  → 코드 레벨 (라이브러리, 빌드 자동화)
Docker → 서버 레벨 (OS, Java 버전, 실행 환경 전체)
```

| 구분 | Maven | Docker |
|-----|-------|--------|
| 역할 | 라이브러리/빌드 환경 통일 | OS/Java/실행 환경 전체 통일 |
| 설정 파일 | pom.xml | Dockerfile |
| 해결 문제 | 라이브러리 버전 충돌 | "내 컴퓨터에서는 되는데" 문제 |

### 실제 배포 흐름

```
Maven으로 빌드 (jar 파일 생성)
        ↓
Docker 이미지에 jar 파일 포함
        ↓
어떤 서버에서 실행해도 동일한 환경 보장
```

### 언제 Maven만 써도 되나?

```
혼자 개발, 내 컴퓨터만 사용  → Maven으로 충분
팀 개발, 여러 서버에 배포    → Maven + Docker 필요
```

---

## 3. 의존성 관리

### pom.xml 기반 라이브러리 관리

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <version>3.0.0</version>  <!-- 이 버전으로 팀 전체 통일 -->
    </dependency>
</dependencies>
```

### 의존성 전이 (Transitive Dependency)

```
spring-boot-starter-web 하나만 추가해도
        ↓
필요한 라이브러리들을 자동으로 같이 다운로드
├── spring-web
├── spring-webmvc
├── tomcat
└── jackson
```

### 중앙 저장소 (Maven Central)

```
pom.xml에 라이브러리 추가
        ↓
Maven Central에서 자동 다운로드
        ↓
로컬 캐시에 저장 (~/.m2)
        ↓
같은 라이브러리 재요청 시 캐시에서 바로 사용
```

---

## 4. 빌드 생명주기

Maven이 빌드 생명주기를 정의하고 관리한다.
특정 단계를 실행하면 **그 이전 단계도 자동으로 순서대로 실행**된다.

```
1. validate  → 프로젝트 구조가 올바른지 확인
      ↓
2. compile   → Java 코드를 .class 파일로 컴파일
      ↓
3. test      → 테스트 코드 실행
      ↓
4. package   → .jar / .war 파일로 패키징
      ↓
5. verify    → 패키지가 올바른지 검증
      ↓
6. install   → 로컬 저장소에 저장
      ↓
7. deploy    → 원격 저장소에 배포
```

### 주요 명령어

| 명령어 | 설명 |
|-------|------|
| `mvn compile` | 문법 오류 확인 |
| `mvn test` | 기능 정상 동작 확인 |
| `mvn package` | 실행 파일(.jar) 생성 |
| `mvn install` | 로컬 저장소에 저장 |
| `mvn deploy` | 원격 서버에 배포 |

### 개발 흐름

```
코드 작성
    ↓
mvn compile  → 문법 오류 확인
    ↓
mvn test     → 기능 정상 동작 확인
    ↓
mvn package  → 실제 실행 파일 생성 및 확인
    ↓
문제 없으면 Docker로 묶어서 서버에 배포
```

> Spring Boot에서는 IDE 실행 버튼이 Maven 생명주기를 내부적으로 자동 실행하고 내장 톰캣으로 바로 실행해줌

---

## 5. Maven의 주요 특징

### 1) 표준화된 프로젝트 구조

```
src/main/java      → 실제 코드
src/main/resources → 설정 파일 (application.properties)
src/test/java      → 테스트 코드
pom.xml            → 의존성/빌드 설정
```
모든 프로젝트가 동일한 구조를 가져서 새 프로젝트도 바로 파악 가능

### 2) 플러그인 시스템

```xml
<!-- 기능을 플러그인으로 확장 가능 -->
<plugin>
    <artifactId>maven-surefire-plugin</artifactId>  <!-- 테스트 플러그인 -->
</plugin>
<plugin>
    <artifactId>maven-docker-plugin</artifactId>    <!-- Docker 빌드 플러그인 -->
</plugin>
```

### 3) 멀티 모듈 프로젝트

```
큰 프로젝트를 모듈로 분리해서 관리

parent-project
├── module-user    → 유저 관련 코드
├── module-order   → 주문 관련 코드
└── module-common  → 공통 코드
```

---

## 6. 전체 요약

```
Maven
├── 의존성 관리       → pom.xml로 라이브러리 버전 통일
├── 빌드 생명주기     → 컴파일부터 배포까지 자동화
├── 표준 프로젝트 구조 → 모든 프로젝트가 동일한 구조
├── 중앙 저장소       → 라이브러리 자동 다운로드/캐시
├── 플러그인 시스템    → 기능 확장 가능
└── 멀티 모듈         → 큰 프로젝트를 모듈로 분리 관리
```
