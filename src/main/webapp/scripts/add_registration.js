var main = function(camp_sessions, campers){
    //get camper name from linked camper registration page

    camp_sessions.forEach(function(session){
        var element = $("<option>");
        element.val(session.name);
        element.text(session.name);
        element.data('sessionID', session._id);
         $('#session').append(element);
    });

    //generate autocomplete selections
    //value is displayed, ID is extra data to send
    console.log('campers: ' + campers);
    var autocomplete_source = campers.map(function(camper){

        console.log('from $get camper name: ' + camper.name + 'id as camperID' + camper._id);
        return {
            value: camper.name,
            ID: camper._id
        };
    });

    autocomplete_source.forEach(function(value){
        console.log(value.value + value.ID);
    });

    var uponSelect = function(event, ui){ //upon selection set camperID to send
                                 //ui.item is selected item
                                 selected_camperID = ui.item.ID;
                                 console.log(ui.item.ID);
                             };
    var selected_camperID;
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
        $.ajax({
            type: 'PUT',
            url: '/api/campers/' + data.camperID + '/' + data.sessionID,
            data: JSON.stringify(data),
            contentType: 'application/JSON',
            success: function(data){
                alert('camper registered for this session');
                window.location.replace('home_page_test.html');
            }
        });

    }); //end submit-registration click handler

    $('#cancel').on('click', function(){
        window.location.replace('home_page_test.html');
    });

};

$(document).ready(function(){
   $.get('/api/campsessions', function(camp_sessions){
       $.get('api/campers', function(campers){
            main(camp_sessions, campers);
       });
   });
});