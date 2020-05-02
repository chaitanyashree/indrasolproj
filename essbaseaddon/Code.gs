/** Essbase Plugin */
/** v22 **/

var IS_LOGGEDIN = false;
// Set a property in each of the three property stores.
var scriptProperties = PropertiesService.getScriptProperties();
var userProperties = PropertiesService.getUserProperties();
var documentProperties = PropertiesService.getDocumentProperties();
var SERVICE_BASE_URL='http://35.184.51.106:8080/essbase/';
scriptProperties.setProperty('SERVICE_BASE_URL', SERVICE_BASE_URL);

var PROP_NAME = "lastSheetIdx";

function didSwitchSheets(from, to) {
  Logger.log("Switched from " + from + " to " + to);
  //SpreadsheetApp.getActiveSheet().appendRow([new Date(), "Switched from " + from + " to " + to]);
  SpreadsheetApp.getActive().getActiveCell().setValue([new Date(), "Switched from " + from + " to " + to]);
}

function timedEventHandler() {
  var currentSheetIdx = SpreadsheetApp.getActiveSheet().getSheetId()
  var previousSheetIdx = parseInt(userProperties.getProperty(PROP_NAME));
  if (currentSheetIdx !== previousSheetIdx) {
    didSwitchSheets(previousSheetIdx, currentSheetIdx);
    userProperties.setProperty(PROP_NAME, currentSheetIdx);
  }
}


function onInstall(e) {
  onOpen(e);
}
function onOpen(e) {

    Logger.log('onOpen ');
    SpreadsheetApp.getUi() // Or DocumentApp or SlidesApp or FormApp.
    //.createMenu('Essbase Connector')
    .createAddonMenu()
    .addItem('Essbase Setup', 'showSidebar')
    .addToUi();
  Logger.log('inside onopen showSidebar');


}

function isLoggedIn() {
  // store in the session storage  selected db and sheetid
  var currentSpreadSheetId = SpreadsheetApp.getActiveSpreadsheet().getId();
  var currentSheetId = SpreadsheetApp.getActive().getActiveSheet().getSheetId();
  var returnData = {
    "currentSpreadSheetId" : currentSpreadSheetId,
    "currentSheetId": currentSheetId
  };

  var isLoggedIn = documentProperties.getProperty('isLoggedIn');
  if(isLoggedIn == 'TRUE') {
    returnData.isLoggedIn = true;
    return returnData;
  } else {
    returnData.isLoggedIn = false;
    return returnData;
  }
}


function showSidebar() {
  var html = HtmlService.createHtmlOutputFromFile('EssbasePage')
    .setTitle('Essbase Connect')
    .setWidth(300);
  SpreadsheetApp.getUi() // Or DocumentApp or SlidesApp or FormApp.
    .showSidebar(html);
  Logger.log('showsidebar...');
}

//var SERVICE_BASE_URL = SERVICE_BASE_URL+'hello';

function makeConnectCall(connecturl, olapServerName, userName, password) {
  // Make a POST request with form data.
  // alert('makeconnectcall');
  try {


    Logger.log('makeConnectCall...');
    Logger.log("connecturl" + connecturl);
    Logger.log("olapServerName" + olapServerName);
    Logger.log("userName" + userName);
    Logger.log("password" + password);

    // Make a POST request with a JSON payload.
    var str = 'Bob Smith';

    var data = {
      "userName": userName,
      "password": password,
      "olapServerName": olapServerName,
      "url": connecturl
    };
    var options = {
      'method': 'post',
      'contentType': 'application/json',
      'payload': JSON.stringify(data)
    };
    //var response = UrlFetchApp.fetch(SERVICE_BASE_URL+'login', options);
    var response = UrlFetchApp.fetch(SERVICE_BASE_URL+'login', options);
    // Logger.log(response.getContentText());
    Logger.log(response);

    documentProperties.setProperties(data);
    documentProperties.setProperty('isLoggedIn', 'TRUE');
    
    Logger.log('done with call..');
    
    return response.getContentText();
  } catch (error) {
    showErrorDialog(error);
    throw error;
  }
}

function makeLoadCall() {
  Logger.log('makeLoadCall....');
  //var response = UrlFetchApp.fetch(SERVICE_BASE_URL+'applications');
  var response = UrlFetchApp.fetch(SERVICE_BASE_URL+'applications');
  //SpreadsheetApp.getActive().getActiveCell().setValue(response.getContentText());
  return response.getContentText();

}

