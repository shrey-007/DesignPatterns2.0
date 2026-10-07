# Builder Pattern

## 1. Problem

### What real software problem does this pattern solve?

You need to construct a **complex object** that has many fields — some required, some optional, some with defaults, some with interdependencies. Using a constructor with many parameters (known as a **telescoping constructor**) becomes unreadable and error-prone.

```java
// Telescoping constructor anti-pattern — which parameter is which?!
HttpRequest request = new HttpRequest(
    "POST",                    // method
    "https://api.example.com", // url
    "{\"key\": \"value\"}",    // body
    "application/json",        // content type
    "Bearer abc123",           // auth token
    30000,                     // timeout ms
    3,                         // retries
    true,                      // follow redirects
    false,                     // verify SSL
    null                       // proxy
);
```

### What becomes difficult without it?

- **Readability** — Constructors with 5+ parameters are impossible to read. Is `30000` the timeout or the max retries?
- **Immutability** — Without a builder, you either use a mutable constructor (risky) or create dozens of constructor overloads.
- **Optional parameters** — Java doesn't have named/default parameters. You end up with `null` placeholders or multiple constructors.
- **Validation** — Hard to validate complex constraints (e.g., "if method is GET, body must be null") when everything is passed at once.
- **Step-by-step construction** — Some objects need to be assembled piece by piece (e.g., SQL queries, HTTP requests, UI layouts).

---

## 2. Core Idea

The Builder pattern **separates the construction of a complex object from its representation**. Instead of calling a constructor with 10 parameters, you use a separate Builder object that lets you set each property by name, one at a time, in a readable fluent chain. When you're done, you call `build()` to get the final, often immutable, object.

**Think of it like ordering at a restaurant:** Instead of shouting "burger-medium-cheddar-no-onions-extra-pickles-wheat-bun" all at once, you fill out an order form one field at a time.

---

## 3. When to Use It

| Signal | Example |
|--------|---------|
| Constructor has **4+ parameters** | `new HttpRequest(method, url, body, headers, timeout, retries, ...)` |
| Many **optional parameters** | A config object where most fields have defaults |
| You need **immutable objects** with many fields | Value objects, DTOs, configuration |
| Object construction has **validation rules** | "If field A is set, field B is required" |
| You're building something **step by step** | SQL query builders, HTML builders, email composers |
| Multiple constructor overloads are getting out of hand | 5+ constructors with different parameter combinations |

---

## 4. When NOT to Use It

| Situation | Why it's harmful |
|-----------|-----------------|
| Object has **1-3 fields** | A simple constructor is perfectly readable |
| Object is **mutable** and has setters | Just use setters — a builder adds nothing |
| No optional fields | If all fields are required and few, a constructor works fine |
| You're using a language with **named parameters** (Kotlin, Python) | Named params solve the readability problem natively |
| The object is **trivially simple** | `new Point(x, y)` doesn't need a builder |

> **Rule of thumb:** If your constructor reads clearly with positional arguments, you don't need a builder.

---

## 5. Structure

### Classes/Interfaces Involved

| Component | Role |
|-----------|------|
| `Product` | The complex object being constructed (often immutable) |
| `Builder` | A mutable companion class that accumulates configuration and calls `build()` to produce the Product |
| `Client` | Uses the Builder to configure and create the Product |

### Who Calls Whom

```
Client  →  Builder.setA(a).setB(b).setC(c)  →  Builder.build()  →  returns Product
```

The Product's constructor is typically **private** — only the Builder can call it.

### Class Diagram

```mermaid
classDiagram
    class HttpRequest {
        -method: String
        -url: String
        -body: String
        -headers: Map
        -timeout: int
        -retries: int
        +getMethod() String
        +getUrl() String
        +getBody() String
        +getHeaders() Map
        +getTimeout() int
        +getRetries() int
    }

    class HttpRequestBuilder {
        -method: String
        -url: String
        -body: String
        -headers: Map
        -timeout: int
        -retries: int
        +method(method: String) HttpRequestBuilder
        +url(url: String) HttpRequestBuilder
        +body(body: String) HttpRequestBuilder
        +header(key: String, value: String) HttpRequestBuilder
        +timeout(ms: int) HttpRequestBuilder
        +retries(count: int) HttpRequestBuilder
        +build() HttpRequest
    }

    class Client {
        +main() void
    }

    HttpRequestBuilder ..> HttpRequest : builds
    HttpRequest +-- HttpRequestBuilder : inner class
    Client --> HttpRequestBuilder : uses
```

---

## 6. How to Code (Step-by-Step Recipe)

### Step 1: Design the Product Class (1 class)

Create the target class with:
- **All fields `private final`** (for immutability)
- **A `private` constructor** that takes the Builder as its sole argument
- **Only getters**, no setters

