/*

THIS IS USERS INDEX

*/

const express = require("express");
const router = express.Router();
const db = require("../../database/mysql");
const admin = require("../../firebase/notification");

function get_all_users(req, res) {
	const sql = "SELECT * FROM users";

	return new Promise ( (resolve, reject) => {
		db.query(sql, (err, result) => {
			if (err) {
				return reject(err);
			}
			return resolve(result);
		});
	})
	.then ( (result) => {
		return res.send(result);
	})
	.catch( (err) => {
		throw err;
	});
}

function get_userid_by_email(req, res) {
	const sql = "select user_id from users where email = ?";

	return db.query(sql, [req.query.email], (err, result) => {
		if (err) {
			throw err;
		}
		res.send(result);
	});
}

function get_specific_user(req, res) {
	const sql = "SELECT * FROM users WHERE user_id = ?";

	return db.query(sql, [req.params.id], (err, row, field) => {
		if (err) {
			throw err;
		}
		res.send(row[0]);
	});
}


function delete_specific_user(req, res) {
	const sql = "DELETE FROM users WHERE user_id = ?";

	return db.query(sql, [req.params.id], (err, result) => {
		if (err) {throw err;
		}
		res.send("Deleted succesfully");
	});
}

function insert_user(req, res) {
	var body = req.body;
	if (body.first_name == null && body.last_name == null && body.email == null) {
		console.log("null is being inserted");
		res.send("null is being inserted");
	}
	else {
		// email is the unique key that is being ignored if already exists
		const sql = "INSERT IGNORE INTO users SET first_name = ?, last_name = ?, email = ?";
		const email = body.email;
		return db.query(sql, [body.first_name, body.last_name, body.email], (err, result) => {
			if (err) {
				throw err;
			}
			if (result.affectedRows === 0 && result.warningCount === 1) {
				// if user already exists, return user_id to the front end  
				
				db.query("select user_id from users where email = ?", [email], (err, row, field) => {
					if (err) throw err;
					console.log("user already exist at user id: " + row[0].user_id);
					res.send("" + row[0].user_id);
				});
			}
			else {
				// if not already exists, insert and return the user_id
				console.log("inserted at user id: " + result.insertId);
				res.send("inserted at user id: " + result.insertId);
			}
		});
	}
}

function update_user_info(req, res) {
	var body = req.body;
	const sql = "UPDATE users SET first_name = ?, last_name = ?, email = ?, skill_level = ?, distance_preference = ? WHERE user_id = ?";
	
    return db.query(sql, [body.first_name, body.last_name, body.email, body.skill_level, body.distance_preference, req.params.id], (err, result) => {
        if (err) throw err;
        res.send(result);
    });
}

function update_user_location(req, res) {
	var body = req.body;
	const sql = "UPDATE users SET location_x = ?, location_y = ? WHERE user_id = ?";
	
    return db.query(sql, [body.location_x, body.location_y, req.params.id], (err, result) => {
        if (err) throw err;
        res.send(result);
    });
}

function update_user_token(req, res) {
	var body = req.body;
	const sql = "UPDATE users set Registration_Token = ? WHERE user_id = ?";

	return db.query(sql, [body.Registration_Token, req.params.id], (err, result) => {
		if (err) throw err;
		res.send(result);
	});
}

router.get("/email", get_userid_by_email);
router.get("/", get_all_users);
router.get("/:id", get_specific_user);
router.delete("/:id", delete_specific_user);
router.post("/", insert_user);
router.put("/:id", update_user_info);
router.put("/location/:id", update_user_location);
router.put("/RegistrationToken/:id", update_user_token);


module.exports = router;