package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.Routes;
import play.libs.Json;
import play.mvc.*;
import play.data.*;
import models.*;

import java.util.List;

import static play.libs.Json.toJson;

public class Application extends Controller {
    static Form<Note> noteForm = Form.form(Note.class);

    public static Result index() {
        //используем реверсную маршрутизацию для генерации строки URL из action-а контролера
        return redirect(controllers.routes.Application.notes());
    }

    /*
    Отдаем базовый шаблон одностраничного приложения
     */
    public static Result notes() {
        return ok(
                views.html.index.render(noteForm)
        );
    }

    //API для взаимодействия клиентов с сервером посредством JSON
    @BodyParser.Of(BodyParser.Json.class)
    public static Result saveNoteJson() {
        play.Logger.info("saveNoteJson()");

        JsonNode json = request().body().asJson();

        if (json == null) {
            return errorJsonResult("JSON expected");
        } else {
            ObjectNode noteNode = Json.fromJson(json,ObjectNode.class);

            //todo note from json

            Long id = json.get("id").asLong();
            Note note = null;
            if (id!=0){
                note = Note.find.byId(id);
            }
            if (note == null){
                //создаем новый Note
                note = new Note();
            }

            note.cellPhone = noteNode.findPath("cellPhone").asText();
            note.homePhone = noteNode.findPath("homePhone").asText();
            note.name = noteNode.findPath("name").asText();

            play.Logger.info("trying to save to DB");
            note.save();

            return ok(toJson(note));
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result deleteNoteJson() {
        play.Logger.info("deleteNoteJson()");
        JsonNode json = request().body().asJson();

        if (json == null) {
            return errorJsonResult("Json expected");
        } else {
            Long id = null;
            try {
                id = json.findPath("id").longValue();
            } catch (NumberFormatException nfe) {
                //id = null, ничего не делаем
            }

            if (id == null) {
                return errorJsonResult("id expected");
            } else {
                Note note = Note.find.byId(id);
                if (note == null) {
                    return notFound(errorJson("note is not found"));
                }
                JsonNode result = toJson(note);
                note.delete();
                return ok(result);
            }
        }
    }

    public static Result notesJson() {
        List<Note> all = Note.all();
        return ok(toJson(all));
    }

    private static Result errorJsonResult(String errorMessage){
        return badRequest(errorJson(errorMessage));
    }

    private static JsonNode errorJson(String errorMessage){
        return Json.newObject().put("error", errorMessage);
    }


    public static Result jsRoutes() {
        response().setContentType("text/javascript");
        return ok(
                Routes.javascriptRouter("jsRoutes",
                        controllers.routes.javascript.Application.saveNoteJson(),
                        controllers.routes.javascript.Application.notesJson(),
                        controllers.routes.javascript.Application.deleteNoteJson()
                )
        );
    }
}
