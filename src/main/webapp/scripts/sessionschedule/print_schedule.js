var print_schedule = function(){

    $(function(){
         var doc = new jsPDF();
    var specialElementHandlers = {
        '#editor': function (element, renderer) {
            return true;
        }
    };

   $('#print').click(function () {

        var table = tableToJson();
        var doc = new jsPDF('landscape','pt', 'a4', true);
        doc.cellInitialize();
        for(var prop in table[0]){
            console.log(prop);

        }
        $.each(table, function (i, row){
            console.debug(row);
            $.each(row, function (j, cell){
                doc.cell(10, 50, 120, 100, cell, i);  // 2nd parameter=top margin,1st=left margin 3rd=row cell width 4th=Row height
            });
        });
//        doc.addJS('print(true)');
        doc.save('Test.pdf');
    });
    function tableToJson(table) {
        console.log(table);
        var data = [];

        // first row needs to be headers
        var headers = [];
        console.log('headers');
        $('#dates td').each(function(index){
            console.log($(this).text());
            headers.push($(this).text());
        });

        //go through cells
        console.log('row data');
        var empResponses = [];
        $('#schedule tr').each(function(index, element){
            //turn each cell into json object of 'header name prop': cell value

            var rowData = {}; //iterate through each cell of row
            $('td', this).each(function(index, element){
                //if activity get employee names of activity
                //otherwise if time just append
                if($('p', this).hasClass('activity')){
                    var activity = $('p', this).data('activity');
                    console.log('has activity');
                    rowData[headers[index]] = activity.title;
                    if(activity.employees.length){//get employee names from ids

                        $('.activity-info #num-employees').text(activity.employees.length);
                        //get employee objects working from ids of activity.employees array
                        var jqXHR = $.ajax({
                            url: '/api/employees/ids',
                            type: "PUT",
                            data: JSON.stringify({employee_ids: activity.employees}),
                            contentType: 'application/JSON',
                            async: false
                        }).done(function(employees, textStatus, jqXHR){
                              console.log('employees: ');
                              console.log(employees);

                              employees.forEach(function(emp, index, employees){
                                console.log('adding employee names');
                                console.log(emp.name);
                                if(index !== employees.length - 1){
                                    emp.name = emp.name + ', ';
                                }
                                rowData[headers[index]] += ',' + emp.name;
                              });

                        });
                        empResponses.push(jqXHR);
                    }
                } else { //just addtime to row data
                    console.log('adding time to table json');
                    rowData[headers[index]] = $(this).text();
                }

            });
        console.log(rowData);
        data.push(rowData);
        });//end row iteration
        console.log(data);
//        // go through cells stackover flow iteration example
//        for (var i=0; i<table.rows.length; i++) {
//
//            var tableRow = table.rows[i];
//            var rowData = {};
//
//            for (var j=0; j<tableRow.cells.length; j++) {
//
//                rowData[ headers[j] ] = tableRow.cells[j].innerHTML;
//
//            }
//
//            data.push(rowData);
//        }
        console.log('returning table');
        return data;
    }

});


};


$(document).ready(print_schedule);