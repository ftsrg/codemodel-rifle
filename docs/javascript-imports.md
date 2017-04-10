```JavaScript
import defaultMember from "module-name";
import * as name from "module-name";
import { member } from "module-name";
import { member as alias } from "module-name";
import { member1 , member2 } from "module-name";
import { member1 , member2 as alias2 , [...] } from "module-name";
import defaultMember, { member [ , [...] ] } from "module-name";
import defaultMember, * as name from "module-name";
import "module-name";
```

Source: [https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/import](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/import)

![Import AST and scopes](https://github.com/steindani/codemodel-rifle/wiki/img/import.dot.png)

[PDF](https://github.com/steindani/codemodel-rifle/wiki/img/import.dot.pdf) 
[DOT](https://github.com/steindani/codemodel-rifle/wiki/img/import.dot)
