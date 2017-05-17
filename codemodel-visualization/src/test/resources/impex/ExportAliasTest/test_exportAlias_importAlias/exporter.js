var name1 = "name1Value";
let name2 = "name2Value";
var name3 = function () {
    return "name3Value";
};
let name4 = function () {
    return "name4Value";
};

export { name1 as default, name2 as exportedName2, name3 as exportedName3, name4 as exportedName4 };
