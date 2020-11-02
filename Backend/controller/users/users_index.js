/*

THIS IS USERS INDEX

*/

const express = require('express')
const router = express.Router()
const db = require('../../database/mysql')
const admin = require("../../firebase/notification");

function get_all_users(req, res) {
	const sql = 'SELECT * FROM users';

	return new Promise ( (resolve, reject) => {
		db.query(sql, (err, result) => {
			if (err) return reject(err);
			return resolve(result);
		})
	})
	.then ( (result) => {
		return res.send(result)
	})
	.catch( (err) => {
		throw err;
	})


	/*
	return db.query(sql, (err, result) => {
	  if (err) throw err;
	  res.send(result);
	});
	*/
}

function get_specific_user(req, res) {
	const sql = 'SELECT * FROM users WHERE user_id = ?';

	return db.query(sql, [req.params.id], (err, row, field) => {
		if (err) throw err;
		res.send(row[0]);
	  });
}

function delete_specific_user(req, res) {
	const sql = 'DELETE FROM users WHERE user_id = ?';

	return db.query(sql, [req.params.id], (err, result) => {
		if (err) throw err;
		res.send("Deleted succesfully");
	  });
}

function insert_user(req, res) {
	var body = req.body;
	const sql = 'INSERT IGNORE INTO users SET first_name = ?, last_name = ?, email = ?';
	const email = body.email;
    return db.query(sql, [body.first_name, body.last_name, body.email], (err, result) => {
		if (err) throw err;
		if (result.affectedRows == 0 && result.warningCount == 1) {
			// duplicate happened 
			
			db.query('select user_id from users where email = ?', [email], (err, row, field) => {
				//console.log(row[0].user_id);
				if (err) throw err;
				console.log("user already exist at user id: " + row[0].user_id);
				res.send("" + row[0].user_id);
			})
		}
		else {
			console.log("inserted at user id: " + result.insertId);
			res.send("inserted at user id: " + result.insertId);
		}
	})
}

function update_user(req, res) {
	var body = req.body;
	const sql = 'UPDATE users SET first_name = ?, last_name = ?, email = ?, location_x = ?, location_y = ?, skill_level = ?, distance_preference = ? WHERE user_id = ?';
	
    return db.query(sql, [body.first_name, body.last_name, body.email, body.location_x, body.location_y, body.skill_level, body.distance_preference, req.params.id], (err, result) => {
        if (err) throw err;
        res.send(result)
    })
}

function update_user_token(req, res) {
	var body = req.body;
	const sql = 'UPDATE users set Registration_Token = ? WHERE user_id = ?';

	return db.query(sql, [body.Registration_Token, req.params.id], (err, result) => {
		if (err) throw err;
		res.send(result);
	})
}

router.get('/', get_all_users);
router.get('/:id', get_specific_user);
router.delete('/:id', delete_specific_user);
router.post('/', insert_user);
router.put('/:id', update_user);
router.put('/RegistrationToken/:id', update_user_token);


module.exports = router
module.exports.get_all_users = get_all_users

// get all users
/*
router.get('/', (req, res) => {
	const sql = 'SELECT * FROM users';

	admin.sendPushNotification('emEZxhmdTNuVuYbNPnZ8Qj:APA91bFSl94lxqIc8I9yeJFuERELO-XKtEjcb5KTBPVJuMMhfPTW4Pm1MiT0jAoGMTXOStAQQK0ZnlQapmXjw4tLseUKGE75amz_YQ8i60kD7F2Kv8cgaJBU8mb6g0PO-goxN9SlxPkW', 'hello', 'world')
  
	db.query(sql, (err, result) => {
	  if (err) throw err;
	  res.send(result);
	});
});
*/

// get specific user
/*
router.get('/:id', (req, res) => {
	const sql = 'SELECT * FROM users WHERE user_id = ?';
  
	db.query(sql, [req.params.id], (err, row, field) => {
	  if (err) throw err;
	  res.send(row[0]);
	});
});
*/
// delete an user
/*
router.delete('/:id', (req, res) => {
	const sql = 'DELETE FROM users WHERE user_id = ?';
  
	db.query(sql, [req.params.id], (err, result) => {
	  if (err) throw err;
	  res.send("Deleted succesfully");
	});
});
*/
//Insert an user
/*
router.post('/', (req, res) => {
    var body = req.body;
	const sql = 'INSERT IGNORE INTO users SET IDToken = ?, first_name = ?, last_name = ?, email = ?';
	const token = body.IDToken;
	const email = body.email;
    db.query(sql, [body.IDToken, body.first_name, body.last_name, body.email], (err, result) => {
		if (err) throw err;
		if (result.affectedRows == 0 && result.warningCount == 1) {
			// duplicate happened 
			
			db.query('select user_id from users where email = ?', [email], (err, row, field) => {
				//console.log(row[0].user_id);
				console.log("" + row[0].user_id);
				res.send("" + row[0].user_id);
			})
		}
		else {
			console.log("inserted at user id: " + result.insertId);
			res.send("inserted at user id: " + result.insertId);
		}
	})
});
*/
// Update an user preerfence
/*
router.put('/:id', (req, res) => {
    var body = req.body;
	const sql = 'UPDATE users SET location_x = ?, location_y = ?, skill_level = ?, distance_preference = ? WHERE user_id = ?';
	
    db.query(sql, [body.location_x, body.location_y, body.skill_level, body.distance_preference, req.params.id], (err, result) => {
        if (err) throw err;
        res.send(result)
    })
});
*/

// puts in a registration token for push notification for the user 
/*
router.put('/RegistrationToken/:id', (req, res) => {
	var body = req.body;
	const sql = 'UPDATE users set Registration_Token = ? WHERE user_id = ?';

	db.query(sql, [body.Registration_Token, req.params.id], (err, result) => {
		if (err) throw err;
		res.send(result);
	})
})
*/