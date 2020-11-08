var log4js = require("log4js");

log4js.configure({
    appenders: { 
        time: { type: "file", filename: "./logs/time.log" },
        users: { type: "file", filename: "./logs/users.log" },
        courts: { type: "file", filename: "./logs/courts.log" },
        bookings: { type: "file", filename: "./logs/bookings.log" },
        availabilitys: { type: "file", filename: "./logs/availabilitys.log" },
        firebase: { type: "file", filename: "./logs/firebase.log" },
        mysql: { type: "file", filename: "./logs/mysql.log" },
        server: {type: "file", filename: "./logs/server.log" }
    },
    categories: {
        time: { appenders: ["time"], level: "error" },
        users: { appenders: ["users"], level: "error" },
        courts: { appenders: ["courts"], level: "error" },
        bookings: { appenders: ["bookings"], level: "error" },
        availabilitys: { appenders: ["availabilitys"], level: "error" },
        firebase: { appenders: ["firebase"], level: "error" },
        mysql: { appenders: ["mysql"], level: "error" },
        server: { appenders: ["server"], level: "error" },
        default: { appenders: ["time"], level: "error" } 
    }
});

var timeLogger = log4js.getLogger("time");
var usersLogger = log4js.getLogger("users");
var courtsLogger = log4js.getLogger("courts");
var bookingsLogger = log4js.getLogger("bookings");
var availabilitysLogger = log4js.getLogger("availabilitys");
var firebaseLogger = log4js.getLogger("firebase");
var mysqlLogger = log4js.getLogger("mysql");
var serverLogger = log4js.getLogger("server");

module.exports = log4js;
module.exports.timeLogger = timeLogger;
module.exports.usersLogger = usersLogger;
module.exports.courtsLogger = courtsLogger;
module.exports.bookingsLogger = bookingsLogger;
module.exports.availabilitysLogger = availabilitysLogger;
module.exports.firebaseLogger = firebaseLogger;
module.exports.mysqlLogger = mysqlLogger;
module.exports.serverLogger = serverLogger;
