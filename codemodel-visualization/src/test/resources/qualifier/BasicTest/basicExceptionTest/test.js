function test() {
    return function () {
        throw new SQLException;
    };
}

let foo = test;

var asd = function () {
    return function () {
        var b = test();

        return 0;
    }
};
