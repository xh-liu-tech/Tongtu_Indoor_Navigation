var wmsUrl = 'http://121.42.25.186:8090/geoserver/IndoorMap/wms';
var currentFloor = 1;

window.app = {};
var app = window.app;

// 定义楼层选择按钮控件
app.btnB1Control = function(opt_options) {
	var options = opt_options || {};
	var btnB1 = document.createElement('button');
	btnB1.innerHTML = 'B1';
	
	var handleBtnB1 = function(e) {
		setCurrentFloor(0);
	}
	
	btnB1.addEventListener('click', handleBtnB1, false);
	btnB1.addEventListener('touchstart', handleBtnB1, false);
	
	var btnB1Element = document.createElement('div');
	btnB1Element.className = 'btnB1 ol-control ol-unselectable';
	btnB1Element.appendChild(btnB1);
	
	ol.control.Control.call(this, {
		element: btnB1Element,
		target: options.target
	});
};
ol.inherits(app.btnB1Control, ol.control.Control);

app.btnF1Control = function(opt_options) {
	var options = opt_options || {};
	var btnF1 = document.createElement('button');
	btnF1.innerHTML = 'F1';
	
	var handleBtnF1 = function(e) {
		setCurrentFloor(1);
	}
	
	btnF1.addEventListener('click', handleBtnF1, false);
	btnF1.addEventListener('touchstart', handleBtnF1, false);
	
	var btnF1Element = document.createElement('div');
	btnF1Element.className = 'btnF1 ol-control ol-unselectable';
	btnF1Element.appendChild(btnF1);
	
	ol.control.Control.call(this, {
		element: btnF1Element,
		target: options.target
	});
};
ol.inherits(app.btnF1Control, ol.control.Control);

app.btnF2Control = function(opt_options) {
	var options = opt_options || {};
	var btnF2 = document.createElement('button');
	btnF2.innerHTML = 'F2';
	
	var handleBtnF2 = function(e) {
		setCurrentFloor(2);
	}
	
	btnF2.addEventListener('click', handleBtnF2, false);
	btnF2.addEventListener('touchstart', handleBtnF2, false);
	
	var btnF2Element = document.createElement('div');
	btnF2Element.className = 'btnF2 ol-control ol-unselectable';
	btnF2Element.appendChild(btnF2);
	
	ol.control.Control.call(this, {
		element: btnF2Element,
		target: options.target
	});
};
ol.inherits(app.btnF2Control, ol.control.Control);

// 加载地图
var mapLayerB1 = new ol.layer.Image({
	visible: false,
	source: new ol.source.ImageWMS({
		ratio: 1,
		url: wmsUrl,
		params: {
			'FORMAT': 'image/png', 
			'VERSION': '1.1.1',
			LAYERS: 'IndoorMap:B1',
		}
	})
});
var mapLayerF1 = new ol.layer.Image({
	source: new ol.source.ImageWMS({
		ratio: 1,
		url: wmsUrl,
		params: {
			'FORMAT': 'image/png', 
			'VERSION': '1.1.1',
			LAYERS: 'IndoorMap:F1',
		}
	})
});
var mapLayerF2 = new ol.layer.Image({
	visible: false,
	source: new ol.source.ImageWMS({
		ratio: 1,
		url: wmsUrl,
		params: {
			'FORMAT': 'image/png', 
			'VERSION': '1.1.1',
			LAYERS: 'IndoorMap:F2',
		}
	})
});

// 投影坐标系
var projection = new ol.proj.Projection({
  code: 'EPSG:666666',
  units: 'm',
  axisOrientation: 'neu'
});
var map = new ol.Map({
	controls: ol.control.defaults({
		attributionOptions: ({
			collapsible: false
		})
	}).extend([
		new app.btnB1Control(),
		new app.btnF1Control(),
		new app.btnF2Control()
	]),
	target: 'map',
	layers: [mapLayerB1, mapLayerF1, mapLayerF2],
	view: new ol.View({
		extent: [0, 0, 520, 520],		// 限制拖动范围
		projection: projection,
		center: [260, 260],
		maxZoom: 20,
		minZoom: 16,
		zoom: 16.5,
	})
});
document.addEventListener('DOMContentLoaded', function() {
  FastClick.attach(document.body);
});

function setCurrentFloor(floor)
{
	currentFloor = floor;
	switch (currentFloor) {
		case 0:
			mapLayerB1.setVisible(true);
			mapLayerF1.setVisible(false);
			mapLayerF2.setVisible(false);
			break;
		case 1:
			mapLayerB1.setVisible(false);
			mapLayerF1.setVisible(true);
			mapLayerF2.setVisible(false);
			break;
		case 2:
			mapLayerB1.setVisible(false);
			mapLayerF1.setVisible(false);
			mapLayerF2.setVisible(true);
			break;
	}
}