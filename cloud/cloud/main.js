Parse.Cloud.afterSave("Meeting", function (request) {

    var toMarketing = "marketing@xebia.fr";

    var message = function (to, subject, text) {
        Mailgun.sendEmail({
            to: to,
            from: "Xebia <marketing@xebia.fr>",
            subject: subject,
            text: text
        }, {
            success: function (response) {
                console.log(response);
            },
            error: function (error) {
                console.log("Cannot send message");
                console.error(error);
            }
        });
    };

    var prettyTime = function (time) {
        var array = time.split('_');
        var hour;
        switch (array[0]) {
            case "TEN":
                hour = 10;
                break;
            case "ELEVEN":
                hour = 11;
                break;
            case "TWELVE":
                hour = 12;
                break;
            case "ONE":
                hour = 13;
                break;
            case "TWO":
                hour = 14;
                break;
            case "THREE":
                hour = 15;
                break;
            case "FOUR":
                hour = 16;
                break;
            case "FIVE":
                hour = 17;
                break;
            case "SIX":
                hour = 18;
                break;
        }
        switch (array[1]) {
            case "0":
                return " de " + hour + "h à " + hour + "h15";
            case "1":
                return " de " + hour + "h15 à " + hour + "h30";
            case "2":
                return " de " + hour + "h30 à " + hour + "h45";
            case "3":
                return " de " + hour + "h45 à " + (hour + 1) + "h";
        }
    };

    var Mailgun = require('mailgun');
    Mailgun.initialize('mailgun.xebia.fr', 'key-3a3bef104e23f25f6a21a2b566f10e23');

    var query = new Parse.Query("Expert");
    query.get(request.object.get("expert").id, {
        success: function (expert) {
            console.log(expert);

            var attendeeName = request.object.get('name');
            var appointmentTime = prettyTime(request.object.get('time'));
            var appointmentSubject = request.object.get('subject');
            var expertName = expert.get('name');

            message(expert.get('email'), "Expert Café : nouveau rendez-vous",
                "Bonjour " + expertName + "," +
                "\n\nNouveau rendez-vous planifié avec " + attendeeName + appointmentTime + "." +
                "\n\nSujet :" +
                "\n" + appointmentSubject + "" +
                "\n\nMerci," +
                "\nBonne journée.");

            message(request.object.get("email"), "Expert Café : rendez-vous",
                "Bonjour " + attendeeName + "," +
                "\n\nRendez-vous pris à " + appointmentTime + " avec " + expertName + " pour " + expert.get('domain') + "." +
                "\n\nSujet :" +
                "\n" + appointmentSubject + "" +
                "\n\nMerci," +
                "\nExpert Café Team");

            message(toMarketing, "Expert Café : rendez-vous",
                "Bonjour," +
                "\n\n" + attendeeName + " (" + request.object.get('email') + ") a pris rendez-vous avec " + expert.get('name') + appointmentTime + "." +
                "\n\nSujet :" +
                "\n" + appointmentSubject + "" +
                "\n\nBonne journée.");
        },
        error: function (error) {
            console.log("Cannot get Expert with id " + request.object.get("expert").id);
            console.log(error);
        }
    });


});