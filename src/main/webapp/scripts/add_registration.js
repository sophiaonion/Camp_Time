var main = function(campers, role){
    //to do get camper name from linked camper registration page

    //generate autocomplete selections
    //value is displayed, ID is extra data to send
    var autocomplete_source = campers.map(function(camper){
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
    var selected_camperAge;
    var uponSelect = function(event, ui){ //upon selection set camperID to send when submitting registration
        //ui.item is selected item, has fields set in source
        selected_camperID = ui.item.ID;
        selected_camperAge=ui.item.age;
        console.log(ui.item.ID);
        console.log(selected_camperAge);

         $.get('/api/campsessions/all', function(camp_sessions){
            //remove options
            var x = document.getElementById("session");
            for (i=x.length;i > 0;i--) {
                x.remove(i);
            }

            //append as option elements for campsession collect
            console.log(camp_sessions);
            camp_sessions.forEach(function(session){
            if(role == 'customer')
            {
                if (session.ageGroup == selected_camperAge){
                    var element = $("<option>");
                     element.val(session.name);
                     element.text(session.name);
                     element.data('sessionID', session._id);
                     $('#session').append(element);
                }
            }else{
             var element = $("<option>");
                 element.val(session.name);
                 element.text(session.name);
                 element.data('sessionID', session._id);
                 $('#session').append(element);
             }

             });
         });
    };

    $('#camper-name').autocomplete({
        source: autocomplete_source, //set possible options
        autoFocus: true, //automatically select closest match
        select: uponSelect,
//        messages: {//get rid of helper text
//            noResults: '',
//            results: function(){}//usually appends selected result text to bottom
//        }
    });

    $('#submit-registration').on('click', function(){
        var approved;
        console.log(role);
        if(role == 'customer')
            {approved = "false";}
        else
            {approved = "true";}

        var data = {
            //for nonhard coded method
            sessionID: $('#session option:selected').data('sessionID'),
            camperID: selected_camperID,
            approved: approved

        };
        console.log('submit clicked');
        console.log(data.approved);
        $.ajax({
            type: 'PUT',
            url: '/api/campers/' + data.camperID + '/' + data.sessionID + '/' + data.approved,
            data: JSON.stringify(data),
            contentType: 'application/JSON',
            success: function(data){
                 if (confirm("Registration Successful: Continue?") == true) {
                     window.location.replace('home_page_test.html');
                 } else {
                     window.location.replace('home_page_test.html');
                 }
            },
        });

    }); //end submit-registration click handler

    $('#cancel').on('click', function(){
        window.location.replace('home_page_test.html');
    });
};

$(document).ready(function(){
        $.get('/api/login/current/user', function(current){
            console.log(current._id);
            console.log(current.roles[0]);
            if(current.roles[0] == 'customer') {
                $.get('/api/campers/customer/'+current._id, function(campers){
                       console.log("Campers:"+ campers);
                       main(campers, current.roles[0]);
                });
           } else {
                $.get('/api/campers/all', function(campers){
                       console.log("Campers:"+ campers);
                       main(campers, current.role);
                });
           };
        });


});