const jest = require("jest");
const users = jest.createMockFromModule("./users_index");


const usersDb = {
    17: {
        firstName: "Neo",
        lastName: "Tsai",
        email: "neo@gmail.com",
        locationX: "-123.1336",
        locationY: "49.167",
        skillLevel: 6,
        distancePreference: 20
    },

    41: {
        firstName: "Eleigh",
        lastName: "Hangeveld",
        email: "neo@gmail.com",
        locationX: "-123.1336",
        locationY: "49.167",
        skillLevel: 6,
        distancePreference: 20
    }
};


function getAllUsers() {
    
    return new Promise( (resolve, reject) => {
        resolve(usersDb);
    });
}

function getSpecificUser(id) {

	return new Promise( (resolve, reject) => {
        resolve(usersDb[17]);
    });
}

function deleteSpecificUser(id) {
	return new Promise( (resolve, reject) => {
        // maybe splice it? 
        resolve("Deleted succesfully");
    });
}

module.exports = users;
module.exports.getAllUsers = getAllUsers;
module.exports.getSpecificUser = getSpecificUser;
module.exports.deleteSpecificUser = deleteSpecificUser;
