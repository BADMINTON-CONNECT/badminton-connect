/*

THIS IS AVAILABILITY INDEX

*/

const express = require('express')
const router = express.Router()
const db = require('../../database/mysql')

// Simple request to see the database
router.get('/', (req, res) => {
	const sql = 'SELECT DISTINCT * FROM availability ORDER BY day asc, hour asc';

	db.query(sql, (err, result) => {
		if (err) throw err;
		res.send(result);
	})
})

/*
	Request to see the availability from a select user.
	Front end does not handle duplicates and it's too much work to handle them
	upon insert. So they are handled at requests.
	Front end also requested the response be formated the same as how it's given
	so here the data is formated into a "jason" array
*/
router.get('/:id', (req, res) => {
	const sql = 'SELECT DISTINCT day, hour FROM availability WHERE user_id = ? ORDER BY day asc, hour asc';
	var day_of_week = [];
	var hours = [];
	var day = -1;

	db.query(sql, [req.params.id], (err, result) => {
		if (err) throw err;

		for (var entry in result) {

			// The first entry does not need to be compared to anything
			if (entry == 0) {
				day = result[entry].day;
				hours.push(result[entry].hour);
			} 
			// If the current day is the same as the last, add the hour to the array
			else if (day == result[entry].day) {
				hours.push(result[entry].hour);
			} 
			// If the current day is different to the last, add the array of the last day and reset
			else {
				day_of_week.push({"day": day, "hours": hours});
				hours = [];
				hours.push(result[entry].hour);
				day = result[entry].day;
			}
		}

		// Check if no days were added (No data available)
		if (day == -1) {
			res.send(day_of_week);
		} 
		// Add the last day of hours
		else { 
			day_of_week.push({"day": day, "hours": hours});
			res.send(day_of_week);
		}
	})
})

/*
	Post request to update user availability. It is expensive to compare their new
	schedule to their old schedule, so instead of doing that we delete the old schedule
	and insert the new one.
*/
router.post('/:id', (req, res) => {
	const sql_get = 'SELECT * FROM users WHERE user_id = ?';
	const sql_ins = 'INSERT INTO availability (user_id,day,hour,skill,location_x,location_y,max_dist) VALUES (?,?,?,?,?,?,?)';
	const sql_del = 'DELETE FROM availability WHERE user_id = ?';
	var body = req.body;

	// Get the information for the user from the users database
	db.query(sql_get, [req.params.id], (err, row) => {
		if (err) throw err;

		// Delete the old schedule of the user
		db.query(sql_del, [req.params.id], (err, result) => {
			if (err) throw err;

			// For each hour given, make an entry into the database
			for(var day in body.hours_available) {
				for(var hour in body.hours_available[day].hour) {

					db.query(sql_ins, [req.params.id, body.hours_available[day].day,
						body.hours_available[day].hour[hour], row[0].skill_level, row[0].location_x,
						row[0].location_y, row[0].distance_preference], (err, result) => {
						if (err) throw err;
					});
				}
			}
		});
	});

	res.send("Success");
});

