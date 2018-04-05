package app;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.DBWorker;

public class Person {

    // Данные записи о человеке.
    private String id = "";
    private String name = "";
    private String surname = "";
    private String middlename = "";
    private HashMap<String, Phone> phones = new HashMap<String, Phone>();
    private DBWorker db = DBWorker.getInstance();

    // Конструктор для создания записи о человеке на основе данных из БД.
    public Person(String id, String name, String surname, String middlename) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.middlename = middlename;
        // Извлечение телефонов человека из БД.
        ResultSet db_data = DBWorker.getInstance().getDBData("SELECT * FROM `phone` WHERE `owner`=" + id);
        Statement statement = null;
        Connection connection = null;
        try {
            statement = db_data.getStatement();
            connection = statement.getConnection();
            // Если у человека нет телефонов, ResultSet будет == null.
            if (db_data != null) {
                while (db_data.next()) {
                    this.phones.put(db_data.getString("id"), new Phone(db_data.getString("id"), db_data.getString("number")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (db_data != null) {
                    db_data.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Конструктор для создания пустой записи о человеке.
    public Person() {
        this.id = "0";
        this.name = "";
        this.surname = "";
        this.middlename = "";
    }

    // Конструктор для создания записи, предназначенной для добавления в БД.
    public Person(String name, String surname, String middlename) {
        this.id = "0";
        this.name = name;
        this.surname = surname;
        this.middlename = middlename;
    }

    // Валидация частей ФИО. Для отчества можно передать второй параетр == true,
    // тогда допускается пустое значение.
    public boolean validateFMLNamePart(String fml_name_part, boolean empty_allowed) {
        if (empty_allowed) {
            Matcher matcher = Pattern.compile("[\\w-]{0,150}").matcher(fml_name_part);
            return matcher.matches();
        } else {
            Matcher matcher = Pattern.compile("[\\w-]{1,150}").matcher(fml_name_part);
            return matcher.matches();
        }
    }

    // ++++++++++++++++++++++++++++++++++++++
    // Геттеры и сеттеры
    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getSurname() {
        return this.surname;
    }

    public String getMiddlename() {
        if (this.middlename != null) {
            return this.middlename;
        } else {
            return "";
        }
    }

    public boolean addPhone(Phone phone) {
        String query;
        int affectedRow = 0;
        // У человека может не быть отчества.
        if ((phone != null && !phone.getNumber().equals(""))) {
            query = "INSERT INTO `phone` (`number`, `owner`) VALUES ('" + phone.getNumber() + "', '" + id + "')";
            affectedRow = this.db.changeDBData(query);
        }
        // Если добавление прошло успешно...
        if (affectedRow > 0) {
            phone.setId(this.db.getLastInsertId().toString());
            // Добавляем запись о телефоне в общий список.
            this.phones.put(phone.getId(), phone);
            return true;
        } else {
            return false;
        }
    }

    public boolean updatePhone(Phone phone) {
        String query;
        int affectedRow = 0;
        // У человека может не быть отчества.
        if ((phone != null && !phone.getNumber().equals(""))) {
            query = "UPDATE `phone` SET `number` = '" + phone.getNumber() + "' WHERE `id` = " + phone.getId();
            affectedRow = this.db.changeDBData(query);
        }
        // Если добавление прошло успешно...
        if (affectedRow > 0) {
            // Добавляем запись о телефоне в общий список.
            this.phones.put(String.valueOf(phone.getId()), phone);
            return true;
        } else {
            return false;
        }
    }

    public boolean deletePhone(Phone phone) {
        if ((phone.getId() != null) && (!phone.getId().equals("null"))) {
            Integer affected_rows = this.db.changeDBData("DELETE FROM `phone` WHERE `id`=" + phone.getId());
            // Если удаление прошло успешно...
            if (affected_rows > 0) {
                // Удаляем запись о человеке из общего списка.
                this.phones.remove(phone.getId());
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public HashMap<String, Phone> getPhones() {
        return this.phones;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public void setPhones(HashMap<String, Phone> phones) {
        this.phones = phones;
    }
    // Геттеры и сеттеры
    // --------------------------------------

}