```java
public class Product {
    private final String fieldA;
    private final int fieldB;
    private final boolean fieldC;

    private Product(Builder builder) {   // PRIVATE — only Builder can call this
        this.fieldA = builder.fieldA;
        this.fieldB = builder.fieldB;
        this.fieldC = builder.fieldC;
    }
    // ... getters only
}
```

### Step 2: Create the Builder as a Static Inner Class (1 inner class)

The builder lives **inside** the product class. It has:
- The **same fields** as the Product (but mutable)
- **Fluent setter methods** that return `this` for chaining
- Required fields in the **Builder's constructor**
- Optional fields via **setter methods with defaults**
- A **`build()` method** that validates and creates the Product

```java
public static class Builder {
    // Required fields — set in constructor
    private final String fieldA;

    // Optional fields — have defaults
    private int fieldB = 0;
    private boolean fieldC = false;

    public Builder(String fieldA) {      // Required params in constructor
        this.fieldA = fieldA;
    }

    public Builder fieldB(int val) {     // Fluent setter
        this.fieldB = val;
        return this;                     // ← return this for chaining!
    }

    public Builder fieldC(boolean val) {
        this.fieldC = val;
        return this;
    }

    public Product build() {
        // Validate here if needed
        return new Product(this);
    }
}
```

### Step 3: Client Uses Fluent API

```java
Product p = new Product.Builder("required-value")  // required in constructor
    .fieldB(42)                                      // optional, fluent
    .fieldC(true)                                    // optional, fluent
    .build();                                        // construct!
```

### Summary Checklist

| What to create | Count | Purpose |
|---------------|-------|---------|
| Product class | 1 | The complex object (immutable, private constructor) |
| Builder (static inner class) | 1 | Mutable accumulator with fluent setters |
| Getters on Product | N (one per field) | Read-only access to the built object |
| `build()` method | 1 | Validates and constructs the Product |

### Key Design Decisions

| Decision | Guideline |
|----------|-----------|
| Required vs optional fields | Required → Builder constructor. Optional → fluent setters with defaults |
| Where to put validation | Inside `build()` — fail fast before creating an invalid object |
| Inner class vs separate file | **Inner class is idiomatic in Java** (e.g., `Product.Builder`) |
| Return type of setters | Always `return this` for fluent chaining |

---

## 7. Code Implementation

### Example: HTTP Request Builder

```java
import java.util.*;

// ─────────────────────────────────────────────
// STEP 1: Product — immutable with private constructor
// ─────────────────────────────────────────────
public class HttpRequest {
    private final String method;
    private final String url;
    private final String body;
    private final Map<String, String> headers;
    private final int timeoutMs;
    private final int retries;
    private final boolean followRedirects;

    // PRIVATE constructor — only Builder can create this
    private HttpRequest(Builder builder) {
        this.method = builder.method;
        this.url = builder.url;
        this.body = builder.body;
        this.headers = Collections.unmodifiableMap(new HashMap<>(builder.headers));
        this.timeoutMs = builder.timeoutMs;
        this.retries = builder.retries;
        this.followRedirects = builder.followRedirects;
    }

    // Only getters — no setters. Object is immutable.
    public String getMethod()           { return method; }
    public String getUrl()              { return url; }
    public String getBody()             { return body; }
    public Map<String, String> getHeaders() { return headers; }
    public int getTimeoutMs()           { return timeoutMs; }
    public int getRetries()             { return retries; }
    public boolean isFollowRedirects()  { return followRedirects; }

    @Override
    public String toString() {
        return String.format(
            "HttpRequest{method='%s', url='%s', body='%s', headers=%s, " +
            "timeout=%dms, retries=%d, followRedirects=%b}",
            method, url, body, headers, timeoutMs, retries, followRedirects
        );
    }

    // ─────────────────────────────────────────────
    // STEP 2: Builder — static inner class
    // ─────────────────────────────────────────────
    public static class Builder {
        // Required fields
        private final String method;
        private final String url;

        // Optional fields with sensible defaults
        private String body = null;
        private Map<String, String> headers = new HashMap<>();
        private int timeoutMs = 30_000;      // default: 30 seconds
        private int retries = 0;              // default: no retries
        private boolean followRedirects = true; // default: follow

        // Required parameters go in the Builder constructor
        public Builder(String method, String url) {
            this.method = method;
            this.url = url;
        }

        // Fluent setters — each returns `this` for chaining
        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder header(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        public Builder timeoutMs(int timeoutMs) {
            this.timeoutMs = timeoutMs;
            return this;
        }

        public Builder retries(int retries) {
            this.retries = retries;
            return this;
        }

        public Builder followRedirects(boolean followRedirects) {
            this.followRedirects = followRedirects;
            return this;
        }

        // Validation + construction
        public HttpRequest build() {
            // Validate constraints
            if (method == null || method.isEmpty()) {
                throw new IllegalStateException("HTTP method is required");
            }
            if (url == null || url.isEmpty()) {
                throw new IllegalStateException("URL is required");
            }
            if (("GET".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method))
                    && body != null) {
                throw new IllegalStateException(method + " requests should not have a body");
            }
            if (timeoutMs < 0) {
                throw new IllegalStateException("Timeout must be non-negative");
            }
            if (retries < 0) {
                throw new IllegalStateException("Retries must be non-negative");
            }

            return new HttpRequest(this);
        }
    }
}

// ─────────────────────────────────────────────
// STEP 3: Client Code
// ─────────────────────────────────────────────
public class Main {
    public static void main(String[] args) {

        // Simple GET request — only required fields
        HttpRequest getRequest = new HttpRequest.Builder("GET", "https://api.example.com/users")
            .header("Accept", "application/json")
            .header("Authorization", "Bearer token123")
            .timeoutMs(5000)
            .build();

        System.out.println(getRequest);

        // Complex POST request — many optional fields
        HttpRequest postRequest = new HttpRequest.Builder("POST", "https://api.example.com/users")
            .body("{\"name\": \"Alice\", \"email\": \"alice@example.com\"}")
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer token123")
            .timeoutMs(10000)
            .retries(3)
            .followRedirects(false)
            .build();

        System.out.println(postRequest);

        // Minimal request — all defaults
        HttpRequest minimal = new HttpRequest.Builder("GET", "https://example.com")
            .build();

        System.out.println(minimal);

        // This would throw an exception:
        // HttpRequest invalid = new HttpRequest.Builder("GET", "https://example.com")
        //     .body("oops")  // GET with body = invalid
        //     .build();
    }
}
```

