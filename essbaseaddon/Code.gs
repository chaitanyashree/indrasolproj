/** Essbase Plugin */

var IS_LOGGEDIN = false;

function onOpen() {
  SpreadsheetApp.getUi() // Or DocumentApp or SlidesApp or FormApp.
    //.createMenu('Essbase Connector')
    .createAddonMenu()
    .addItem('Essbase Setup', 'showSidebar')
    .addToUi();
  Logger.log('inside onopen');
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


  Logger.log('done with call..');
  return response.getContentText();
}

function makeLoadCall() {
  Logger.log('makeLoadCall....');
  var response = UrlFetchApp.fetch('http://35.184.51.106:8080/essbase/applications');
  //SpreadsheetApp.getActive().getActiveCell().setValue(response.getContentText());
  return response.getContentText();

}

function makeZoomInNextCall(selectedCube,sMetaDataGrid) {
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
  if(selCelRow>0) {
    selCelRow = selCelRow - 1;
  }
  if(selCelCol>0) {
    selCelCol = selCelCol -1;
  }
  Logger.log('activecell - '+selCelRow+"\t"+selCelCol);

  var zoomUrl = 'http://35.184.51.106:8080/essbase/applications/' + selectedCube + '/zoomIn/'+selCelRow+'/'+selCelCol;

  var response = UrlFetchApp.fetch(zoomUrl,options);
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
   SpreadsheetApp.getActive().getActiveSheet().getRange(1, 1, totalRowNum, totalColNum).setValues(dataGrid);
   //SpreadsheetApp.getActive().getActiveSheet().addDeveloperMetadata('metaDataGrid',metaDataGrid);
   return response.getContentText();

}

function makeZoomInBottomCall(selectedCube,sMetaDataGrid) {
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
  if(selCelRow>0) {
    selCelRow = selCelRow - 1;
  }
  if(selCelCol>0) {
    selCelCol = selCelCol -1;
  }
  Logger.log('activecell - '+selCelRow+"\t"+selCelCol);

  var zoomUrl = 'http://35.184.51.106:8080/essbase/applications/' + selectedCube + '/zoomInBottom/'+selCelRow+'/'+selCelCol;

  var response = UrlFetchApp.fetch(zoomUrl,options);
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
   SpreadsheetApp.getActive().getActiveSheet().getRange(1, 1, totalRowNum, totalColNum).setValues(dataGrid);
   //SpreadsheetApp.getActive().getActiveSheet().addDeveloperMetadata('metaDataGrid',metaDataGrid);
   return response.getContentText();

}

function makeZoomInAllCall(selectedCube,sMetaDataGrid) {
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
  if(selCelRow>0) {
    selCelRow = selCelRow - 1;
  }
  if(selCelCol>0) {
    selCelCol = selCelCol -1;
  }
  Logger.log('activecell - '+selCelRow+"\t"+selCelCol);

  var zoomUrl = 'http://35.184.51.106:8080/essbase/applications/' + selectedCube + '/zoomInAll/'+selCelRow+'/'+selCelCol;

  var response = UrlFetchApp.fetch(zoomUrl,options);
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
   Logger.log('resstr='+resstr);
   Logger.log('metaDataGrid.length='+metaDataGrid.length);

   SpreadsheetApp.getActive().getActiveSheet().getRange(1, 1, totalRowNum, totalColNum).setValues(dataGrid);
   //SpreadsheetApp.getActive().getActiveSheet().addDeveloperMetadata('metaDataGrid',metaDataGrid);
   return response.getContentText();

}

function makeZoomOutCall(selectedCube,sMetaDataGrid) {
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
  if(selCelRow>0) {
    selCelRow = selCelRow - 1;
  }
  if(selCelCol>0) {
    selCelCol = selCelCol -1;
  }
  Logger.log('activecell - '+selCelRow+"\t"+selCelCol);

  var zoomUrl = 'http://35.184.51.106:8080/essbase/applications/' + selectedCube + '/zoomOut/'+selCelRow+'/'+selCelCol;

  var response = UrlFetchApp.fetch(zoomUrl,options);
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
   Logger.log('metaDataGrid.length='+metaDataGrid.length);
   Logger.log('received data ='+JSON.stringify(jsonObj));
   SpreadsheetApp.getActive().getActiveSheet().getDataRange().clear();
   SpreadsheetApp.getActive().getActiveSheet().getRange(1, 1, totalRowNum, totalColNum).setValues(dataGrid);
  //  SpreadsheetApp.getActive().getActiveSheet().addDeveloperMetadata('metaDataGrid',metaDataGrid);
   return response.getContentText();

}