function makeZoomInNextCall(selectedCube, sMetaDataGrid) {
  Logger.log('makeZoomInCall....');
  selectedCube = getActiveSheetSelectedCube();
  sMetaDataGrid = getActiveSheetMetaDataGrid();
  var range = SpreadsheetApp.getActive().getDataRange();
  var totalCols = range.getNumColumns();
  var totalRows = range.getNumRows();
  var dataGridValues = range.getDisplayValues();
  // var devMetaFinder = SpreadsheetApp.getActive().getActiveSheet().createDeveloperMetadataFinder();

  // //var developerMetaData = SpreadsheetApp.getActive().getActiveSheet().getDeveloperMetadata();
  // var developerMetaData =  devMetaFinder.withKey('metaDataGrid').find();
  // Logger.log(developerMetaData[0].getKey()+'range=>'+developerMetaData[0].getValue());
  // console.log(developerMetaData[0].getKey()+'range=>'+developerMetaData[0].getValue());

  var data = {
    "totalRows": totalRows,
    "totalCols": totalCols,
    "dataGrid": dataGridValues,
    "dataGridMetaData": JSON.parse(sMetaDataGrid)
  };
  Logger.log(JSON.stringify(data));
  var options = {
    'method': 'post',
    'contentType': 'application/json',
    'payload': JSON.stringify(data)
  };
  var selCel = SpreadsheetApp.getActive().getCurrentCell();
  var selCelRow = selCel.getRow();
  var selCelCol = selCel.getColumn();
  if (selCelRow > 0) {
    selCelRow = selCelRow - 1;
  }
  if (selCelCol > 0) {
    selCelCol = selCelCol - 1;
  }
  Logger.log('activecell - ' + selCelRow + "\t" + selCelCol);

  var zoomUrl = SERVICE_BASE_URL+'applications/' + selectedCube + '/zoomIn/' + selCelRow + '/' + selCelCol;

  var response = UrlFetchApp.fetch(zoomUrl, options);
  //SpreadsheetApp.getActive().getActiveCell().setValue(response.getContentText());
  var resstr = response.getContentText();
  var jsonObj = JSON.parse(resstr);
  var totalRowNum = 0;
  var totalColNum = 0;
  var dataGrid;
  var metaDataGrid;

  if (jsonObj) {
    totalRowNum = jsonObj.totalRows;
    totalColNum = jsonObj.totalCols;
    dataGrid = jsonObj.dataGrid;
    metaDataGrid = JSON.stringify(jsonObj.dataGridMetaData);

  }
  Logger.log('totalRowNum=' + totalRowNum);
  Logger.log('totalColNUm=' + totalColNum);
  if(totalRowNum>0) {
    SpreadsheetApp.getActive().getActiveSheet().getRange(1, 1, totalRowNum, totalColNum).setValues(dataGrid);
    setActiveSheetMetaDataGrid(metaDataGrid);
    setActiveSheetSelectedCube(selectedCube);
  }
  
  //SpreadsheetApp.getActive().getActiveSheet().addDeveloperMetadata('metaDataGrid',metaDataGrid);
  return response.getContentText();

}

function makeZoomInBottomCall(selectedCube, sMetaDataGrid) {
  Logger.log('makeZoomInBottomCall....');
  selectedCube = getActiveSheetSelectedCube();
  sMetaDataGrid = getActiveSheetMetaDataGrid();
  var range = SpreadsheetApp.getActive().getDataRange();
  var totalCols = range.getNumColumns();
  var totalRows = range.getNumRows();
  var dataGridValues = range.getDisplayValues();


  // var devMetaFinder = SpreadsheetApp.getActive().getActiveSheet().createDeveloperMetadataFinder();

  // //var developerMetaData = SpreadsheetApp.getActive().getActiveSheet().getDeveloperMetadata();
  // var developerMetaData =  devMetaFinder.withKey('metaDataGrid').find();
  // Logger.log(developerMetaData[0].getKey()+' range=>'+developerMetaData[0].getValue());
  // console.log(developerMetaData[0].getKey()+' range=>'+developerMetaData[0].getValue());

  var data = {
    "totalRows": totalRows,
    "totalCols": totalCols,
    "dataGrid": dataGridValues,
    "dataGridMetaData": JSON.parse(sMetaDataGrid)
  };
  Logger.log(JSON.stringify(data));
  var options = {
    'method': 'post',
    'contentType': 'application/json',
    'payload': JSON.stringify(data)
  };
  var selCel = SpreadsheetApp.getActive().getCurrentCell();
  var selCelRow = selCel.getRow();
  var selCelCol = selCel.getColumn();
  if (selCelRow > 0) {
    selCelRow = selCelRow - 1;
  }
  if (selCelCol > 0) {
    selCelCol = selCelCol - 1;
  }
  Logger.log('activecell - ' + selCelRow + "\t" + selCelCol);

  var zoomUrl = SERVICE_BASE_URL+'applications/' + selectedCube + '/zoomInBottom/' + selCelRow + '/' + selCelCol;

  var response = UrlFetchApp.fetch(zoomUrl, options);
  //SpreadsheetApp.getActive().getActiveCell().setValue(response.getContentText());
  var resstr = response.getContentText();
  var jsonObj = JSON.parse(resstr);
  var totalRowNum = 0;
  var totalColNum = 0;
  var dataGrid;
  var metaDataGrid;

  if (jsonObj) {
    totalRowNum = jsonObj.totalRows;
    totalColNum = jsonObj.totalCols;
    dataGrid = jsonObj.dataGrid;
    metaDataGrid = JSON.stringify(jsonObj.dataGridMetaData);
  }
  Logger.log('totalRowNum=' + totalRowNum);
  Logger.log('totalColNUm=' + totalColNum);
  if(totalRowNum > 0){
    SpreadsheetApp.getActive().getActiveSheet().getRange(1, 1, totalRowNum, totalColNum).setValues(dataGrid);
    setActiveSheetMetaDataGrid(metaDataGrid);
    setActiveSheetSelectedCube(selectedCube);
  }
  //SpreadsheetApp.getActive().getActiveSheet().addDeveloperMetadata('metaDataGrid',metaDataGrid);
  return response.getContentText();

}

