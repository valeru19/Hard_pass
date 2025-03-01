package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Класс User представляет обычного пользователя.
class User {
    private String username; // Имя пользователя
    private String password; // Пароль пользователя
    private boolean isBlocked; // Флаг блокировки пользователя
    private boolean passwordRestrictionsEnabled; // Флаг ограничений на пароль

    // Конструктор для создания объекта пользователя.
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.isBlocked = false; // По умолчанию пользователь не заблокирован
        this.passwordRestrictionsEnabled = true; // Ограничения на пароль включены по умолчанию
    }

    // Геттер для получения имени пользователя.
    public String getUsername() {
        return username;
    }

    // Геттер для получения пароля пользователя.
    public String getPassword() {
        return password;
    }

    // Сеттер для установки пароля пользователя.
    public void setPassword(String password) {
        this.password = password;
    }

    // Геттер для проверки, заблокирован ли пользователь.
    public boolean isBlocked() {
        return isBlocked;
    }

    // Сеттер для блокировки/разблокировки пользователя.
    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    // Геттер для проверки, включены ли ограничения на пароль.
    public boolean isPasswordRestrictionsEnabled() {
        return passwordRestrictionsEnabled;
    }

    // Сеттер для включения/отключения ограничений на пароль.
    public void setPasswordRestrictionsEnabled(boolean passwordRestrictionsEnabled) {
        this.passwordRestrictionsEnabled = passwordRestrictionsEnabled;
    }

    // Метод для проверки пароля пользователя.
    public boolean login(String enteredPassword) {
        return this.password.equals(enteredPassword);
    }

    // Переопределенный метод toString для строкового представления пользователя.
    @Override
    public String toString() {
        return "Пользователь: " + username + " | Заблокирован: " + isBlocked + " | Ограничения на пароль: " + passwordRestrictionsEnabled;
    }
}

// Класс Admin представляет администратора.
class Admin extends User {
    // Конструктор для создания объекта администратора.
    public Admin(String username, String password) {
        super(username, password); // Вызов конструктора родительского класса
    }

    // Переопределенный метод toString для строкового представления администратора.
    @Override
    public String toString() {
        return "Администратор: " + getUsername();
    }
}

// Класс AuthenticationSystem управляет аутентификацией и хранением данных.
class AuthenticationSystem {
    private List<User> users; // Список пользователей
    private String dataFile;   // Имя файла для хранения данных

    // Конструктор для инициализации системы аутентификации.
    public AuthenticationSystem(String dataFile) {
        this.dataFile = dataFile;
        this.users = loadUsers(); // Загрузка пользователей из файла
        if (users.isEmpty()) {
            // При первом запуске создаем администратора с пустым паролем
            users.add(new Admin("admin", ""));
            saveUsers();
        }
    }

    // Метод для загрузки пользователей из файла.
    private List<User> loadUsers() {
        List<User> userList = new ArrayList<>(); // Создаем пустой список пользователей
        try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
            String line;
            // Читаем файл построчно
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(","); // Разделяем строку по запятой
                String username = parts[0];      // Имя пользователя
                String password = parts[1];      // Пароль
                boolean isBlocked = Boolean.parseBoolean(parts[2]); // Флаг блокировки
                boolean passwordRestrictionsEnabled = Boolean.parseBoolean(parts[3]); // Флаг ограничений на пароль
                // Создаем объект User или Admin в зависимости от роли
                User user = (username.equals("admin")) ? new Admin(username, password) : new User(username, password);
                user.setBlocked(isBlocked);
                user.setPasswordRestrictionsEnabled(passwordRestrictionsEnabled);
                userList.add(user);
            }
        } catch (IOException e) {
            System.out.println("Данные о пользователях не найдены. Начинаем с пустого списка.");
        }
        return userList;
    }

    // Метод для сохранения пользователей в файл.
    public void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile))) {
            for (User user : users) {
                // Записываем данные в файл в формате: username,password,isBlocked,passwordRestrictionsEnabled
                writer.write(user.getUsername() + "," + user.getPassword() + "," + user.isBlocked() + "," + user.isPasswordRestrictionsEnabled());
                writer.newLine(); // Переход на новую строку
            }
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении данных о пользователях.");
        }
    }

    // Метод для аутентификации пользователя.
    public User authenticateUser(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.login(password)) {
                return user; // Возвращаем пользователя, если аутентификация успешна
            }
        }
        return null; // Возвращаем null, если пользователь не найден
    }

    // Метод для поиска пользователя по имени.
    public User findUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    // Метод для добавления нового пользователя.
    public void addUser(String username) {
        if (findUser(username) != null) {
            System.out.println("Пользователь уже существует.");
            return;
        }
        users.add(new User(username, "")); // Добавляем пользователя с пустым паролем
        saveUsers(); // Сохраняем изменения в файл
        System.out.println("Пользователь " + username + " успешно добавлен.");
    }

    // Метод для блокировки пользователя.
    public void blockUser(String username) {
        User user = findUser(username);
        if (user != null) {
            user.setBlocked(true); // Блокируем пользователя
            saveUsers(); // Сохраняем изменения в файл
            System.out.println("Пользователь " + username + " успешно заблокирован.");
        } else {
            System.out.println("Пользователь не найден.");
        }
    }

    // Метод для включения/отключения ограничений на пароли.
    public void togglePasswordRestrictions(boolean enabled) {
        for (User user : users) {
            user.setPasswordRestrictionsEnabled(enabled); // Устанавливаем ограничения для всех пользователей
        }
        saveUsers(); // Сохраняем изменения в файл
        System.out.println("Ограничения на пароли " + (enabled ? "включены" : "отключены") + " для всех пользователей.");
    }

    // Метод для получения списка всех пользователей.
    public List<User> getUsers() {
        return users;
    }
}

