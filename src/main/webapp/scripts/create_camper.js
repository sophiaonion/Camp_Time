var main = function(campers){
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
             window.location.replace('register_camper.html');
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
$(document).ready(function(){
       $.get('/api/campers', function(campers){
                main(campers);
       });

});
