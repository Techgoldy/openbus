$('#sourceId').click(function() {
	$('#idSource').val($('#sourceId').val());
	$('#selSourceName').val($( "#sourceId option:selected" ).text());
	$('#selFields').load('getFieldsBySource', {idSource: $('#idSource').val()} );
	$('#fromQuery').val("FROM " + $( "#sourceId option:selected" ).text());
});

/*
$('#selFields').change(function(){ 
	query += $('#selFields option:selected' ).text() + ' ';
	$('#selectQuery').val($('#selectQuery').val() + query);
});

$('input[type=radio][name=rdTimestamp]').change(function() {
	if($("#rdTimestampSi").is(':checked')){
		if($('#txtTimestamp').val() == ''){
			$("#txtTimestamp").attr('required', '');					
		}
		else{
			$("#txtTimestamp").removeAttr('required'); 
		}
	}
	else{
		$("#txtTimestamp").removeAttr('required'); 
	}
});
*/

function CreateForm(){			
	var createForm = this;
	createForm.batchMetricName = $('#batchMetricName').val();
	createForm.batchMetricDesc = $('#batchMetricDesc').val();
	createForm.hidModif = $('#hidModif').val();			
	createForm.rdMetricType = $('input[name=rdMetricType]:checked').val();
	createForm.sourceId = $('#sourceId').val()[0];
	createForm.typeQuery = $('textarea#typeQuery').val();
	createForm.fromQuery = $('textarea#fromQuery').val();
	createForm.selectQuery = $('textarea#selectQuery').val();
	createForm.whereQuery = $('textarea#whereQuery').val();
	createForm.esTimestamp = $('#esTimestamp').val();
	createForm.selSourceName = $('#selSourceName').val();
	createForm.error = null;
}

$("#createForm").submit(function() {
	$("#errorDiv").hide();
	var createForm = new CreateForm();
	$('#fromQuery').prop('disabled', false);
	$('#batchMetricName').prop('disabled', false);
	$('#batchMetricDesc').prop('disabled', false);
	$('#sourceId').prop('disabled', false);
	$('#selFields').prop('disabled', false);
	$('#rdMetricType').prop('disabled', false);
	$('#typeQuery').prop('disabled', false);
	$('#esTimestamp').prop('disabled', false);
	$("#loadingDiv").show();
	
	if ($("#hidModif").val() != 0){			
		$.ajax({ 
		    url: "/web_console/console/updateMetricBBDDES", 
		    type: 'POST', 
		    dataType: 'json', 
		    data: JSON.stringify(createForm), 
		    contentType: 'application/json',
		    mimeType: 'application/json',
		    success: function(data) { 
				if(data.id == 'ERROR'){
			    	$("#loadingDiv").hide();
			    	$("#errorDiv").show();
			    	document.getElementById('errorMessage').innerHTML = data.error;
					$('#fromQuery').prop('disabled', true);
					$('#rdMetricType').prop('disabled', true);
					$('#sourceId').prop('disabled', true);
				}
				else{
					$.ajax({ 						
					    url: "/web_console/console/insertIntoHiveRel", 
					    type: 'POST', 
					    dataType: 'json', 
					    data: JSON.stringify(createForm), 
					    contentType: 'application/json',
					    mimeType: 'application/json'				    
					});
			    	$(".main-content").load('/web_console/console/show');
				}
		    },
		    error:function(error) {
		    	document.getElementById('errorMessage').innerHTML = error;
		    	$("#loadingDiv").hide();
		    	$("#errorDiv").show();
		    }
		});
	}
	else{
		$.ajax({ 
		    url: "/web_console/console/createMetricBBDDES", 
		    type: 'POST', 
		    dataType: 'json', 
		    data: JSON.stringify(createForm), 
		    contentType: 'application/json',
		    mimeType: 'application/json',
		    success: function(data) { 
				if(data.id == 'ERROR'){
			    	$("#loadingDiv").hide();
			    	$("#errorDiv").show();
			    	document.getElementById('errorMessage').innerHTML = data.error;
				}
				else{
					$.ajax({ 						
					    url: "/web_console/console/insertIntoHive", 
					    type: 'GET', 
					    data: {idMetric: data.id}
					});
			    	$(".main-content").load('/web_console/console/show');
				}
		    },
		    error:function(error) {
		    	document.getElementById('errorMessage').innerHTML = error;
		    	$("#loadingDiv").hide();
		    	$("#errorDiv").show();
		    }
		});
	}
});

$('#imgTypeHelp').popover({
    html: true,
    content: function () {
        return $("#divTypeTooltipHelp").html();
    }
});
$('#imgSelectHelp').popover({
    html: true,
    content: function () {
        return $("#divSelectTooltipHelp").html();
    }
});
$('#imgWhereHelp').popover({
    html: true,
    content: function () {
        return $("#divWhereTooltipHelp").html();
    }
});
$('#imgTimestampHelp').popover({
    html: true,
    content: function () {
        return $("#divTimestampTooltipHelp").html();
    }
});

$(".close").click(function(){
      $("#errorDiv").hide();
      $("#warningDiv").hide();
      $("#messageDiv").hide();
});	