function makeZoomInAllCall(selectedCube, sMetaDataGrid) {
  Logger.log('makeZoomInAllCall....');
  selectedCube = getActiveSheetSelectedCube();
  sMetaDataGrid = getActiveSheetMetaDataGrid();
  var range = SpreadsheetApp.getActive().getDataRange();
  var totalCols = range.getNumColumns();
  var totalRows = range.getNumRows();
  var dataGridValues = range.getDisplayValues();

  // var devMetaFinder = SpreadsheetApp.getActive().getActiveSheet().createDeveloperMetadataFinder();

  // //var developerMetaData = SpreadsheetApp.getActive().getActiveSheet().getDeveloperMetadata();
  // var developerMetaData =  devMetaFinder.withKey('metaDataGrid').find();
  // Logger.log(developerMetaData[0].getKey()+'range=>'+developerMetaData[0].getValue());
  // console.log(developerMetaData[0].getKey()+'range=>'+developerMetaData[0].getValue());


  var data = {
    "totalRows": totalRows,
    "totalCols": totalCols,
    "dataGrid": dataGridValues,
    "dataGridMetaData": JSON.parse(sMetaDataGrid)
  };
  Logger.log(JSON.stringify(data));
  var options = {
    'method': 'post',
    'contentType': 'application/json',
    'payload': JSON.stringify(data)
  };
  var selCel = SpreadsheetApp.getActive().getCurrentCell();
  var selCelRow = selCel.getRow();
  var selCelCol = selCel.getColumn();
  if (selCelRow > 0) {
    selCelRow = selCelRow - 1;
  }
  if (selCelCol > 0) {
    selCelCol = selCelCol - 1;
  }
  Logger.log('activecell - ' + selCelRow + "\t" + selCelCol);

  var zoomUrl = SERVICE_BASE_URL+'applications/' + selectedCube + '/zoomInAll/' + selCelRow + '/' + selCelCol;

  var response = UrlFetchApp.fetch(zoomUrl, options);
  //SpreadsheetApp.getActive().getActiveCell().setValue(response.getContentText());
  var resstr = response.getContentText();
  var jsonObj = JSON.parse(resstr);
  var totalRowNum = 0;
  var totalColNum = 0;
  var dataGrid;
  var metaDataGrid;

  if (jsonObj) {
    totalRowNum = jsonObj.totalRows;
    totalColNum = jsonObj.totalCols;
    dataGrid = jsonObj.dataGrid;
    metaDataGrid = JSON.stringify(jsonObj.dataGridMetaData);
  }
  Logger.log('totalRowNum=' + totalRowNum);
  Logger.log('totalColNUm=' + totalColNum);
  Logger.log('resstr=' + resstr);
  Logger.log('metaDataGrid.length=' + metaDataGrid.length);
  if(totalRowNum > 0) {
    SpreadsheetApp.getActive().getActiveSheet().getRange(1, 1, totalRowNum, totalColNum).setValues(dataGrid);
    setActiveSheetMetaDataGrid(metaDataGrid);
    setActiveSheetSelectedCube(selectedCube);
  }
  
  //SpreadsheetApp.getActive().getActiveSheet().addDeveloperMetadata('metaDataGrid',metaDataGrid);
  return response.getContentText();

}

function makeZoomOutCall(selectedCube, sMetaDataGrid) {
  Logger.log('makeZoomOutCall....');
  selectedCube = getActiveSheetSelectedCube();
  sMetaDataGrid = getActiveSheetMetaDataGrid();
  var range = SpreadsheetApp.getActive().getDataRange();
  var totalCols = range.getNumColumns();
  var totalRows = range.getNumRows();
  var dataGridValues = range.getDisplayValues();

  // var devMetaFinder = SpreadsheetApp.getActive().getActiveSheet().createDeveloperMetadataFinder();

  // //var developerMetaData = SpreadsheetApp.getActive().getActiveSheet().getDeveloperMetadata();
  // var developerMetaData =  devMetaFinder.withKey('metaDataGrid').find();
  // Logger.log(developerMetaData[0].getKey()+'range=>'+developerMetaData[0].getValue());
  // console.log(developerMetaData[0].getKey()+'range=>'+developerMetaData[0].getValue());


  var data = {
    "totalRows": totalRows,
    "totalCols": totalCols,
    "dataGrid": dataGridValues,
    "dataGridMetaData": JSON.parse(sMetaDataGrid)
  };
  Logger.log(JSON.stringify(data));
  var options = {
    'method': 'post',
    'contentType': 'application/json',
    'payload': JSON.stringify(data)
  };
  var selCel = SpreadsheetApp.getActive().getCurrentCell();
  var selCelRow = selCel.getRow();
  var selCelCol = selCel.getColumn();
  if (selCelRow > 0) {
    selCelRow = selCelRow - 1;
  }
  if (selCelCol > 0) {
    selCelCol = selCelCol - 1;
  }
  Logger.log('activecell - ' + selCelRow + "\t" + selCelCol);

  var zoomUrl = SERVICE_BASE_URL+'applications/' + selectedCube + '/zoomOut/' + selCelRow + '/' + selCelCol;

  var response = UrlFetchApp.fetch(zoomUrl, options);
  //SpreadsheetApp.getActive().getActiveCell().setValue(response.getContentText());
  var resstr = response.getContentText();
  var jsonObj = JSON.parse(resstr);
  var totalRowNum = 0;
  var totalColNum = 0;
  var dataGrid;
  var metaDataGrid;

  if (jsonObj) {
    totalRowNum = jsonObj.totalRows;
    totalColNum = jsonObj.totalCols;
    dataGrid = jsonObj.dataGrid;
    metaDataGrid = JSON.stringify(jsonObj.dataGridMetaData);
  }
  Logger.log('totalRowNum=' + totalRowNum);
  Logger.log('totalColNUm=' + totalColNum);
  Logger.log('metaDataGrid.length=' + metaDataGrid.length);
  Logger.log('received data =' + JSON.stringify(jsonObj));
  SpreadsheetApp.getActive().getActiveSheet().getDataRange().clear();
  if(totalRowNum>0){
    SpreadsheetApp.getActive().getActiveSheet().getRange(1, 1, totalRowNum, totalColNum).setValues(dataGrid);
    setActiveSheetMetaDataGrid(metaDataGrid);
    setActiveSheetSelectedCube(selectedCube);
  }
  
  //  SpreadsheetApp.getActive().getActiveSheet().addDeveloperMetadata('metaDataGrid',metaDataGrid);
  return response.getContentText();

}

