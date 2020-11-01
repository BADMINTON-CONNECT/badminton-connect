/*

THIS IS TIME INDEX

*/

const express = require('express')
const router = express.Router()
const db = require('../../database/mysql')
const admin = require("../../firebase/notification");

router.get('/', (req, res) => {
	// Send a message to the device corresponding to the provided
    // registration token.
    /*
	admin.messaging().sendToDevice(neo_registrationToken, message_notification, notification_options)
	.then((response) => {
		// Response is a message ID string.
		console.log('Successfully sent message:', response);
	})
	.catch((error) => {
		console.log('Error sending message:', error);
    });
    */
	var date = new Date();
	res.send(date.toString())
})

module.exports = router