var main = function(){
    $('#create-camper').on('click', function(){
    //have to match attributes of java class exactly
    var data = {
        name: $('#camper-name').val(),
        age: $('#camper-age').val(),
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