function testFunction() {
  var documentProperties = PropertiesService.getDocumentProperties();
  var sheets = SpreadsheetApp.getActive().getSheets();
  
  documentProperties.setProperty(sheets[0].getSheetId(), sheets[0].getSheetName());
  documentProperties.setProperty(sheets[1].getSheetId(), sheets[1].getSheetName());
  
  Logger.log(""+documentProperties.getProperty(sheets[0].getSheetId()));
   Logger.log(""+documentProperties.getProperty(sheets[1].getSheetId()));
             
}
