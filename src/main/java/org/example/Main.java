package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

// Класс User представляет обычного пользователя.
class User {
    private String username; // Имя пользователя
    private String password; // Пароль пользователя
    private boolean isBlocked; // Флаг блокировки пользователя
    private boolean passwordRestrictionsEnabled; // Флаг ограничений на пароль
    private int failedLoginAttempts; // Счетчик неудачных попыток ввода пароля

    // Поля для индивидуальных ограничений
    private int minPasswordLength = 6;  // Минимальная длина пароля
    private int maxPasswordLength = 12; // Максимальная длина пароля
    private int minDigit = 2;   // Минимально кол-во цифр в пароле
    private int minSpecialChars = 2;    // Минимальное кол-во специальных символов в пароле

    // Конструктор для создания объекта пользователя.
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.isBlocked = false; // По умолчанию пользователь не заблокирован
        this.passwordRestrictionsEnabled = true; // Ограничения на пароль включены по умолчанию
        this.failedLoginAttempts = 0; // Инициализация счетчика неудачных попыток
    }

    // Геттеры и сеттеры для ограничений
    public int getMinPasswordLength(){return minPasswordLength;}
    public void setMinPasswordLength(int minPasswordLength){this.minPasswordLength = minPasswordLength;}
    public int getMaxPasswordLength(){return maxPasswordLength;}
    public void setMaxPasswordLength(int maxPasswordLength) {this.maxPasswordLength = maxPasswordLength;}
    public int getMinDigit(){return minDigit;}
    public void setMinDigit(int minDigit) {this.minDigit = minDigit;}
    public int getMinSpecialChars(){return minSpecialChars;}
    public void setMinSpecialChars(int minSpecialChars) {this.minSpecialChars = minSpecialChars;}

    // Геттеры и сеттеры для основных полей
    public String getUsername() {return username;}
    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}
    public boolean isBlocked() {return isBlocked;}
    public void setBlocked(boolean blocked) {isBlocked = blocked;}
    public boolean isPasswordRestrictionsEnabled() {return passwordRestrictionsEnabled;}
    public void setPasswordRestrictionsEnabled(boolean passwordRestrictionsEnabled) {this.passwordRestrictionsEnabled = passwordRestrictionsEnabled;}
    public int getFailedLoginAttempts() {return failedLoginAttempts;}
    public void setFailedLoginAttempts(int failedLoginAttempts) {this.failedLoginAttempts = failedLoginAttempts;}

    // Метод для проверки пароля пользователя.
    public boolean login(String enteredPassword) {
        return this.password.equals(enteredPassword);
    }

    // Метод проверки пароля на соответствие индивидуальным ограничениям
    public boolean isPasswordValid(String password){
        //
        if(password.length() < minPasswordLength){
            return false;
        }
        //
        if(password.length() > maxPasswordLength){
            return false;
        }
        //
        int digitCount = 0;
        for(char c : password.toCharArray()){
            if(Character.isDigit(c)){
                digitCount++;
            }
        }
        if(digitCount < minDigit){
            return false;
        }
        int specialCharCount = 0;
        for(char c : password.toCharArray()){
            if(!Character.isLetterOrDigit(c)){
                specialCharCount++;
            }
        }
        if(specialCharCount < minSpecialChars){
            return false;
        }
        return true;
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
            JOptionPane.showMessageDialog(null, "Пользователь уже существует.");
            return;
        }
        users.add(new User(username, "")); // Добавляем пользователя с пустым паролем
        saveUsers(); // Сохраняем изменения в файл
        JOptionPane.showMessageDialog(null, "Пользователь " + username + " успешно добавлен.");
    }

    // Метод для блокировки пользователя.
    public void blockUser(String username) {
        User user = findUser(username);
        if (user != null) {
            user.setBlocked(true); // Блокируем пользователя
            saveUsers(); // Сохраняем изменения в файл
            JOptionPane.showMessageDialog(null, "Пользователь " + username + " успешно заблокирован.");
        } else {
            JOptionPane.showMessageDialog(null, "Пользователь не найден.");
        }
    }

    // Метод для включения/отключения ограничений на пароли.
    public void togglePasswordRestrictions(boolean enabled) {
        for (User user : users) {
            user.setPasswordRestrictionsEnabled(enabled); // Устанавливаем ограничения для всех пользователей
        }
        saveUsers(); // Сохраняем изменения в файл
        JOptionPane.showMessageDialog(null, "Ограничения на пароли " + (enabled ? "включены" : "отключены") + " для всех пользователей.");
    }

    // Метод для получения списка всех пользователей.
    public List<User> getUsers() {
        return users;
    }
}

