/*function onOpen() {
  SpreadsheetApp.getUi() // Or DocumentApp or SlidesApp or FormApp.
      .createMenu('Custom Menu')
      .addItem('Show sidebar', 'showSidebar')
      .addToUi();
}

function showSidebar() {
  var html = HtmlService.createHtmlOutputFromFile('Page')
      .setTitle('My custom sidebar')
      .setWidth(300);
  SpreadsheetApp.getUi() // Or DocumentApp or SlidesApp or FormApp.
      .showSidebar(html);
}*/

/** newer function **/

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
  // var data = {
  //   'name': str,
  //   'age': 35,
  //   'pets': ['fido', 'fluffy']
  // };
  // var options = {
  //   'method': 'post',
  //   'contentType': 'application/json',
  //   // Convert the JavaScript object to a JSON string.
  //   'payload': JSON.stringify(data)
  // };
  //var response = UrlFetchApp.fetch('https://httpbin.org/post', options);
  //Logger.log(response);
  // essbase service API  calls 
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
  var response = UrlFetchApp.fetch('http://35.184.51.106:8080/essbase/login',options);
 // Logger.log(response.getContentText());
 Logger.log(response);
  
  /*  var data = {
    'userName' : userName,
    'password' : password,
    'olapServerName' : olapServerName,
    'url': connecturl,

  };

  var options = {
    'method': 'post',
    'contentType': 'application/json',
    //Convert the JavaScript object to a JSON string
    'payload': JSON.stringify(data)
  };
  //var response = UrlFetchApp.fetch(SERVICE_BASE_URL,options);
  var response = UrlFetchApp.fetch('https://8080-dot-11138311-dot-devshell.appspot.com/essbase/hello');
  Logger.log(response.getContentText());
*/
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
  var response = UrlFetchApp.fetch('http://35.184.51.106:8080/essbase/applications/'+selectedCube+'/defaultGrid');
  //SpreadsheetApp.getActive().getActiveCell().setValue(response.getContentText());
  var resstr = response.getContentText();
  var jsonObj = JSON.parse(resstr);
  var totalRowNum = 0;
  var totalColNum = 0;
  if(jsonObj) {
    totalRowNum = jsonObj.length;
    if(totalRowNum > 0) {
      totalColNum = jsonObj[0].length;
    }
  }
  Logger.log('totalRowNum='+totalRowNum);
  Logger.log('totalColNUm='+totalColNum);
  SpreadsheetApp.getActive().getActiveSheet().getRange(1,1,totalRowNum,totalColNum).setValues(jsonObj);
  return response.getContentText();
  
}

function makeLogoutCall() {
  Logger.log('inside makeLogoutCall...');
  var response = UrlFetchApp.fetch('http://35.184.51.106:8080/essbase/logout');
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


