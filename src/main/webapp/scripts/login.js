var main = function(){
    $('#login').on('click', function(){
        console.log('login clicked');
    var username = $('#username').val();
    var password = $('#password').val()


        $.ajax({
            type: 'POST',
            url: '/api/login',
            contentType: 'application/JSON',
            data: JSON.stringify({
                name: username,
                password: password
                    })
            }).done(function(data, textStatus, jqXHR){//returns user with response as username
                console.log(data.name);
                if(data.name === 'pw'){
                    alert('Invalid password');
                    $('#password').val("").focus();
                } else if(data.name === 'usr'){
                    alert('Invalid username');
                    $('#password').val("");
                    $('#username').val("").focus();
                } else {
                    window.location.href = "home_page_test.html";

               }

            }); //end done handler
        }); //end login click handler

    $('#register').on('click', function(){
        window.location.href = "create_user.html";
    });

    };//end main



$(document).ready(main);