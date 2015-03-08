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
        },
        fail: function(data) {
            $("#busy").hide();
            if (confirm("Update failed: go to manual adjust mode?") == true) {
                 window.location.replace('session_schedule.html');
            } else {
                 window.location.replace('home_page_test.html');
            }
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