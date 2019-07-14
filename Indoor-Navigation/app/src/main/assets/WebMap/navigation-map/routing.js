var routingUrl = 'http://121.42.25.186/routing.php';
var pointBufferSize = 12;
var routeBufferSize = 8;

var destJSON = null;
var destArray = new Array();

var geojsonFormat = new ol.format.GeoJSON();
var wktFormat = new ol.format.WKT();
var jstsWKTReader = new jsts.io.WKTReader();

// 标记样式计算函数
var markerStyleFunction = function(feature) {
	return new ol.style.Style({
		image: new ol.style.Icon({
			anchor: [0.5, 1],
			src: '../img/point_marker.png'
		}),
		text: new ol.style.Text({
			font: '12px helvetica, sans-serif',
			text: feature.get('name'),
			offsetY: -21,
			fill: new ol.style.Fill({
				color: '#000'
			}),
			stroke: new ol.style.Stroke({
				color: '#fff',
				width: 2
			})
		})
	});
}
// 路径样式
var vectorLayerStyle = new ol.style.Style({
	stroke: new ol.style.Stroke({
		color: 'green',
		width: 2
	})
});

// 各个楼层的矢量图层，用来存放路径等
var vectorLayerB1 = new ol.layer.Vector({
	source: new ol.source.Vector(),
	style: vectorLayerStyle
});
var vectorLayerF1 = new ol.layer.Vector({
	source: new ol.source.Vector(),
	style: vectorLayerStyle
});
var vectorLayerF2 = new ol.layer.Vector({
	source: new ol.source.Vector(),
	style: vectorLayerStyle
});

// 各个楼层的路径缓冲区数组
var routeBufferArrayB1 = new Array();
var routeBufferArrayF1 = new Array();
var routeBufferArrayF2 = new Array();
// 各个楼层的路径点的缓冲区数组
var pointBufferArrayB1 = new Array();
var pointBufferArrayF1 = new Array();
var pointBufferArrayF2 = new Array();

map.addLayer(vectorLayerB1);
map.addLayer(vectorLayerF1);
map.addLayer(vectorLayerF2);

function startRouting()
{
	$.post(routingUrl,
		{
			destJSON: destJSON
		},
		function (geojsonObjectArray)
		{
			// 绘制路径并生成缓冲区
			for (var i = 0; i < geojsonObjectArray.length; i++)
			{
				var features = geojsonFormat.readFeatures(geojsonObjectArray[i]);
				var jstsFeatures = jstsWKTReader.read(wktFormat.writeFeatures(features));
				var routeBuffer = jstsFeatures.buffer(routeBufferSize);
				switch (geojsonObjectArray[i].properties.z)
				{
					case "0":
						vectorLayerB1.getSource().addFeatures(features);
						routeBufferArrayB1.push(routeBuffer);
						break;
					case "1":
						vectorLayerF1.getSource().addFeatures(features);
						routeBufferArrayF1.push(routeBuffer);
						break;
					case "2":
						vectorLayerF2.getSource().addFeatures(features);
						routeBufferArrayF2.push(routeBuffer);
						break;
				}
			}
		},
		'json'
	);
}

function setDestJSON(json)
{
	destJSON = json;
	destArray = JSON.parse(destJSON);
	
	// 设置起点楼层为当前显示楼层
	setCurrentFloor(parseInt(destArray[0].z));
	
	var point, pointBuffer, markerFeature, markerStyle;
	// 以各路径点为中心生成缓冲区并添加标记
	for (var i = 0; i < destArray.length; i++)
	{
		// 生成缓冲区
		point = new jsts.geom.Point(new jsts.geom.Coordinate(destArray[i].x, destArray[i].y));
		pointBuffer = point.buffer(pointBufferSize);
		
		// 绘制标记
		markerFeature = new ol.Feature({
			geometry: new ol.geom.Point([parseFloat(destArray[i].x), parseFloat(destArray[i].y)]),
			name: (i + 1).toString()
		});
		markerStyle = markerStyleFunction(markerFeature);
		markerFeature.setStyle(markerStyle);
		switch (destArray[i].z)
		{
			case "0":
				pointBufferArrayB1.push(pointBuffer);
				vectorLayerB1.getSource().addFeature(markerFeature);
				break;
			case "1":
				pointBufferArrayF1.push(pointBuffer);
				vectorLayerF1.getSource().addFeature(markerFeature);
				break;
			case "2":
				pointBufferArrayF2.push(pointBuffer);
				vectorLayerF2.getSource().addFeature(markerFeature);
				break;
		}
	}
}