// ------数组Array----
/**
 * 数组去重⬇
 */
// 方案一：Set + ...
function noRepeat(arr) {
  return [...new Set(arr)];
}
noRepeat([1, 2, 3, 1, 2, 3]);

// 方案二：Set + Array.from
function noRepeat(arr) {
  return Array.from(new Set(arr));
}
noRepeat([1, 2, 3, 1, 2, 3]);

// 方案三：双重遍历比对下标
function noRepeat(arr) {
  return arr.filter((v, idx) => idx == arr.lastIndexOf(v));
}
noRepeat([1, 2, 3, 1, 2, 3]);

// 方案四：单遍历 + Object特性
// Object的特性是Key不会重复。
// 这里使用values是因为可以保留类型，keys会变成字符串。
function noRepeat(arr) {
  return Object.values(
    arr.reduce((s, n) => {
      s[n] = n;
      return s;
    }, {})
  );
}
noRepeat([1, 2, 3, 1, 2, 3]);

/**
 * 查找数组最大⬇
 */
// 方案一：Math.max + ...
function arrayMax(arr) {
  return Math.max(...arr);
}
arrayMax([-1, -4, 5, 2, 0]);

// 方案二：Math.max + apply
function arrayMax(arr) {
  return Math.max.apply(Math, arr);
}
arrayMax([-1, -4, 5, 2, 0]);

// 方案三：Math.max + 遍历
function arrayMax(arr) {
  return arr.reduce((s, n) => Math.max(s, n));
}
arrayMax([-1, -4, 5, 2, 0]);

// 方案四：比较、条件运算法 + 遍历
function arrayMax(arr) {
  return arr.reduce((s, n) => (s > n ? s : n));
}
arrayMax([-1, -4, 5, 2, 0]);

// 方案五：排序
function arrayMax(arr) {
  return arr.sort((n, m) => m - n)[0];
}
arrayMax([-1, -4, 5, 2, 0]);

/**
 *  查找数组最小⬇
 */
// Math.max换成Math.min
// s>n?s:n换成s<n?s:n
// (n,m)=>m-n换成(n,m)=>n-m，或者直接取最后一个元素

/**
 * 返回已size为长度的数组分割的原数组⬇
 */
// 方案一：Array.from + slice
function chunk(arr, size = 1) {
  return Array.from(
    {
      length: Math.ceil(arr.length / size),
    },
    (v, i) => arr.slice(i * size, i * size + size)
  );
}
chunk([1, 2, 3, 4, 5, 6, 7, 8], 3);

// 方案二：Array.from + splice
function chunk(arr, size = 1) {
  return Array.from(
    {
      length: Math.ceil(arr.length / size),
    },
    (v, i) => arr.splice(0, size)
  );
}
chunk([1, 2, 3, 4, 5, 6, 7, 8], 3);

// 方案三：遍历 + splice
function chunk(arr, size = 1) {
  var _returnArr = [];
  while (arr.length) {
    _returnArr.push(arr.splice(0, size));
  }
  return _returnArr;
}
chunk([1, 2, 3, 4, 5, 6, 7, 8], 3);

/**
 * 检查数组中某元素出现的次数⬇
 */
// 方案一：reduce
function countOccurrences(arr, value) {
  return arr.reduce((a, v) => (v === value ? a + 1 : a + 0), 0);
}
countOccurrences([1, 2, 3, 4, 5, 1, 2, 1, 2, 3], 1);

// 方案二：filter
function countOccurrences(arr, value) {
  return arr.filter((v) => v === value).length;
}
countOccurrences([1, 2, 3, 4, 5, 1, 2, 1, 2, 3], 1);

/**
 * 扁平化数组⬇
 */
// 方案一：递归 + ...
function flatten(arr, depth = -1) {
  if (depth === -1) {
    return [].concat(
      ...arr.map((v) => (Array.isArray(v) ? this.flatten(v) : v))
    );
  }
  if (depth === 1) {
    return arr.reduce((a, v) => a.concat(v), []);
  }
  return arr.reduce(
    (a, v) => a.concat(Array.isArray(v) ? this.flatten(v, depth - 1) : v),
    []
  );
}
flatten([1, [2, [3]]]);

// 方案二：es6原生flat
function flatten(arr, depth = Infinity) {
  return arr.flat(depth);
}
flatten([1, [2, [3]]]);

/**
 * 对比两个数组并且返回其中不同的元素⬇
 */
// 方案一：filter + includes
// 他原文有问题，以下方法的4,5没有返回

function diffrence(arrA, arrB) {
  return arrA.filter((v) => !arrB.includes(v));
}
diffrence([1, 2, 3], [3, 4, 5, 2]);
// 需要再操作一遍
function diffrence(arrA, arrB) {
  return arrA
    .filter((v) => !arrB.includes(v))
    .concat(arrB.filter((v) => !arrA.includes(v)));
}
diffrence([1, 2, 3], [3, 4, 5, 2]);

