//make sure user is logged in order to see the page

$("body").hide();
var main = function(){

    $.ajax({
        type: 'GET',
        url: '/api/login/role',
        contentType: 'application/JSON',
        success: function(){
            $("body").show();
        },
        error: function(request, status, error){
            console.log(error);
            alert('You do not have permission!');
            window.location.href = history.go(-1);
        }
    });
}

var secondary = function (type){
    $.ajax({
        type: 'GET',
        url: '/api/login/role',
        contentType: 'application/JSON',
        success: function(role){
            role=role.replace(/\s+/g, '');
            var parts = type.split(" ");
            var exist=false;
            for (i = 0; i < parts.length; i++) {
                if (role == parts[i])
                 {
                  exist=true;
                 }
            }

            if(exist==false){
                alert('You do not have permission!');
                window.location.href = history.go(-1);
            }

        },
        error: function(request, status, error){
            console.log(error);
            alert('You do not have permission!');
            window.location.href = history.go(-1);
        }
    });
}

$(document).ready(main);