// From all players find the 10 (currently top 3) players that are the most compatible with the user
router.get('/top10/:id', (req, res) => {

	// SQL call which grabs all users that have an overlaping time with a given user
	// From all those users, only return the users who are within each other user's max distance
	sql_get = 'SELECT DISTINCT user2 as matched_player, day, hour, skill_diff ' 
	+ 'FROM ('
	+ 'SELECT us1.user_id as user1, us2.user_id as user2, us1.day, us1.hour, '
	+ 'us1.max_dist as d1, us2.max_dist as d2, (us1.skill - us2.skill) as skill_diff, '
	+ '(6387.7 * ACOS((sin(us1.location_y / 57.29577951) * SIN(us2.location_y / 57.29577951)) + '
    + '(COS(us1.location_y / 57.29577951) * COS(us2.location_y / 57.29577951) * '
	+ 'COS(us2.location_x / 57.29577951 - us1.location_x/ 57.29577951)))) as dist_diff '
	+ 'FROM availability us1 JOIN availability us2 ON us1.user_id != us2.user_id '
	+ 'AND us1.day = us2.day AND us1.hour = us2.hour '
	+ 'WHERE us1.user_id = ? '
	+ 'ORDER BY us2.user_id asc, day asc, hour asc '
	+ ') as Matches '
	+ 'WHERE (dist_diff <= d1 AND dist_diff <= d2)';
	
	var last_user = -1;
	var index = -1;
	var points_total = 0;
	var consecutive = 0;
	var match_points = [];

	// Grab all of the compatible users from the availability db
	db.query(sql_get, [req.params.id], (err, result) => {
		if (err) throw err;

		for(var entry in result) {

			// Skip calculations on the first entry
			if (entry == 0) {
				// Set the first user up on the match_points array, which will be used to find the top 10 scores
				// skill_muliplier will get their multiplier based on how different their skill levels are
				match_points.push({"id": result[0].matched_player, "score": skill_multiplier(result[0].skill_diff)});
				last_user = result[0].matched_player;
				index++;
				consecutive = 1;
			} 
			// Check for when the current user is different, then calculate their score
			else if (result[entry].matched_player != last_user) {
				// Multiply the total points from all matching hours, and multiply it to the skill multiplier
				match_points[index].score *= (points_total + consec_score(consecutive));
				// Reset points and consecutive hours
				points_total = 0;
				consecutive = 1;
				// Add the next user to match_points and increment index for that array
				match_points.push({"id": result[entry].matched_player, "score": skill_multiplier(result[entry].skill_diff)});
				last_user = result[entry].matched_player;
				index++;
			} 
			else {
				
				// Check if the day has changed or the hours are no longer consecutive
				if (result[entry-1].day != result[entry].day || (result[entry-1].hour + 1) != result[entry].hour) {
					// Calculate the score for that number of consecutive hours and reset consecutive
					points_total += consec_score(consecutive);
					consecutive = 1;
				} 
				// If we got here, that means the hours were consecutive
				else {
					consecutive++;
				}
			}
		}

		// Check if there was no data available/no matches
		if (index == -1) {
			// Send empty array
			res.send(match_points);
		} 
		else {
			// Add the points for the last user since the loop will break before it does
			match_points[index].score *= (points_total + consec_score(consecutive));

			// Sort the array in decending order, leaving the the highest score at the lowest index
			match_points.sort(function(a, b) {
				return b.score - a.score;
			});

			// Remove extra users
			while (match_points.length > 3) {
				match_points.pop();
			}

			res.send(match_points);
		}
	});
});

/* 	Function user for calculating the skill multiplier
	Negative numbers are from users with a higher skill than the given user
	A higher skilled player is more desirable to play with
	Positive numbers are from skill levels that were lower. 
*/
function skill_multiplier(skill_diff) {
	// Sd = standard deviation
	const sd = 2;
	// Slightly shift the distribution left so that negative scores are weighed higher
	const mean = -0.4; 
	// Multiplier to make the max score 10 at 0
	const multiplier = 51.14349127; 
	return multiplier/(sd*Math.sqrt(2*Math.PI))*Math.pow(Math.E, -1 * Math.pow(skill_diff - mean, 2)/(2 * Math.pow(sd, 2)));
}

/*
	Function to give a point score to the number of consecutive overlapping hours
	of availablilty. The difference between 1 and 2 is almost triple
	where as the difference between 2 and 3 is relatively small
	This is weighted this way since it's most desirable to have a session
	of 2 hours, where as larger consecutive sections are only slightly more
	desirable because it makes it easier to plan a session
*/
function consec_score(consecutive) {
	// Variables to give the consecutive score a sharp curve
	const a = 0.11;
	const b = 0.93;
	const c = 0.2;
	return a * Math.log10(consecutive - b) + c;
}

module.exports = router