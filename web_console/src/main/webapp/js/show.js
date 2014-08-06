$('#example tbody').on('click', 'tr', function () {
	idMetric = table.row(this).data()[0];		
    if ( $(this).hasClass('selected') ) {
        $(this).removeClass('selected');	            
        rowSelected = false;
    }
    else {
        table.$('tr.selected').removeClass('selected');
        $(this).addClass('selected');
        rowSelected = true;
    }
});

$('#example tbody').on('dblclick', 'tr', function () {
    if ( $(this).hasClass('selected') ) {
        $(this).removeClass('selected');	            
        rowSelected = false;
    }
    else {
        table.$('tr.selected').removeClass('selected');
        $(this).addClass('selected');
        rowSelected = true;
        idMetric = table.row(this).data()[0];
        $(".main-content").load('console/updateMetric', {idMetric: idMetric} );
    }
});

function deleteMetric(){
	if (idMetric != null){
		if(rowSelected){
			$.ajax({ 						
			    url: "/web_console/console/deleteMetric", 
			    type: 'GET', 
			    data: {idMetric: idMetric},
			    success: function(data) { 
					$(".main-content").load('/web_console/console/show');
			    },
			    error:function(error) {
			    	$("#loadingDiv").hide();
			    	$("#errorDiv").show();
			    }			    
			});			
		}
		else{
			$("#warningDiv").show();
		}
	}
	else{
		$("#warningDiv").show();
	}
}

function modifyMetric(){
	if (idMetric != null){
		if(rowSelected){
			$(".main-content").load('console/updateMetric', {idMetric: idMetric} );
		}
		else{
			$("#warningDiv").show();
		}
	}
	else{
		$("#warningDiv").show();
	}
}

function refreshTable(){
	var txtSearch = $('input[type=search]').val();	
	$(".main-content").load('console/refresh', {search: txtSearch});
}		

function reLaunchMetric(idMetric){
	$.ajax({ 						
	    url: "/web_console/console/reLaunchMetric", 
	    type: 'GET', 
	    data: {idMetric: idMetric},
	    success: function(data) {
	    	$(".main-content").load('/web_console/console/show');
			$.ajax({ 						
			    url: "/web_console/console/insertIntoHive", 
			    type: 'GET', 
			    data: {idMetric: idMetric}
			});
	    },
	    error:function(error) {
	    	$("#loadingDiv").hide();
	    	$("#errorDiv").show();
	    }			    
	});
}