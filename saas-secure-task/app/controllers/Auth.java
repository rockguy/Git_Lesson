package controllers;

import models.*;
import play.data.Form;
import play.mvc.*;
import views.html.*;

public class Auth extends Controller {

    /**
     * Страница выдачи формы для аутентификации
     * @return страница с пустой формой логин/пароль.
     *
     * NB: В случае, если пользователь уже аутентифицирован должна возвращать редирект на Application.index()
     *
     * Подсказка: воспользоваться генераторами форм на базе класса Login
     * Form.form(Login.class)
     *
     */
    public static Result login() {
        //todo В случае, если пользователь уже аутентифицирован должна возвращать редирект на Application.index()
        //todo страница с пустой формой логин/пароль.
        return TODO;
    }

    /**
     * Обработка формы аутентификации
     * Производит валидацию штатными средствами засчет Login.validate(). Он неявно вызывается при вызове
     *
     * loginForm.hasErrors()
     *
     * Подсказка: этот метод уже реализован для демонстрации.
     *
     * @return форму с ошибкой в случае не корректной валидации.
     * В случае успеха устанавливает ключ в сессии и перенаправляет на главную страницу
     */
    public static Result auth() {
        Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
        if (loginForm.hasErrors())
            //форма содержит ошибку и будет выдана пользователю обратно. При ошибки валидации покажутся автоматически засчет form-helper-ов
            return badRequest(login.render(loginForm));
        else {
            //Установка ключа email в сессии равной email аутентифицированного пользователя.
            session("email", loginForm.get().email);
            //При успехе можем установить во flash значение строки успеха, которое будет отображено в интерфейсе с помощью alert
            flash("success","Вы успешно аутентифицировались. Добро пожаловать!");
            //в случае успеха переходим на главную страницу
            return redirect(routes.Application.index());
        }
    }

    // Выход и очистка сессии

    /**
     * Выход пользователя.
     * @return Осуществляет очистку сессии и перенаправление на главную страницу
     */
    public static Result logout() {
        //todo
        return TODO;
    }

    // Страница регистрации

    /**
     * Страница выдает пустую форму регистрации.
     * Если осуществлен вход(логин) перенаправляет на гл. страницу
     * @return пустая форма регистрации.
     */
    public static Result signup() {
        //todo Если осуществлен вход(логин) перенаправляет на гл. страницу
        //todo выдаем пустую форму регистрации. Воспользоваться классом Form[Register.class]
        return TODO;
    }


    /**
     * Обработка формы регистрации.
     * Производится валидация формы. Штатными средствами класса Register
     *
     * Проверяется, что данный email еще не зарегистрирован в системе.
     *
     *
     * @return В случае успеха создает пользоваетеля в базе, аутентифицирует и перенаправляет на гл. страницу.
     */
    public static Result register() {
        return TODO;
    }

    /**
     * @return email текущего пользователя , сохраненный в сессии
     */
    public static String currentUserEmail() {
        return session("email");
    }

    /**
     *
     * @return Объект User для текущего пользователя. В случае гостя возвращает null
     */
    public static User currentUser(){
        return User.find.byId(currentUserEmail());
    }
}
