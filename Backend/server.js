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

// get time of server, part of M5
app.get('/time', (req, res) => {
  var date = new Date();
  res.send(date.toString())
});

// get all users
app.get('/users', (req, res) => {
  const sql = 'SELECT * FROM users';

  db.query(sql, (err, result) => {
    if (err) throw err;
    res.send(result);
  });
});

// get specific user
app.get('/users/:id', (req, res) => {
  const sql = 'SELECT * FROM users WHERE user_id = ?';

  db.query(sql, [req.params.id], (err, result) => {
    if (err) throw err;
    res.send(result);
  });
});

// delete an user: needs to use POSTMAN 
app.delete('/users/:id', (req, res) => {
  const sql = 'DELETE FROM users WHERE user_id = ?';

  db.query(sql, [req.params.id], (err, result) => {
    if (err) throw err;
    res.send("Deleted succesfully");
  });
});

app.post('/users/add', (req, res) => {
  let data = [req.body.name, req.body.username, req.body.email];
  const sql = 'INSERT INTO users (first_name, last_name, email) VALUES (?, ?, ?)';
  db.query(sql, data, (err, result) => {
    if (err) throw err;
    res.send(result);
  });
});

app.listen(port, () => {
  console.log(`Example app listening at http://localhost:${port}`)
})

