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

function makeLoadDimensions(selectedCube) {
  Logger.log('makeLoadCall....');
  Logger.log('http://35.184.51.106:8080/essbase/applications/' + selectedCube + '/defaultGrid');
  var response = UrlFetchApp.fetch('http://35.184.51.106:8080/essbase/applications/' + selectedCube + '/defaultGrid');
  //SpreadsheetApp.getActive().getActiveCell().setValue(response.getContentText());
  var resstr = response.getContentText();
  var jsonObj = JSON.parse(resstr);
  var totalRowNum = 0;
  var totalColNum = 0;
  var dataGrid;
  if (jsonObj) {
    totalRowNum = jsonObj.totalRows;
    totalColNum = jsonObj.totalCols;
    dataGrid = jsonObj.dataGrid;
  }
  Logger.log('totalRowNum=' + totalRowNum);
  Logger.log('totalColNUm=' + totalColNum);
  SpreadsheetApp.getActive().getActiveSheet().getRange(1, 1, totalRowNum, totalColNum).setValues(dataGrid);
  //add Menu item for applications

  // SpreadsheetApp.getUi()
  //   .createAddonMenu()
  //   .addSubMenu(SpreadsheetApp.getUi().createMenu(selectedCube)
  //     .addItem('List Applications', 'showLoggedInSideBar')
  //     .addSeparator()
  //     .addItem('Zoom In', 'showLoggedInSideBar')
  //     .addItem('Zoom Out', 'showLoggedInSideBar'))
  //   .addSeparator()
  //   .addItem('Logout', 'makeLogoutCall')
  //   .addToUi();

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