function makeRefreshCall(selectedCube,sMetaDataGrid) {
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
  if(selCelRow>0) {
    selCelRow = selCelRow - 1;
  }
  if(selCelCol>0) {
    selCelCol = selCelCol -1;
  }
  Logger.log('activecell - '+selCelRow+"\t"+selCelCol);

  var zoomUrl = 'http://35.184.51.106:8080/essbase/applications/' + selectedCube + '/refresh';

  var response = UrlFetchApp.fetch(zoomUrl,options);
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
   Logger.log('metaDataGrid.length='+metaDataGrid.length);
   Logger.log('received data ='+JSON.stringify(jsonObj));
   SpreadsheetApp.getActive().getActiveSheet().getDataRange().clear();
   SpreadsheetApp.getActive().getActiveSheet().getRange(1, 1, totalRowNum, totalColNum).setValues(dataGrid);
  //  SpreadsheetApp.getActive().getActiveSheet().addDeveloperMetadata('metaDataGrid',metaDataGrid);
   return response.getContentText();

}

function makeKeepOnlyCall(selectedCube,sMetaDataGrid) {
  Logger.log('makeKeepOnlyCall....');
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
  //var selRange = SpreadsheetApp.getActive().getCurrentCell();
  var selRange = SpreadsheetApp.getActive().getSelection().getActiveRangeList().getRanges();
  var selCelRow = selRange[0].getRow();
  var selCelCol = selRange[0].getColumn();

  var selCountRow = selRange[0].getHeight();
  var selCountCol = selRange[0].getWidth();

  if(selCelRow>0) {
    selCelRow = selCelRow - 1;
  }
  if(selCelCol>0) {
    selCelCol = selCelCol -1;
  }
  Logger.log('activecell - '+selCelRow+"\t"+selCelCol);

  var zoomUrl = 'http://35.184.51.106:8080/essbase/applications/' + selectedCube + '/keepOnly/'+selCelRow+'/'+selCelCol+'/'+selCountRow+'/'+selCountCol;

  var response = UrlFetchApp.fetch(zoomUrl,options);
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
   Logger.log('metaDataGrid.length='+metaDataGrid.length);
   Logger.log('received data ='+JSON.stringify(jsonObj));
   SpreadsheetApp.getActive().getActiveSheet().getDataRange().clear();
   SpreadsheetApp.getActive().getActiveSheet().getRange(1, 1, totalRowNum, totalColNum).setValues(dataGrid);
  //  SpreadsheetApp.getActive().getActiveSheet().addDeveloperMetadata('metaDataGrid',metaDataGrid);
   return response.getContentText();

}

function makeRemoveOnlyCall(selectedCube,sMetaDataGrid) {
  Logger.log('makeRemoveOnlyCall....');
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
  //var selCel = SpreadsheetApp.getActive().getCurrentCell();
  var selRange = SpreadsheetApp.getActive().getSelection().getActiveRangeList().getRanges();
  var selCelRow = selRange[0].getRow();
  var selCelCol = selRange[0].getColumn();

  var selCountRow = selRange[0].getHeight();
  var selCountCol = selRange[0].getWidth();

  if(selCelRow>0) {
    selCelRow = selCelRow - 1;
  }
  if(selCelCol>0) {
    selCelCol = selCelCol -1;
  }
  Logger.log('activecell - '+selCelRow+"\t"+selCelCol);

  var zoomUrl = 'http://35.184.51.106:8080/essbase/applications/' + selectedCube + '/removeOnly/'+selCelRow+'/'+selCelCol+'/'+selCountRow+'/'+selCountCol;

  var response = UrlFetchApp.fetch(zoomUrl,options);
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
   Logger.log('metaDataGrid.length='+metaDataGrid.length);
   Logger.log('received data ='+JSON.stringify(jsonObj));
   SpreadsheetApp.getActive().getActiveSheet().getDataRange().clear();
   SpreadsheetApp.getActive().getActiveSheet().getRange(1, 1, totalRowNum, totalColNum).setValues(dataGrid);
  //  SpreadsheetApp.getActive().getActiveSheet().addDeveloperMetadata('metaDataGrid',metaDataGrid);
   return response.getContentText();

}


function makeLoadDimensions(selectedCube) {
  Logger.log('makeLoadCall....');
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
  SpreadsheetApp.getActive().getActiveSheet().getRange(1, 1, totalRowNum, totalColNum).setValues(dataGrid);
  // SpreadsheetApp.getActive().getActiveSheet().addDeveloperMetadata('metaDataGrid',metaDataGrid);
  //add Menu item for applications


  return response.getContentText();

}

function makeDefaultRetrieve(selectedCube) {
  Logger.log('makeDefaultRetrieve....');
  //Logger.log('http://35.184.51.106:8080/essbase/applications/' + selectedCube + '/defaultGrid');
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
  Logger.log('metaDataGrid.length='+metaDataGrid.length);
  SpreadsheetApp.getActive().getActiveSheet().getRange(1, 1, totalRowNum, totalColNum).setValues(dataGrid);
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
  SpreadsheetApp.getUi()
    .createAddonMenu()
    .addItem('Essbase Setup', 'showSidebar')
    .addToUi();
  Logger.log(response.getContentText());

}


function showError(message) {
  //document.getElementById('result').innerHTML = 'Error: ' + message;
  //alert('showerror');
  Logger.log('inside the show error' + message);
}

function sayHello() {
  Logger.log('sayhello..');
}


