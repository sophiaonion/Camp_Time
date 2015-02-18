var button_handler = function(){
    console.log('button handler hi');
    $('button').not($('.confirm-buttons')).click(function(event){
        if(!$('.selected')) return;
        $('.activity-control').hide();
        $('.confirm-buttons').css('visibility', 'visible');
        $('.edit-section').show();
    });


    $('.available-emp option').each(function(){
    console.log($(this).text);
    console.log($(this).data('employee')._id);
    });

    $('#edit-employees').click(function(event){
        var activity = $('.selected').data('activity');
        if(!activity) return; //no activity selected
        //append employees of activity to working select element

        $('.edit-section').load('/scripts/sessionschedule/edit_employees.html');
        var working = $('.working-emp');
        console.log(working);
        if (activity.employees){//check if employees uninitialized
            activity.employees.forEach(function(employee){
                var nextEmp = $('<option>').text(employee.name).data('employee', employee);
                working.append(nextEmp);
            });
        }



        //get available employees at time and append to available select
        console.log('activity time: ' + activity.time)
        $.get('/api/employees/time/' + activity.time, function(employees){
            employees.forEach(function(emp){
                var nextEmp = $('<option>').text(emp.name).data('employee', emp);
                console.log(nextEmp.data('employee')._id);
                $('.available-emp').append(nextEmp);
            });

        });
        


    });

    $('.cancel').click(function(event){
        $('.activity-control').show();
        $('.confirm-buttons').hide();
    });
};

$(document).ready(button_handler);