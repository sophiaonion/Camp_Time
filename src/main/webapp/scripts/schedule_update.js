var main = function(){


    $("#busy").show();

    $.ajax({
        type: 'GET',
        url: '/api/activities',
        contentType: 'application/JSON',
        success: function(data){
            $("#busy").hide();
            alert('nice, it worked');
            window.location.replace('home_page_test.html');
        }

    });


    $('#cancel').on('click', function(){
        window.location.replace('home_page_test.html');
    });

};



$(document).ready(function(){
     main();
});