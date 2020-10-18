var express = require('express');
var mysql = require('mysql');
var app = express();
const bodyparser = require('body-parser');
const port = 8080;

app.use(bodyparser.json());

const db = mysql.createConnection({
  host: 'localhost',
  user: 'root',
  password: 'root',
  database: 'badminton_connect'
});

db.connect();

app.get('/users', (req, res) => {
  const sql = 'SELECT * FROM users';

  db.query(sql, (err, result) => {
    if (err) throw err;
    res.send(result);
  });
});

app.get('/time', (req, res) => {
  var date = new Date();
  res.send(date.toString())
})

app.listen(port, () => {
  console.log(`Example app listening at http://localhost:${port}`)
})

