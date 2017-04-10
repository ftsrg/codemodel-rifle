export default function* name1 () {
    let counter = 0;
    while (counter < 10) {
        yield counter++;
    }
};
