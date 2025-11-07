# Projekty szkoleniowe Spring – Zarządzanie zadaniami i wydatkami

## Projekt 1: Aplikacja do zarządzania zadaniami (Task Manager)

### Cel

Stworzenie prostej aplikacji REST API do zarządzania listą zadań (dodawanie, aktualizacja, usuwanie, oznaczanie jako wykonane). Projekt ma pokazać praktyczne wykorzystanie Spring Boot, Dependency Injection (DI), AOP i obsługi wyjątków.

---

### 1. Konfiguracja projektu

1. Utwórz nowy projekt Spring Boot (np. `task-manager`) w Spring Initializr lub IDE.
2. Wybierz zależności:

    * Spring Web
    * Spring Data JPA
    * H2 Database (lub PostgreSQL, jeśli ćwiczenie ma obejmować konfigurację zewnętrznej bazy)
    * Lombok
    * Spring AOP
3. Skonfiguruj plik `application.yml` lub `application.properties`:

    * Ustaw połączenie z bazą danych.
    * Ustal port aplikacji i nazwę kontekstu.
4. Zweryfikuj uruchomienie aplikacji.

---

### 2. Model danych

1. Utwórz encję `Task` z polami:

    * `id` (Long, @Id, @GeneratedValue)
    * `title` (String)
    * `description` (String)
    * `deadline` (LocalDate)
    * `done` (boolean)
2. Zastosuj adnotacje JPA (`@Entity`, `@Table`).

---

### 3. Warstwa repozytorium

1. Utwórz interfejs `TaskRepository` rozszerzający `JpaRepository<Task, Long>`.
2. Dodaj metody niestandardowe, np. `List<Task> findByDone(boolean done)`.

---

### 4. Warstwa serwisowa (DI)

1. Utwórz serwis `TaskService`.
2. Zastosuj wstrzykiwanie zależności (`@Service`, `@Autowired` lub konstruktor DI).
3. Zaimplementuj metody:

    * `List<Task> getAllTasks()`
    * `Task getTask(Long id)`
    * `Task addTask(Task task)`
    * `Task updateTask(Long id, Task task)`
    * `void deleteTask(Long id)`
4. Dodaj prostą walidację danych i rzucanie wyjątków, np. `TaskNotFoundException`.

---

### 5. Warstwa kontrolera REST

1. Utwórz kontroler `TaskController`.
2. Endpointy:

    * `GET /tasks`
    * `GET /tasks/{id}`
    * `POST /tasks`
    * `PUT /tasks/{id}`
    * `DELETE /tasks/{id}`
3. Zwracaj `ResponseEntity` z odpowiednimi kodami statusów.
4. Dodaj `@RestController` i `@RequestMapping("/tasks")`.

---

### 6. Obsługa wyjątków

1. Utwórz klasę `GlobalExceptionHandler` oznaczoną `@ControllerAdvice`.
2. Zaimplementuj obsługę np. `TaskNotFoundException` → `HttpStatus.NOT_FOUND`.

---

### 7. Aspekt – Retry (AOP)

1. Utwórz aspekt `RetryAspect`.
2. Zastosuj `@Aspect` i `@Component`.
3. Utwórz poradę `@Around("@annotation(Retryable)")`.
4. Implementacja:

    * Przechwyć wywołanie metody.
    * Spróbuj wykonać ją kilka razy (np. 3) przy wystąpieniu określonego wyjątku (np. `DataAccessException`).
    * Po niepowodzeniu zaloguj błąd.
5. Utwórz własną adnotację `@Retryable(retries = 3)`.
6. Oznacz wybrane metody serwisowe, np. `addTask`.

---

### 8. Testy

1. Dodaj testy jednostkowe (JUnit + Mockito):

    * Dla `TaskService` (mock repozytorium).
    * Dla `TaskController` (mock MVC).

---

### 9. Rozszerzenie (opcjonalne)

1. Dodaj filtrowanie zadań po dacie lub statusie.
2. Zaimplementuj paginację (`Pageable`).

---

## Projekt 2: Aplikacja do zarządzania wydatkami (Expense Tracker)

### Cel

Stworzenie aplikacji REST API umożliwiającej zarządzanie wydatkami użytkownika – rejestrowanie transakcji, kategoryzację i sumowanie kosztów.

---

### 1. Konfiguracja projektu

1. Utwórz nowy projekt Spring Boot (`expense-tracker`).
2. Wybierz zależności:

    * Spring Web
    * Spring Data JPA
    * H2 lub MySQL
    * Lombok
    * Spring AOP
3. Skonfiguruj `application.yml` (nazwa bazy, port, logowanie SQL).

---

### 2. Model danych

1. Klasa `Expense`:

    * `id` (Long)
    * `title` (String)
    * `category` (enum lub String)
    * `amount` (BigDecimal)
    * `date` (LocalDate)
2. Klasa `Category` (jeśli osobna encja).
3. Dodaj adnotacje JPA i Lombok.

---

### 3. Repozytoria

1. `ExpenseRepository extends JpaRepository<Expense, Long>`.
2. Dodaj metody:

    * `List<Expense> findByCategory(String category)`
    * `List<Expense> findByDateBetween(LocalDate start, LocalDate end)`.

---

### 4. Warstwa serwisowa

1. Utwórz serwis `ExpenseService`.
2. Wstrzykuj repozytorium przez konstruktor.
3. Zaimplementuj metody:

    * `addExpense(Expense expense)`
    * `getAllExpenses()`
    * `getExpenseById(Long id)`
    * `getExpensesByCategory(String category)`
    * `getExpensesBetweenDates(LocalDate start, LocalDate end)`
    * `deleteExpense(Long id)`
4. Dodaj prostą walidację – np. nie można dodać wydatku z ujemną kwotą.

---

### 5. Kontroler REST

1. Utwórz `ExpenseController`.
2. Adnotacje: `@RestController`, `@RequestMapping("/expenses")`.
3. Endpointy:

    * `GET /expenses`
    * `GET /expenses/{id}`
    * `POST /expenses`
    * `GET /expenses/category/{category}`
    * `GET /expenses/range?start=...&end=...`
    * `DELETE /expenses/{id}`.

---

### 6. AOP – Aspekt logowania

1. Utwórz `LoggingAspect`.
2. Zastosuj `@Before` i `@AfterReturning` do logowania wywołań metod serwisowych.
3. Loguj nazwę metody, argumenty i czas wykonania.

---

### 7. Aspekt Retry

1. Ponownie użyj lub utwórz `@Retryable` i `RetryAspect` z poprzedniego projektu.
2. Oznacz metody serwisowe, które mogą mieć problemy z bazą (np. `addExpense`).

---

### 8. Testy

1. Testy jednostkowe dla `ExpenseService` i `ExpenseController`.
2. Testy integracyjne z bazą H2.

---

### 9. Rozszerzenia (opcjonalne)

1. Dodaj sumowanie wydatków po kategorii (np. `GET /expenses/summary`).
2. Zastosuj DTO i mapowanie (np. z użyciem MapStruct).
3. Dodaj walidację danych wejściowych przy pomocy `@Valid`.

