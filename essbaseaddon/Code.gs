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
    .createMenu('Essbase Connector')
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



function makeConnectCall(connecturl, olapServerName, userName, password) {
  // Make a POST request with form data.
  //alert('makeconnectcall');
  Logger.log('makeConnectCall...');
  Logger.log("connecturl" + connecturl);
  Logger.log("olapServerName" + olapServerName);
  Logger.log("userName" + userName);
  Logger.log("password" + password);

  // Make a POST request with a JSON payload.
  var str = 'Bob Smith';
  var data = {
    'name': str,
    'age': 35,
    'pets': ['fido', 'fluffy']
  };
  var options = {
    'method': 'post',
    'contentType': 'application/json',
    // Convert the JavaScript object to a JSON string.
    'payload': JSON.stringify(data)
  };
  var response = UrlFetchApp.fetch('https://httpbin.org/post', options);
  Logger.log(response);

  Logger.log('done with call..');
}

function showError(message) {
  //document.getElementById('result').innerHTML = 'Error: ' + message;
  //alert('showerror');
  Logger.log('inside the show error' + message);
}

function sayHello() {
  Logger.log('sayhello..');
}


