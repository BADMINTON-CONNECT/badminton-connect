const users = jest.createMockFromModule('./users_index');


const users_db = {
    17: {
        first_name: 'Neo',
        last_name: 'Tsai',
        email: 'neo@gmail.com',
        location_x: '-123.1336',
        location_y: '49.167',
        skill_level: 6,
        distance_preference: 20
    },

    41: {
        first_name: 'Eleigh',
        last_name: 'Hangeveld',
        email: 'neo@gmail.com',
        location_x: '-123.1336',
        location_y: '49.167',
        skill_level: 6,
        distance_preference: 20
    }
}


function get_all_users() {
    //console.log("calling from mocks")
    
    return new Promise( (resolve, reject) => {
        resolve(users_db);
    })
}

function get_specific_user(id) {

	return new Promise( (resolve, reject) => {
        console.log(users_db[17].first_name)
        resolve(users_db[id]);
    })
}

function delete_specific_user(id) {
	return new Promise( (resolve, reject) => {
        // maybe splice it? 
        resolve("Deleted succesfully");
    })
}

module.exports = users
module.exports.get_all_users = get_all_users
module.exports.get_specific_user = get_specific_user
module.exports.delete_specific_user = delete_specific_user
