var admin = require("firebase-admin");

var serviceAccount = require("/home/m5/M5/badminton-connect-4976a-firebase-adminsdk-391bo-e5eaea9e1a.json");

const notification_options = {
  priority: "high",
  timeToLive: 60 * 60 * 24
};

admin.initializeApp({
	credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://badminton-connect-4976a.firebaseio.com"
});

function sendPushNotification(reg_token, notification_title, notification_body) {
  
  const message_notification = {
    notification: {
      title: notification_title,
      body: notification_body
    }
  };

  admin.messaging().sendToDevice(reg_token, message_notification, notification_options)
	.then((response) => {
		// Response is a message ID string.
		console.log("Successfully sent message:", response);
	})
	.catch((error) => {
		console.log("Error sending message:", error);
  });
  
}




module.exports = admin;
module.exports.sendPushNotification = sendPushNotification;