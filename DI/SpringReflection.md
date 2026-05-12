# Spring Reflection 정리

## 1. Reflection이란?

Java의 핵심 기능으로, **런타임 시점에 클래스, 메서드, 필드 등의 정보를 동적으로 조회하고 조작**할 수 있는 메커니즘.

- 자바 클래스 자체에 대한 정보에 접근
- reflexivity: 거울처럼 자기 자신을 들여다 봄
- `SomeClass.class` : 클래스 자체에 대한 정보를 담고 있는 객체
- `java.lang.Class` : 클래스(인터페이스) 자체에 대한 정보를 담는 클래스
- `java.lang.reflect.*` : 나머지 reflection와 관련된 클래스
    - `java.lang.reflect.Method`  : 메서드 자체에 대한 정보를 담는 클래스
    - `java.lang.reflect.Parameter`
    - `java.lang.reflect.Type`

**클래스 정보에 접근하는 방법**

- `Class c1 = Class.forName("풀패키지명");`
- `Class c2 = SomeClass.class`
- `Class c3 = obj_var.getClass()`




```java
// 일반 방식
Cat cat = new Cat();
cat.setName("Dasy");
cat.setAge(2);

// Reflection 방식
Class<?> c = Class.forName("com.ssafy.reflection.Cat");
Object cat = c.getDeclaredConstructor().newInstance();
c.getDeclaredMethod("setName", String.class).invoke(cat, "Dasy");
c.getDeclaredMethod("setAge", int.class).invoke(cat, 2);
```

결과는 동일하지만, Reflection은 **클래스 이름 문자열만 있으면** 컴파일 타임에 해당 클래스를 몰라도 동작한다.

---

## 2. Spring이 Reflection을 사용하는 곳

| 기능 | 설명 |
|---|---|
| **의존성 주입 (DI)** | `@Autowired` 필드/생성자를 Reflection으로 찾아 Bean 주입 |
| **Bean 생성** | `getDeclaredConstructor().newInstance()`로 인스턴스 생성 |
| **AOP** | 메서드 호출을 가로채기 위해 동적으로 메서드 호출 |
| **어노테이션 처리** | `@Transactional`, `@Cacheable` 등을 런타임에 읽어 동작 결정 |
| **Spring MVC** | `@RequestMapping` 메서드를 찾아 HTTP 요청 매핑 |

---

## 3. Bean 등록 방법

Spring IoC 컨테이너에 Bean을 등록하는 방식은 크게 **XML 방식**과 **Java 방식**으로 나뉜다.

IoC(Inversion of Control) -> 구현 방법이 DI
 - 객체의 생성과 생명주기 관리를 개발자가 아닌 프레임워크(Spring Container)에게 위임

Bean은 Spring IoC Container가 관리하는 객체
 - Spring Container에 의해 생성되고 관리
 - 기본적으로 싱글톤(Singleton)으로 관리
 - 설정파일(XML 또는 Java Config)에 정의



```java

//IoC 적용: 프레임워크가 객체를 생성하고 주입
public class OrderService{
    private OrderRepository repository; //Spring이 주입
    private PaymentService payment; //Spring이 주입
}
```


```
Bean 등록 방법
├── 1. XML 설정
│   ├── 1.1 <bean> 태그로 직접 등록
│   └── 1.2 @Component + component-scan
└── 2. Java 설정 (@Configuration)
    ├── 2.1 @Bean 메서드로 직접 등록
    └── 2.2 @Component + @ComponentScan
```

### 1.1 XML - 직접 등록
```xml
<bean id="userService" class="com.example.UserService" />
```

### 1.2 XML - 컴포넌트 스캔
```xml
<context:component-scan base-package="com.example" />
```
```java
@Component
public class UserService { }
```

### 2.1 Java - 직접 등록
```java
@Configuration
public class AppConfig {
    @Bean
    public UserService userService() { // id = "userService"
        return new UserService();      // class = UserService
    }
}
```

### 2.2 Java - 컴포넌트 스캔
```java
@Configuration
@ComponentScan("com.example")
public class AppConfig { }
```
```java
@Component
public class UserService { }
```

> **직접 등록**: 개발자가 객체 생성을 직접 제어 → 외부 라이브러리 Bean 등록 시 유용  
> **컴포넌트 스캔**: Spring이 자동으로 찾아서 등록 → 편리하지만 제어권이 줄어듦

---

## 4. @Component Bean을 찾는 방식

단순히 Reflection만 쓰는 게 아니라 **ClassPath Scanning + Reflection** 두 가지가 함께 사용된다.

```
1. ClassPath Scanning  →  클래스 파일(.class)을 탐색
2. Reflection          →  어노테이션 정보를 읽음
3. IoC 컨테이너        →  Bean으로 등록
```

### 1단계 - ClassPath Scanning (ASM 라이브러리)
- `.class` 파일들을 **바이트코드 수준**에서 훑음
- 클래스를 실제로 로딩(load)하지 않고 파일만 읽음 → 성능 최적화

### 2단계 - Reflection으로 어노테이션 확인
```java
Class<?> clazz = Class.forName("com.example.UserService");
Component component = clazz.getAnnotation(Component.class); // reflection
if (component != null) {
    // Bean으로 등록!
}
```

### 3단계 - Bean 등록
- Reflection으로 생성자를 찾아 인스턴스 생성
- IoC 컨테이너에 등록

> **"클래스를 찾는 것"** → ClassPath Scanning (ASM)  
> **"어노테이션을 읽는 것"** → Reflection

---

## 5. Spring vs Spring Boot에서의 Reflection

| | Spring | Spring Boot |
|---|---|---|
| Reflection 사용 | ✅ | ✅ (동일) |
| Bean 등록 | 개발자가 설정 | 자동 설정 추가 |
| Auto Configuration | ❌ | ✅ Reflection으로 조건부 로딩 |

Spring Boot는 Reflection을 그대로 사용하되, **Auto Configuration** 레이어가 추가된다.

```java
// @ConditionalOnClass → reflection으로 해당 클래스 존재 여부 확인
@ConditionalOnClass(DataSource.class)
@EnableAutoConfiguration
public class DataSourceAutoConfiguration { }
```

```java
@SpringBootApplication
// ↓ 이 안에 아래 3개가 합쳐져 있음
@SpringBootConfiguration   // = @Configuration
@EnableAutoConfiguration   // auto config 로딩 (reflection)
@ComponentScan             // 컴포넌트 스캔 (reflection)
```

---

## 6. Reflection 장단점

| 장점 | 단점 |
|---|---|
| 유연하고 동적인 코드 가능 | 일반 호출보다 느림 |
| 프레임워크 개발에 적합 | 컴파일 타임 타입 체크 불가 |
| 어노테이션 기반 설정 가능 | 캡슐화 원칙 우회 가능 |

---

## 핵심 요약

> Spring은 Reflection을 통해 개발자가 `new` 키워드 없이도 객체를 생성하고 주입받을 수 있게 해주며,  
> 어노테이션만으로 복잡한 동작(트랜잭션, AOP 등)을 처리할 수 있는 편의성의 근간이다.