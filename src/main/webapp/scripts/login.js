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
            console.log(data.roles);
            if(data.name === 'pw'){
                alert('Invalid password');
                $('#password').val("").focus();
            } else if(data.name === 'usr'){
                alert('Invalid username');
                $('#password').val("");
                $('#username').val("").focus();
            } else {
               // roles= data.roles;
               // key=roles[0];
               // var address;
               // if(key == "counselor"){
               //     console.log("first if");
               //     address = "home_page_counselor.html";}
               // if (key == "specialty"){
               //     console.log("second if");
               //     address = "home_page_specialty.html";}
               // if (key == "admin"){
               //     console.log("third if");
               //     address = "home_page_test.html";}
                window.location.href = "home_page_test.html";

           }

        }); //end done handler
        }); //end login click handler

    $('#register').on('click', function(){
        window.location.href = "create_user.html";
    });

    };//end main



$(document).ready(main);