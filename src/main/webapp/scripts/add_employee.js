var main = function() {
	console.log('hi');

	//dummy post -- 1st post call does not work on server due to Jackson error...but the rest do after that
	//$.post('/api/employees');

	$('#employee-info').submit(function(event){
		//stop form from submitting normally would refresh page and server can't read format since it isn't
		//JSON
		console.log('submitting employee');
		event.preventDefault();
		var data = 	JSON.stringify({
                   			'name': $('#name').val(),
                   			'age': $('#age').val(),
                   			'job': $('#job').val()
                   	});

		//to send request must use $.ajax to so content type can be specified as JSON
		//otherwise the Jackson on the server side will intermittently screw up
		console.log(data);
		$.ajax({
			//type: 'POST',
			url: '/api/employees',
			data: data,
			contentType: 'application/json'
		});
		$('#employee-info input').not('[type=submit]').val("");


	});
};

$(document).ready(main);
