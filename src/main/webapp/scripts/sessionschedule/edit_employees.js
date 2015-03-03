var edit_employees_setup = function() {
    //only string _id of each employee
    console.log("edit employees called");
    $('edit-employees .remove').off('click');
    $('edit-employees .add').off('click');
    $('#submit-emp-changes').off('click');
    $('#cancel-emp-changes').off('click');

    $('.edit-employees .remove').click(function(event){
        console.log('remove called');
        $('.working-emp option:selected').each(function(){
            console.log(this);
            var employee = $(this).data('employee');
            console.log('employee to remove');
            console.log(employee);
            var _id = employee._id;
            console.log(_id);
            $(this).detach();//detach from working container
            $('.available-emp').append($(this));
            $(".edit-employees option:selected").removeAttr("selected");
        });
    });

    $('.edit-employees .add').click(function(event){
        console.log('add clicked');
        $('.available-emp option:selected').each(function(){
            var _id = $(this).data('employee')._id;
            console.log('from add: ' + _id);
            $(this).detach();
            $('.working-emp').append($(this));

            $(".edit-employees option:selected").removeAttr("selected");
        });
    });


    $('#submit-emp-changes').click(function(event){
        var activity = $('.selected').data('activity');
        console.log('activity id' + activity._id);
        console.log(activity);
        var add_employees = [];
        $('.working-emp option').each(function(index){
            var employee = $(this).data('employee');
            if(activity.employees.indexOf(employee._id) === -1){
                add_employees.push(employee._id);
            }
        });
        $.ajax({
                type: 'PUT',
                url: '/api/employees/activities/add',
                data: JSON.stringify({
                    employees: add_employees,
                    activity_id: activity._id
                }),
                contentType: 'application/JSON'
                }).done(function(activity){
                    var activity = $('.selected').data('activity');
                    var remove_employees = [];
                    $('.available-emp option').each(function(index){
                        var employee = $(this).data('employee');
                        if(activity.employees.indexOf(employee._id) === -1){
                            remove_employees.push(employee._id);
                        }
                    });
                    $.ajax({
                        type: 'PUT',
                        url: '/api/employees/activities/remove',
                        data: JSON.stringify({
                            employees: remove_employees,
                            activity_id: activity._id
                        }),
                        contentType: 'application/JSON'
                        }).done(function(activity){
                            console.log(activity.name);
                            $('#session-select').trigger('change');
                            $('body').trigger('click');
                            $('.session-schedule').append($('<h4>').text("Successfully submitted changes").addClass('message'));
                            setTimeout(function(){
                                $('.message').remove();
                            }, 700);
                        });
                });
        }); //end employee click

    $('#cancel-emp-changes').click(function(event){
        $('table .selected').trigger('click');
    });

};


$(document).ready(edit_employees_setup);