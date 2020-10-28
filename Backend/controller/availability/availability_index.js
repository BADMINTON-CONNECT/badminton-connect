/*

THIS IS THE availability index


*/
const express = require('express')
const router = express.Router()
const db = require('../../database/mysql')
const admin = require("../../firebase/notification");

router.post('/', (req, res) => {
	const sql = 'INSERT INTO availability (user_id,day,hour,skill,location_x,location_y,max_dist) VALUES (?,?,?,?,?,?,?)';
	var body = req.body;
	//Hi Neo .
	console.log(body.user_id);
	console.log(body.skill);
	console.log(body.x);
	console.log(body.y);
	console.log(body.distance);

	const sql_del = 'DELETE FROM availability WHERE user_id = ?';

	db.query(sql_del, [body.user_id], (err, result) => {
		if (err) throw err;
	});

	for(var day in body.hours_available) {
		// console.log(body.hours_available[day].day);
		for(var hour in body.hours_available[day].hour) {
			db.query(sql, [body.user_id, body.hours_available[day].day, body.hours_available[day].hour[hour], body.skill, body.x, body.y, body.distance], (err, result) => {
				if (err) throw err;
				//res.send(result)
			})
			console.log(body.hours_available[day].day);
			console.log(body.hours_available[day].hour[hour]);
		}
	}
	res.send("Success");
});

module.exports = router