<?php
define("PG_DB"  , /* 此部分代码已隐藏 */);
define("PG_HOST", "localhost");
define("PG_USER", /* 此部分代码已隐藏 */);
define("PG_PASSWORD", /* 此部分代码已隐藏 */);
define("PG_PORT", /* 此部分代码已隐藏 */);
define("MAXDIS", 9999);
header("Access-Control-Allow-Origin: *");

$destJSON = $_POST["destJSON"];
$destArray = json_decode($destJSON, true);
$jsonArray = array();

for ($i = 0; $i < count($destArray) - 1; $i++)
{
	$startPoint = array($destArray[$i]["x"], $destArray[$i]["y"]);
	$endPoint = array($destArray[$i + 1]["x"], $destArray[$i + 1]["y"]);
	if ($destArray[$i]["z"] == $destArray[$i + 1]["z"])
	{
		array_push($jsonArray, routing($startPoint, $endPoint, $destArray[$i]["z"]));
	}
	else
	{
		// 读取电梯坐标，编号分别为1 2 3 4 7 8 5 6
		$liftArray = json_decode(file_get_contents("./LiftPoint.json"), true);
		
		if ($destArray[$i]["z"] == "0" || $destArray[$i + 1]["z"] == "0")
			// 起点或终点在B1时，只能使用南边的4个电梯（前4项）
			$availableLiftCount = 4;
		else if ($destArray[$i]["z"] == "2")
			// 起点在F2时，只能使用南边的4个电梯和北边的2个电梯（前6项）
			// 5号和6号电梯只能从公共区域到安全区域，即从F1到F2
			$availableLiftCount = 6;
		else
			$availableLiftCount = 8;
		
		// 找到距离起点最近的电梯
		$minDis = MAXDIS;
		for ($j = 0; $j < $availableLiftCount; $j++)
		{
			$dis = calcDistance($liftArray[$j], $startPoint);
			if ($dis < $minDis)
			{
				$minDis = $dis;
				$minDisIndex = $j;
			}
		}
		$liftPoint = $liftArray[$minDisIndex];
		
		// 分两段进行路径规划
		array_push($jsonArray, routing($startPoint, $liftPoint, $destArray[$i]["z"]));
		array_push($jsonArray, routing($liftPoint, $endPoint, $destArray[$i + 1]["z"]));
	}
}

echo json_encode($jsonArray);

function routing($startPoint, $endPoint, $z)
{
	switch ($z)
	{
		/* 此部分代码已隐藏 */
	}
	
	$startId = findNearestVertex($startPoint, $z);
	$endId = findNearestVertex($endPoint, $z);
	
	$pathSQL = /* 此部分代码已隐藏 */;
	$pathPointSQL = /* 此部分代码已隐藏 */;
	$con = pg_connect("dbname=".PG_DB." host=".PG_HOST." user=".PG_USER." password=".PG_PASSWORD);
	$pathQuery = pg_query($con, $pathSQL);
	
	$pathPointQuery = pg_query($con, $pathPointSQL);
	$pathEndPoint = array(pg_fetch_result($pathPointQuery, 0, 0), pg_fetch_result($pathPointQuery, 0, 1));
	
	$pathPointSQL = /* 此部分代码已隐藏 */;
	$pathPointQuery = pg_query($con, $pathPointSQL);
	$pathStartPoint = array(pg_fetch_result($pathPointQuery, 0, 0), pg_fetch_result($pathPointQuery, 0, 1));
	
	// Close database connection
	pg_close($con);
	
	// Return route as GeoJSON
	$geojson = array(
		'type' => 'FeatureCollection',
		'features' => array(),
		'properties' => array(
			'z' => $z
		)
	);

	// Add edges to GeoJSON array
	while($edge=pg_fetch_assoc($pathQuery)) {
		$feature = array(
			'type' => 'Feature',
			'geometry' => json_decode($edge['geojson'], true),
			'properties' => array(
				'length' => $edge['cost']
			)
		);
		// Add feature array to feature collection array
		array_push($geojson['features'], $feature);
	}
	
	if (calcDistance($pathStartPoint, $startPoint) > 0.001)
	{
		$feature = array(
			'type' => 'Feature',
			'geometry' => array(
				'type' => 'LineString',
				'coordinates' => array($pathStartPoint, $startPoint)
			),
			'properties' => array(
				'length' => number_format(calcDistance($pathStartPoint, $startPoint), 10)
			)
		);
		array_unshift($geojson['features'], $feature);
	}
	
	if (calcDistance($pathEndPoint, $endPoint) > 0.001)
	{
		$feature = array(
			'type' => 'Feature',
			'geometry' => array(
				'type' => 'LineString',
				'coordinates' => array($pathEndPoint, $endPoint)
			),
			'properties' => array(
				'length' => number_format(calcDistance($pathEndPoint, $endPoint), 10)
			)
		);
		array_push($geojson['features'], $feature);
	}

	return $geojson;
}

function findNearestVertex($point, $z)
{
	switch ($z)
	{
		/* 此部分代码已隐藏 */
	}
	$con = pg_connect("dbname=".PG_DB." host=".PG_HOST." user=".PG_USER." password=".PG_PASSWORD);
	$sql = /* 此部分代码已隐藏 */;
	$query = pg_query($con, $sql);
	pg_close($con);
	return pg_fetch_result($query, 0, 0);
}

function calcDistance($pointA, $pointB)
{
	return sqrt(pow($pointA[0] - $pointB[0], 2) + pow($pointA[1] - $pointB[1], 2));
}

?>