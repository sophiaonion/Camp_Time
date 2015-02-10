var main = function(camp_sessions){
    var autocomplete_source = camp_sessions.map(function(session){
        return{
            value: session.name, //text to match
            ID: session._id //extra value can have any name
        };
    });

    var selected_sessionName;
    var uponSelect = function(event, ui){ //upon selection set camperID to send when submitting registration
         selected_sessionName = ui.item.name; //ui.item is selected item, has fields set in source
     };

    $('#session-lookup').autocomplete({
        source: autocomplete_source,
        autoFocus: true,
        select: uponSelect
    });

    $('#look-up').on('click', function(){
        var data = {
            sessionName: selected_sessionName
        };

        console.log("clicked");

        $.get('/api/campsessions/' + data.sessionName, function(activities){
        console.log(data.sessionName);
        console.log(activities);
           activities.forEach(function(activity){
           //TODO need to put them in an editable area
           var emp = $("<li>").text(activity.title + " " + activity.time + " "+
                     activity.session + " " + activity.activityArea);
           $(".session").append(emp);

           });
         });

    });

    $('#submit').on('click', function(){
        //TODO need to fix this based on different field
        var data = {
            //for nonhard coded method
            sessionID: $('#session option:selected').data('sessionID'),
            camperID: selected_camperID
        };

        $.ajax({
            type: 'DELETE',
            url: '/api/campers/' + data.camperID + '/' + data.sessionID,
            data: JSON.stringify(data),
            contentType: 'application/JSON',
            success: function(data){
                alert('Successfully delete registration');
                window.location.replace('home_page_test.html');
            },
            error: function(request, status, error){
                alert(error);
            }
        });
    });

    $('#cancel').on('click', function(){
        window.location.replace('home_page_test.html');
    });
}

$(document).ready(function(){
    $.get('/api/campsessions', function(camp_sessions){
        main(camp_sessions);
    });
});