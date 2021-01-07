// 插入数组：
// 看看如下代码，不使用扩展语法：
var mid = [3, 4];
var arr = [1, 2, mid, 5, 6];
console.log(arr);  // [1, 2, [3, 4] , 5, 6]
// 上面这段代码将得到一个嵌套数组的数组。大部分情况，我们希望一个 array（mid） 展开后再插入到另一个 array（arr） 中。
// 使用 spread 操作符我们可以这样：
var mid = [3, 4];
var arr = [1, 2, ...mid, 5, 6];
console.log(arr); // [1，2，3，4，5，6]

// 展开数组作为参数
// 当一个函数接收多个参数，比如 Math.max，当我们有一个数组需要找到你了的最大值，我们可以使用如下代码进行调用。
var arr = [2, 4, 8, 6, 0];
function max(arr) {
  return Math.max.apply(null, arr);
}
console.log(max(arr)); // 8

// 如果这时候使用 spread 运算符会变得非常方便。
var arr = [2, 4, 8, 6, 0];
var max = Math.max(...arr);
console.log(max); // 8

// 复制数组
// 用数组给新数组赋值只是获取到数组引用，并没有达到深复制的效果。
var arr = ['a', 'b', 'c'];
var arr2 = arr;
arr2.push('d');
console.log(arr);// ["a", "b", "c", "d"]

// 有多重方法可以实现深复制，但是使用 spread 操作符是最简洁的一种实现：
var arr = ['a', 'b', 'c'];
var arr2 = [...arr];
arr2.push('d');
console.log(arr);  // ["a", "b", "c"]

// 展开 String
// 如果想将字符串转为字符数组，如果不实用展开操作：
var str = "hello";
"hello".split('') // ["h", "e", "l", "l", "o"]

// 使用展开操作符可以这样写：
var str = "hello";
var chars = [...str]; // ["h", "e", "l", "l", "o"]

// 展开 Object
// 我们还可以对对象进行展开，如果有两个对象，有不同的 key-value, 我们需要将这两个对象合并起来 (需要使用 Object rest spread transform)，我们可以这样：

let Obj1 = {
 key1: 'value1'
}

let Obj2 = {
 key2: 'value3'
}

let concatValue = {
 ...Obj1,
 ...Obj2
}
console.log(concatValue) // {key1: 'value1', key2: 'value2'}