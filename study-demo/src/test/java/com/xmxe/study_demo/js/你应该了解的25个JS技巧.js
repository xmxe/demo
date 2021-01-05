// ---https://mp.weixin.qq.com/s/xtQbPTW6iDqHH-muu_Vlcg-----
// 1. 类型检查小工具
const isOfType = (() => {
  // create a plain object with no prototype
  const type = Object.create(null);
  // check for null type
  type.null = (x) => x === null;
  // check for undefined type
  type.undefined = (x) => x === undefined;
  // check for nil type. Either null or undefined
  type.nil = (x) => type.null(x) || type.undefined(x);
  // check for strings and string literal type. e.g: 's', "s", `str`, new String()
  type.string = (x) => !type.nil(x) && (typeof x === "string" || x instanceof String);
  // check for number or number literal type. e.g: 12, 30.5, new Number()
  type.number = (x) =>
!type.nil(x) && // NaN & Infinity have typeof "number" and this excludes that
    ((!isNaN(x) && isFinite(x) && typeof x === "number") ||
      x instanceof Number);
  // check for boolean or boolean literal type. e.g: true, false, new Boolean()
  type.boolean = (x) =>
    !type.nil(x) && (typeof x === "boolean" || x instanceof Boolean);
  // check for array type
  type.array = (x) => !type.nil(x) && Array.isArray(x);
  // check for object or object literal type. e.g: {}, new Object(), Object.create(null)
  type.object = (x) => ({}.toString.call(x) === "[object Object]");
  // check for provided type instance
  type.type = (x, X) => !type.nil(x) && x instanceof X;
  // check for set type
  type.set = (x) => type.type(x, Set);
  // check for map type
  type.map = (x) => type.type(x, Map);
  // check for date type
  type.date = (x) => type.type(x, Date);

  return type;
})();

// 2. 检查是否为空
function isEmpty(x) {
  if (Array.isArray(x) || typeof x === "string" || x instanceof String) {
    return x.length === 0;
  }
  if (x instanceof Map || x instanceof Set) {
    return x.size === 0;
  }
  if ({}.toString.call(x) === "[object Object]") {
    return Object.keys(x).length === 0;
  }

  return false;
}

// 3. 获取列表最后一项
function lastItem(list) {
  if (Array.isArray(list)) {
    return list.slice(-1)[0];
  }

  if (list instanceof Set) {
    return Array.from(list).slice(-1)[0];
  }

  if (list instanceof Map) {
    return Array.from(list.values()).slice(-1)[0];
  }
}

// 4. 带有范围的随机数生成器
function randomNumber(max = 1, min = 0) {
  if (min >= max) {
    return max;
  }

  return Math.floor(Math.random() * (max - min) + min);
}

// 5. 随机 ID 生成器
// create unique id starting from current time in milliseconds
// incrementing it by 1 everytime requested
const uniqueId = (() => {
  const id = (function* () {
    let mil = new Date().getTime();

    while (true) yield (mil += 1);
  })();

  return () => id.next().value;
})();
// create unique incrementing id starting from provided value or zero
// good for temporary things or things that id resets
const uniqueIncrementingId = ((lastId = 0) => {
  const id = (function* () {
    let numb = lastId;

    while (true) yield (numb += 1);
  })();

  return (length = 12) => `${id.next().value}`.padStart(length, "0");
})();
// create unique id from letters and numbers
const uniqueAlphaNumericId = (() => {
  const heyStack = "0123456789abcdefghijklmnopqrstuvwxyz";
  const randomInt = () =>
    Math.floor(Math.random() * Math.floor(heyStack.length));

  return (length = 24) =>
    Array.from({ length }, () => heyStack[randomInt()]).join("");
})();

// 6. 创建一个范围内的数字
function range(maxOrStart, end = null, step = null) {
  if (!end) {
    return Array.from({ length: maxOrStart }, (_, i) => i);
  }

  if (end <= maxOrStart) {
    return [];
  }

  if (step !== null) {
    return Array.from(
      { length: Math.ceil((end - maxOrStart) / step) },
      (_, i) => i * step + maxOrStart
    );
  }

  return Array.from(
    { length: Math.ceil(end - maxOrStart) },
    (_, i) => i + maxOrStart
  );
}

// 7. 格式化 JSON 字符串，stringify 任何内容
const stringify = (() => {
  const replacer = (key, val) => {
    if (typeof val === "symbol") {
      return val.toString();
    }
    if (val instanceof Set) {
      return Array.from(val);
    }
    if (val instanceof Map) {
      return Array.from(val.entries());
    }
    if (typeof val === "function") {
      return val.toString();
    }
    return val;
  };

  return (obj, spaces = 0) => JSON.stringify(obj, replacer, spaces);
})();