// Основной класс программы с графическим интерфейсом.
public class Main extends JFrame {
    private AuthenticationSystem authSystem; // Система аутентификации
    private JTextField usernameField; // Поле для ввода имени пользователя
    private JPasswordField passwordField; // Поле для ввода пароля
    private JButton loginButton; // Кнопка входа
    private JButton registerButton; // Кнопка регистрации
    private JLabel messageLabel; // Метка для вывода сообщений

    // Конструктор для инициализации графического интерфейса
    public Main() {
        authSystem = new AuthenticationSystem("users.txt"); // Инициализация системы аутентификации

        // Настройка окна
        setTitle("Система аутентификации");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Центрирование окна

        // Создание панели для компонентов
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10)); // Сетка 4x2 с отступами

        // Добавление компонентов
        panel.add(new JLabel("Имя пользователя:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Пароль:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        loginButton = new JButton("Войти");
        panel.add(loginButton);

        registerButton = new JButton("Зарегистрироваться");
        panel.add(registerButton);

        messageLabel = new JLabel("");
        panel.add(messageLabel);

        // Добавление панели в окно
        add(panel);

        // Обработчики событий для кнопок
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                User user = authSystem.findUser(username);
                if (user == null) {
                    messageLabel.setText("Пользователь не найден.");
                    return;
                }

                if (user.isBlocked()) {
                    messageLabel.setText("Пользователь заблокирован.");
                    return;
                }

                if (user.login(password)) {
                    messageLabel.setText("Вход выполнен успешно.");
                    if (user instanceof Admin) {
                        openAdminMenu((Admin) user);
                    } else {
                        openUserMenu(user);
                    }
                } else {
                    user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
                    if (user.getFailedLoginAttempts() >= 3) {
                        messageLabel.setText("Превышено количество попыток. Программа завершается.");
                        System.exit(0);
                    }
                    messageLabel.setText("Неверный пароль. Осталось попыток: " + (3 - user.getFailedLoginAttempts()));
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                authSystem.addUser(username);
            }
        });
    }

    // Метод для открытия меню администратора
    private void openAdminMenu(Admin admin) {
        JFrame adminFrame = new JFrame("Меню администратора");
        adminFrame.setSize(500, 400);
        adminFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        adminFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1, 10, 10));

        JButton changePasswordButton = new JButton("Сменить пароль");
        JButton viewUsersButton = new JButton("Просмотреть пользователей");
        JButton addUserButton = new JButton("Добавить пользователя");
        JButton blockUserButton = new JButton("Заблокировать пользователя");
        JButton toggleRestrictionsButton = new JButton("Включить/отключить ограничения на пароли");
        JButton setPasswordRulesButton = new JButton("Настроить ограничения на пароль");

        panel.add(changePasswordButton);
        panel.add(viewUsersButton);
        panel.add(addUserButton);
        panel.add(blockUserButton);
        panel.add(toggleRestrictionsButton);
        panel.add(setPasswordRulesButton);

        adminFrame.add(panel);
        adminFrame.setVisible(true);

        // Обработчики событий для кнопок меню администратора
        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newPassword = JOptionPane.showInputDialog("Введите новый пароль:");
                if (newPassword != null) {
                    if (admin.isPasswordValid(newPassword)) {
                        admin.setPassword(newPassword);
                        authSystem.saveUsers();
                        JOptionPane.showMessageDialog(null, "Пароль успешно изменен.");
                    } else {
                        JOptionPane.showMessageDialog(null, "Пароль не соответствует ограничениям.");
                    }
                }
            }
        });

        viewUsersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StringBuilder usersList = new StringBuilder();
                for (User user : authSystem.getUsers()) {
                    usersList.append(user.toString()).append("\n");
                }
                JOptionPane.showMessageDialog(null, usersList.toString());
            }
        });

        addUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = JOptionPane.showInputDialog("Введите имя пользователя:");
                if (username != null) {
                    authSystem.addUser(username);
                }
            }
        });

        blockUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = JOptionPane.showInputDialog("Введите имя пользователя для блокировки:");
                if (username != null) {
                    authSystem.blockUser(username);
                }
            }
        });

        toggleRestrictionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean enabled = Boolean.parseBoolean(JOptionPane.showInputDialog("Включить ограничения на пароли? (true/false):"));
                authSystem.togglePasswordRestrictions(enabled);
            }
        });

        setPasswordRulesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Создаем список пользователей для выбора
                List<User> users = authSystem.getUsers();
                String[] userNames = new String[users.size()];
                for (int i = 0; i < users.size(); i++) {
                    userNames[i] = users.get(i).getUsername();
                }

                // Диалог для выбора пользователя
                String selectedUser = (String) JOptionPane.showInputDialog(
                        null,
                        "Выберите пользователя для настройки ограничений:",
                        "Настройка ограничений",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        userNames,
                        userNames[0]
                );

                if (selectedUser != null) {
                    User user = authSystem.findUser(selectedUser);
                    if (user != null) {
                        // Диалог для настройки ограничений
                        JPanel rulesPanel = new JPanel(new GridLayout(5, 2, 10, 10));
                        JTextField minLengthField = new JTextField(String.valueOf(user.getMinPasswordLength()));
                        JTextField maxLengthField = new JTextField(String.valueOf(user.getMaxPasswordLength()));
                        JTextField minDigitsField = new JTextField(String.valueOf(user.getMinDigit()));
                        JTextField minSpecialCharsField = new JTextField(String.valueOf(user.getMinSpecialChars()));

                        rulesPanel.add(new JLabel("Минимальная длина пароля:"));
                        rulesPanel.add(minLengthField);
                        rulesPanel.add(new JLabel("Максимальная длина пароля:"));
                        rulesPanel.add(maxLengthField);
                        rulesPanel.add(new JLabel("Минимальное количество цифр:"));
                        rulesPanel.add(minDigitsField);
                        rulesPanel.add(new JLabel("Минимальное количество спецсимволов:"));
                        rulesPanel.add(minSpecialCharsField);

                        int result = JOptionPane.showConfirmDialog(null, rulesPanel, "Настройка ограничений", JOptionPane.OK_CANCEL_OPTION);
                        if (result == JOptionPane.OK_OPTION) {
                            try {
                                int minLength = Integer.parseInt(minLengthField.getText());
                                int maxLength = Integer.parseInt(maxLengthField.getText());
                                int minDigits = Integer.parseInt(minDigitsField.getText());
                                int minSpecialChars = Integer.parseInt(minSpecialCharsField.getText());

                                // Установка новых ограничений
                                user.setMinPasswordLength(minLength);
                                user.setMaxPasswordLength(maxLength);
                                user.setMinDigit(minDigits);
                                user.setMinSpecialChars(minSpecialChars);

                                authSystem.saveUsers();
                                JOptionPane.showMessageDialog(null, "Ограничения успешно обновлены для пользователя " + user.getUsername() + ".");
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(null, "Некорректный ввод. Пожалуйста, введите числа.");
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Пользователь не найден.");
                    }
                }
            }
        });
    }

    // Метод для открытия меню пользователя
    private void openUserMenu(User user) {
        JFrame userFrame = new JFrame("Меню пользователя");
        userFrame.setSize(300, 200);
        userFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        userFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1, 10, 10));

        JButton changePasswordButton = new JButton("Сменить пароль");
        JButton logoutButton = new JButton("Выйти");

        panel.add(changePasswordButton);
        panel.add(logoutButton);

        userFrame.add(panel);
        userFrame.setVisible(true);

        // Обработчики событий для кнопок меню пользователя
        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changePassword(user);
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userFrame.dispose();
            }
        });
    }

    // Метод для смены пароля
    private void changePassword(User user) {
        String oldPassword = JOptionPane.showInputDialog("Введите старый пароль:");
        if (oldPassword != null && user.login(oldPassword)) {
            String newPassword = JOptionPane.showInputDialog("Введите новый пароль:");
            if (newPassword != null) {
                if (!user.isPasswordRestrictionsEnabled() || user.isPasswordValid(newPassword)) {
                    user.setPassword(newPassword);
                    authSystem.saveUsers();
                    JOptionPane.showMessageDialog(null, "Пароль успешно изменен.");
                } else {
                    JOptionPane.showMessageDialog(null, "Пароль не соответствует ограничениям.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Неверный старый пароль.");
        }
    }

    // Точка входа в программу
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main().setVisible(true); // Запуск графического интерфейса
            }
        });
    }
}
