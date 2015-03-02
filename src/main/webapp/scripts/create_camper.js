var main = function(campers){
    console.log("start create camper");
    $('#create-camper').on('click', function(){
    var data = {
        name: $('#camper-name').val(),
        age: $('#camper-age').val(),
        extraInfo: $('#camper-info').val()
    };

    $.ajax({
        type: 'POST',
        url: '/api/campers',
        data: JSON.stringify(data),
        contentType: 'application/JSON',
        success: function(data){
             alert('Camper Registered.');

             console.log("Before get current user's role");
             //link the camper with this customer account
             $.get('/api/login/role', function(role){
                console.log("start to use get current role");
                role = role.replace(/\s+/g, '');

                console.log("test whether user is a customer");
                 console.log("current role is"+role);
                 if (role == "customer"){
                     console.log("create_camper: user is a customer");

                     $.get('/api/login/current/user', function(current){
                        console.log("create camper, current user"+current);
                        console.log(current);
                        console.log(current._id);
                        console.log(data);
                        console.log(data._id);
                         $.ajax({
                             type: 'PUT',
                             url: '/api/users/campers/add',
                             data: JSON.stringify({
                               user_id:current._id,
                               camper_id:data._id
                              }),
                             contentType: 'application/JSON',
                             success: function(data){
                                 console.log('added camper to the current user');
                                 window.location.replace('register_camper.html');
                             },
                             error: function(request, status, error){
                                 alert(error);
                             }
                         });
                     });

                 }
             });


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

//eventually get campers and sessions and pass to main function
$(document).ready(main);
