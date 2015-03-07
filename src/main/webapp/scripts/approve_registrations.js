var main = function(registrations){
console.log('got it');
console.log(registrations);

        //counselor selection
        registrations.forEach(function(value){
            console.log('step one');
            console.log(value);
            $.get('/api/campers/camperInfo/'+value.camperID, function(camper){
                console.log('step two');
                console.log(camper);
                camper.forEach(function(cvalue){
                    $.get('/api/campers/registrations/'+value.camperID, function(camp_sessions){
                        //append as option elements for campsession collect
                        camp_sessions.forEach(function(session){
                            var camper = $('<option>').text(cvalue.name+'/'+session.name).val(value._id);
                            $('.requesting').append(camper);
                        });
                     });
                });
            });
        });

        //add approvals
        $('.add-camper').click(function(){
            $('.requesting option:selected').each( function() {
                $('.approving').append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option>");
            });
        });

        //remove approvals
        $('.remove-camper').click(function(){
            $('.approving option:selected').each( function() {
                $(this).remove();
            });
        });

    //submit approvals
    $('#submit').on('click', function(){

        var approved=[];
        $( ".approving option" ).each(function(index) {
            approved.push(new String($(this).val()));
        });

        console.log("data going to be sent");
        console.log(approved);
        //var newdata = JSON.stringify(approved);
        //console.log("test stringify data "+newdata);
        var data = {
                ids: approved
            };

        $.ajax({
            type: 'PUT',
            url: '/api/campers/approve',
            data: JSON.stringify(data),
            contentType: 'application/JSON',
            success: function(data){
                 if (confirm("Registration Approved: Update schedule now?") == true) {
                     window.location.replace('trigger_update.html');
                 } else {
                     window.location.replace('home_page_test.html');
                 }
                     window.location.replace('home_page_test.html');
            },
            error: function(request, status, error){
                alert(error);
            }
        });

    }); //end submit-registration click handler

    $('#delete').on('click', function(){

        $( ".approving option" ).each(function(index){
            console.log("data going to be delete");
            console.log($(this).val());

                $.ajax({
                    type: 'DELETE',
                    url: '/api/campers/registrations/byregid/'+$(this).val(),
                    contentType: 'application/JSON',
                    success:function(data){
                        console.log(data);
                        alert("Request Successfully Deleted");
                        window.location.replace('home_page_test.html');
                    },
                    error: function(request, status, error){
                        alert(error);
                    }
                });
        });

    }); //end submit-registration click handler

    $('#cancel').on('click', function(){
        window.location.replace('home_page_test.html');
    });
};

$(document).ready(function(){
        console.log("before get");
       $.get('/api/campsessions/unapproved', function(registrations){
            console.log("in the get");
            console.log(registrations);
            main(registrations);
        });
});