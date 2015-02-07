var main = function(employees){

    var autocomplete_source = employees.map(function(employee){
        return {
            value: employee.name,
            ID: employee._id,
        };
    });


    var selected_employeeID;
    //get selected employee
    var uponSelect = function(event, ui){ //upon selection set employeeID to send when submitting
        //ui.item is selected item, has fields set in source
        selected_employeeID = ui.item.ID;
        console.log(ui.item.ID);
    };

    $('#employee-name').autocomplete({
        source: autocomplete_source, //set possible options
        autoFocus: true, //automatically select closest match
        select: uponSelect,
        messages: {//get rid of helper text
            noResults: '',
            results: function(){}//usually appends selected result text to bottom
        }
    });


    $('#submit-employee').on('click', function(){
        var data = {
            employeeID: selected_employeeID
        };
        console.log('submit clicked');
        console.log(selected_employeeID);

        $.ajax({
            type: 'DELETE',
            url: '/api/employees/' + data.employeeID,
            data: JSON.stringify(data),
            contentType: 'application/JSON',
            success: function(data){
                alert('Successfully deleted employee');
                window.location.replace('home_page_test.html');
            },
            error: function(request, status, error){
                alert(error);
            }
        });
    }); //end submit click handler

    $('#cancel').on('click', function(){
        window.location.replace('home_page_test.html');
    });
};

$(document).ready(function(){
       $.get('api/employees', function(employees){
            main(employees);
        });
});