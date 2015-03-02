$('.customer-section').hide();
var main = function(campers, if_customer){

    //if it's customer, then give customer the option to add /delete a camper
        if (if_customer){
            $('.customer-section').show();
        }

    campers.forEach(function(value,index){
        var element = $("<option>");
         console.log(index);
         element.val(index);
         element.text(value.name);
         $('#select-camper').append(element);
    });

    $('#select-camper').on('change', function(){
        var camper_index = $(this).val();
        var camper=campers[camper_index];
        console.log("here");
        console.log(camper.age);
        $('.campers-info #age').text(camper.age);
        $('.campers-info #extra-info').text(camper.extraInfo);

    });

    ////////////////////////add /delete a camper
    $('#create-camper').click(function(event){
        window.location.href = "create_camper.html"; //todo need to differentiate create camper for admin vs for customer
    });

    $('#delete-camper').click(function(event){
        var camper_index=$('#select-camper').val();
        var camper=campers[camper_index];
        $.ajax({
            type: 'DELETE',
            url: '/api/campers/' + camper.camperID,
            data: JSON.stringify(data),
            contentType: 'application/JSON',
            success: function(data){
                alert('Successfully deleted camper');
                location.reload();
            },
            error: function(request, status, error){
                alert(error);
            }
        });

    });
    ////////////////////////////////////

     $('#cancel').on('click', function(){
        window.location.replace('home_page_test.html');
    });


};

$(document).ready(function(){

    $.get('/api/login/role', function(role){
             role = role.replace(/\s+/g, ''); //eliminate unnessacery whitespace, from stackoverflow

             console.log('here');
             var userId;
             $.get('/api/login/current/user', function(data){
                userId=data._id;
                console.log(userId);
                if (role == "customer"){
                //get the campers that user created
                console.log(userId);
                  $.ajax({
                        type: 'GET',
                        url: '/api/campers/customer/' + userId,
                        contentType: 'application/JSON',
                        success: function(result){
                            console.log(result);
                            main(result, true);
                        },
                        error: function(request, status, error){
                            console.log(request);
                            alert(status),
                            alert(error);
                        }
                    });

                }
             });

            // console.log(userId);

             if(role == "counselor"){ //find out which session is counselor working with (first get),
             //use sessionId to request campers via camper resource (second get)
             var camperList;
              $.get('/api/campsessions/'+userID, function(sessions){

                    sessions.forEach(function(session){

                        $.get('/api/campers/'+session.sessionID, function(campers){
                           camperList.add(campers);
                        });

                    });

                    main(camperList, false);
              });


             }




        //todo what if admin????!!!!!!!
        });


});