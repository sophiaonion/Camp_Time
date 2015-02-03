var main = function(campers){
    $('#create-camper').on('click', function(){
    var data = {
        camperName: $('#camper-name').val(),
        camperAge: $('#camper-age').val(),
        extraInfo: $('#extra-info').val()
    };


    $.ajax({
        type: 'POST',
        url: '/api/campers',
        data: JSON.stringify(data),
        contentType: 'application/JSON',
        success: function(data){
             alert('Session Created');
             window.location.replace('register_camper.html');
        },
        error: function(request, status, error){
             alert(error);
        }
    });

    });
}
//eventually get campers and sessions and pass to main function
$(document).ready(function(){
       $.get('/api/campers', function(campers){
                main(campers);
       });

});