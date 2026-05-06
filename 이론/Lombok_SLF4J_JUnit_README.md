# Lombok / SLF4J / JUnit 핵심 개념 정리

## 1. Lombok

### Lombok이란?
반복적인 Java 코드를 어노테이션으로 자동 생성해주는 도구.
AOP, DI, PSA와는 관계없이 순수하게 **개발 편의성**을 위한 라이브러리.

### Lombok 없이 vs 있을 때

```java
// Lombok 없이 — 수백 줄의 반복 코드 발생
public class User {
    private Long id;
    private String name;
    private String email;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String toString() { ... }
    public boolean equals() { ... }
}
```

```java
// Lombok 사용 — 어노테이션 하나로 끝
@Data
public class User {
    private Long id;
    private String name;
    private String email;
}
```

### 대표 어노테이션

| 어노테이션 | 역할 |
|-----------|------|
| `@Getter` | getter 메서드 자동 생성 |
| `@Setter` | setter 메서드 자동 생성 |
| `@ToString` | toString() 자동 생성 |
| `@AllArgsConstructor` | 전체 필드 생성자 자동 생성 |
| `@NoArgsConstructor` | 기본 생성자 자동 생성 |
| `@Data` | Getter + Setter + ToString + equals 한번에 적용 |
| `@Slf4j` | 로그 객체 자동 생성 |
| `@Builder` | 빌더 패턴 자동 생성 |

### Spring Boot에서 필수인가?

```
필수 여부  → 필수 아님
실무 사용  → 거의 모든 프로젝트에서 사용
이유       → 반복 코드 제거로 생산성 향상
```

Spring Initializr(프로젝트 생성 도구)에서도 기본 옵션으로 제공할 만큼 사실상 표준처럼 사용됨.

### Maven 설정

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

---

## 2. SLF4J

### SLF4J란?
**Simple Logging Facade for Java** — 로깅을 위한 추상화 레이어.
DI, AOP, PSA 중 **PSA와 AOP 두 가지 성격**을 모두 가지고 있음.

### PSA 관점 — 로깅 구현체 교체가 쉬움

```
SLF4J (추상화 레이어)
    ↓
Logback / Log4j / JUL (실제 구현체)
```

```java
// 구현체가 바뀌어도 이 코드는 절대 안 바뀜
log.info("유저 저장");
log.error("오류 발생");
```

```xml
<!-- 구현체만 바꾸면 끝 -->
implementation 'ch.qos.logback'   <!-- Logback -->
implementation 'log4j:log4j'      <!-- Log4j -->
```

### AOP 관점 — 공통 기능 분리

```java
@Slf4j
public class UserService {

    public void saveUser() {
        log.info("저장 시작");   // 공통 기능 (비즈니스 로직 아님)
        // 비즈니스 로직
        log.info("저장 완료");   // 공통 기능 (비즈니스 로직 아님)
    }
}
```

로깅 자체가 비즈니스 로직과 분리된 공통 기능이라 AOP적 성격도 가짐.

### Spring Boot에서 Maven 설정

```xml
<!-- spring-boot-starter 하나만 추가하면 SLF4J + Logback 자동 포함 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>
```

의존성 전이로 자동 포함되는 것들:
```
spring-boot-starter
├── spring-core
├── spring-context
├── slf4j-api        ← 자동으로 딸려옴
└── logback-classic  ← 자동으로 딸려옴
```

### 로그 레벨

```java
log.trace("가장 상세한 로그");
log.debug("디버깅용 로그");
log.info("일반 정보 로그");
log.warn("경고 로그");
log.error("오류 로그");
```

---

## 3. JUnit

### JUnit이란?
Java의 단위 테스트 프레임워크.
Maven 생명주기의 **test 단계에서 자동 실행**되어 잘못된 코드가 배포되는 것을 방지.

### Maven 생명주기와의 관계

```
1. validate
      ↓
2. compile
      ↓
3. test      ← 여기서 JUnit 자동 실행
      ↓
4. package
      ↓
...
```

`mvn package`만 실행해도 test 단계가 자동으로 포함됨:

```
mvn package 실행
      ↓
compile 단계 실행
      ↓
test 단계에서 JUnit 자동 실행
      ↓
테스트 실패 시 → 빌드 중단 (잘못된 배포 방지)
테스트 성공 시 → package 단계로 진행
```

### 기본 사용법

```java
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void 유저저장_테스트() {
        // given
        User user = new User("홍길동", "hong@email.com");

        // when
        User savedUser = userService.save(user);

        // then
        assertNotNull(savedUser.getId());
        assertEquals("홍길동", savedUser.getName());
    }
}
```

### 주요 어노테이션

| 어노테이션 | 역할 |
|-----------|------|
| `@Test` | 테스트 메서드 지정 |
| `@SpringBootTest` | Spring 컨텍스트 로드 후 테스트 |
| `@BeforeEach` | 각 테스트 실행 전 실행 |
| `@AfterEach` | 각 테스트 실행 후 실행 |
| `@DisplayName` | 테스트 이름 지정 |

### 테스트 없이 빌드하고 싶을 때

```
mvn package -DskipTests  → 테스트 건너뛰고 빌드
```

### Maven 설정

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>  <!-- 테스트 환경에서만 사용 -->
</dependency>
```

---

## 4. 전체 요약

```
Lombok
├── 목적  → 개발 편의성 (반복 코드 제거)
├── 관계  → AOP/PSA와 무관
└── 특징  → 어노테이션으로 getter/setter 등 자동 생성

SLF4J
├── 목적  → 로깅 추상화
├── PSA  → 로깅 구현체 교체해도 코드 그대로
└── AOP  → 비즈니스 로직과 분리된 공통 기능

JUnit
├── 목적  → 단위 테스트
├── 관계  → Maven test 단계에서 자동 실행
└── 특징  → 테스트 실패 시 빌드 중단으로 잘못된 배포 방지
```
