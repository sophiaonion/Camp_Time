var main = function(campers){
    $('#create-camper').on('click', function(){
    var data = {
        name: $('#camper-name').val(),
        age: $('#camper-age').val(),
        extraInfo: $('#extra-info').val()
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
}

$(document).ready(main);
