var main = function(employees){
    var $okayBtn = $('<button>').text('Okay').addClass('okay-button');
    var $cancelBtn = $('<button>').text('Cancel').addClass('cancel-button');
    var $input = $('<input>').attr('type', 'text').addClass('new-info');
    var $confirmCancel = $('<div>').append($okayBtn).append($cancelBtn);

    $cancelBtn.on('click', function(event){
        $input.siblings(':hidden').show(); //show old info
        $confirmCancel.detach();
        $('custom-input').detach();
        $input.detach();
    });

    $('input').on('blur', function(event){
        $cancelBtn.trigger('click');
    });

    $okayBtn.on('click', function(event){
        $input.siblings(':hidden').show();
        $('.new-info').siblings('span').text($('.new-info').val());
        $input.detach();
        $('custom-input').detach();
        $confirmCancel.detach();
    });

    $('#edit-name').on('click', function(event){
        $input.off('keypress');
        $('#name').after($input.val($('#name').text()));
        $('#name').hide();
        $(this).hide();
        $input.focus();
        $input.after($confirmCancel);
        $input.on('keypress', function(event){
            var val = $input.val() + String.fromCharCode(event.keyCode)
            console.log(val);
            if (!val.match(/^[a-zA-Z]+$/)) event.preventDefault();
        });
    });

    $('#edit-age').on('click', function(event){
        $input.off('keypress');
        $('#age').after($input.val($('#age').text()));
        $('#age').hide();
        $(this).hide();
        $input.focus();
        $input.after($confirmCancel);
        $input.on('keypress', function(event){
            var val = $input.val() + String.fromCharCode(event.keyCode)
            console.log(val);
            if (!val.match(/^\d+$/)) event.preventDefault();
        });
    });

    $('#edit-interval').on('click', function(event){
        $input.off('keypress')
        $('#interval').after($input.val($('#interval').text()));
        $('#interval').hide();
        $input.focus();
        $(this).hide();
        $input.after($confirmCancel);
        $input.on('keypress', function(event){
            var val = $input.val() + String.fromCharCode(event.keyCode)
            if (!val.match(/^\d+$/)) event.preventDefault();
            if (val > 10){
                $input.val(10);
                event.preventDefault();
            }
            if (val < 0){
                $input.val(0);
                event.preventDefault();
            }
        });
    });


    $('#choose-first-break');

    var empObjects = employees.map(function(employee){
        return {
            label: employee.name,
            employee: employee
        };
    });

    console.log(empObjects);

    $('#employee-search').autocomplete({
        source: empObjects,
        autoSelect: true,
    });

    var setUpEdit = function setUpEdit(event, ui){
        console.log('setup edit called');
        $('.employee-edit').data('employee', ui);
        console.log(ui.item.employee);
        $('#name').text(ui.item.employee.name);
        $('#age').text(ui.item.employee.age);

        $('#certifications input').prop("checked", false);

        ui.item.employee.certifications.forEach(function(certification){
            var selectString = "#certifications input[value='%s']".replace(/%s/, certification);
            console.log(selectString);
            $(selectString).prop("checked", true);

        });
        var jobString = "#job input[value='%s']".replace(/%s/, ui.item.employee.job);
        $(jobString).prop("checked", true);

        $('#interval').text(ui.item.employee.intervalBreak);
        var dateString = ui.item.employee.startBreak.split("T")[0];
        console.log(dateString);
        $('#choose-first-break').val(dateString);
    };

    $( "#employee-search" ).on( "autocompleteselect", setUpEdit);

    $('#reset-edit').on('click', function(){
       var employee = $('.employee-edit').data('employee');//actually ui, but this way can just pass it in as if selected
       console.log(employee);
       setUpEdit(null, employee);
    });

    $('#submit-edit').on('click', function(){
        if(!$(".employee-edit").data('employee')) return;
        var updateEmp = getUpdatedInfo();
        $.ajax({
            url: "/api/employee/update",
            type: 'PUT',
            contentType: 'application/JSON',
            data: JSON.stringify(updateEmp)
        }).done(alert.bind(null, 'employee updated'));
    });

    var getUpdatedInfo = function(){
        var employee =  $(".employee-edit").data('employee').item.employee; //stored as ui object from autocomplete
        employee.name = $('#name').text();
        employee.age = $('#age').text();
        var certifications = [];
        $('#certifications input:checked').each(function(index){
            certifications.push($(this).val());
        });
        employee.certifications = certifications;
        employee.job = $('#job input:checked').val();
        employee.intervalBreak = $('#interval').text();
        employee.startBreak = $('#choose-first-break').val();
        console.log(employee);
        return employee;

    };


};

$(document).ready(function(){
    //get all employees then set up page functionality in main
    $.ajax({
        url: '/api/employees/all',
        type: 'GET',
        contentType: 'application/JSON'
    }).done(function(employees){
        main(employees);
    }).fail(alert.bind(null, 'failed to get employees'));
});;;