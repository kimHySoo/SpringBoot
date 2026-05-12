### Annotation

- `@Type` , `@Data` , ….
- 소스 코드에 메타데이터를 삽입하여 추가적인 정보를 넣기 위한 방법
- 생성: `interface` 키워드 앞에 `@` 를 넣어서 만든다.
- 종류
    - 표준 어노테이션: 자바에서 기본적으로 제공하는 어노테이션
        - `@Override` , `@Deprecated` , `@SuppressWarnings`
    - 메타 어노테이션: 어노테이션을 정의할 때 사용하는 어노테이션
        - `@Target` , `@Retention`
    - 사용자 정의 어노테이션: 개발자가 정의하는 어노테이션

### 사용자 정의 어노테이션 만드는 방법

- 생성: `interface` 키워드 앞에 `@` 를 넣어서 만든다.
- 내부 요소를 마치 메서드처럼 선언한다
- 내부 요소 선언 규칙
    - 파라미터 선언 불가
    - 예외 선언 불가
    - 허용되는 반환 타입
        - 기본형: `int` , `boolean` , `double` , …
        - `String`
        - `java.lang.Class`
        - Enum
        - 다른 어노테이션
        - 위 타입들의 배열
    - `default`  키워드를 사용하여 요소의 기본값 지정 가능
    - 요소의 이름: 관습적으로 `value` 를 사용
        - 이름이 `value` 인 요소는 생략 가능
    - 요소의 반환 타입을 배열로 정의하면 해당 요소에 단일한 값, 배열을 모두 할당 가능


```java
public @interface CustomAnnotation {
	String[] value();
}


@CustomAnnotation(value={"Luna", "Max"})
@CustomAnnotation("Dasy")
```

어노테이션의 종류

- Marker : 요소를 하나도 정의하지 않은 어노테이션
- Single-value annotation: 요소가 1개인 어노테이션
- Multi-value annotation: 요소가 여러개인 어노테이션

```java

public @interface CustomAnnotation {
	  String value();
}

```
### @Target

- 기본적으로 우리가 어노테이션을 만들면, 어노테이션은 모든 곳에 붙일 수 있다.
    - 클래스 정의 앞에, 메서드 정의, 지역변수, 멤버변수, …..
- 어노테이션에 `@Target` 이 붙어 있으면 `@Target` 에 의해 지정된 위치에만 사용 가능
- 어노테이션이 붙을 수 있는 위치를 지정
- `ElementType` 이라는 `Enum` 을 통해 지정
    - `TYPE` : 클래스, 인터페이스, Enum
    - `FIELD` : 멤버 변수 앞
    - `METHOD` : 메서드 앞에
    - `PARAMETER` : 파라미터 앞에
    - `CONSTRUCTOR` : 생성자
    - `LOCAL_VARIABLE` : 지역변수
    - `ANNOTATION_TYPE` : 어노테이션



### @Retention

- 어노테이션의 정보가 언제까지 유지될 것인지(수명)를 지정
- `RetentionPolicy` 라는 `Enum` 타입으로 지정
    - `SOURCE` : 소스 코드(.java)에만 존재, 컴파일 시점에 사라짐. 주로 컴파일러에 의해 오류, 경고 등을 처리할 때 사용.
        - `@Override`
        - `@SuppressWarnings`
    - `CLASS` : 컴파일된 바이트 코드(.class)에는 남아있음, 실제 프로그램이 실행되는 런타임에는 메모리에 로드 되지 않음
        - Retention을 지정하지 않는 경우 `CLASS` 가 기본값
    - `RUNTIME` : 프로그램이 실행되는 런타임 시점까지 메모리에 유지됨!
        - `Reflection API` 를 이용해 실행 중에도 어노테이션 정보를 읽어와서 특정 로직 수행 가능
    

### @Component 계열 어노테이션
| 어노테이션 | 용도 | 설명 |
|---|---|---|
| @Component | 일반 컴포넌트 | 범용적인 스프링 빈 등록 |
| @Controller | 프레젠테이션 계층 | MVC 컨트롤러 클래스에 사용 |
| @Service | 비즈니스 계층 | 비즈니스 로직 클래스에 사용 |
| @Repository | 데이터 접근 계층 | DAO 클래스에 사용, 예외 변환 기능 |
| @Configuration | 설정 클래스 | Java Config 설정 클래스에 사용 |

@Controller, @Service, @Repository는 모두 @Component를 상속받은 특수화된 어노테이션
기능적으로 동일하지만, 계층을 구분하기 위해 사용
따라서 DTO처럼 특정 계층에 해당하지 않는 클래스는 @Component, 나머지는 역할에 맞게 사용


