/*

THIS IS BOOKINGS INDEX

*/

const express = require('express')
const router = express.Router()
const db = require('../../database/mysql')
const admin = require("../../firebase/notification");


// get all the bookings 
router.get('/', (req, res) => {
	const sql = 'SELECT * FROM bookings';

	db.query(sql, (err, result) => {
		if (err) throw err;
		res.send(result);
	})
})

// get all the booking by a specific user
router.get('/:id', (req, res) => {
	const sql = 'SELECT * FROM bookings WHERE user_id = ?';
  
	db.query(sql, [req.params.id], (err, result) => {
	  if (err) throw err;
	  res.send(result);
	});
});

// post of booking 
router.post('/', (req, res) => {
    var body = req.body;
	const sql = 'INSERT INTO bookings (user_id, Year, Month, Date, time_slot1, time_slot2, time_slot3, time_slot4) values (?, ?, ?, ?, ?, ?, ?, ?)';
    db.query(sql, [body.user_id, body.Year, body.Month, body.Date, body.time_slot1, body.time_slot2, body.time_slot3, body.time_slot4], (err, result) => {
        if (err) throw err;
        res.send(result)
    })
});

// delete(cancel) an booking
router.delete('/:id', (req, res) => {
	const sql = 'DELETE FROM bookings WHERE booking_id = ?';
  
	db.query(sql, [req.params.id], (err, result) => {
	  if (err) throw err;
	  res.send("Deleted succesfully");
	});
});



function notify_bookings_near() {
	date = new Date();
	//console.log(date.toString());
	const current_year = date.getFullYear();
	const current_month = date.getMonth(); // month is 0 indexed
	const current_date = date.getDate();

	const sql = 'SELECT DISTINCT users.Registration_Token FROM users INNER JOIN bookings ON users.user_id = bookings.user_id WHERE bookings.Year = ? and bookings.Month = ? and bookings.Date = ?';

	db.query(sql, [current_year, current_month+1, current_date], (err, row) => {
		if (err) throw err;
		for(var r in row) {
			//console.log(row[r].Registration_Token);
			if (row[r].Registration_Token != null) {
				admin.sendPushNotification(row[r].Registration_Token, 'Booking Notification', 'Your booking is coming up today!');
			}
		}
	})
	
}



//setInterval(check_bookings_near, 1500); // this is in ms
// how often do i need to call this??
// how to prevent from users already notified to get notify again? 
notify_bookings_near();

module.exports = router