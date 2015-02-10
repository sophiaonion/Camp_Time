var button_handler = function(){
    console.log('button handler hi');
    $('button').not($('.confirm-buttons')).click(function(event){
        if(!$('.selected')) return;
        $('.activity-control').hide();
        $('.confirm-buttons').show();
        $('.edit-section').show();
    });


    $('#edit-employees').click(function(event){
        var activity = $('.selected').data('activity');
        if(!activity) return; //no activity selected
        //append employees of activity to working select element

        $('.edit-section').load('/scripts/sessionschedule/edit_employees.html');
        var working = $('.working-emp');
        if (activity.employees){//check if employees uninitialized
            activity.employees.forEach(function(employee){
                var nextEmp = $('<option>').val(employee).text(employee.name);
                working.append(nextEmp);
            });
        }



        //get available employees at time and append to available select
        console.log('activity time: ' + activity.time)
        var available = $('.available-emp');
        available.attr('caption', 'available');
        $.get('/api/employees/time/' + activity.time, function(employees){
            employees.forEach(function(emp){
                var nextEmp = $('<option>').val(emp).text(emp.name);
                available.append(nextEmp);
            });

        });
        


    });

    $('.cancel').click(function(event){
        $('.activity-control').show();
        $('.confirm-buttons').hide();
    });
};

$(document).ready(button_handler);