var express = require('express');
var mysql = require('mysql');
var app = express();
//var admin = require('firebase-admin');
const bodyparser = require('body-parser');
const port = 8080;

app.use(bodyparser.json());

/*
admin.initializeApp({
  credential: admin.credential.applicationDefault(),
  databaseURL: 'https://<DATABASE_NAME>.firebaseio.com'
});
*/

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

/*
User tables requests
*/

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

//Insert an user
app.post('/users', (req, res) => {
    var body = req.body;
	const sql = 'INSERT IGNORE INTO users SET IDToken = ?, first_name = ?, last_name = ?, email = ?';
	const token = body.IDToken;
    db.query(sql, [body.IDToken, body.first_name, body.last_name, body.email], (err, result) => {
		if (err) throw err;
		if (result.affectedRows == 0 && result.warningCount == 1) {
			// duplicate happened 
			// get the user_id somehow?
			console.log("User already existed - duplicate token");
			res.send("User already existed - duplicate token");
		}
		else {
			console.log("inserted at user id: " + result.insertId);
			res.send("Inserted at user id: " + result.insertId);
		}
    })
});

//Update an employees
app.put('/users/:id', (req, res) => {
    var body = req.body;
	const sql = 'UPDATE users SET email = ? WHERE user_id = ?';
	//UPDATE `m5trial`.`users` SET `first_name` = 'David', `last_name` = 'Kang' WHERE (`id` = '5'); -> this updates multiple thigns at once
	/*
	This calls a "procedure" to do data parse basically -> need to create procedure in the database
	var sql = "SET @EmpID = ?;SET @Name = ?;SET @EmpCode = ?;SET @Salary = ?; \
    CALL EmployeeAddOrEdit(@EmpID,@Name,@EmpCode,@Salary);";
	*/
    db.query(sql, [body.email, req.params.id], (err, result) => {
        if (err) throw err;
        res.send(result)
    })
});

/*
Court availability table 
*/

// get all courts availability for all dates
app.get('/courts', (req, res) => {
	const sql = 'SELECT * FROM courts';
  
	db.query(sql, (err, result) => {
	  if (err) throw err;
	  res.send(result);
	});
});

// get specific date
app.get('/courts/:year/:month/:date', (req, res) => {
	var sql = 'SELECT * FROM courts WHERE year = ? AND month = ? AND date = ?';
	db.query(sql, [req.params.year, req.params.month, req.params.date], (err, result) => {
	  if (err) throw err;
	  res.send(result);
	});
});

//Update an court availability 
app.put('/courts', (req, res) => {
    var body = req.body;
	const sql = 'UPDATE courts SET time_slot1 = ?, time_slot2 = ?, time_slot3 = ?, time_slot4 = ? WHERE court_id = ?';
    db.query(sql, [body.time_slot1, body.time_slot2, body.time_slot3, body.time_slot4, body.court_id], (err, result) => {
        if (err) throw err;
        res.send(result)
    })
});


app.listen(port, () => {
  console.log(`Example app listening at http://localhost:${port}`)
})



/* example
// getting data from client 
app.post('/client', (req, res) => {
    var body = req.body;
	
	console.log("Got this from the client: " + body.first_name)
	console.log("Got this from the client: " + body.last_name)
	console.log("Got this from the client: " + body.email)

	res.send("got it!!");
});
*/

/*
// This registration token comes from the client FCM SDKs.
var registrationToken = 'YOUR_REGISTRATION_TOKEN';

var message = {
  data: {
    score: '850',
    time: '2:45'
  },
  token: registrationToken
};

// Send a message to the device corresponding to the provided
// registration token.
admin.messaging().send(message)
  .then((response) => {
    // Response is a message ID string.
    console.log('Successfully sent message:', response);
  })
  .catch((error) => {
    console.log('Error sending message:', error);
  });
  */