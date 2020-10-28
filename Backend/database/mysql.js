var mysql = require('mysql');

const db = mysql.createConnection({
	host: 'localhost',
	user: 'root',
	password: 'root',
	database: 'badminton_connect',
	multipleStatements: true
});

db.connect();

module.exports = db