// 8. 顺序执行 promise
const asyncSequentializer = (() => {
  const toPromise = (x) => {
    if (x instanceof Promise) {
      // if promise just return it
      return x;
    }

    if (typeof x === "function") {
      // if function is not async this will turn its result into a promise
      // if it is async this will await for the result
      return (async () => await x())();
    }

    return Promise.resolve(x);
  };

  return (list) => {
    const results = [];

    return (
      list.reduce((lastPromise, currentPromise) => {
          return lastPromise.then((res) => {
            results.push(res); // collect the results
            return toPromise(currentPromise);
          });
        }, toPromise(list.shift()))
        // collect the final result and return the array of results as resolved promise
        .then((res) => Promise.resolve([...results, res]))
    );
  };
})();

// 9. 轮询数据
async function poll(fn, validate, interval = 2500) {
  const resolver = async (resolve, reject) => {
    try {
      // catch any error thrown by the "fn" function
      const result = await fn(); // fn does not need to be asynchronous or return promise
      // call validator to see if the data is at the state to stop the polling
      const valid = validate(result);
      if (valid === true) {
        resolve(result);
      } else if (valid === false) {
        setTimeout(resolver, interval, resolve, reject);
      } // if validator returns anything other than "true" or "false" it stops polling
    } catch (e) {
      reject(e);
    }
  };
  return new Promise(resolver);
}

// 10. 等待所有 promise 完成
const prom1 = Promise.reject(12);
const prom2 = Promise.resolve(24);
const prom3 = Promise.resolve(48);
const prom4 = Promise.resolve("error");
// completes when all promises resolve or at least one fail
// if all resolve it will return an array of results in the same order of each promise
// if fail it will return the error in catch

Promise.all([prom1, prom2, prom3, prom4])
  .then((res) => console.log("all", res))
  .catch((err) => console.log("all failed", err));

// completes with an array of objects with "status" and "value" or "reason" of each promise
// status can be "fullfilled" or "rejected"
// if fullfilled it will contain a "value" property

// if failed it will contain a "reasor property
Promise.allSettled([prom1, prom2, prom3, prom4])
  .then((res) => console.log("allSettled", res))
  .catch((err) => console.log("allSettled failed", err));

// completes with the first promise that resolves
// fails if all promises fail
Promise.any([prom1, prom2, prom3, prom4])
  .then((res) => console.log("any", res))
  .catch((err) => console.log("any failed", err));

// completes with the first promise that either resolve or fail
// whichever comes first
Promise.race([prom1, prom2, prom3, prom4])
  .then((res) => console.log("race", res))
  .catch((err) => console.log("race failed", err));

// 11. 交换数组值的位置
const array = [12, 24, 48];
const swap0ldway = (arr, i, j) => {
  const arrayCopy = [...arr];
  let temp = arayCopy[i];
  arrayCopy[i] = arrayCopy[j];

  arrayCopy[j] = temp;
  return arrayCopy;
};

const swapNewWay = (arr, i, j) => {
  const arrayCopy = [...arr];
  [arrayCopy[0], arrayCopy[2]] = [arrayCopy[2], arrayCopy[0]];
  return arrayCopy;
};

console.log(swap0ldWay(array, 0, 2)); // outputs: [48, 24, 12]
console.log(swapNewWay(array, 0, 2)); // outputs: [48, 24, 12]

// 12. 条件对象键
let condition = true;
const man = {
  someProperty: "some value",
  // the parenthesis will execute the ternary that will
  // result in the object with the property you want to insert
  // or an empty object.then its content is spreaded in the wrapper object
  ...(condition === true ? { newProperty: "value" } : {}),
};

// 13. 使用变量作为对象键
let property = "newValidProp";
const man = {
  someProperty: "some value",
  // the "square bracket" notation is a valid way to acces object key
  // like object[prop] but it is used inside to assign a property as well
  // using the 'backtick' to first change it into a string

  // but it is optional
  ["${property}"]: "value",
};

// 14. 检查对象里的键
const sample = {
  prop: "value",
};
// using the "in" keyword will still consider proptotype keys
// which makes it unsafe and one of the issues with "for...in" loop
console.log("prop" in sample); // prints "true"
console.log("toString" in sample); // prints "true"
// using the "hasOwnProperty" methods is safer
console.log(sample.hasOwnProperty("prop")); // prints "true"
console.log(sample.hasOwnProperty("toString")); // prints "false"

// 15. 删除数组重复项
const numberArrays = [undefined,Infinity,
  12,NaN,false,5,7,null,12,false,5,undefined,89,9,
  null,Infinity,5, NaN];
const objArrays = [{ id: 1 }, { id: 4 }, { id: 1 }, { id: 5 }, { id: 4 }];
console.log(
  // prints [undefined, Infinity, 12, NaN, false, 5, 7, null, 89, 9]
  Array.from(new Set(numberArrays)),
  // prints [{id: 1}, {id: 4}, {id: 1}, {id: 5}, {id: 4}]
  // nothing changes because even though the ids repeat in some objects
  // the objects are different instances, different objects
  Array.from(new Set(objArrays))
);
const idSet = new Set();
console.log(
  // prints [{id: 1}, {id: 4}, {id: 5}] using id to track id uniqueness
  objArrays.filter((obj) => {
    const existingId = idSet.has(obj.id);
    idSet.add(obj.id);
    return !existingId;
  })
);

