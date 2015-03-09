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
             alert('Camper Created.');

             //link the camper with this customer account
             $.get('/api/login/role', function(role){
                 role = role.replace(/\s+/g, '');
                 if (role == "customer"){

                     $.get('/api/login/current/user', function(current){
                         $.ajax({
                             type: 'PUT',
                             url: '/api/users/campers/add',
                             data: JSON.stringify({
                               user_id:current._id,
                               camper_id:data._id
                              }),
                             contentType: 'application/JSON',
                             success: function(data){
                                 if (confirm('Camper Added to User: Continue to Registration?') == true) {
                                    window.location.replace('register_camper.html');
                                 } else {
                                    window.location.replace('home_page_test.html');
                                 }
                                    window.location.replace('home_page_test.html');
                             },

                             error: function(request, status, error){
                                 alert(error);
                             }
                         });
                        })

                 };
                 if (confirm('Camper Added: Continue to Registration?') == true) {
                    window.location.replace('register_camper.html');
                 } else {
                    window.location.replace('home_page_test.html');
                 }
                    window.location.replace('home_page_test.html');
                 console.log('Camper Added: Continue to Registration?');
                 window.location.replace('register_camper.html');

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
