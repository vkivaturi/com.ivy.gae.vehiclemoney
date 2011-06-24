google.load('visualization', '1', {packages: ['gauge']});
google.load('visualization', '1', {packages: ['table']});

function drawVisualization() {
  // Create and populate the data table.
  var data = new google.visualization.DataTable();
  data.addColumn('string', 'Label');
  data.addColumn('number', 'Value');
  data.addRows(3);
  data.setValue(0, 0, 'Kms/day');
  data.setValue(0, 1, parseInt(document.getElementById('kmspd').value));
  data.setValue(1, 0, 'Ltrs/day');
  data.setValue(1, 1, parseInt(document.getElementById('ltrpd').value));
  data.setValue(2, 0, 'Rs/day');
  data.setValue(2, 1, parseInt(document.getElementById('rpspd').value));
  
  var options = {width: 400, height: 120, redFrom: 90, redTo: 100,
          yellowFrom:75, yellowTo: 90, minorTicks: 5};

  // Create and draw the visualization.
  new google.visualization.Gauge(document.getElementById('gauge_div')).
      draw(data, options);
}

function drawTable() {
    var data = new google.visualization.DataTable();
    data.addColumn('string', 'Expense Date');
    data.addColumn('string', 'Distance Reading');
    data.addColumn('boolean', 'Volume Filled');
    data.addColumn('string', 'Unit Price');
    data.addRows(4);
    data.setCell(0, 0, 'Mike');
    data.setCell(0, 1, '10000');
    data.setCell(0, 2, true);
    data.setCell(1, 0, 'Jim');
    data.setCell(1, 1, '8000');
    data.setCell(1, 2, false);
    data.setCell(2, 0, 'Alice');
    data.setCell(2, 1, '12500');
    data.setCell(2, 2, true);
    data.setCell(3, 0, 'Bob');
    data.setCell(3, 1, '7000');
    data.setCell(3, 2, true);

    var table = new google.visualization.Table(document.getElementById('table_div'));
    table.draw(data, {showRowNumber: true});
  }