// Основной класс программы.
public class Main {
    private static AuthenticationSystem authSystem; // Система аутентификации
    private static Scanner scanner; // Сканер для ввода данных

    public static void main(String[] args) {
        authSystem = new AuthenticationSystem("users.txt"); // Инициализация системы аутентификации
        scanner = new Scanner(System.in); // Инициализация сканера

        while (true) {
            System.out.print("Введите имя пользователя: ");
            String username = scanner.nextLine();
            System.out.print("Введите пароль: ");
            String password = maskPassword(); // Маскировка ввода пароля

            User user = authSystem.authenticateUser(username, password); // Аутентификация пользователя
            if (user != null) {
                if (user.isBlocked()) {
                    System.out.println("Пользователь заблокирован. Обратитесь к администратору.");
                    continue;
                }
                if (user instanceof Admin) {
                    adminMenu((Admin) user); // Меню администратора
                } else {
                    userMenu(user); // Меню обычного пользователя
                }
            } else {
                System.out.println("Неверное имя пользователя или пароль.");
            }
        }
    }

    // Метод для маскировки ввода пароля.
    private static String maskPassword() {
        Console console = System.console();
        if (console != null) {
            return new String(console.readPassword()); // Используем Console для маскировки
        } else {
            return scanner.nextLine(); // Если Console недоступен, используем обычный ввод
        }
    }

    // Меню администратора.
    private static void adminMenu(Admin admin) {
        while (true) {
            System.out.println("\nМеню администратора:");
            System.out.println("1. Сменить пароль администратора");
            System.out.println("2. Просмотреть список пользователей");
            System.out.println("3. Добавить нового пользователя");
            System.out.println("4. Заблокировать пользователя");
            System.out.println("5. Включить/отключить ограничения на пароли");
            System.out.println("6. Выйти");
            System.out.print("Выберите опцию: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    changePassword(admin); // Смена пароля администратора
                    break;
                case "2":
                    viewUsers(); // Просмотр списка пользователей
                    break;
                case "3":
                    System.out.print("Введите имя пользователя: ");
                    String username = scanner.nextLine();
                    authSystem.addUser(username); // Добавление нового пользователя
                    break;
                case "4":
                    System.out.print("Введите имя пользователя для блокировки: ");
                    String blockUsername = scanner.nextLine();
                    authSystem.blockUser(blockUsername); // Блокировка пользователя
                    break;
                case "5":
                    System.out.print("Включить ограничения на пароли? (true/false): ");
                    boolean enabled = Boolean.parseBoolean(scanner.nextLine());
                    authSystem.togglePasswordRestrictions(enabled); // Включение/отключение ограничений на пароли
                    break;
                case "6":
                    System.out.println("Выход...");
                    System.exit(0); // Завершение работы программы
                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    // Меню обычного пользователя.
    private static void userMenu(User user) {
        while (true) {
            System.out.println("\nМеню пользователя:");
            System.out.println("1. Сменить пароль");
            System.out.println("2. Выйти");
            System.out.print("Выберите опцию: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    changePassword(user); // Смена пароля пользователя
                    break;
                case "2":
                    System.out.println("Выход...");
                    System.exit(0); // Завершение работы программы
                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    // Метод для смены пароля.
    private static void changePassword(User user) {
        System.out.print("Введите старый пароль: ");
        String oldPassword = maskPassword();
        if (!user.login(oldPassword)) {
            System.out.println("Неверный старый пароль.");
            return;
        }

        System.out.print("Введите новый пароль: ");
        String newPassword = maskPassword();
        if (user.isPasswordRestrictionsEnabled() && !isPasswordValid(newPassword)) {
            System.out.println("Пароль не соответствует ограничениям.");
            return;
        }

        System.out.print("Подтвердите новый пароль: ");
        String confirmPassword = maskPassword();
        if (!newPassword.equals(confirmPassword)) {
            System.out.println("Пароли не совпадают.");
            return;
        }

        user.setPassword(newPassword); // Устанавливаем новый пароль
        authSystem.saveUsers(); // Сохраняем изменения в файл
        System.out.println("Пароль успешно изменен.");
    }

    // Метод для проверки пароля на соответствие ограничениям.
    private static boolean isPasswordValid(String password) {
        if (password.length() < 8) {
            return false; // Пароль должен быть не менее 8 символов
        }
        if (!password.matches(".*\\d.*")) {
            return false; // Пароль должен содержать хотя бы одну цифру
        }
        if (!password.matches(".*[!@#$%^&*()].*")) {
            return false; // Пароль должен содержать хотя бы один специальный символ
        }
        return true;
    }

    // Метод для просмотра списка пользователей.
    private static void viewUsers() {
        List<User> users = authSystem.getUsers();
        for (User user : users) {
            System.out.println(user); // Вывод информации о каждом пользователе
        }
    }
}