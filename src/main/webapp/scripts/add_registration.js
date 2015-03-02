var main = function(campers){
    //to do get camper name from linked camper registration page

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
    var uponSelect = function(event, ui){ //upon selection set camperID to send when submitting registration
                                 //ui.item is selected item, has fields set in source
                                 selected_camperID = ui.item.ID;
                                 $.get('/api/campsessions/agegroup/' + ui.item.age, function(camp_sessions){
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
        $.get('/api/login/current/user', function(current){
           user=current;

           $.get('api/campers/customer/'+current.userID, function(campers){
                       console.log("Campers:"+ campers);
                       main(campers);
                   });
        });


});