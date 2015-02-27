var logout = function(){
    $('#log-out').click(function(event){
        console.log('log out js');

        var sessionKey;

        $.ajax({
            type: 'GET',
            url: '/api/login/current',
            contentType: 'application/JSON',
            success: function(key){
                console.log(key);
                sessionKey = key;
            },
            error: function(request, status, error){
                alert(error);
            }
        });

        console.log(sessionKey);

        $.ajax({
            type: 'DELETE',
            url: '/api/login/current/' + sessionKey,
            //data: JSON.stringify(data),
            contentType: 'application/JSON',
            success: function(){
                alert("Bye!");
                window.location.href = "index.html";
            },
            error: function(request, status, error){
                alert(error);
            }
        });

    });


};

$(document).ready(logout);