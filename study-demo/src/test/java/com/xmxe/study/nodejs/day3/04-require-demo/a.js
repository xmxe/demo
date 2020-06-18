

// // // 加载 b.js 模块
// // require('./b.js');


// // // 下面这些加载模块都是从缓存里面获取的，所以不会再去执行b.js文件了。
// // require('./b.js');

// // require('./b.js');

// // require('./b.js');

// // require('./b.js');

// // require('./b.js');

// // require('./b.js');

// // var b = require('./b.js');


// // Module

// // module
// console.log(module.paths);
// // [ 'C:\\Users\\Humble\\Desktop\\12期\\03-HackerNews\\02-源代码\\课堂代码\\04-require-demo\\node_modules',
// //   'C:\\Users\\Humble\\Desktop\\12期\\03-HackerNews\\02-源代码\\课堂代码\\node_modules',
// //   'C:\\Users\\Humble\\Desktop\\12期\\03-HackerNews\\02-源代码\\node_modules',
// //   'C:\\Users\\Humble\\Desktop\\12期\\03-HackerNews\\node_modules',
// //   'C:\\Users\\Humble\\Desktop\\12期\\node_modules',
// //   'C:\\Users\\Humble\\Desktop\\node_modules',
// //   'C:\\Users\\Humble\\node_modules',
// //   'C:\\Users\\node_modules',
// //   'C:\\node_modules' ]








// 一个模块，默认被 require() 加载后，返回的是一个对象 {}
var b = require('./b.js');
console.log(b);

// b.show();

// b('哈哈哈哈66666');