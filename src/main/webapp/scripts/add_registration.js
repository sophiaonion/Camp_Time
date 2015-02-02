var main = function(camp_sessions, campers){
    //get camper name from linked camper registration page

    camp_sessions.forEach(function(session){
        var element = $("<option>");
        element.html('value', session.name);
        element.data('sessionID', session.sessionID);
          $('#session').append(element);
    });

    //generate autocomplete selections
    //value is displayed, ID is extra data to send
    var autocomplete_source = campers.forEach(function(camper){

        return {
            value: camper.name,
            ID: camper.camperID
        };
    });

    var selected_camperID;
    $('#camper-name').autocomplete({
        source: autocomplete_source, //set possible options
        autoFocus: true, //automatically select closest match
        select: function(event, ui){ //upon selection set camperID to send
            //ui.item is selected item
            selected_camperID = ui.item.ID;
            console.log(ui.item.ID);
        }
    });


    $('#submit-registration').on('click', function(){
        var data = {
            //for nonhard coded method
            sessionID: $('#session option:selected').data(sessionID),
            camperID: selected_camperID
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