function makeRefreshCall(selectedCube, sMetaDataGrid) {
  Logger.log('makeRefreshCall....');
  var range = SpreadsheetApp.getActive().getDataRange();
  var totalCols = range.getNumColumns();
  var totalRows = range.getNumRows();
  var dataGridValues = range.getDisplayValues();

  // var devMetaFinder = SpreadsheetApp.getActive().getActiveSheet().createDeveloperMetadataFinder();

  // //var developerMetaData = SpreadsheetApp.getActive().getActiveSheet().getDeveloperMetadata();
  // var developerMetaData =  devMetaFinder.withKey('metaDataGrid').find();
  // Logger.log(developerMetaData[0].getKey()+'range=>'+developerMetaData[0].getValue());
  // console.log(developerMetaData[0].getKey()+'range=>'+developerMetaData[0].getValue());
  selectedCube = getActiveSheetSelectedCube();
  sMetaDataGrid = getActiveSheetMetaDataGrid();
  Logger.log('selectedCube=>'+selectedCube);
  Logger.log('sMetaDataGrid=>'+sMetaDataGrid);

  var data = {
    "totalRows": totalRows,
    "totalCols": totalCols,
    "dataGrid": dataGridValues,
    "dataGridMetaData": JSON.parse(sMetaDataGrid)
  };

  Logger.log(JSON.stringify(data));
  var options = {
    'method': 'post',
    'contentType': 'application/json',
    'payload': JSON.stringify(data)
  };

  var selCel = SpreadsheetApp.getActive().getCurrentCell();
  var selCelRow = selCel.getRow();
  var selCelCol = selCel.getColumn();
  if (selCelRow > 0) {
    selCelRow = selCelRow - 1;
  }
  if (selCelCol > 0) {
    selCelCol = selCelCol - 1;
  }
  Logger.log('activecell - ' + selCelRow + "\t" + selCelCol);

  var zoomUrl = SERVICE_BASE_URL+'applications/' + selectedCube + '/refresh';

  var response = UrlFetchApp.fetch(zoomUrl, options);
  //SpreadsheetApp.getActive().getActiveCell().setValue(response.getContentText());
  var resstr = response.getContentText();
  var jsonObj = JSON.parse(resstr);
  var totalRowNum = 0;
  var totalColNum = 0;
  var dataGrid;
  var metaDataGrid;

  if (jsonObj) {
    totalRowNum = jsonObj.totalRows;
    totalColNum = jsonObj.totalCols;
    dataGrid = jsonObj.dataGrid;
    metaDataGrid = JSON.stringify(jsonObj.dataGridMetaData);
  }
  Logger.log('totalRowNum=' + totalRowNum);
  Logger.log('totalColNUm=' + totalColNum);
  Logger.log('metaDataGrid.length=' + metaDataGrid.length);
  Logger.log('received data =' + JSON.stringify(jsonObj));
  SpreadsheetApp.getActive().getActiveSheet().getDataRange().clear();
  if(totalRowNum > 0) {
    SpreadsheetApp.getActive().getActiveSheet().getRange(1, 1, totalRowNum, totalColNum).setValues(dataGrid);
    setActiveSheetMetaDataGrid(metaDataGrid);
    setActiveSheetSelectedCube(selectedCube);
  }
  
  //  SpreadsheetApp.getActive().getActiveSheet().addDeveloperMetadata('metaDataGrid',metaDataGrid);
  return response.getContentText();

}

