var main = function(){
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

    });

}
//eventually get campers and sessions and pass to main function
$(document).ready(main);