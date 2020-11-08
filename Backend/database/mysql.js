var mysql = require("mysql");
const logger = require("../../logs/logger.js");
const mysqlLogger = logger.mysqlLogger;

var db;

function connectDb() {
	db = mysql.createConnection({
		host: "localhost",
		user: "root",
		password: "root",
		database: "badminton_connect",
		multipleStatements: true
	});
	
	db.connect( (err) => {
		if (err) {
			mysqlLogger.error("Error when connecting to the database: ", + err);
			setTimeout(connectDb, 2000);
		}
	});

	db.on("error", (err) => {
		mysqlLogger.error("database error" + err);
		if (err.code === "PROTOCOL_CONNECTION_LOST") {
			mysqlLogger.error("Error caught, re-creating the connection");
			connectDb();
		}
		else {
			throw err;
		}
	});
}

connectDb();

module.exports = db;