function makePivotCall(selectedCube, sMetaDataGrid) {
  try {
  Logger.log('makePivotCall....');
  selectedCube = getActiveSheetSelectedCube();
  sMetaDataGrid = getActiveSheetMetaDataGrid();
  var range = SpreadsheetApp.getActive().getDataRange();
  var totalCols = range.getNumColumns();
  var totalRows = range.getNumRows();
  var dataGridValues = range.getDisplayValues();


  var data = {
    "totalRows": totalRows,
    "totalCols": totalCols,
    "dataGrid": dataGridValues,
    "dataGridMetaData": JSON.parse(sMetaDataGrid)
  };

  Logger.log(JSON.stringify(data));
  var options = {
    'method': 'post',
    'contentType': 'application/json',
    'payload': JSON.stringify(data)
  };

  var selCel = SpreadsheetApp.getActive().getCurrentCell();
  var selCelRow = selCel.getRow();
  var selCelCol = selCel.getColumn();
  if (selCelRow > 0) {
    selCelRow = selCelRow - 1;
  }
  if (selCelCol > 0) {
    selCelCol = selCelCol - 1;
  }
  Logger.log('activecell - ' + selCelRow + "\t" + selCelCol);

  var zoomUrl = SERVICE_BASE_URL+'applications/' + selectedCube + '/pivot/' + selCelRow + '/' + selCelCol;

  var response = UrlFetchApp.fetch(zoomUrl, options);
  if(response.getResponseCode() == 500) {
    var resstr = response.getContentText();
    var jsonObj = JSON.parse(resstr);
    showErrorDialog(response.getContentText());
    //throw new Error('got error');
  }
  //SpreadsheetApp.getActive().getActiveCell().setValue(response.getContentText());
  var resstr = response.getContentText();
  var jsonObj = JSON.parse(resstr);
  var totalRowNum = 0;
  var totalColNum = 0;
  var dataGrid;
  var metaDataGrid;

  if (jsonObj) {
    totalRowNum = jsonObj.totalRows;
    totalColNum = jsonObj.totalCols;
    dataGrid = jsonObj.dataGrid;
    metaDataGrid = JSON.stringify(jsonObj.dataGridMetaData);
  }
  Logger.log('totalRowNum=' + totalRowNum);
  Logger.log('totalColNUm=' + totalColNum);
  Logger.log('metaDataGrid.length=' + metaDataGrid.length);
  Logger.log('received data =' + JSON.stringify(jsonObj));
  SpreadsheetApp.getActive().getActiveSheet().getDataRange().clear();
  if(totalRowNum > 0) {
    SpreadsheetApp.getActive().getActiveSheet().getRange(1, 1, totalRowNum, totalColNum).setValues(dataGrid);
    setActiveSheetSelectedCube(selectedCube);
    setActiveSheetMetaDataGrid(metaDataGrid);

  }
  
  //  SpreadsheetApp.getActive().getActiveSheet().addDeveloperMetadata('metaDataGrid',metaDataGrid);
  return response.getContentText();
} catch (error) {
  showErrorDialog(error.message);
  //throw error;
}  

}
function makeKeepOnlyCall(selectedCube, sMetaDataGrid) {
  try {


    Logger.log('makeKeepOnlyCall....');
    selectedCube = getActiveSheetSelectedCube();
    sMetaDataGrid = getActiveSheetMetaDataGrid();

    var range = SpreadsheetApp.getActive().getDataRange();
    var totalCols = range.getNumColumns();
    var totalRows = range.getNumRows();
    var dataGridValues = range.getDisplayValues();


    var rangeList = SpreadsheetApp.getActive().getSelection().getActiveRangeList();
    var ranges = rangeList.getRanges();
    Logger.log(ranges.length);

    var selMap = [];
    var selRangeArry = [];
    for (var i = 0; i < ranges.length; i++) {
      selRangeArry = [];
      Logger.log('Active Ranges: ' + ranges[i].getA1Notation());
      Logger.log(ranges[i].getRow());
      Logger.log(ranges[i].getColumn());
      Logger.log(ranges[i].getHeight());
      Logger.log(ranges[i].getWidth());

      var selCelRow = ranges[i].getRow();
      var selCelCol = ranges[i].getColumn();

      var selCountRow = ranges[i].getHeight();
      var selCountCol = ranges[i].getWidth();

      if (selCelRow > 0) {
        selCelRow = selCelRow - 1;
      }
      if (selCelCol > 0) {
        selCelCol = selCelCol - 1;
      }
      Logger.log('activecell - ' + selCelRow + "\t" + selCelCol);


      selRangeArry.push(selCelRow);
      selRangeArry.push(selCelCol);
      selRangeArry.push(selCountRow);
      selRangeArry.push(selCountCol);
      selMap.push(selRangeArry);

    }
    Logger.log(selMap);




    var data = {
      "totalRows": totalRows,
      "totalCols": totalCols,
      "dataGrid": dataGridValues,
      "dataGridMetaData": JSON.parse(sMetaDataGrid),
      "selectedRanges": selMap
    };
    Logger.log(JSON.stringify(data));
    var options = {
      'method': 'post',
      'contentType': 'application/json',
      'payload': JSON.stringify(data)
    };


    var zoomUrl = SERVICE_BASE_URL+'applications/' + selectedCube + '/keepOnly';

    var response = UrlFetchApp.fetch(zoomUrl, options);
    //SpreadsheetApp.getActive().getActiveCell().setValue(response.getContentText());
    var resstr = response.getContentText();
    var jsonObj = JSON.parse(resstr);
    var totalRowNum = 0;
    var totalColNum = 0;
    var dataGrid;
    var metaDataGrid;

    if (jsonObj) {
      totalRowNum = jsonObj.totalRows;
      totalColNum = jsonObj.totalCols;
      dataGrid = jsonObj.dataGrid;
      metaDataGrid = JSON.stringify(jsonObj.dataGridMetaData);
    }
    Logger.log('totalRowNum=' + totalRowNum);
    Logger.log('totalColNUm=' + totalColNum);
    Logger.log('metaDataGrid.length=' + metaDataGrid.length);
    Logger.log('received data =' + JSON.stringify(jsonObj));
    SpreadsheetApp.getActive().getActiveSheet().getDataRange().clear();

    if(totalRowNum > 0) {
      SpreadsheetApp.getActive().getActiveSheet().getRange(1, 1, totalRowNum, totalColNum).setValues(dataGrid);
      setActiveSheetMetaDataGrid(metaDataGrid);
      setActiveSheetSelectedCube(selectedCube);
    }
    
    //  SpreadsheetApp.getActive().getActiveSheet().addDeveloperMetadata('metaDataGrid',metaDataGrid);
    return response.getContentText();

  } catch (error) {
    showErrorDialog(error);
    throw error;
  }

}

