var mysql = require('mysql');

var db;

function connect_db() {
	db = mysql.createConnection({
		host: 'localhost',
		user: 'root',
		password: 'root',
		database: 'badminton_connect',
		multipleStatements: true
	});
	
	db.connect( (err) => {
		if (err) {
			console.log("Error when connecting to the database: ", + err);
			setTimeout(connect_db, 2000);
		}
	});

	db.on('error', (err) => {
		console.log('database error' + err);
		if (err.code === 'PROTOCOL_CONNECTION_LOST') {
			console.log("Error caught, re-creating the connection");
			connect_db();
		}
		else {
			throw err;
		}
	})
}

connect_db();

module.exports = db