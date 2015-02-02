var main = function(camp_sessions){
    //get camper name from linked camper registration page
    var qs = new QueryString();

    camp_sessions.forEach(function(session){
        var element = $("<option>");
        element.html('value', session.name);
        element.data('sessionID', session.sessionID);
    });

    $('#submit-registration').on('click', function(){
    var data = {
        camperID: $('#camper-name').val(),
        sessionID: $('#session-name').val()
    };



    $.ajax({
        type: 'PUT',
        url: '/api/campers/' + data.camperID + '/' + data.sessionID,
        data: JSON.stringify(data),
        contentType: 'application/JSON'
    });

    $('#camper-name').val("");
    $('#session-name').val("");
});

};
//eventually get campers and sessions and pass to main function
$(document).ready(function(){
        $.get('/api/campsessions', function(camp_sessions){
            main(campSessions);
        });
});