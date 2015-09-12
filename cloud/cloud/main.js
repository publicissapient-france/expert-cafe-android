Parse.Cloud.afterSave("Meeting", function (request) {

    var toMarketing = "b.lacroix@meetic-corp.com";

    var message = function (to, subject, text) {
        Mailgun.sendEmail({
            to: to,
            from: "marketing@xebia.fr",
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
        switch (time) {
            case "TEN_0":
                return " from 10h to 10h15";
            case "TEN_1":
                return " from 10h15 to 10h30";
            case "TEN_2":
                return " from 10h30 to 10h45";
            case "TEN_3":
                return " from 10h45 to 11h";
            case "ELEVEN_0":
                return " from 11h to 11h15";
            case "ELEVEN_1":
                return " from 11h15 to 11h30";
            case "ELEVEN_2":
                return " from 11h30 to 11h45";
            case "ELEVEN_3":
                return " from 11h45 to 12h";
        }
    };

    var Mailgun = require('mailgun');
    Mailgun.initialize('sandbox61d26f12d55746249806db70da16101b.mailgun.org', 'key-3a3bef104e23f25f6a21a2b566f10e23');

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
                "\n\nAppointment booked at " + appointmentTime + " with " + expertName + " for " + expert.get('domain') + "." +
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