# Spring Boot 정리

## 목차
- [등장 배경](#등장-배경)
- [3가지 핵심 특징](#3가지-핵심-특징)
- [JSP 사용 설정](#jsp-사용-설정)
- [application.properties](#applicationproperties)
- [Configuration & Bean 등록](#configuration--bean-등록)
- [의존성 주입(DI)](#의존성-주입di)

---

## 등장 배경

기존 Spring Framework는 아래와 같은 불편함이 있었다.

- XML 기반의 복잡한 빈(Bean) 설정
- 라이브러리 버전 충돌 문제를 개발자가 직접 관리
- 내장 서버가 없어 별도의 WAS(Tomcat 등)를 설치·배포해야 함
- 반복적인 boilerplate 설정 코드

**Spring Boot**는 이러한 불편함을 해소하고 빠른 개발 환경 구성을 목표로 등장했다.

---

## 3가지 핵심 특징

| 특징 | 설명 |
|---|---|
| 자동 구성 (Auto Configuration) | 클래스패스의 라이브러리를 감지해 필요한 빈을 자동으로 등록·초기화 |
| 독선적 접근 방식 (Opinionated Defaults) | Spring 관련 기술의 버전을 자동으로 호환 조정 (spring-boot-starter로 관리) |
| 독립적 애플리케이션 (Standalone Application) | 내장 서버(Tomcat/Jetty/Undertow)를 제공하여 별도 WAS 없이 `java -jar`로 실행 가능 |

> **Auto Configuration 동작 원리**
> `@SpringBootApplication` 안에 포함된 `@EnableAutoConfiguration`이 `spring.factories` 또는
> `AutoConfiguration.imports` 파일을 읽어 조건(`@ConditionalOn*`)에 맞는 설정 클래스를 자동 적용한다.

---

## JSP 사용 설정

Spring Boot는 기본적으로 **Thymeleaf**를 권장 템플릿 엔진으로 사용한다.
JSP를 사용하려면 내장 Tomcat에 Jasper(JSP 컴파일러)와 JSTL을 추가해야 한다.

```xml
<!-- pom.xml -->

<!-- JSP 컴파일러 (내장 Tomcat용) -->
<dependency>
    <groupId>org.apache.tomcat.embed</groupId>
    <artifactId>tomcat-embed-jasper</artifactId>
</dependency>

<!-- JSTL API -->
<dependency>
    <groupId>jakarta.servlet.jsp.jstl</groupId>
    <artifactId>jakarta.servlet.jsp.jstl-api</artifactId>
</dependency>

<!-- JSTL 구현체 -->
<dependency>
    <groupId>org.glassfish.web</groupId>
    <artifactId>jakarta.servlet.jsp.jstl</artifactId>
</dependency>
```

> **주의:** Spring Boot는 실행 가능한 JAR(Executable JAR) 패키징 시 내장 Tomcat 내부에서 JSP를 지원하지 않는다.
> JSP를 사용할 경우 **WAR로 패키징**하거나, 내장 서버를 별도로 구성해야 한다.
> 가능하면 Thymeleaf 또는 Mustache 같은 템플릿 엔진 사용을 권장한다.

`application.properties`에 JSP 뷰 리졸버 경로도 추가해야 한다.

```properties
spring.mvc.view.prefix=/WEB-INF/views/
spring.mvc.view.suffix=.jsp
```

---

## application.properties

기존 Spring에서는 `applicationContext.xml` 등 XML 파일에 설정을 작성했지만,
Spring Boot에서는 `src/main/resources/application.properties` (또는 `application.yml`)로 일원화하여 관리한다.

설정값을 소스 코드 밖으로 분리(외부화, Externalized Configuration)함으로써,
값 변경 시 재컴파일·재빌드 없이 설정만 바꿔 적용할 수 있다.

### 주요 속성

| 항목 | 설명 |
|---|---|
| `server.port` | 내장 서버 포트 설정 (기본값: `8080`) |
| `spring.application.name` | 애플리케이션 이름 (로깅, 모니터링 등에 활용) |
| `spring.mvc.view.prefix` / `suffix` | JSP 뷰 리졸버 경로 |
| 커스텀 프로퍼티 | 개발자가 직접 정의하는 설정값 |

```properties
server.port=8080
spring.application.name=hello-boot
app.greeting=hello
```

### @Value로 프로퍼티 주입

`@Value` 어노테이션으로 프로퍼티 값을 빈의 필드에 직접 주입할 수 있다.

```java
@Service
public class HelloService {

    @Value("${app.greeting}")
    private String greeting;

    public String say() {
        return greeting;
    }
}
```

> **주의:** `@Value`는 `${키}` 형식을 사용한다. (`#{...}`는 SpEL 표현식용)
> 키가 존재하지 않으면 런타임 오류가 발생하므로 기본값을 지정하는 것이 안전하다.
>
> ```java
> @Value("${app.greeting:안녕하세요}")  // 키 없을 때 기본값 지정
> private String greeting;
> ```

### @ConfigurationProperties (권장)

프로퍼티가 많아질 경우 `@ConfigurationProperties`로 묶어서 관리하는 방법이 더 권장된다.

```properties
app.greeting=hello
app.max-count=10
```

```java
@ConfigurationProperties(prefix = "app")
@Component
public class AppProperties {
    private String greeting;
    private int maxCount;

    // getter, setter 필요
}
```

---

## Configuration & Bean 등록

`@Configuration` + `@Bean` 어노테이션을 사용하면 XML 없이 Java 코드만으로 빈을 등록하고 생명주기를 제어할 수 있다.

### @Bean 기본 사용

```java
@Configuration
public class AppConfig {

    // 메서드 이름이 기본 빈 이름이 됨 → "myService"
    @Bean
    public MyService myService() {
        return new MyService();
    }
}
```

### @Bean 옵션

빈 이름을 명시하거나, 초기화·소멸 시점에 특정 로직을 실행해야 할 때 사용한다.

```java
@Configuration
public class AppConfig {

    // 빈 이름을 "customBeanName"으로 명시
    @Bean(name = "customBeanName")
    public MyService myService() {
        return new MyService();
    }

    // 빈 초기화 후 init() 호출, 컨테이너 종료 전 cleanup() 호출
    @Bean(initMethod = "init", destroyMethod = "cleanup")
    public MyResource myResource() {
        return new MyResource();
    }
}
```

| 옵션 | 설명 |
|---|---|
| `name` | 빈 이름 명시 (미지정 시 메서드 이름이 빈 이름) |
| `initMethod` | 빈 생성 후 호출할 초기화 메서드 이름 |
| `destroyMethod` | 컨테이너 종료 시 호출할 소멸 메서드 이름 |

> **initMethod / destroyMethod 대안**
> 어노테이션으로 처리하려면 `@PostConstruct` / `@PreDestroy`를 사용한다.
>
> ```java
> @Component
> public class MyResource {
>     @PostConstruct
>     public void init() { /* 초기화 */ }
>
>     @PreDestroy
>     public void cleanup() { /* 소멸 */ }
> }
> ```

### @Value와 함께 사용

`@Configuration` 클래스 내에서도 `@Value`로 프로퍼티를 주입할 수 있다.

```java
@Configuration
public class AppConfig {

    @Value("${app.name}")
    private String appName;

    @Bean
    public MyService myService() {
        return new MyService(appName);
    }
}
```

---

## 의존성 주입(DI)

Spring Boot에서 DI를 적용하는 주요 방법 3가지.

### 1. 생성자 주입 (권장)

```java
@Service
public class OrderService {

    private final UserRepository userRepository;

    // @Autowired 생략 가능 (생성자가 하나일 때)
    public OrderService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

### 2. 필드 주입

```java
@Service
public class OrderService {

    @Autowired
    private UserRepository userRepository;
}
```

> 테스트 시 의존성을 외부에서 주입하기 어려워 **권장하지 않는다.**

### 3. 세터 주입

```java
@Service
public class OrderService {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

> 선택적 의존성(Optional Dependency)에 사용한다.

### 빈 자동 등록 어노테이션

| 어노테이션 | 용도 |
|---|---|
| `@Component` | 일반 컴포넌트 (다른 어노테이션의 기반) |
| `@Service` | 비즈니스 로직 계층 |
| `@Repository` | 데이터 접근 계층 (예외 변환 기능 포함) |
| `@Controller` / `@RestController` | 웹 요청 처리 계층 |
| `@Configuration` | 설정 클래스 |

---

## 정리

```
Spring Boot = Spring Framework
            + Auto Configuration
            + Embedded Server
            + Starter 의존성 관리
```

Spring Boot는 복잡한 설정을 자동화하고 개발자가 비즈니스 로직에 집중할 수 있도록 돕는다.
설정보다 관례(Convention over Configuration)를 따르되, 필요한 경우 `application.properties`와
`@Configuration`을 통해 세밀하게 제어할 수 있다.