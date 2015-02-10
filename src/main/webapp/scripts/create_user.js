var main = function(){
    $('#create-user').on('click', function(){

    if ($('#password').val()!= $('#confirmation').val()){
        alert("Password inputs are not the same!")
        return;
    }

    var data = {
        name: $('#user-name').val(),
        password: $('#password').val(),
        roles: ["admin"]
    };

    console.log(JSON.stringify(data));

    $.ajax({
        type: 'POST',
        url: '/api/users',
        data: JSON.stringify(data),
        contentType: 'application/JSON',
        success: function(data){
             alert('User Created.');
             window.location.replace('index.html');
        },
        error: function(request, status, error){
             alert(error);
        }
    });

    });
}

$(document).ready(main);