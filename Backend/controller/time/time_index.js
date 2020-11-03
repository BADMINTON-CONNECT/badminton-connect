/*

THIS IS TIME INDEX

*/

const express = require("express");
const router = express.Router();

router.get("/", (req, res) => {
	var date = new Date();
	res.send(date.toString());
});

module.exports = router;