function makeRemoveOnlyCall(selectedCube, sMetaDataGrid) {
  try {
    Logger.log('makeRemoveOnlyCall....');
    selectedCube = getActiveSheetSelectedCube();
    sMetaDataGrid = getActiveSheetMetaDataGrid();

    var range = SpreadsheetApp.getActive().getDataRange();
    var totalCols = range.getNumColumns();
    var totalRows = range.getNumRows();
    var dataGridValues = range.getDisplayValues();

    var rangeList = SpreadsheetApp.getActive().getSelection().getActiveRangeList();
    var ranges = rangeList.getRanges();
    Logger.log(ranges.length);

    var selMap = [];
    var selRangeArry = [];
    for (var i = 0; i < ranges.length; i++) {
      selRangeArry = [];
      Logger.log('Active Ranges: ' + ranges[i].getA1Notation());
      Logger.log(ranges[i].getRow());
      Logger.log(ranges[i].getColumn());
      Logger.log(ranges[i].getHeight());
      Logger.log(ranges[i].getWidth());

      var selCelRow = ranges[i].getRow();
      var selCelCol = ranges[i].getColumn();

      var selCountRow = ranges[i].getHeight();
      var selCountCol = ranges[i].getWidth();

      if (selCelRow > 0) {
        selCelRow = selCelRow - 1;
      }
      if (selCelCol > 0) {
        selCelCol = selCelCol - 1;
      }
      Logger.log('activecell - ' + selCelRow + "\t" + selCelCol);


      selRangeArry.push(selCelRow);
      selRangeArry.push(selCelCol);
      selRangeArry.push(selCountRow);
      selRangeArry.push(selCountCol);
      selMap.push(selRangeArry);

    }
    Logger.log(selMap);

    var data = {
      "totalRows": totalRows,
      "totalCols": totalCols,
      "dataGrid": dataGridValues,
      "dataGridMetaData": JSON.parse(sMetaDataGrid),
      "selectedRanges": selMap
    };
    Logger.log(JSON.stringify(data));
    var options = {
      'method': 'post',
      'contentType': 'application/json',
      'payload': JSON.stringify(data)
    };

    var zoomUrl = SERVICE_BASE_URL+'applications/' + selectedCube + '/removeOnly';

    var response = UrlFetchApp.fetch(zoomUrl, options);
    //SpreadsheetApp.getActive().getActiveCell().setValue(response.getContentText());
    var resstr = response.getContentText();
    var jsonObj = JSON.parse(resstr);
    var totalRowNum = 0;
    var totalColNum = 0;
    var dataGrid;
    var metaDataGrid;

    if (jsonObj) {
      totalRowNum = jsonObj.totalRows;
      totalColNum = jsonObj.totalCols;
      dataGrid = jsonObj.dataGrid;
      metaDataGrid = JSON.stringify(jsonObj.dataGridMetaData);
    }
    Logger.log('totalRowNum=' + totalRowNum);
    Logger.log('totalColNUm=' + totalColNum);
    Logger.log('metaDataGrid.length=' + metaDataGrid.length);
    Logger.log('received data =' + JSON.stringify(jsonObj));
    SpreadsheetApp.getActive().getActiveSheet().getDataRange().clear();
    if(totalRowNum > 0){
      SpreadsheetApp.getActive().getActiveSheet().getRange(1, 1, totalRowNum, totalColNum).setValues(dataGrid);
      setActiveSheetMetaDataGrid(metaDataGrid);
      setActiveSheetSelectedCube(selectedCube);
    }
    
    //  SpreadsheetApp.getActive().getActiveSheet().addDeveloperMetadata('metaDataGrid',metaDataGrid);
    return response.getContentText();
  } catch (error) {
    showErrorDialog(error);
    throw error;
  }

}


function makeloadApplications(selectedCube) {
  Logger.log('makeLoadCall....');
  var lastDataRangeRow = SpreadsheetApp.getActive().getActiveSheet().getLastRow();
  var lastDataRangeCol = SpreadsheetApp.getActive().getActiveSheet().getLastColumn();
  if(lastDataRangeCol > 0 || lastDataRangeRow >0) {
    showDialog();
  }

  //Logger.log(SERVICE_BASE_URL+'applications/' + selectedCube + '/defaultGrid');
  var response = UrlFetchApp.fetch(SERVICE_BASE_URL+'applications/' + selectedCube + '/defaultGrid');
  //SpreadsheetApp.getActive().getActiveCell().setValue(response.getContentText());
  var resstr = response.getContentText();
  var jsonObj = JSON.parse(resstr);
  var totalRowNum = 0;
  var totalColNum = 0;
  var dataGrid;
  var metaDataGrid;

  // store in the session storage  selected db and sheetid
  var currentSpreadSheetId = SpreadsheetApp.getActiveSpreadsheet().getId();
  var currentSheetId = SpreadsheetApp.getActive().getActiveSheet().getSheetId();

  if (jsonObj) {
    totalRowNum = jsonObj.totalRows;
    totalColNum = jsonObj.totalCols;
    dataGrid = jsonObj.dataGrid;
    metaDataGrid = JSON.stringify(jsonObj.dataGridMetaData);
  }
  Logger.log('totalRowNum=' + totalRowNum);
  Logger.log('totalColNUm=' + totalColNum);
  
  if(totalRowNum > 0) {
    SpreadsheetApp.getActive().getActiveSheet().getRange(1, 1, totalRowNum, totalColNum).setValues(dataGrid);
    // documentProperties.setProperty(currentSpreadSheetId+'-'+currentSheetId+ '-selectedCube', selectedCube);
    // documentProperties.setProperty(currentSpreadSheetId+'-'+currentSheetId+ '-metaDataGrid', metaDataGrid);
    jsonObj.selectedCubeName = currentSpreadSheetId+'-'+currentSheetId+ '-selectedCube';
    jsonObj.selectedMetaDataGridName = currentSpreadSheetId+'-'+currentSheetId+ '-metaDataGrid';
    setActiveSheetMetaDataGrid(metaDataGrid);
    setActiveSheetSelectedCube(selectedCube);
  }
  
  // SpreadsheetApp.getActive().getActiveSheet().addDeveloperMetadata('metaDataGrid',metaDataGrid);
  //add Menu item for applications

  //return response.getContentText();
  return JSON.stringify(jsonObj);

}

