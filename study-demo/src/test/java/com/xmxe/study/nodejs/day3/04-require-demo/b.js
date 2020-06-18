


function add(x, y) {
  return x + y;
}



var result = add(100, 1000);


console.log(result);







// -----------------module.exports 介绍 --------------------------

// // return "hello";

// // module.exports = 'hello world!';

// // module.exports = 6666;

// // module.exports = function (msg) {
// //   console.log(msg);
// // };


// module.exports.name = '张三';
// module.exports.age = 18;

// module.exports.show = function () {
//   console.log(this.name + this.age);
// };




// // function require(/* ... */) {

// //   const module = { exports: {} };

// //   ((module, exports) => {

// //     // Your module code here. In this example, define a function.
// //     function someFunc() {}
// //     exports = someFunc;

// //     // return "fdsfds"
// //     // At this point, exports is no longer a shortcut to module.exports, and
// //     // this module will still export an empty default object.
// //     module.exports = someFunc;
// //     // At this point, the module will now export someFunc, instead of the
// //     // default object.
// //   })(module, module.exports);


// //   return module.exports;
// // }




// -----------------exports 介绍 --------------------------

// 1. 
// module.exports.name = '张三';
// exports.age = 18;
// exports.show = function () {
//   console.log(this.name + ': ' + this.age);
// };


// 2.
// exports 和 module.exports 指向的是同一个对象
// 最终 require() 函数返回的的是 module.exports 中的数据
// return module.exports;
module.exports.name = '张三';
exports.age = 18;
exports.show = function () {
  console.log(this.name + ': ' + this.age);
};

// module.exports = 'Hello World!';
exports = 'Hello World!';