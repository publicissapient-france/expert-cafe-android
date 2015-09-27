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
                return " from " + hour + "h to " + hour + "h15";
            case "1":
                return " from " + hour + "h15 to " + hour + "h30";
            case "2":
                return " from " + hour + "h30 to " + hour + "h45";
            case "3":
                return " from " + hour + "h45 to " + (hour + 1) + "h";
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

            message(expert.get('email'), "Expert Café: new appointment",
                "Hello " + expertName + "," +
                "\n\nYou got a new appointment with " + attendeeName + appointmentTime + "." +
                "\n\nHe wants to talk about:" +
                "\n" + appointmentSubject + "" +
                "\n\nThanks," +
                "\nHave fun.");

            message(request.object.get("email"), "Expert Café: booked",
                "Hello " + attendeeName + "," +
                "\n\nAppointment booked " + appointmentTime + " with " + expertName + " for " + expert.get('domain') + "." +
                "\n\nYou want to talk about:" +
                "\n" + appointmentSubject + "" +
                "\n\nRegards," +
                "\nExpert Café Team");

            message(toMarketing, "Expert Café: booked",
                "Hello," +
                "\n\n" + attendeeName + " (" + request.object.get('email') + ") booked an appointment with " + expert.get('name') + appointmentTime + "." +
                "\n\nHe wants to talk about:" +
                "\n" + appointmentSubject + "" +
                "\n\nSee ya.");
        },
        error: function (error) {
            console.log("Cannot get Expert with id " + request.object.get("expert").id);
            console.log(error);
        }
    });


});