function makeDefaultRetrieve(selectedCube) {
  selectedCube = getActiveSheetSelectedCube();
  Logger.log('makeDefaultRetrieve....'+selectedCube);
  Logger.log(SERVICE_BASE_URL+'applications/' + selectedCube + '/defaultGrid');
  var response = UrlFetchApp.fetch(SERVICE_BASE_URL+'applications/' + selectedCube + '/defaultGrid');
  //SpreadsheetApp.getActive().getActiveCell().setValue(response.getContentText());
  SpreadsheetApp.getActive().getActiveSheet().getDataRange().clear();
  // if(SpreadsheetApp.getActive().getActiveSheet().getDeveloperMetadata()){
  //   Logger.log('clearing the meta data...');
  //   SpreadsheetApp.getActive().getActiveSheet().getDeveloperMetadata()[0].remove();
  // }

  var resstr = response.getContentText();
  var jsonObj = JSON.parse(resstr);
  var totalRowNum = 0;
  var totalColNum = 0;
  var dataGrid;
  var metaDataGrid;
  if (jsonObj) {
    totalRowNum = jsonObj.totalRows;
    totalColNum = jsonObj.totalCols;
    dataGrid = jsonObj.dataGrid;
    metaDataGrid = JSON.stringify(jsonObj.dataGridMetaData);
  }
  Logger.log('totalRowNum=' + totalRowNum);
  Logger.log('totalColNUm=' + totalColNum);
  Logger.log('metaDataGrid.length=' + metaDataGrid.length);
  if(totalRowNum > 0) {
    SpreadsheetApp.getActive().getActiveSheet().getRange(1, 1, totalRowNum, totalColNum).setValues(dataGrid);
    setActiveSheetMetaDataGrid(metaDataGrid);
    setActiveSheetSelectedCube(selectedCube);
  }
  
  // SpreadsheetApp.getActive().getActiveSheet().addDeveloperMetadata('metaDataGrid',metaDataGrid);
  //add Menu item for applications


  return response.getContentText();

}



function showLoggedInSideBar() {
  Logger.log('showLoggedInSideBar called...');
  // makeLoadCall();
  var html = HtmlService.createHtmlOutputFromFile('EssbasePage')
    .setTitle('Essbase Applications')
    .setWidth(300);
  html.append('<script>\n var IS_LOGGEDIN = true; \n</script>');
  SpreadsheetApp.getUi()
    .showSidebar(html);
  return true;

}

function makeLogoutCall() {
  Logger.log('inside makeLogoutCall...');
  var response = UrlFetchApp.fetch(SERVICE_BASE_URL+'logout');
  documentProperties.deleteAllProperties();
  SpreadsheetApp.getUi()
    .createAddonMenu()
    .addItem('Essbase Setup', 'showSidebar')
    .addToUi();
    documentProperties.deleteAllProperties();
  Logger.log(response.getContentText());
  

}


function showErrorDialog(message) {
  //document.getElementById('result').innerHTML = 'Error: ' + message;
  //alert('showerror');
  Logger.log('inside the show error' + message);
  var ui = SpreadsheetApp.getUi(); // Same variations.

  var result = ui.alert(
    'Error',
    message,
    ui.ButtonSet.OK);



}

function sayHello() {
  Logger.log('sayhello..');
}

function showDialog() {
  var yesClick=false;
  var html = HtmlService.createHtmlOutputFromFile('ContentDialog')
      .setWidth(400)
      .setHeight(300);
  //SpreadsheetApp.getUi() // Or DocumentApp or SlidesApp or FormApp.
    //  .showModalDialog(html, 'Clear Content dialog');
    var ui = SpreadsheetApp.getUi(); // Same variations.

   var result= ui.alert('Please Confirm', 'Clear Sheet Contents and POV',ui.ButtonSet.OK);
  // Process the user's response.
  if (result == ui.Button.OK) {
    // User clicked "Yes".
    //ui.alert('Confirmation received.');
    clearSheetContent();
    yesClick=true;
  } else {
    // User clicked "No" or X in the title bar.
    //ui.alert('Permission denied.');
    yesClick=false;
  }
  return yesClick;


}

function clearSheetContent() {
  SpreadsheetApp.getActive().getActiveSheet().getDataRange().clear();
}

