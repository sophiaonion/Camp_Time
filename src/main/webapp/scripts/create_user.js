var main = function(users){
    $('#create-user').on('click', function(){

    if ($('#password').val()!= $('#confirmation').val()){
        alert("Password inputs are not the same!")
        return;
    }

    var data = {
        username: $('#user-name').val(),
        password: $('#password').val(),
        usertype: "customers"
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

$(document).ready(function(){
       $.get('/api/users', function(users){
                main(users);
       });

});