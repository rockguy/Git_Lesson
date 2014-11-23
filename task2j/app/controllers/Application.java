package controllers;

import play.mvc.*;
import play.data.*;
import models.*;

public class Application extends Controller {
    static Form<Note> noteForm = Form.form(Note.class);

    public static Result index() {
        return redirect(routes.Application.notes());
    }

    public static Result notes() {
        return ok(
            views.html.index.render(Note.all(), noteForm)
        );
    }

    public static Result createNote() {
        Form<Note> filledForm = noteForm.bindFromRequest();
        if(filledForm.hasErrors()) {
            return badRequest(
                    views.html.index.render(Note.all(), filledForm)
            );
        } else {
            Note note = filledForm.get();
            if (note!=null) note.save();
            return redirect(routes.Application.notes());
        }
    }

    public static Result editNote(Long id) {
        return ok(
                views.html.edit.render(noteForm.fill(Note.find.byId(id)))
        );
     }

    public static Result saveNote(Long id) {
        Form<Note> filledForm = noteForm.bindFromRequest();
        if(filledForm.hasErrors()) {
            return badRequest(
                    views.html.edit.render(filledForm)
            );
        } else {
            Note.edit(filledForm.get(), id);
            return redirect(routes.Application.notes());
        }
    }


    public static Result deleteNote(Long id) {
        Note note = Note.find.byId(id);
        if (note!=null) note.delete();
        return redirect(routes.Application.notes());
    }
}
