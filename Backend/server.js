var express = require('express');
var mysql = require('mysql');
var app = express();
var admin = require('firebase-admin');
//const {OAuth2Client} = require('google-auth-library');
const bodyparser = require('body-parser');
const port = 8080;
//const CLIENT_ID = '397498185353-cujn0kce0e155ttq0p3gp8lm0l339amg.apps.googleusercontent.com';
//const client = new OAuth2Client(CLIENT_ID);
var serviceAccount = require("/home/m5/M5/badminton-connect-4976a-firebase-adminsdk-391bo-e5eaea9e1a.json");

//import { admin } from './firebase-config'

app.use(bodyparser.json());


/*
Google Auth function Start
*/
/*
async function verify() {
	const ticket = await client.verifyIdToken({
		idToken: token,
		audience: CLIENT_ID,  // Specify the CLIENT_ID of the app that accesses the backend
		// Or, if multiple clients access the backend:
		//[CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3]
	});
	const payload = ticket.getPayload();
	const userid = payload['sub'];
	// If request specified a G Suite domain:
	// const domain = payload['hd'];
  }
  verify().catch(console.error);

*/

/*
Google Auth function End
*/




/*
FCM function Start
*/
admin.initializeApp({
	credential: admin.credential.cert(serviceAccount),
  databaseURL: 'https://badminton-connect-4976a.firebaseio.com'
});

const notification_options = {
    priority: "high",
    timeToLive: 60 * 60 * 24
};

const message_notification = {
	notification: {
		title: "First notification",
		body: "Hello world!"
	}
};

// This registration token comes from the client FCM SDKs.
const naomi_registrationToken = 'f5ThjTDyTTukl2bASqxvf0:APA91bGEpVj3dLea-o5CBap7jiwSGl6mzzuhzS1BjAk09Jyiw9jockue2wZGZUaal5WboPdbGE3lZrHgzNYLllMu2PQ56bDrXXiN40ENf6AHmy8Mxv3wu4-T7Iml3Lxazzgo8AOYkcw5';
const neo_registrationToken = 'emEZxhmdTNuVuYbNPnZ8Qj:APA91bFSl94lxqIc8I9yeJFuERELO-XKtEjcb5KTBPVJuMMhfPTW4Pm1MiT0jAoGMTXOStAQQK0ZnlQapmXjw4tLseUKGE75amz_YQ8i60kD7F2Kv8cgaJBU8mb6g0PO-goxN9SlxPkW';

// Send a message to the device corresponding to the provided
// registration token.
/*
admin.messaging().sendToDevice(neo_registrationToken, message_notification, notification_options)
  .then((response) => {
    // Response is a message ID string.
    console.log('Successfully sent message:', response);
  })
  .catch((error) => {
    console.log('Error sending message:', error);
});
*/

/*
app.post('/firebase/notification', (req, res)=>{
    const registrationToken = neo_registrationToken;
    const message = message_notification;
    const options =  notification_options;
    
      admin.messaging().sendToDevice(registrationToken, message, options)
      .then( response => {

       res.status(200).send("Notification sent successfully")
       
      })
      .catch( error => {
          console.log(error);
      });
})
*/


/*
FCM function End
*/



/*
Database function Start
*/
const db = mysql.createConnection({
	host: 'localhost',
	user: 'root',
	password: 'root',
	database: 'badminton_connect',
	multipleStatements: true
});

db.connect();

/*
Database function End
*/


/*
Endpoints
*/

// get time of server, part of M5
app.get('/time', (req, res) => {
	// Send a message to the device corresponding to the provided
	// registration token.
	admin.messaging().sendToDevice(neo_registrationToken, message_notification, notification_options)
	.then((response) => {
		// Response is a message ID string.
		console.log('Successfully sent message:', response);
	})
	.catch((error) => {
		console.log('Error sending message:', error);
	});
	var date = new Date();
	res.send(date.toString())
})

/*
User tables requests
*/

// get all users
app.get('/users', (req, res) => {
	const sql = 'SELECT * FROM users';
	admin.messaging().sendToDevice(naomi_registrationToken, message_notification, notification_options)
	.then((response) => {
		// Response is a message ID string.
		console.log('Successfully sent message:', response);
	})
	.catch((error) => {
		console.log('Error sending message:', error);
	});
  
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