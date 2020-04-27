/** Essbase Plugin */

var IS_LOGGEDIN = false;
// Set a property in each of the three property stores.
var scriptProperties = PropertiesService.getScriptProperties();
var userProperties = PropertiesService.getUserProperties();
var documentProperties = PropertiesService.getDocumentProperties();



function onInstall(e) {
  onOpen(e);
}
function onOpen(e) {

    Logger.log('onOpen');
    SpreadsheetApp.getUi() // Or DocumentApp or SlidesApp or FormApp.
    //.createMenu('Essbase Connector')
    .createAddonMenu()
    .addItem('Essbase Setup', 'showSidebar')
    .addToUi();
  Logger.log('inside onopen showSidebar');


}

function isLoggedIn() {
  var isLoggedIn = documentProperties.getProperty('isLoggedIn');
  if(isLoggedIn == 'TRUE') {
    return true;
  } else {
    return false;
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

//var SERVICE_BASE_URL = 'http://35.184.51.106:8080/essbase/hello';

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
    var response = UrlFetchApp.fetch('http://35.184.51.106:8080/essbase/login', options);
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
  var response = UrlFetchApp.fetch('http://35.184.51.106:8080/essbase/applications');
  //SpreadsheetApp.getActive().getActiveCell().setValue(response.getContentText());
  return response.getContentText();

}

function makeZoomInNextCall(selectedCube, sMetaDataGrid) {
  Logger.log('makeZoomInCall....');
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

  var zoomUrl = 'http://35.184.51.106:8080/essbase/applications/' + selectedCube + '/zoomIn/' + selCelRow + '/' + selCelCol;

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
  }
  
  //SpreadsheetApp.getActive().getActiveSheet().addDeveloperMetadata('metaDataGrid',metaDataGrid);
  return response.getContentText();

}

function makeZoomInBottomCall(selectedCube, sMetaDataGrid) {
  Logger.log('makeZoomInBottomCall....');
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

  var zoomUrl = 'http://35.184.51.106:8080/essbase/applications/' + selectedCube + '/zoomInBottom/' + selCelRow + '/' + selCelCol;

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
  }
  //SpreadsheetApp.getActive().getActiveSheet().addDeveloperMetadata('metaDataGrid',metaDataGrid);
  return response.getContentText();

}

function makeZoomInAllCall(selectedCube, sMetaDataGrid) {
  Logger.log('makeZoomInAllCall....');
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

  var zoomUrl = 'http://35.184.51.106:8080/essbase/applications/' + selectedCube + '/zoomInAll/' + selCelRow + '/' + selCelCol;

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
  }
  
  //SpreadsheetApp.getActive().getActiveSheet().addDeveloperMetadata('metaDataGrid',metaDataGrid);
  return response.getContentText();

}

function makeZoomOutCall(selectedCube, sMetaDataGrid) {
  Logger.log('makeZoomOutCall....');
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

  var zoomUrl = 'http://35.184.51.106:8080/essbase/applications/' + selectedCube + '/zoomOut/' + selCelRow + '/' + selCelCol;

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

  var zoomUrl = 'http://35.184.51.106:8080/essbase/applications/' + selectedCube + '/refresh';

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

  }
  
  //  SpreadsheetApp.getActive().getActiveSheet().addDeveloperMetadata('metaDataGrid',metaDataGrid);
  return response.getContentText();

}

function makePivotCall(selectedCube, sMetaDataGrid) {
  try {
  Logger.log('makePivotCall....');
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

  var zoomUrl = 'http://35.184.51.106:8080/essbase/applications/' + selectedCube + '/pivot/' + selCelRow + '/' + selCelCol;

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


    var zoomUrl = 'http://35.184.51.106:8080/essbase/applications/' + selectedCube + '/keepOnly';

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

    var zoomUrl = 'http://35.184.51.106:8080/essbase/applications/' + selectedCube + '/removeOnly';

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

  //Logger.log('http://35.184.51.106:8080/essbase/applications/' + selectedCube + '/defaultGrid');
  var response = UrlFetchApp.fetch('http://35.184.51.106:8080/essbase/applications/' + selectedCube + '/defaultGrid');
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
  if(totalRowNum > 0) {
    SpreadsheetApp.getActive().getActiveSheet().getRange(1, 1, totalRowNum, totalColNum).setValues(dataGrid);
  }
  
  // SpreadsheetApp.getActive().getActiveSheet().addDeveloperMetadata('metaDataGrid',metaDataGrid);
  //add Menu item for applications


  return response.getContentText();

}

function makeDefaultRetrieve(selectedCube) {
  Logger.log('makeDefaultRetrieve....'+selectedCube);
  Logger.log('http://35.184.51.106:8080/essbase/applications/' + selectedCube + '/defaultGrid');
  var response = UrlFetchApp.fetch('http://35.184.51.106:8080/essbase/applications/' + selectedCube + '/defaultGrid');
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
  var response = UrlFetchApp.fetch('http://35.184.51.106:8080/essbase/logout');
  documentProperties.deleteAllProperties();
  SpreadsheetApp.getUi()
    .createAddonMenu()
    .addItem('Essbase Setup', 'showSidebar')
    .addToUi();
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

  var data = {
    'repeatLabel': (optionsObj.isRepeatLabel === 'true'),
    'suppressMissingRows': (optionsObj.isSuppressMissingRows === 'true'),
    'suppressZeroRows': (optionsObj.isSuppressZeroRows === 'true'),
    'suppressMissingColumns': (optionsObj.isSuppressMissingColumns === 'true'),
    'suppressZeroColumns': (optionsObj.isSuppressZeroColumns === 'true'),
    'preseveFormatting': (optionsObj.isPreseveFormatting === 'true'),
    'adjustColumnWidth': (optionsObj.isAdjustColumnWidth === 'true'),
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
  var response = UrlFetchApp.fetch('http://35.184.51.106:8080/essbase/essbaseUserOptions', options);
  Logger.log('done with call..');
  return response.getContentText();

}

function getOptions() {
  return userProperties.getProperties();
}
