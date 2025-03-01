# Система разграничения доступа на основе парольной аутентификации

![Java](https://img.shields.io/badge/Java-17%2B-blue)
![License](https://img.shields.io/badge/License-MIT-green)

Проект представляет собой консольное приложение на Java для управления доступом пользователей на основе парольной аутентификации. Программа поддерживает два типа учетных записей: **администратор** и **обычный пользователь**. Все данные о пользователях хранятся в файле `users.txt`, что обеспечивает сохранение информации между запусками.

---

## 📋 Оглавление
- [Основные функции](#-основные-функции)
- [Требования](#-требования)
- [Установка и запуск](#-установка-и-запуск)
- [Использование](#-использование)
- [Структура проекта](#-структура-проекта)
- [Формат хранения данных](#-формат-хранения-данных)
- [Лицензия](#-лицензия)
- [Автор](#-автор)

---

## 🚀 Основные функции

### 🔹 Режим администратора:
- **Смена пароля администратора**.
- **Просмотр списка пользователей** с указанием имени, статуса блокировки и ограничений на пароль.
- **Добавление нового пользователя** (создается с пустым паролем, пароль устанавливается при первом входе).
- **Блокировка пользователя** (после блокировки пользователь не может войти в систему).
- **Включение/отключение ограничений на пароли** (ограничения включают минимум 8 символов, наличие цифр и специальных символов).
- **Выход из системы**.

### 🔹 Режим обычного пользователя:
- **Смена пароля пользователя** (пользователь должен ввести текущий пароль перед сменой).
- **Выход из системы**.

### 🔹 Общие функции:
- **Аутентификация пользователей** (ввод имени пользователя и пароля).
- **Сохранение данных** в файл `users.txt` для постоянного хранения информации.
- **Проверка пароля** на соответствие заданным ограничениям.

---

## 📋 Требования

Для корректной работы программы требуется:
- **Java Development Kit (JDK) 17 или выше**.
- **Любая IDE** (рекомендуется IntelliJ IDEA или VS Code) или консольный текстовый редактор.
- **Консоль/терминал** для запуска программы.

---

## ⚙️ Установка и запуск

1. **Скачайте или клонируйте репозиторий**:
   ```sh
   git clone https://github.com/your-repo/access-control-system.git
   cd access-control-system
   ```
2. **Скомпилируйте код**:
   ```sh
   javac -d bin src/org/example/Main.java
   ```
3. **Запустите программу**:
   ```sh
   java -cp bin org.example.Main
   ```

После первого запуска создается файл `users.txt`, в котором будет добавлена учетная запись администратора `admin` с пустым паролем.

---

## 📖 Использование

### 🔹 Вход в систему
При запуске программы запрашивается имя пользователя и пароль:
```sh
Введите имя пользователя: admin
Введите пароль: 
```
Если введен неправильный пароль или пользователь заблокирован, вход будет отклонен.

### 🔹 Доступные команды

#### 🛠 Команды администратора
- **1. Сменить пароль администратора** – позволяет задать новый пароль.
- **2. Просмотреть список пользователей** – выводит всех пользователей с их статусами.
- **3. Добавить нового пользователя** – создает нового пользователя с пустым паролем.
- **4. Заблокировать пользователя** – делает учетную запись недоступной для входа.
- **5. Включить/отключить ограничения на пароли** – управляет требованиями к сложности пароля.
- **6. Выйти** – завершает работу программы.

#### 👤 Команды пользователя
- **1. Сменить пароль** – требует ввода текущего пароля перед сменой.
- **2. Выйти** – завершает программу.

---
## UML-диаграмма

```mermaid
classDiagram
    class User {
        -String username
        -String password
        -boolean isBlocked
        -boolean passwordRestrictionsEnabled
        +User(String username, String password)
        +String getUsername()
        +String getPassword()
        +void setPassword(String password)
        +boolean isBlocked()
        +void setBlocked(boolean blocked)
        +boolean isPasswordRestrictionsEnabled()
        +void setPasswordRestrictionsEnabled(boolean enabled)
        +boolean login(String enteredPassword)
        +String toString()
    }

    class Admin {
        +Admin(String username, String password)
        +String toString()
    }

    class AuthenticationSystem {
        -List~User~ users
        -String dataFile
        +AuthenticationSystem(String dataFile)
        +List~User~ loadUsers()
        +void saveUsers()
        +User authenticateUser(String username, String password)
        +User findUser(String username)
        +void addUser(String username)
        +void blockUser(String username)
        +void togglePasswordRestrictions(boolean enabled)
        +List~User~ getUsers()
    }

    class Main {
        -AuthenticationSystem authSystem
        -Scanner scanner
        +void main(String[] args)
        +String maskPassword()
        +void adminMenu(Admin admin)
        +void userMenu(User user)
        +void changePassword(User user)
        +boolean isPasswordValid(String password)
        +void viewUsers()
    }

    User <|-- Admin
    Main --> AuthenticationSystem
    AuthenticationSystem --> User
---
## 📂 Структура проекта
```
access-control-system/
├── src/
│   ├── org/example/
│   │   ├── User.java       # Класс пользователя
│   │   ├── Admin.java      # Класс администратора
│   │   ├── AuthenticationSystem.java  # Основная логика аутентификации
│   │   ├── Main.java       # Основной файл программы
├── users.txt              # Файл хранения пользователей
├── README.md              # Документация проекта
```
## UML-диаграмма

### Классы и их атрибуты

#### Класс `User`
- **Атрибуты**:
  - `username: String` — имя пользователя.
  - `password: String` — пароль пользователя.
  - `isBlocked: boolean` — флаг блокировки пользователя.
  - `passwordRestrictionsEnabled: boolean` — флаг ограничений на пароль.
- **Методы**:
  - `User(username: String, password: String)` — конструктор.
  - `getUsername(): String` — возвращает имя пользователя.
  - `getPassword(): String` — возвращает пароль.
  - `setPassword(password: String): void` — устанавливает пароль.
  - `isBlocked(): boolean` — проверяет, заблокирован ли пользователь.
  - `setBlocked(blocked: boolean): void` — блокирует/разблокирует пользователя.
  - `isPasswordRestrictionsEnabled(): boolean` — проверяет, включены ли ограничения на пароль.
  - `setPasswordRestrictionsEnabled(enabled: boolean): void` — включает/отключает ограничения на пароль.
  - `login(enteredPassword: String): boolean` — проверяет пароль.
  - `toString(): String` — возвращает строковое представление пользователя.

#### Класс `Admin` (наследует `User`)
- **Методы**:
  - `Admin(username: String, password: String)` — конструктор.
  - `toString(): String` — возвращает строковое представление администратора.

#### Класс `AuthenticationSystem`
- **Атрибуты**:
  - `users: List<User>` — список пользователей.
  - `dataFile: String` — имя файла для хранения данных.
- **Методы**:
  - `AuthenticationSystem(dataFile: String)` — конструктор.
  - `loadUsers(): List<User>` — загружает пользователей из файла.
  - `saveUsers(): void` — сохраняет пользователей в файл.
  - `authenticateUser(username: String, password: String): User` — аутентифицирует пользователя.
  - `findUser(username: String): User` — ищет пользователя по имени.
  - `addUser(username: String): void` — добавляет нового пользователя.
  - `blockUser(username: String): void` — блокирует пользователя.
  - `togglePasswordRestrictions(enabled: boolean): void` — включает/отключает ограничения на пароли.
  - `getUsers(): List<User>` — возвращает список пользователей.

#### Класс `Main`
- **Атрибуты**:
  - `authSystem: AuthenticationSystem` — система аутентификации.
  - `scanner: Scanner` — объект для ввода данных.
- **Методы**:
  - `main(args: String[]): void` — точка входа в программу.
  - `maskPassword(): String` — маскирует ввод пароля.
  - `adminMenu(admin: Admin): void` — меню администратора.
  - `userMenu(user: User): void` — меню обычного пользователя.
  - `changePassword(user: User): void` — смена пароля.
  - `isPasswordValid(password: String): boolean` — проверка пароля на соответствие ограничениям.
  - `viewUsers(): void` — просмотр списка пользователей.

### Отношения между классами

1. **Наследование**:
   - Класс `Admin` наследует класс `User`.

2. **Ассоциация**:
   - Класс `Main` использует объект класса `AuthenticationSystem`.
   - Класс `AuthenticationSystem` управляет списком объектов класса `User`.

### Схема отношений

```plaintext
+-------------------+        +-------------------+        +-------------------+
|      User         |        |      Admin        |        | AuthenticationSys |
+-------------------+        +-------------------+        +-------------------+
| - username        |        |                   |        | - users           |
| - password        |<|------|                   |        | - dataFile        |
| - isBlocked       |        |                   |        +-------------------+
| - passwordRestrict|        |                   |        | + loadUsers()     |
+-------------------+        +-------------------+        | + saveUsers()     |
| + getUsername()   |        | + toString()      |        | + authenticateUser|
| + getPassword()   |        +-------------------+        | + findUser()      |
| + setPassword()   |                                     | + addUser()       |
| + isBlocked()     |                                     | + blockUser()     |
| + setBlocked()    |                                     | + togglePasswordR |
| + login()         |                                     | + getUsers()      |
| + toString()      |                                     +-------------------+
+-------------------+                                              ^
                                                                   |
                                                                   |
                                                          +-------------------+
                                                          |      Main         |
                                                          +-------------------+
                                                          | - authSystem      |
                                                          | - scanner         |
                                                          +-------------------+
                                                          | + main()          |
                                                          | + maskPassword()  |
                                                          | + adminMenu()     |
                                                          | + userMenu()      |
                                                          | + changePassword()|
                                                          | + isPasswordValid()|
                                                          | + viewUsers()     |
                                                          +-------------------+
---





