var main = function(){
    $('#login').on('click', function(){
    var username = $('#username').val();
    var password = $('#password').val()

    $.ajax({
        type: 'GET',
        url: '/api/users',
        contentType: 'application/JSON',
        beforeSend: function (xhr) { //add authentication to the request, the text is a way of authenticating
        //from Stackoverflow
                xhr.setRequestHeader('Authorization', "Basic " + btoa(username + ":" + password));
            },
        success: function(data){
             window.location.replace('home_page_test.html');
        },
        error: function(request, status, error){
            alert(error);
             alert("Not be able to log in! ");
        }
    });

    });
}

$(document).ready(main);