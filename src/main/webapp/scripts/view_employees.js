var main = function(employees){

    employees.forEach(function(value){
        var emp = $("<li>").text(value.name + " " + value.age);
        $(".employees").append(emp);
    });

     $('#cancel').on('click', function(){
        window.location.replace('home_page_test.html');
    });


};

$(document).ready(function(){
    $.get('/api/employees', function(result){
        console.log(result);
        main(result);
    });
});