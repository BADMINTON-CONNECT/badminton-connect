const dut = require("../users_index");
jest.mock("../users_index");


const request = {
    body: {
        parameter: 3
    }
};

const response = {
    send: jest.fn( (input) => {
        return input;
    })
};

const empty = {

};

const allUsers = {
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

/*
beforeAll(async() => {

    await db.connect();

    console.log("this is being run")
})

*/
const req = Object.create(request);
const res = Object.create(response);

test("Testing get all request", () => {
    
    expect.assertions(1);
    return dut.getAllUsers(req, res).then( (data) => {
        expect(data).toEqual(allUsers);
    });
    
});


var id = 17;
test("Testing get specific request", () => {
    return dut.getSpecificUser(id).then( (data) => {
        var expected = allUsers[id];
        expect(data).toEqual(expected);
    });
});


test("Testing delete specific request", () => {
    return dut.deleteSpecificUser(id).then( (data) => {
        expect(data).toEqual("Deleted succesfully");
    });
});



afterAll(async () => {

    //const collection = "test_"+process.env.COLLECTION;
    //await db.dropCollection(collection);
    //await db.dropDatabase();
    //await db.end();
    //await connection.close();

});