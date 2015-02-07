var main = function(employees){

    employees.forEach(function(value){
        var emp = $("<li>").text(value.name + " " + value.age);
        $(".employees").append(emp);
    });


};

$(document).ready(function(){
    $.get('/api/employees', function(result){
        console.log(result);
        main(result);
    });
});