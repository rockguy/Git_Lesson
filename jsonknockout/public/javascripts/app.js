//Model
function Note(id, name, homePhone, cellPhone) {
    var self = this;
    self.name = ko.observable(name);
    self.homePhone = ko.observable(homePhone);
    self.cellPhone = ko.observable(cellPhone);
    self.id = id; // не observable поле
}

//ViewModel
function NotesViewModel() {
    var self = this;

    // Editable data
    self.notes = ko.observableArray([]);

    self.id = ko.observable(null);
    self.name = ko.observable("");
    self.cellPhone = ko.observable("");
    self.homePhone = ko.observable("");

    // Operations
    self.saveNote = function() {
        //Получаем объект Note из observable полей формы.
        var note = new Note(self.id(), self.name(), self.homePhone(), self.cellPhone());
        //Преобразовываем в json-строку
        var jsonData = ko.toJSON(note);

        console.log(jsonData);
        //уведомляем сервер
        jsRoutes.controllers.Application.saveNoteJson().ajax({
            dataType : 'json',
            contentType : 'application/json; charset=utf-8',
            data : jsonData,
            success : function(data) {
                console.log("Успешно обработан ajax запрос. Запись добавлена");
                console.log(data);
                //обновляем данные локально
                if (note.id == null){ /*создание нового */
                    //добавляем в массив записей
                    note.id = data.id;  //только что создан и получен от сервера
                    self.notes.push(note);
                } else {
                    //редактирование - ищем и обновляем
                    console.log(self.notes());
                     for (i=0; i< self.notes().length; i++){
                         console.log(self.notes()[i].id);
                         if (self.notes()[i].id === data.id){
                             console.log("found");
                             self.notes()[i].name(data.name);
                             self.notes()[i].homePhone(data.homePhone);
                             self.notes()[i].cellPhone(data.cellPhone);
                             console.log(self.notes()[i]);
                             break;
                         }
                     }
                }

                //обновляем (очищаем) форму
                self.cleanForm();
            },
            error : function(data) {
                alert("error! "+ data.error);
                console.log('error! Не могу отправить json запрос');
                console.log(data);
            }
        });
    };

    self.cleanForm = function(){
        self.name("");
        self.cellPhone("");
        self.homePhone("");
        self.id(null);
    };

    self.editNote = function(note) {
        console.log(note);
        self.id(note.id);
        self.name(note.name());
        self.cellPhone(note.cellPhone());
        self.homePhone(note.homePhone());

    };

    self.removeNote = function(note) {
        //todo Уведомляем сервер
        var jsonData = ko.toJSON(note);
        console.log(jsonData);
        //уведомляем сервер
        jsRoutes.controllers.Application.deleteNoteJson().ajax({
            dataType : 'json',
            contentType : 'application/json; charset=utf-8',
            data : jsonData,
            success : function() {
                console.log("Успешно обработан ajax запрос. Запись удалена");
                //console.log(data);
                //обновляем данные локально. Обращаю внимание, что при удалении элементов работаем с notes.remove вместо notes().remove
                self.notes.remove(note);
            },
            error : function(data) {
                alert("error! "+ data.error);
                console.log('error! Не могу отправить json запрос');
                console.log(data);
            }
        });


    };

    self.reload = function() {
        self.notes.destroyAll(); //при удалении работаем с самим объектом notes вместо notes()
        self.load();
    };

    self.load = function() {
        jsRoutes.controllers.Application.notesJson().ajax({
            dataType : 'json',
            contentType : 'application/json; charset=utf-8',
            //data : jsonData,
            success : function(data) {
                console.log("Успешно обработан json запрос. Записи загружены");
                for (i=0; i< data.length; i++){
                    self.notes.push(new Note(data[i].id, data[i].name, data[i].homePhone, data[i].cellPhone));
                }
            },
            error : function(data) {
                alert("error! "+ data.error);
                console.log('error! Не могу отправить json запрос');
                console.log(data);
            }
        });
    };

    self.load();
}
var notesViewModel = new NotesViewModel();
ko.applyBindings(notesViewModel);

