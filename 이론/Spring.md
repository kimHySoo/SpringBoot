# Spring & Spring Boot 핵심 개념 정리

## 1. POJO란?

**Plain Old Java Object** — 특정 프레임워크에 종속되지 않은 순수한 Java 객체.
Spring의 핵심 철학은 POJO 기반으로 개발하는 것이며, 이를 구현하기 위한 세 가지 핵심 기술이 DI, AOP, PSA다.

---

## 2. Spring vs Spring Boot

```
Spring
└── POJO 철학을 DI, AOP, PSA로 구현한 프레임워크

Spring Boot
└── Spring을 더 쉽고 빠르게 쓰기 위한 도구
      ├── Auto Configuration — 설정 자동화
      ├── 내장 톰캣 — 서버 별도 설치 불필요
      ├── Starter 의존성 — 버전 충돌 없이 묶음 제공
      └── application.properties — 한 파일에서 설정 관리
```

---

## 3. DI (Dependency Injection, 의존성 주입)

### Bean이란?
Spring 컨테이너가 생성하고 관리하는 객체.
`@Service`, `@Component`, `@Controller` 등의 어노테이션을 붙이면 Bean으로 등록된다.

### Bean의 구성요소
| 구성요소 | 설명 |
|--------|------|
| 필드 (Field) | Bean이 가지는 데이터. `@Value`, `@Autowired`로 값 주입 |
| 생성자 (Constructor) | Spring이 Bean 생성 시 호출. 의존성 주입에도 사용 |
| 메서드 (Method) | Bean이 제공하는 기능. 다른 Bean에서 호출하여 사용 |

### DI 동작 방식

```java
// 일반 Java — 개발자가 직접 생성
private HelloService helloService = new HelloService();

// Spring DI — Spring이 자동으로 주입
@Autowired
private HelloService helloService;
```

### DI의 흐름

```
앱 시작
  ↓
@ComponentScan으로 Bean 스캔 및 등록
  ↓
@Autowired 위치에 해당 Bean 자동 주입
```

### DI의 장점
- 개발자가 `new` 키워드로 직접 객체 생성/관리 불필요
- 싱글톤으로 하나의 인스턴스만 생성하여 메모리 절약
- 구현체 교체 시 호출부 코드 수정 불필요 (인터페이스 유지 조건)

```java
// 인터페이스 유지 시 구현체가 바뀌어도 호출부는 그대로
public interface HelloService {
    String sayHello();
}

@Service
public class MySQLHelloService implements HelloService { ... }  // 교체 전

@Service
public class MongoHelloService implements HelloService { ... }  // 교체 후

// Controller는 코드 변경 없음
@Autowired
private HelloService helloService;
```

---

## 4. AOP (Aspect Oriented Programming, 관점 지향 프로그래밍)

### 개념
공통 기능(로깅, 트랜잭션, 권한 등)을 비즈니스 로직과 분리하여 어노테이션 하나로 적용하는 방식.

### 동작 방식
코드를 수정하는 게 아니라, **런타임에 프록시 객체를 생성**하여 앞뒤에 공통 기능을 자동 실행.

```
전처리                    핵심 로직          후처리
─────────────────────    ──────────────    ─────────────────────
권한 체크 (@Secured)  →  비즈니스 로직  →  트랜잭션 커밋/롤백
트랜잭션 시작                               로그 기록
```

### 대표 어노테이션
| 어노테이션 | 역할 |
|-----------|------|
| `@Transactional` | 트랜잭션 시작/커밋/롤백 자동 처리 |
| `@Secured` | 권한/인증 체크 |
| `@Cacheable` | 캐시 조회/저장 |
| `@Slf4j` | 로깅 기능 추가 |

### AOP의 장점

```
Before AOP                      After AOP
──────────────────────          ──────────────────────
트랜잭션 시작 코드 작성           @Transactional 한 줄
비즈니스 로직 작성                비즈니스 로직 작성
트랜잭션 커밋/롤백 코드 작성      끝
예외처리 코드 작성
```

---

## 5. PSA (Portable Service Abstraction, 이식 가능한 서비스 추상화)

### 개념
기술이 바뀌어도 개발자 코드는 그대로 유지되도록 Spring이 중간 추상화 레이어를 제공하는 것.

```
개발자 코드
    ↓
Spring 추상화 레이어 (인터페이스)
    ↓
실제 기술 구현체 (MySQL, Redis, Kafka 등)
```

### 예시

```java
// DB 기술이 바뀌어도 JPA 코드는 그대로
public interface UserRepository extends JpaRepository { ... }

// 캐시 기술이 바뀌어도 코드는 그대로
@Cacheable("users")
public User getUser(Long id) { ... }
```

```properties
# application.properties 설정만 바꾸면 기술 교체 완료
spring.cache.type=redis   # → ehcache로 바꿔도 코드 수정 없음
```

### PSA 지원 범위
| 구분 | 설명 |
|-----|------|
| Spring 공식 지원 | DB, 캐시, 메시지 큐, 이메일 등 → PSA 완벽 적용, 교체해도 코드 그대로 |
| 써드파티 라이브러리 | import해서 사용 가능하지만 교체 시 코드 수정 필요 |
| 외부 API (OpenAI 등) | 개발자가 직접 호출 코드 작성 필요. Spring은 DI, AOP로 보조만 해줌 |

---

## 6. 전체 구조 요약

```
POJO (Spring의 핵심 철학)
├── DI  → 객체 생성/관리를 Spring에 위임, 교체가 쉬움
├── AOP → 공통 기능을 어노테이션으로 분리, 비즈니스 로직에 집중
└── PSA → 기술 스택 교체가 쉬움, Spring이 중간 다리 역할

Spring Boot = Spring + 자동설정
└── 위 개념들을 더 쉽고 빠르게 사용할 수 있도록 편의기능 제공
```
