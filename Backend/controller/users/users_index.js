/*

THIS IS USERS INDEX

*/

const express = require("express");
const router = express.Router();
const db = require("../../database/mysql");
const admin = require("../../firebase/notification");
const logger = require("../../logs/logger.js");
const usersLogger = logger.usersLogger;

function getAllUsers(req, res) {
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

function getUserIdByEmail(req, res) {
	const sql = "select user_id from users where email = ?";

	return db.query(sql, [req.query.email], (err, result) => {
		if (err) {
			throw err;
		}
		res.send(result);
	});
}

function getSpecificUser(req, res) {
	const sql = "SELECT * FROM users WHERE user_id = ?";

	return db.query(sql, [req.params.id], (err, row, field) => {
		if (err) {
			throw err;
		}
		res.send(row[0]);
	});
}


function deleteSpecificUser(req, res) {
	const sql = "DELETE FROM users WHERE user_id = ?";

	return db.query(sql, [req.params.id], (err, result) => {
		if (err) {
			throw err;
		}
		res.send("Deleted succesfully");
	});
}

function insertUser(req, res) {
	var body = req.body;
	if (body.first_name == null && body.last_name == null && body.email == null) {
		usersLogger.info("null is being inserted");
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
					if (err) {
						throw err;
					}
					usersLogger.info("user already exist at user id: " + row[0].user_id);
					res.send("" + row[0].user_id);
				});
			}
			else {
				// if not already exists, insert and return the user_id
				usersLogger.info("inserted at user id: " + result.insertId);
				res.send("inserted at user id: " + result.insertId);
			}
		});
	}
}

function updateUserInfo(req, res) {
	var body = req.body;
	const sql = "UPDATE users SET first_name = ?, last_name = ?, skill_level = ?, distance_preference = ? WHERE user_id = ?";
	
    return db.query(sql, [body.first_name, body.last_name, body.skill_level, body.distance_preference, req.params.id], (err, result) => {
        if (err) {
			throw err;
		}
        res.send(result);
    });
}

function updateUserLocation(req, res) {
	var body = req.body;
	const sql = "UPDATE users SET location_x = ?, location_y = ? WHERE user_id = ?";
	
    return db.query(sql, [body.location_x, body.location_y, req.params.id], (err, result) => {
        if (err) {
			throw err;
		}
        res.send(result);
    });
}

function updateUserToken(req, res) {
	var body = req.body;
	const sql = "UPDATE users set Registration_Token = ? WHERE user_id = ?";

	return db.query(sql, [body.Registration_Token, req.params.id], (err, result) => {
		if (err) {
			throw err;
		}
		res.send(result);
	});
}

router.get("/email", getUserIdByEmail);
router.get("/", getAllUsers);
router.get("/:id", getSpecificUser);
router.delete("/:id", deleteSpecificUser);
router.post("/", insertUser);
router.put("/:id", updateUserInfo);
router.put("/location/:id", updateUserLocation);
router.put("/RegistrationToken/:id", updateUserToken);


module.exports = router;