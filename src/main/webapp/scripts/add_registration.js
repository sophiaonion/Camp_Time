var main = function(camp_sessions, campers){
    //get camper name from linked camper registration page

//    camp_sessions.forEach(function(session){
//        var element = $("<option>");
//        element.html('value', session.name);
//        element.data('sessionID', session.sessionID);
//          $('#session').append(element);
//    });

    //hard code session options for now

    var autocomplete =

    $('#camper-name').autocomplete({
        source:
        autoFocus: true
        autocompletechange:
        }
    })


    $('#submit-registration').on('click', function(){
        var data = {
            camperID: $('#camper-name').val(),
            sessionID: $('#session').val()
            //for nonhard coded method
            //sessionID: $('#session option:selected').data(sessionID);
        };

        $.ajax({
            type: 'PUT',
            url: '/api/campers/' + data.camperID + '/' + data.sessionID,
            data: JSON.stringify(data),
            contentType: 'application/JSON',
            success: function(data){
                alert('camper registered');
            }
        });

        $('#camper-name').val("");
        $('#session-name').val("");
    }); //end submit-registration click handler

    $('#cancel').on('click', function(){
        window.location.replace('home_page_test.html');
    });

};
//eventually get campers and sessions and pass to main function
$(document).ready(function(){
   $.get('/api/campsessions', function(camp_sessions){
       $.get('api/campers', function(campers){
            main(camp_sessions, campers);
       });
   });
});