// 方案二：hash + 遍历
// 算是方案1的变种吧，优化了includes的性能。

/**
 * 返回两个数组中相同的元素⬇
 */
// 方案一：filter + includes
function intersection(arr1, arr2) {
  return arr2.filter((v) => arr1.includes(v));
}
intersection([1, 2, 3], [3, 4, 5, 2]);

// 方案二：同理变种用 hash
function intersection(arr1, arr2) {
  var set = new Set(arr2);
  return arr1.filter((v) => set.has(v));
}
intersection([1, 2, 3], [3, 4, 5, 2]);

/**
 * 从右删除n个元素⬇
 */
// 方案一：slice
function dropRight(arr, n = 0) {
  return n < arr.length ? arr.slice(0, arr.length - n) : [];
}
dropRight([1, 2, 3, 4, 5], 2);

// 方案二: splice
function dropRight(arr, n = 0) {
  return arr.splice(0, arr.length - n);
}
dropRight([1, 2, 3, 4, 5], 2);

// 方案三: slice另一种
function dropRight(arr, n = 0) {
  return arr.slice(0, -n);
}
dropRight([1, 2, 3, 4, 5], 2);

// 方案四: 修改length
function dropRight(arr, n = 0) {
  arr.length = Math.max(arr.length - n, 0);
  return arr;
}
dropRight([1, 2, 3, 4, 5], 2);

/**
 * 截取第一个符合条件的元素及其以后的元素⬇
 */
// 方案一：slice + 循环
function dropElements(arr, fn) {
  while (arr.length && !fn(arr[0])) arr = arr.slice(1);
  return arr;
}
dropElements([1, 2, 3, 4, 5, 1, 2, 3], (v) => v == 2);

// 方案二：findIndex + slice
function dropElements(arr, fn) {
  return arr.slice(Math.max(arr.findIndex(fn), 0));
}
dropElements([1, 2, 3, 4, 5, 1, 2, 3], (v) => v === 3);

// 方案三：splice + 循环
function dropElements(arr, fn) {
  while (arr.length && !fn(arr[0])) arr.splice(0, 1);
  return arr;
}
dropElements([1, 2, 3, 4, 5, 1, 2, 3], (v) => v == 2);

/**
 * 返回数组中下标间隔nth的元素⬇
 */
// 方案一：filter
function everyNth(arr, nth) {
  return arr.filter((v, i) => i % nth === nth - 1);
}
everyNth([1, 2, 3, 4, 5, 6, 7, 8], 2);

// 方案二：方案一修改判断条件
function everyNth(arr, nth) {
  return arr.filter((v, i) => (i + 1) % nth === 0);
}
everyNth([1, 2, 3, 4, 5, 6, 7, 8], 2);

/**
 * 返回数组中第n个元素（支持负数）⬇
 */
// 方案一：slice
function nthElement(arr, n = 0) {
  return (n >= 0 ? arr.slice(n, n + 1) : arr.slice(n))[0];
}
nthElement([1, 2, 3, 4, 5], 0);
nthElement([1, 2, 3, 4, 5], -1);

// 方案二：三目运算符
function nthElement(arr, n = 0) {
  return n >= 0 ? arr[0] : arr[arr.length + n];
}
nthElement([1, 2, 3, 4, 5], 0);
nthElement([1, 2, 3, 4, 5], -1);

/**
 * 返回数组头元素⬇
 */
// 方案一：
function head(arr) {
  return arr[0];
}
head([1, 2, 3, 4]);

// 方案二：
function head(arr) {
  return arr.slice(0, 1)[0];
}
head([1, 2, 3, 4]);

/**
 * 返回数组末尾元素⬇
 */
// 方案一：
function last(arr) {
  return arr[arr.length - 1];
}

// 方案二：
function last(arr) {
  return arr.slice(-1)[0];
}
last([1, 2, 3, 4, 5]);

/**
 * 数组乱排⬇
 */
// 方案一：洗牌算法
function shuffle(arr) {
  let array = arr;
  let index = array.length;

  while (index) {
    index -= 1;
    let randomInedx = Math.floor(Math.random() * index);
    let middleware = array[index];
    array[index] = array[randomInedx];
    array[randomInedx] = middleware;
  }

  return array;
}
shuffle([1, 2, 3, 4, 5]);

/**
 * 方案二：sort + random
 */
function shuffle(arr) {
  return arr.sort((n, m) => Math.random() - 0.5);
}
shuffle([1, 2, 3, 4, 5]);

/**
 * 伪数组转换为数组⬇
 */
// 方案一：Array.from
Array.from({ length: 2 });
// 方案二：prototype.slice
Array.prototype.slice.call({ length: 2, 1: 1 });
// 方案三：prototype.splice
Array.prototype.splice.call({ length: 2, 1: 1 }, 0);