**Output:**
```
HttpRequest{method='GET', url='https://api.example.com/users', body='null', headers={Accept=application/json, Authorization=Bearer token123}, timeout=5000ms, retries=0, followRedirects=true}
HttpRequest{method='POST', url='https://api.example.com/users', body='{"name": "Alice", "email": "alice@example.com"}', headers={Content-Type=application/json, Authorization=Bearer token123}, timeout=10000ms, retries=3, followRedirects=false}
HttpRequest{method='GET', url='https://example.com', body='null', headers={}, timeout=30000ms, retries=0, followRedirects=true}
```

---

## 8. Real-World Examples

| Where | How Builder is used |
|-------|-------------------|
| **`StringBuilder` in Java** | Classic builder — `.append()` fluently, then `.toString()` to build the final `String` |
| **`OkHttp Request.Builder`** | `new Request.Builder().url(...).header(...).post(body).build()` |
| **`Lombok @Builder`** | Annotation that auto-generates the builder pattern — you write the class, Lombok writes the builder |
| **`Stream.Builder`** in Java 8+ | `Stream.builder().add(1).add(2).add(3).build()` |
| **Protobuf message builders** | `Person.newBuilder().setName("Alice").setId(123).build()` |
| **JPA `CriteriaBuilder`** | Building type-safe SQL queries step by step |
| **Spring `WebClient`** | `WebClient.builder().baseUrl(...).defaultHeader(...).build()` |
| **`AlertDialog.Builder` (Android)** | `.setTitle(...).setMessage(...).setPositiveButton(...).create()` |

---

## 9. Trade-offs

### ✅ Advantages

- **Readability** — Named methods are infinitely clearer than positional constructor args
- **Immutability** — Easily create immutable objects with many fields
- **Validation** — Centralized in `build()`, runs before the object exists
- **Flexible construction** — Set only what you need; everything else uses defaults
- **Self-documenting** — `.timeout(5000)` is clearer than a bare `5000` in a constructor
- **Step-by-step assembly** — Build complex objects incrementally

### ❌ Disadvantages

- **Code duplication** — Fields are declared in both the Product and the Builder
- **Verbosity** — A lot of boilerplate (mitigated by Lombok `@Builder` in practice)
- **Overkill for simple objects** — `new Point(x, y)` is perfectly readable without a builder
- **Mutable builder** — The builder itself is mutable, even if the product is immutable. Don't share builders across threads without care.

### Complexity Introduced

| Aspect | Impact |
|--------|--------|
| Number of new classes | 1 Product + 1 Builder (usually inner class) = effectively 1 file |
| Extra code | ~2x the field declarations (Product + Builder), plus fluent setters |
| Learning curve | Low — very intuitive API for users |
| Maintenance burden | Medium — adding a field means updating both Product and Builder |
| Mitigation | Use **Lombok `@Builder`** to auto-generate all the boilerplate |
