var name1 = "name1";
let name2 = "name2";
var name3 = function () {
    return "name3";
};
let name4 = function () {
    return "name4";
};

export { name1 as exportedName1, name2 as exportedName2, name3 as exportedName3, name4 as exportedName4 };
