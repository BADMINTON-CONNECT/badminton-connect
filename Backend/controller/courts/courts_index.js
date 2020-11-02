/*

THIS IS COURTS INDEX

*/

const DAILY = 86400000; //1 * 24 * 60 * 60 * 1000 ms
const express = require('express')
const router = express.Router()
const db = require('../../database/mysql')
const admin = require("../../firebase/notification");



// get all courts availability for all dates
router.get('/', (req, res) => {
	const sql = 'SELECT * FROM courts';
  
	db.query(sql, (err, result) => {
	  if (err) throw err;
	  res.send(result);
	});
});

// get specific date
router.get('/:year/:month/:date', (req, res) => {
	var sql = 'SELECT * FROM courts WHERE year = ? AND month = ? AND date = ?';
	db.query(sql, [req.params.year, req.params.month, req.params.date], (err, result) => {
	  if (err) throw err;
	  res.send(result);
	});
});

//Update an court availability 
router.put('/', (req, res) => {
    var body = req.body;
	const sql = 'UPDATE courts SET time_slot1 = ?, time_slot2 = ?, time_slot3 = ?, time_slot4 = ? WHERE court_id = ?';
    db.query(sql, [body.time_slot1, body.time_slot2, body.time_slot3, body.time_slot4, body.court_id], (err, result) => {
        if (err) throw err;
        res.send(result)
    })
});


// set up court stats a week in advance 
function add_court_date() {
	const sql = 'INSERT INTO courts (year, month, date, time_slot1, time_slot2, time_slot3, time_slot4) VALUES (?, ?, ?, 10, 10, 10, 10)'

	date = new Date();
	date.setDate(d.getDate() + 7);
	const current_year = date.getFullYear();
	const current_month = date.getMonth(); // month is 0 indexed
	const current_date = date.getDate();

	db.query(sql, [current_year, current_month+1, current_date], (err, row) => {
		if (err) throw err;
		console.log("Inserted court for: " + date.toString());
	})
}

// setInterval(add_court_date, DAILY); 


// delete court that's a week old? 
function delete_court() {
	const sql = 'DELETE FROM courts WHERE year = ? AND month = ? AND date = ?';

	date = new Date();
	date.setDate(d.getDate() - 7);
	const current_year = date.getFullYear();
	const current_month = date.getMonth(); // month is 0 indexed
	const current_date = date.getDate();

	db.query(sql, [current_year, current_month+1, current_date], (err, row) => {
		if (err) throw err;
		console.log("Deleted court for: " + date.toString());
	})
}

// setInterval(add_court_date, DAILY); 

module.exports = router