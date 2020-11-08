/*

THIS IS BOOKINGS INDEX

*/

const express = require("express");
const router = express.Router();
const db = require("../../database/mysql");
const admin = require("../../firebase/notification");
const logger = require("../../logs/logger.js");
const bookingsLogger = logger.bookingsLogger;

// get all the bookings 
router.get("/", (req, res) => {
	const sql = "SELECT * FROM bookings";

	db.query(sql, (err, result) => {
		if (err) {
			throw err;
		}
		res.send(result);
	});
});

// get all the booking by a specific user
router.get("/:id", (req, res) => {
	const sql = "SELECT * FROM bookings WHERE user_id = ?";
  
	db.query(sql, [req.params.id], (err, result) => {
		if (err) {
			throw err;
		}
		res.send(result);
	});
});

// post a booking 
router.post("/", (req, res) => {
    var body = req.body;
	const sql = "INSERT INTO bookings (user_id, Year, Month, Date, time_slot1, time_slot2, time_slot3, time_slot4) values (?, ?, ?, ?, ?, ?, ?, ?)";
    db.query(sql, [body.user_id, body.Year, body.Month, body.Date, body.time_slot1, body.time_slot2, body.time_slot3, body.time_slot4], (err, result) => {
        if (err) {
			throw err;
		}
        res.send(result);
    });
});

// delete(cancel) an booking
router.delete("/:id", (req, res) => {
	const sql = "DELETE FROM bookings WHERE booking_id = ?";
  
	db.query(sql, [req.params.id], (err, result) => {
		if (err) {
			throw err;
		}
		res.send("Deleted succesfully");
	});
});



function notifyBookingsNear() {
	var date = new Date();
	const currentYear = date.getFullYear();
	const currentMonth = date.getMonth(); // month is 0 indexed
	const currentDate = date.getDate();

	const sql = "SELECT DISTINCT users.Registration_Token FROM users INNER JOIN bookings ON users.user_id = bookings.user_id WHERE bookings.Year = ? and bookings.Month = ? and bookings.Date = ?";

	db.query(sql, [currentYear, currentMonth+1, currentDate], (err, row) => {
		if (err) {
			throw err;
		}
		for(var r in row) {
			if (row[parseInt(r, 10)].Registration_Token != null) {
				admin.sendPushNotification(row[parseInt(r, 10)].Registration_Token, "Booking Notification", "Your booking is coming up today!");
			}
		}
	});
}



//setInterval(notify_bookings_near, 3000); // 3 seconds
notifyBookingsNear();

module.exports = router;