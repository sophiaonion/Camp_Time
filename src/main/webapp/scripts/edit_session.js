var main = function(camp_sessions){
    var autocomplete_source = camp_sessions.map(function(session){
        return{
            value: session.name, //text to match
            ID: session._id //extra value can have any name
        };
    });

    //get session ID and query for session info -- at this time just get camper list
    var uponSelect =  function(event, ui){
        selected_sessionID = ui.item.ID;
        $.get('/api/campsessions/campers/' + selected_sessionID, function(campers){
            console.log('got campers');
            console.log(campers);
            campers.forEach(function(camper){
                console.log(camper.name);

            });

        });
    }

    var selected_sessionID;
    $('#session-lookup').autocomplete({
        source: autocomplete_source,
        autoFocus: true,
        select: uponSelect

    });
}

$(document).ready(function(){
    $.get('/api/campsessions', function(camp_sessions){
        main(camp_sessions);
    });
});