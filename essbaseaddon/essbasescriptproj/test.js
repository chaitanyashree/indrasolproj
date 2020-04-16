function testFunction() {
 Logger.log('testFucntion...');
  var rangeList = SpreadsheetApp.getActive().getSelection().getActiveRangeList();
  var ranges = rangeList.getRanges();
  Logger.log(ranges.length);
  
  var mmap=[];
  for (var i = 0; i < ranges.length; i++) {
    var na = [];
    Logger.log('Active Ranges: ' + ranges[i].getA1Notation());
    Logger.log(ranges[i].getRow());
    Logger.log(ranges[i].getColumn());
    Logger.log(ranges[i].getHeight());
    Logger.log(ranges[i].getWidth());
    na.push(ranges[i].getRow());
    na.push(ranges[i].getColumn());
    mmap.push(na);
      
  }
  Logger.log(mmap);
 
//  var range = SpreadsheetApp.getActive().getSelection().getActiveRange();
//  Logger.log(range.getA1Notation());
  
//  Logger.log(rangeArry[0].getRow());
//  Logger.log(rangeArry[0].getLastRow());
//  Logger.log(rangeArry[0].getRowIndex());
//  Logger.log(rangeArry[0].getColumn());
//  Logger.log(rangeArry[0].getLastColumn());
//  Logger.log(rangeArry[0].getA1Notation());
//  
//  Logger.log('---------');
//  Logger.log(rangeArry[0].getHeight());
//  Logger.log(rangeArry[0].getWidth());
  
//  Logger.log(rangeArry[1].getRow());
//  Logger.log(rangeArry[1].getLastRow());
//  Logger.log(rangeArry[1].getColumn());
//  Logger.log(rangeArry[1].getLastColumn());
//  Logger.log(rangeArry[1].getA1Notation());
}
