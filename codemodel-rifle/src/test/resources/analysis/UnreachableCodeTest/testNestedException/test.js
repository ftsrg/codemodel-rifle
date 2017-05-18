function test() {
    return function () {
        throw new SQLException;
    };
}
let foo = test;
let asd = function () {
    return function () {
        let b = test();
        return 0;
    }
};
