var main = function(){
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
        contentType: 'application/JSON'
    });

    });
}


$(document).ready(main);