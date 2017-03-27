export default function* () {
    let counter = 0;
    while (counter < 10) {
        yield counter++;
    }
};
