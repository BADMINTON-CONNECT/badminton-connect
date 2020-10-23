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
	database: 'badminton_connect',
	multipleStatements: true
});

db.connect();


// get time of server, part of M5
app.get('/time', (req, res) => {
	var date = new Date();
	res.send(date.toString())
})



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

// delete an user: needs to use POSTMAN this so far does not work
app.delete('/users/:id', (req, res) => {
	const sql = 'DELETE FROM users WHERE user_id = ?';
  
	db.query(sql, [req.params.id], (err, result) => {
	  if (err) throw err;
	  res.send("Deleted succesfully");
	});
});

//Insert an employees
app.post('/users', (req, res) => {
    var body = req.body;
    const sql = 'INSERT INTO users (first_name, last_name, email) values (?, ?, ?)';
    db.query(sql, [body.first_name, body.last_name, body.email], (err, result) => {
        if (err) throw err;
        res.send("Inserted at user id: " + result.insertId)
    })
});

//Update an employees
app.put('/users/:id', (req, res) => {
    var body = req.body;
	const sql = 'UPDATE users SET email = ? WHERE user_id = ?';
	//UPDATE `m5trial`.`users` SET `first_name` = 'David', `last_name` = 'Kang' WHERE (`id` = '5'); -> this updates multiple thigns at once
	/*
	This calls a function to do data parse basically -> need to create in the database
	var sql = "SET @EmpID = ?;SET @Name = ?;SET @EmpCode = ?;SET @Salary = ?; \
    CALL EmployeeAddOrEdit(@EmpID,@Name,@EmpCode,@Salary);";
	*/
    db.query(sql, [body.email, req.params.id], (err, result) => {
        if (err) throw err;
        res.send(result)
    })
});


app.listen(port, () => {
  console.log(`Example app listening at http://localhost:${port}`)
})
