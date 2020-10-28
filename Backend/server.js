var express = require('express');
var app = express();

const bodyparser = require('body-parser');
const port = 8080;

const db = require('./database/mysql')
const admin = require("./firebase/notification");
const timeController = require('./controller/time/time_index');
const usersController = require('./controller/users/users_index');
const courtsController = require('./controller/courts/courts_index');
const bookingsController = require('./controller/bookings/bookings_index');
const availabilityController = require('./controller/availability/availability_index');


app.use(bodyparser.json());


/*
Endpoints
*/

// getting the server time, part of M5
app.use('/time', timeController);

// dealing with the useres table from database 
app.use('/users', usersController);

// dealing with the courts table from database 
app.use('/courts', courtsController);

// dealing with the bookings table from database 
app.use('/bookings', bookingsController);

// dealing with the availability table from database 
app.use('/availability', availabilityController);


app.listen(port, () => {
  console.log(`Server app listening at http://40.88.38.140:${port}`)
})
