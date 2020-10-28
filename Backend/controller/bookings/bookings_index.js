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

module.exports = router