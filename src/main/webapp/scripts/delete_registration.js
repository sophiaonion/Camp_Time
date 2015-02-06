var main = function(campers){

    //generate autocomplete selections
    //value is displayed, ID is extra data to send
    console.log('campers: ' + campers);
    var autocomplete_source = campers.map(function(camper){

        console.log('from $get camper name: ' + camper.name + 'id as camperID' + camper._id);
        return {
            value: camper.name,
            ID: camper._id,
            age: camper.age //string of scout level to be used to get appropriate available sessions
        };
    });

    autocomplete_source.forEach(function(value){
        console.log(value.value + value.ID);
    });

    var selected_camperID;
    //get the sessions that campers are in
    var uponSelect = function(event, ui){ //upon selection set camperID to send when submitting registration
                                 //ui.item is selected item, has fields set in source
                                 selected_camperID = ui.item.ID;
                                 //get the list of registered sessions for this particular camper
                                 $.get('/api/campers/registrations/'+ui.item.ID, function(camp_sessions){
                                 //append as option elements for campsession collect
                                        camp_sessions.forEach(function(session){
                                            var element = $("<option>");
                                            element.val(session.name);
                                            element.text(session.name);
                                            element.data('sessionID', session._id);
                                             $('#session').append(element);
                                        });
                                 });
                                 console.log(ui.item.ID);
                             };

    $('#camper-name').autocomplete({
        source: autocomplete_source, //set possible options
        autoFocus: true, //automatically select closest match
        select: uponSelect,
        messages: {//get rid of helper text
            noResults: '',
            results: function(){}//usually appends selected result text to bottom
        }
    });


    $('#submit-registration').on('click', function(){
        var data = {
            //for nonhard coded method
            sessionID: $('#session option:selected').data('sessionID'),
            camperID: selected_camperID
        };
        console.log('submit clicked');
        console.log(data.camperID);
        console.log(data.sessionID);

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

    }); //end submit-registration click handler

    $('#cancel').on('click', function(){
        window.location.replace('home_page_test.html');
    });
};

$(document).ready(function(){
       $.get('api/campers', function(campers){
            main(campers);
        });
});