var geolocationUrl = 'http://121.42.25.186:8001/loc';
var enableRerouting = false;

var deviceOrientation = new ol.DeviceOrientation();

// Geolocation marker
var iconFeature = new ol.Feature({
	geometry: new ol.geom.Point([0, 0]),
});
var iconStyle = new ol.style.Style({
	image: new ol.style.Icon({
		anchor: [0.5, 25],
		anchorYUnits: 'pixels',
		src: '../img/geolocation_marker_heading.png'
	})
});
iconFeature.setStyle(iconStyle);
var iconSource = new ol.source.Vector({
  features: [iconFeature]
});
var iconLayer = new ol.layer.Vector({
  source: iconSource
});
map.addLayer(iconLayer);

// tracking heading
deviceOrientation.setTracking(true);
deviceOrientation.on('change:heading', function(event)
{
	var heading = deviceOrientation.getHeading() || 0;
	iconFeature.getStyle().getImage().setRotation(2 * Math.PI - heading);
});

function geolocation()
{
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function()
	{
		if (xmlhttp.readyState == 4 && xmlhttp.status == 200)
		{
			// 设置当前位置
			var pos = JSON.parse(xmlhttp.responseText);
			iconFeature.getGeometry().setCoordinates([pos[0] * 520, pos[1] * 520]);
			if (pos[2] == currentFloor)
				iconLayer.setVisible(true);
			else
				iconLayer.setVisible(false);
			
			// 纠偏功能被启用
			if (enableRerouting)
			{
				// 检测是否偏离路线
				var intersected = false;
				var jstsLocPoint = new jsts.geom.Point(new jsts.geom.Coordinate(pos[0] * 520, pos[1] * 520));
				switch (pos[2])
				{
					case 0:
						intersected = checkRouteBuffer(routeBufferArrayB1 || [], jstsLocPoint);
						break;
					case 1:
						intersected = checkRouteBuffer(routeBufferArrayF1 || [], jstsLocPoint);
						break;
					case 2:
						intersected = checkRouteBuffer(routeBufferArrayF2 || [], jstsLocPoint);
						break;
				}
				if (!intersected)
				{
					// 当前位置偏离路线，重新规划路径
					
					// 清空矢量图层及缓冲区数组
					vectorLayerB1.getSource().clear();
					vectorLayerF1.getSource().clear();
					vectorLayerF2.getSource().clear();
					routeBufferArrayB1 = new Array();
					routeBufferArrayF1 = new Array();
					routeBufferArrayF2 = new Array();
					pointBufferArrayB1 = new Array();
					pointBufferArrayF1 = new Array();
					pointBufferArrayF2 = new Array();
					
					// 将当前位置作为起点
					locPointObject = {
						x: (pos[0] * 520).toString(),
						y: (pos[1] * 520).toString(),
						z: pos[2].toString()
					};
					destArray.unshift(locPointObject);
					if (destArray.length <= 1)
						enableRerouting = false;
					setDestJSON(JSON.stringify(destArray));
					startRouting();
				}
				
				// 移除已经到达的路径点
				intersected = false;
				switch (pos[2])
				{
					case 0:
						intersected = checkPointBuffer(pointBufferArrayB1 || [], jstsLocPoint);
						break;
					case 1:
						intersected = checkPointBuffer(pointBufferArrayF1 || [], jstsLocPoint);
						break;
					case 2:
						intersected = checkPointBuffer(pointBufferArrayF2 || [], jstsLocPoint);
						break;
				}
				if (intersected)
				{
					destArray.shift();
				}
			}
		}
	}
	xmlhttp.open("GET", geolocationUrl, true);
	xmlhttp.send();
	var t = setTimeout("geolocation();", 1000);
}

// 检查点是否与路径缓冲区相交
function checkRouteBuffer(routeBufferArray, point)
{
	for (var i = 0; i < routeBufferArray.length; i++)
	{
		if (routeBufferArray[i].intersects(point))
			return true;
	}
	return false;
}

// 检查点是否与路径点缓冲区相交，并移除该缓冲区
function checkPointBuffer(pointBufferArray, point)
{
	for (var i = 0; i < pointBufferArray.length; i++)
	{
		if (pointBufferArray[i].intersects(point))
		{
			pointBufferArray.splice(i, 1);
			return true;
		}
	}
	return false;
}

function setGeolocationUrl(url)
{
	// start geolocation
	geolocationUrl = url;
	geolocation();
}

function setEnableRerouting(enableRerouting_t)
{
	enableRerouting = enableRerouting_t;
}