package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.Play;
import play.Routes;
import play.libs.Json;
import play.mvc.*;
import play.data.*;
import models.*;

import java.io.File;
import java.util.List;

public class Application extends Controller {
    static Form<Note> noteForm = Form.form(Note.class);

    public static Result index() {
        return redirect(controllers.routes.Application.notes());
    }

    /*
    Отдаем базовый шаблон одностраничного приложения
     */
    public static Result notes() {
        return ok(
                views.html.index.render(Note.all(), noteForm)
        );
    }

    //API для взаимодействия клиентов с сервером посредством JSON
    @BodyParser.Of(BodyParser.Json.class)
    public static Result saveNoteJson() {
        play.Logger.info("saveNoteJson()");

        JsonNode json = request().body().asJson();

        ObjectNode result = Json.newObject();
        if (json == null) {
            result.put("status", "KO");
            result.put("error", "JSON expected");
            return badRequest(result);
        } else {
            String name = json.findPath("name").textValue();
            String homePhone = json.findPath("homePhone").textValue();
            String cellPhone = json.findPath("cellPhone").textValue();
            Long id = null;
            try {
                id = json.findPath("id").longValue();
                if (id == 0) id = null;
            } catch (NumberFormatException nfe) {
                //id = null, ничего не делаем
            }

            ObjectNode noteNode = Json.newObject();
            noteNode.put("name", name);
            noteNode.put("homePhone", homePhone);
            noteNode.put("cellPhone", cellPhone);

            Note note = new Note();
            note.name = name;
            note.homePhone = homePhone;
            note.cellPhone = cellPhone;

            if (id == null) {
                //create
                play.Logger.info("trying to save note");
                note.save();
                noteNode.put("id", note.id);
            } else {
                //update
                note.id = id;
                Note.edit(note, id);
                noteNode.put("id", id);
            }

            result.put("status", "OK");
            result.put("note", noteNode);
            return ok(result);
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result deleteNoteJson() {
        play.Logger.info("deleteNoteJson()");
        JsonNode json = request().body().asJson();

        ObjectNode result = Json.newObject();
        if (json == null) {
            result.put("status", "KO");
            result.put("error", "JSON expected");
            return badRequest(result);
        } else {
            Long id = null;
            try {
                id = Long.valueOf(json.findPath("id").longValue());
            } catch (NumberFormatException nfe) {
                //id = null, ничего не делаем
            }

            if (id == null) {
                result.put("status", "KO");
                result.put("error", "id expected");
                return badRequest(result);
            } else {

                Note note = Note.find.byId(id);
                if (note == null) {
                    result.put("status", "KO");
                    result.put("error", "note is not found");
                    return badRequest(result);
                }

                note.delete();
            }
            result.put("status", "OK");
            return ok(result);
        }
    }

    public static Result notesJson() {
        ObjectNode result = Json.newObject();
        List<Note> all = Note.all();

        if (all == null) {
            result.put("status", "KO");
            result.put("error", "list of notes is null");
            return badRequest(result);
        } else {
            JsonNode notesJson = Json.toJson(all);
            result.put("status", "OK");
            result.put("objects", notesJson);
            return ok(result);
        }
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