// 16. 在 ArrayforEach 中执行“break”和“continue”
const numbers = [1, 2, 3, 4, 5, 6, 7, 8, 9];
for (const number of numbers) {
  if (number % 2 === 0) {
    continue;
  }
  if (number > 5) {
    break;
  }
  console.log(number);
}
numbers.some((number) => {
  if (number % 2 === 0) {
    continue;
  }
  if (number > 5) {
    break;
  }
  console.log(number);
});

// 17. 使用别名和默认值来销毁
function demo1({ dt: data }) {
  // rename "dt" to "data"
  console.log(data); // prints {name: 'sample', id: 50}
}
function demo2({ dt: { name, id = 10 } }) {
  // deep destruct "dt" and if no "id" use 10 as default
  console.log(name, id); // prints 'sample', '10'
}
demo1({
  dt: { name: "sample", id: 50 },
});

demo2({
  dt: { name: "sample" },
});

// 18. 可选链和空值合并
const obj = {
  data: {
    container: {
      name: {
        value: "sample",
      },
      int: {
        value: 0,
      },
    },
  },
};

console.log(
  // even though the "int.value" exists, it is falsy therefore fails to be printed
  obj.data.container.int.value || "no int value", // prints 'no int value'
  // the ?? make sure to fallback to the right side only if left is null or undefined
  obj.data.container.int.value ?? "no int value" // prints 0
);
console.log(
  // "wrapper" does not exist inside "data"
  obj.data.wrapper.name.value, // throws "Cannot read property 'name' of undefined"
  // this is better but can be a problem if object is deep
  (obj && obj.data && obj.data.wrapper && obj.data.wrapper.name) || "no name", // prints 'no name"
  // using optional chaining "?" is better
  obj?.data?.wrapper?.name || "no name" // prints 'no name'
);

// 19. 用函数扩展类
function Parent() {
  const privateProp = 12;
  const privateMethod = () => privateProp + 10;
  this.publicMethod = (x = 0) => privateMethod() + x;
  this.publicProp = 10;
}
class Child extends Parent {
  myProp = 20;
}
const child = new Child();
console.log(
  child.myProp, // prints 20
  child.publicProp, // prints 10
  child.publicMethod(40), // prints 62
  child.privateProp, // prints undefined
  child.privateMethod() // throws "child.privateMethod is not a function"
);

// 20. 扩展构造函数
function Employee() {
  this.profession = "Software Engineer";
  this.salary = "$150000";
}

function DeveloperFreelancer() {
  this.programmingLanguages = ["Javascript", "Python", " Swift"];
  this.avgPerHour = "$100";
}

function Engineer(name) {
  this.name = name;
  this.freelancer = {};
  Employee.apply(this);
  DeveloperFreelancer.apply(this.freelancer);
}
const john = new Engineer("John Doe");
console.log(
  john.name, // prints "John Doe"
  john.profession, // prints "Software Engineer"
  john.salary, // prints "$150000"
  john.freelancer // prints {programmingLanguages: ['Javascript', 'Python', 'Swift'], avgPerHour: '$100'}
);

// 21. 循环任何内容
function forEach(list, callback) {
  const entries = Object.entries(list);
  let i = 0;
  const len = entries.length;

  for (; i < len; i++) {
    const res = callback(entries[i][1], entries[i][0], list);
    if (res === true) break;
  }
}

// 22. 使函数参数为 required
function required(argName = "param") {
  throw new Error(`"${argName}" is required`);
}
function iHaveRequiredOptions(arg1 = required("arg1"), arg2 = 10) {
  console.log(arg1, arg2);
}
iHaveRequiredOptions(); // throws "arg1" is required
iHaveRequiredOptions(12); // prints 12, 10
iHaveRequiredOptions(12, 24); // prints 12, 24
iHaveRequiredOptions(undefined, 24); // throws "arg1" is required

// 23. 创建模块或单例
class Service {
  name = "service";
}
const service = (function (S) {
  // do something here like preparing data that you can use to initialize service
  const service = new S();
  return () => service;
})(Service);
const element = (function (S) {
  const element = document.createElement("DIV");
  // do something here to grab somethin on the dom
  // or create elements with javasrcipt setting it all up
  // than to return it
  return () => element;
})();

// 24. 深度克隆对象
const deepClone = (obj) => {
  let clone = obj;
  if (obj && typeof obj === "object") {
    clone = new obj.constructor();

    Object.getOwnPropertyNames(obj).forEach(
      (prop) => (clone[prop] = deepClone(obj[prop]))
    );
  }
  return clone;
};

// 25. 深度冻结对象
const deepClone = (obj) => {
  let clone = obj;
  if (obj && typeof obj === "object") {
    clone = new obj.constructor();

    Object.getOwnPropertyNames(obj).forEach(
      (prop) => (clone[prop] = deepClone(obj[prop]))
    );
  }
  return clone;
};
