<?php 
	
	if($_SERVER['REQUEST_METHOD']=='POST'){
		//Getting values 
		$num_scenario = $_POST['num_scenario'];
		$action = $_POST['action'];
		
		//Creating sql query
		$sql = "UPDATE `Bungalow` SET $num_scenario = $action WHERE `Bungalow`.`bun_id` = 1;";
		
		//importing dbConnect.php script
		require_once('dbConnect.php');
		
		//executing query
		$result = mysqli_query($con,$sql);
	
		mysqli_close($con);
		
	}
	
	?>