function makeSaveOptions(optionsObj) {
  
  Logger.log('isRepeatLabel=' + optionsObj.isRepeatLabel);
  userProperties.setProperties(optionsObj);

  // var data = {
  //   'repeatLabel': (optionsObj.isRepeatLabel === 'true'),
  //   'suppressMissingRows': (optionsObj.isSuppressMissingRows === 'true'),
  //   'suppressZeroRows': (optionsObj.isSuppressZeroRows === 'true'),
  //   'suppressMissingColumns': (optionsObj.isSuppressMissingColumns === 'true'),
  //   'suppressZeroColumns': (optionsObj.isSuppressZeroColumns === 'true'),
  //   'preseveFormatting': (optionsObj.isPreseveFormatting === 'true'),
  //   'adjustColumnWidth': (optionsObj.isAdjustColumnWidth === 'true'),
  //   'indentationGroupVal': optionsObj.indentationGroupVal,
  //   'noDataMissingInputVal': optionsObj.noDataMissingInputVal,
  //   'noAccessInputVal': optionsObj.noAccessInputVal,
  //   'undoRedoCountInputVal': optionsObj.undoRedoCountInputVal,
  //   'userId': Session.getActiveUser().getEmail()
  // };
  var data = {
    'repeatLabel': (optionsObj.isRepeatLabel),
    'suppressMissingRows': (optionsObj.isSuppressMissingRows),
    'suppressZeroRows': (optionsObj.isSuppressZeroRows),
    'suppressMissingColumns': (optionsObj.isSuppressMissingColumns),
    'suppressZeroColumns': (optionsObj.isSuppressZeroColumns),
    'preseveFormatting': (optionsObj.isPreseveFormatting),
    'adjustColumnWidth': (optionsObj.isAdjustColumnWidth),
    'indentationGroupVal': optionsObj.indentationGroupVal,
    'noDataMissingInputVal': optionsObj.noDataMissingInputVal,
    'noAccessInputVal': optionsObj.noAccessInputVal,
    'undoRedoCountInputVal': optionsObj.undoRedoCountInputVal,
    'userId': Session.getActiveUser().getEmail()
  };

  Logger.log('data=='+JSON.stringify(data));
  var options = {
    'method': 'post',
    'contentType': 'application/json',
    'payload': JSON.stringify(data)
  };
  var response = UrlFetchApp.fetch(SERVICE_BASE_URL+'essbaseUserOptions', options);
  Logger.log('done with call..');
  return response.getContentText();

}

function getOptions() {
  //  userProperties.getProperties();
  // var data = {
  //   'repeatLabel': (userProperties.getProperty('repeatLabel') === 'true'),
  //   'suppressMissingRows': (userProperties.getProperty('suppressMissingRows') === 'true'),
  //   'suppressZeroRows': (userProperties.getProperty('suppressZeroRows') === 'true'),
  //   'suppressMissingColumns': (userProperties.getProperty('suppressMissingColumns') === 'true'),
  //   'suppressZeroColumns': (userProperties.getProperty('suppressZeroColumns') === 'true'),
  //   'preseveFormatting': (userProperties.getProperty('preseveFormatting') === 'true'),
  //   'adjustColumnWidth': (userProperties.getProperty('adjustColumnWidth') === 'true'),
  //   'indentationGroupVal': userProperties.getProperty('indentationGroupVal'),
  //   'noDataMissingInputVal': userProperties.getProperty('noDataMissingInputVal'),
  //   'noAccessInputVal': userProperties.getProperty('noAccessInputVal'),
  //   'undoRedoCountInputVal': userProperties.getProperty('undoRedoCountInputVal'),
  //   'userId': userProperties.getProperty('userId')
  // };


  // return data;
  return userProperties.getProperties();
}

function getActiveSheetId() {
  return SpreadsheetApp.getActiveSheet().getSheetId();
}

function getActiveSheetSelectedCube() {
    // store in the session storage  selected db and sheetid
    var currentSpreadSheetId = SpreadsheetApp.getActiveSpreadsheet().getId();
    var currentSheetId = SpreadsheetApp.getActive().getActiveSheet().getSheetId();
    var selectedCubeName = currentSpreadSheetId+'-'+currentSheetId+ '-selectedCube';
    //var selectedMetaDataGridName = currentSpreadSheetId+'-'+currentSheetId+ '-metaDataGrid';
    var selCube = documentProperties.getProperty(selectedCubeName);
    Logger.log('selectedCube from document properties is ' +selCube);
    if(selCube && selCube.length > 0) {
      return selCube;
    } else {
      return "Applications";
    }
}

function getActiveSheetMetaDataGrid() {
  // store in the session storage  selected db and sheetid
  var currentSpreadSheetId = SpreadsheetApp.getActiveSpreadsheet().getId();
  var currentSheetId = SpreadsheetApp.getActive().getActiveSheet().getSheetId();
  var selectedMetaDataGridName = currentSpreadSheetId+'-'+currentSheetId+ '-metaDataGrid';
  var selMetaDataGrid = documentProperties.getProperty(selectedMetaDataGridName);
  Logger.log('selMetaDataGrid from document properties is ' +selMetaDataGrid);
  if(selMetaDataGrid && selMetaDataGrid.length > 0) {
    return selMetaDataGrid;
  } else {
    return "";
  }
}

function setActiveSheetSelectedCube(selCube) {
    // store in the session storage  selected db and sheetid
    var currentSpreadSheetId = SpreadsheetApp.getActiveSpreadsheet().getId();
    var currentSheetId = SpreadsheetApp.getActive().getActiveSheet().getSheetId();
    var selectedCubeName = currentSpreadSheetId+'-'+currentSheetId+ '-selectedCube';
    documentProperties.setProperty(selectedCubeName, selCube);
}

function setActiveSheetMetaDataGrid(selMetaDataGrid) {
  // store in the session storage  selected db and sheetid
  var currentSpreadSheetId = SpreadsheetApp.getActiveSpreadsheet().getId();
  var currentSheetId = SpreadsheetApp.getActive().getActiveSheet().getSheetId();
  var selectedMetaDataGridName = currentSpreadSheetId+'-'+currentSheetId+ '-metaDataGrid';
  documentProperties.setProperty(selectedMetaDataGridName, selMetaDataGrid);
}