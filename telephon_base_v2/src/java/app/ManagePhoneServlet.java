package app;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

public class ManagePhoneServlet extends HttpServlet {
    // Идентификатор для сериализации/десериализации.
    private static final long serialVersionUID = 1L;
    // Основной объект, хранящий данные телефонной книги.
    private Phonebook phonebook;

    public ManagePhoneServlet() {
        // Вызов родительского конструктора.
        super();

        // Создание экземпляра телефонной книги.
        try {
            this.phonebook = Phonebook.getInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // Валидация ФИО и генерация сообщения об ошибке в случае невалидных данных.
    private String validatePhone(Phone phone) {
        String regExp = "^(\\+|\\#)?[0-9]{2,5}(\\-)?[0-6]{0,45}$";
        if (phone.getNumber().matches(regExp)) {
            return "";
        } else {
            return "Номер указан неверно";
        }
    }

    // Реакция на GET-запросы.
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Обязательно ДО обращения к любому параметру нужно переключиться в UTF-8,
        // иначе русский язык при передаче GET/POST-параметрами превращается в "кракозябры".
        request.setCharacterEncoding("UTF-8");
        // В JSP нам понадобится сама телефонная книга. Можно создать её экземпляр там,
        // но с архитектурной точки зрения логичнее создать его в сервлете и передать в JSP.
        //request.setAttribute("phonebook", this.phonebook);
        // Хранилище параметров для передачи в JSP.
        HashMap<String, String> jsp_parameters = new HashMap<String, String>();
        // Диспетчеры для передачи управления на разные JSP (разные представления (view)).
        RequestDispatcher dispatcher_for_manager = request.getRequestDispatcher("/ManagePerson.jsp");
        RequestDispatcher dispatcher_for_phone = request.getRequestDispatcher("/ManagePhone.jsp");
        // Действие (action) и идентификатор записи (id) над которой выполняется это действие.
        String action = request.getParameter("action");
        String personId = request.getParameter("personId");
        String phoneId = request.getParameter("phoneId");
        // Если идентификатор и действие не указаны, мы находимся в состоянии
        // "просто показать список и больше ничего не делать".
        if ((action == null) || (personId == null)) {
            request.setAttribute("jsp_parameters", jsp_parameters);
            dispatcher_for_manager.forward(request, response);
        }
        // Если же действие указано, то...
        else {
            Person person = phonebook.getPerson(personId);
            request.setAttribute("person", person);
            switch (action) {
                // Добавление записи.
                case "add_phone":
                    // Создание новой пустой записи о телефоне.
                    Phone empty_phone = new Phone("", "");
                    // Подготовка параметров для JSP.
                    jsp_parameters.put("current_action", "add_phone");
                    jsp_parameters.put("next_action", "add_phone_go");
                    jsp_parameters.put("next_action_label", "Добавить номер");
                    // Установка параметров JSP.
                    request.setAttribute("phone", empty_phone);
                    request.setAttribute("jsp_parameters", jsp_parameters);
                    // Передача запроса в JSP.
                    dispatcher_for_phone.forward(request, response);
                    break;
                // Редактирование записи.
                case "edit_phone":
                    // Извлечение из пользователя информации о редактируемом телефоне.
                    Phone editable_phone = person.getPhones().get(phoneId);
                    // Подготовка параметров для JSP.
                    jsp_parameters.put("current_action", "edit_phone");
                    jsp_parameters.put("next_action", "edit_phone_go");
                    jsp_parameters.put("next_action_label", "Сохранить номер");
                    // Установка параметров JSP.
                    request.setAttribute("phone", editable_phone);
                    request.setAttribute("jsp_parameters", jsp_parameters);
                    // Передача запроса в JSP.
                    dispatcher_for_phone.forward(request, response);
                    break;
                // Удаление записи.
                case "delete_phone":
                    // Если запись удалось удалить...
                    if (person.deletePhone(new Phone(phoneId))) {
                        jsp_parameters.put("current_action_result", "DELETION_SUCCESS");
                        jsp_parameters.put("current_action_result_label", "Удаление выполнено успешно");
                    }
                    // Если запись не удалось удалить (например, такой записи нет)...
                    else {
                        jsp_parameters.put("current_action_result", "DELETION_FAILURE");
                        jsp_parameters.put("current_action_result_label", "Ошибка удаления (возможно, запись не найдена)");
                    }
                    // Установка параметров JSP.
                    request.setAttribute("person", person);
                    // Подготовка параметров для JSP.
                    jsp_parameters.put("current_action", "edit");
                    jsp_parameters.put("next_action", "edit_go");
                    jsp_parameters.put("next_action_label", "Сохранить");

                    // Установка параметров JSP.
                    request.setAttribute("jsp_parameters", jsp_parameters);
                    // Передача запроса в JSP.
                    dispatcher_for_manager.forward(request, response);
                    break;
            }
        }
    }

    // Реакция на POST-запросы.
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Обязательно ДО обращения к любому параметру нужно переключиться в UTF-8,
        // иначе русский язык при передаче GET/POST-параметрами превращается в "кракозябры".
        request.setCharacterEncoding("UTF-8");
        // В JSP нам понадобится сама телефонная книга. Можно создать её экземпляр там,
        // но с архитектурной точки зрения логичнее создать его в сервлете и передать в JSP.
        request.setAttribute("phonebook", this.phonebook);
        // Хранилище параметров для передачи в JSP.
        HashMap<String, String> jsp_parameters = new HashMap<String, String>();
        // Диспетчеры для передачи управления на разные JSP (разные представления (view)).
        RequestDispatcher dispatcher_for_manager = request.getRequestDispatcher("/ManagePerson.jsp");
        RequestDispatcher dispatcher_for_phone = request.getRequestDispatcher("/ManagePhone.jsp");
        RequestDispatcher dispatcher_for_list = request.getRequestDispatcher("/List.jsp");
        // Действие (add_go, edit_go) и идентификатор записи (id) над которой выполняется это действие.
        String add_phone_go = request.getParameter("add_phone_go");
        String edit_phone_go = request.getParameter("edit_phone_go");
        Person person = this.phonebook.getPerson(request.getParameter("personId"));
        request.setAttribute("person", person);
        // Добавление записи.
        if (add_phone_go != null) {
            // Создание записи на основе данных из формы.
            // Валидация телефона.
            //String error_message = this.validatePersonFMLName(new_person);
            Phone phone = new Phone("", request.getParameter("number"));
            String error_message = validatePhone(phone);
            if (error_message.equals("")) {
                // Если запись удалось добавить...
                if (person.addPhone(phone)) {
                    jsp_parameters.put("current_action_result", "ADDITION_SUCCESS");
                    jsp_parameters.put("current_action_result_label", "Добавление выполнено успешно");
                }
                // Если запись НЕ удалось добавить...
                else {
                    jsp_parameters.put("current_action_result", "ADDITION_FAILURE");
                    jsp_parameters.put("current_action_result_label", "Ошибка добавления");
                }
                // Установка параметров JSP.
                jsp_parameters.put("current_action", "edit");
                jsp_parameters.put("next_action", "edit_go");
                jsp_parameters.put("next_action_label", "Сохранить");
                request.setAttribute("jsp_parameters", jsp_parameters);
                // Передача запроса в JSP.
                dispatcher_for_manager.forward(request, response);
            }
            // Если в данных были ошибки, надо заново показать форму и сообщить об ошибках.
            else {
                // Подготовка параметров для JSP.
                jsp_parameters.put("current_action", "add_phone");
                jsp_parameters.put("next_action", "add_phone_go");
                jsp_parameters.put("next_action_label", "Добавить номер");
                jsp_parameters.put("error_message", error_message);
                // Установка параметров JSP.
                request.setAttribute("person", person);
                request.setAttribute("phone", phone);
                request.setAttribute("jsp_parameters", jsp_parameters);
                // Передача запроса в JSP.
                dispatcher_for_phone.forward(request, response);
            }
            // Редактирование записи.
        } else if (edit_phone_go != null) {
            // Получение записи и её обновление на основе данных из формы.
            Phone phone = person.getPhones().get(request.getParameter("phoneId"));
            phone.setNumber(request.getParameter("number"));
            // Валидация ФИО.
            String error_message = validatePhone(phone);
            // Если данные верные, можно производить добавление.
            if (error_message.equals("")) {
                // Если запись удалось обновить...
                if (person.updatePhone(phone)) {
                    jsp_parameters.put("current_action_result", "UPDATE_SUCCESS");
                    jsp_parameters.put("current_action_result_label", "Обновление выполнено успешно");
                }
                // Если запись НЕ удалось обновить...
                else {
                    jsp_parameters.put("current_action_result", "UPDATE_FAILURE");
                    jsp_parameters.put("current_action_result_label", "Ошибка обновления");
                }
                // Установка параметров JSP.
                request.setAttribute("jsp_parameters", jsp_parameters);
                // Передача запроса в JSP.
                jsp_parameters.put("current_action", "edit");
                jsp_parameters.put("next_action", "edit_go");
                jsp_parameters.put("next_action_label", "Сохранить");
                dispatcher_for_manager.forward(request, response);
            }
            // Если в данных были ошибки, надо заново показать форму и сообщить об ошибках.
            else {
                // Подготовка параметров для JSP.
                jsp_parameters.put("error_message", error_message);
                // Установка параметров JSP.
                request.setAttribute("person", person);
                request.setAttribute("phone", phone);
                request.setAttribute("jsp_parameters", jsp_parameters);
                // Передача запроса в JSP.
                dispatcher_for_manager.forward(request, response);
            }
        }


    }
}