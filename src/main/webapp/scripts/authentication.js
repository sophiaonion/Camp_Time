//make sure user is logged in order to see the page

$("body").hide();
var main = function(){

    console.log("Test whether user logged in");
    $.ajax({
        type: 'GET',
        url: '/api/role',
        contentType: 'application/JSON',
        success: function(){
            console.log('success');
            $("body").show();
        },
        error: function(request, status, error){
            console.log(error);
            alert('You do not have permission!');
            window.location.href = history.go(-1);
        }
    });
}

var secondary = function (user){
    console.log("successfully get here!");
    $.ajax({
        type: 'GET',
        url: '/api/role',
        contentType: 'application/JSON',
        success: function(role){
            role=role.replace(/\s+/g, '');
            console.log(role);
            var parts = user.split(" ");
            var exist=false;
            for (i = 0; i < parts.length; i++) {
                console.log(parts[i]);
                if (role == parts[i])
                 {
                  console.log('success');
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