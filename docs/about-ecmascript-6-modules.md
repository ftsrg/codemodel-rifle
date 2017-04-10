# About ECMAScript 6 modules

## Introduction

ECMAScript 6 gives us the possibility to write modular JavaScript softwares by using sort of Java-like importing.

* Introduction to ECMAScript 6 modules: <http://2ality.com/2014/09/es6-modules-final.html>
* Further very good summary documentation: <http://exploringjs.com/es6/ch_modules.html>
* Exports' grammar from ECMA International: <http://www.ecma-international.org/ecma-262/6.0/#sec-exports>
* Imports' grammar from ECMA International: <http://www.ecma-international.org/ecma-262/6.0/#sec-imports>

### Export syntaxes

```JavaScript
export { name1, name2, … };
export { name1 as exportedName1, name2 as exportedName2, … };
export let name1, name2, … ;
export var name1, name2, … ;
export let name1 = …, name2 = …, … ;
export var name1 = …, name2 = …, … ;
export const name1 = …, name2 = …, … ;

export expression;
export default expression;
export default function (…) { … }
export default class (…) { … }
export default function* (…) { … } // generator
export default function name1(…) { … }
export default class name1(…) { … }
export default function* name1(…) { … } // generator
export { name1 as default, … };
export * from …;
export { name1, name2, … } from … ;
export { import1 as importedName1, import2 as importedName2, … } from …;
```

Source: <https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/export>

### Import syntaxes

```JavaScript
import defaultName from "exporter";
import * as exportedModule from "exporter";
import { name1, … } from "exporter";
import { name1 as importedName1, … } from "exporter";
import defaultName, { name1 [ , [...] ] } from "exporter";
import defaultName, * as exportedModule from "exporter";
import "exporter";
```

Source: <https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/import>

## Processing modules with Codemodel-Rifle

Codemodel-Rifle should be able to process multiple related JavaScript files (modules) connected with ECMAScript 6's import and export statements. We need to connect the separate ASG's into one ASG containing all modules' contents in one GlobalScope. This is done via Cypher queries after synchronising a code repository into Codemodel-Rifle. (See Connecting separate modules in the ASG.)

### Distinct cases of import-export on AST-/ASG-level

We distinguish different import-export *syntaxes* (above) from different import-export *cases*. We do not care about the syntax, we are only interested in the AST- (and thus ASG-) representation of a certain case. The individual cases are detailed below. Within a case, all syntaxes are represented in the same way.

#### Export cases

These names are used in the code consistently.

* **exportAlias**

```JavaScript
export { name1 as exportedName1 };
export { name1 as exportedName1, name2 as exportedName2, … };
```

* **exportDeclaration**

```JavaScript
export let name1;
export let name1, name2, …;
export var name1;
export var name1, name2, …;
export let name1 = …;
export let name1 = …, name2 = …, …;
export var name1 = …;
export var name1 = …, name2 = …, …;
export const name1 = …;
export const name1 = …, name2 = …, …;
```

* **exportName**

```JavaScript
export { name1 };
export { name1, name2, … };
```

**Work in progress…**

#### Import cases

These names are used in the code consistently.

* **importAlias**

```JavaScript
import { name1 as importedName1, … } from "exporter";
```

* **importDefault**

```JavaScript
import defaultName from "exporter";
```

* **importModule** (omitted)

*Omitted: in this case, no bindings are made between the two module. The first such import executes the imported module's body. See [here](http://exploringjs.com/es6/ch_modules.html#_importing-styles).*

```JavaScript
import "exporter";
```

* **importName**

```JavaScript
import { name1, … } from "exporter";
```

* **importNamespace**

```JavaScript
import * as exportedModule from "exporter";
```

Since there are multiple default exports, this code is not valid. Thus the graph may also be incorrect.

![Import AST and scopes](https://github.com/steindani/codemodel-rifle/wiki/img/export.dot.png)

[PDF](https://github.com/steindani/codemodel-rifle/wiki/img/export.dot.pdf) 
[DOT](https://github.com/steindani/codemodel-rifle/wiki/img/export.dot)
