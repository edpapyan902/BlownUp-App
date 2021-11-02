
$(document).ready(function () {
	$('#datatables').DataTable({
		"pagingType": "full_numbers",
		"lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
		responsive: true,
		language: {
			search: "_INPUT_",
			searchPlaceholder: "Search records",
		},
		"columnDefs": [{
			'targets': [0],
			'orderable': false,
		}]
	});

	// Edit record
	table.on('click', '.edit', function () {
		$tr = $(this).closest('tr');

		if ($tr.hasClass('child')) {
			$tr = $tr.prev('.parent');
		}

		var data = table.row($tr).data();
		alert('You press on Row: ' + data[0] + ' ' + data[1] + ' ' + data[2] + '\'s row.');
	});

	// Delete a record
	table.on('click', '.remove', function (e) {
		$tr = $(this).closest('tr');
		table.row($tr).remove().draw();
		e.preventDefault();
	});

	//Like record
	table.on('click', '.like', function () {
		alert('You clicked on Like button');
	});
});

