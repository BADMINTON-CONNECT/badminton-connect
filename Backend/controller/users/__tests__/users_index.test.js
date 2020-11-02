const dut = require('../users_index')
//const db = require('../../../database/mysql')
jest.mock('../users_index')


const request = {
    body: {
        parameter: 3
    }
};

const response = {
    send: jest.fn( (input) => {
        return input;
    })
}

const empty = {

}
const all_users = {
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

/*
beforeAll(async() => {

    await db.connect();

    console.log("this is being run")
})

*/
const req = Object.create(request);
const res = Object.create(response);

test('Testing get all request', () => {
    /*
    db.query('select * from users;', (err, result) => {
        console.log("this is " + result)
    })
    */
   /*
    var data = dut.get_all_users(req, res);
    await data.then( result => {
        //console.log(result);
        expect(result).toEqual(empty);
    })
    */
    expect.assertions(1);
    return dut.get_all_users(req, res).then(data => {
        //console.log(data);
        expect(data).toEqual(all_users)
    })
    
    
})


var id = 17;
test('Testing get specific request', () => {
    return dut.get_specific_user(id).then(data => {
        expect(data).toEqual(all_users[id])
    })
})


test('Testing delete specific request', () => {
    return dut.delete_specific_user(id).then(data => {
        expect(data).toEqual("Deleted succesfully")
    })
})



afterAll(async () => {

    //const collection = "test_"+process.env.COLLECTION;
    //await db.dropCollection(collection);
    //await db.dropDatabase();
    //await db.end();
    //await connection.close();

});