var admin = require("firebase-admin");
const logger = require("../../logs/logger.js");
const firebaseLogger = logger.firebaseLogger;

var serviceAccount = require("/home/m5/M5/badminton-connect-4976a-firebase-adminsdk-391bo-e5eaea9e1a.json");

const notificationOptions = {
  priority: "high",
  timeToLive: 60 * 60 * 24
};

admin.initializeApp({
	credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://badminton-connect-4976a.firebaseio.com"
});

function sendPushNotification(regToken, notificationTitle, notificationBody) {
  
  const messageNotification = {
    notification: {
      title: notificationTitle,
      body: notificationBody
    }
  };

  admin.messaging().sendToDevice(regToken, messageNotification, notificationOptions)
	.then((response) => {
    // Response is a message ID string.
    firebaseLogger.info("Successfully sent message:", response);
	})
	.catch((error) => {
    firebaseLogger.error("Error sending message:", error);
  });
  
}




module.exports = admin;
module.exports.sendPushNotification = sendPushNotification;