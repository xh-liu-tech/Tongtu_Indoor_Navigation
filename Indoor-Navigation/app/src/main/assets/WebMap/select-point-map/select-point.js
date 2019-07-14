var markerCount = 0;

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
};

// 存放选点标记的矢量图层
var vectorLayerB1 = new ol.layer.Vector({
	source: new ol.source.Vector()
});
var vectorLayerF1 = new ol.layer.Vector({
	source: new ol.source.Vector()
});
var vectorLayerF2 = new ol.layer.Vector({
	source: new ol.source.Vector()
});

map.addLayer(vectorLayerB1);
map.addLayer(vectorLayerF1);
map.addLayer(vectorLayerF2);

// 监听点击地图事件
map.on('click', function(evt) {
	var mapWidth = 520;
	var mapHeight = 520;
	if (currentFloor == 0)
		mapHeight = 197.5;
	var pos = map.getEventCoordinate(evt.originalEvent);
	if (pos[0] > 0 && pos[0] < mapWidth && pos[1] > 0 && pos[1] < mapHeight)
	{
		// 在地图上绘制标记
		var markerFeature = new ol.Feature({
			geometry: new ol.geom.Point(pos),
			name: (++markerCount).toString()
		});
		markerStyle = markerStyleFunction(markerFeature);
		markerFeature.setStyle(markerStyle);
		switch (currentFloor)
		{
			case 0:
				vectorLayerB1.getSource().addFeature(markerFeature);
				break;
			case 1:
				vectorLayerF1.getSource().addFeature(markerFeature);
				break;
			case 2:
				vectorLayerF2.getSource().addFeature(markerFeature);
				break;
		}
		
		// 在Android界面中添加地图选点信息
		window.AndroidWebView.addPointByTouchMap(markerCount.toString(), pos[0].toString(), pos[1].toString(), currentFloor.toString());
	}
});

// 删除在楼层floor上且编号为markerId的地图标记
function deleteMarker(floor, markerId)
{
	switch (floor)
	{
		case "0":
			var vectorSource = vectorLayerB1.getSource();
			break;
		case "1":
			var vectorSource = vectorLayerF1.getSource();
			break;
		case "2":
			var vectorSource = vectorLayerF2.getSource();
			break;
	}
	var features = vectorSource.getFeatures();
	for (var i = 0; i < features.length; i++)
	{
		if (features[i].get('name') == markerId)
		{
			vectorSource.removeFeature(features[i]);
			return;
		}
	}
}