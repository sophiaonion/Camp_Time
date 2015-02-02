var main = function(){
    $('#create-camper').on('click', function(){
    var data = {
        camperName: $('#camper-name').val(),
        camperAge: $('#camper-age').val(),
        extraInfo: $('#extra-info').val()
    };


    $.ajax({
        type: 'POST',
        url: '/api/campers/' + data.camperName + '/' + data.camperAge + '/' data.extraInfo,
        data: JSON.stringify(data),
        contentType: 'application/JSON'
    });

    });
}
//eventually get campers and sessions and pass to main function
$(document).ready(function(){


});