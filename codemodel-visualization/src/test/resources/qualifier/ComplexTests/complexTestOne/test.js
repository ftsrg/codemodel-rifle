var foo = 0;

let bar = function () {
    return foo;
};

function baz() {
    return function () {
        return bar();
    }
}

let a = bar;

let b = a();

let c = function () {
    return b();
}
