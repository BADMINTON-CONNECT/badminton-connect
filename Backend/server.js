var express = require('express');
var mysql = require('mysql');
var app = express();
const port = 8080;

app.get('/time', (req, res) => {
  var date = new Date();
  res.send(date.toString())
})

app.listen(port, () => {
  console.log(`Example app listening at http://localhost:${port}`)
})

