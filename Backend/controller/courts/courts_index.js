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

module.exports = router