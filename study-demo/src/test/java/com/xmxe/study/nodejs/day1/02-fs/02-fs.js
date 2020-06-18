
// 执行文件操作


// // ---------- 实现文件写入操作 -----------------------
// // 1. 加载文件操作模块 ，fs 模块。
// var fs = require('fs');

// console.log('000');

// // 2. 实现文件写入操作
// var msg = 'Hello World, 你好世界!';

// // 调用 fs.writeFile() 进行文件写入
// // fs.writeFile(file, data[, options], callback)
// fs.writeFile('./hello.txt', msg, 'utf8', function (err) {

//   console.log('111');
//   // body...
//   // 如果 err === null, 表示写入文件成功，没有错误！
//   // 只要 err 里面不是 null, 就表示写入文件失败了！
//   if (err) {
//     console.log('写文件出错啦！具体错误：' + err);
//   } else {
//     console.log('ok');
//   }
// });

// console.log('222');










// ------------ 实现文件读取操作 --------------------------
// 1. 加载 fs 模块
var fs = require('fs');


// // 2. 调用 fs.readFile() 方法来读取文件
// // fs.readFile(file[, options], callback)
// fs.readFile('./hello.txt', function (err, data) {
//   if (err) {
//     throw err;
//   }
//   // data 参数的数据类型是一个 Buffer 对象，里面保存的就是一个一个的字节（理解为字节数组）
//   // 把 buffer 对象转换为字符串, 调用 toString() 方法
//   // console.log(data);
//   // console.log(data.toString('utf8'));

//   // 调用 Buffer 对象的 toString() 方法的时候，不传 utf8 参数，默认也是 utf8
//   console.log(data.toString());

//   // 5242880
//   // 1024 * 1024 * 5
// });



// ---------------------------- 读取文件时，传递 utf8 编码 -----------------------
// 2. 调用 fs.readFile() 方法来读取文件
// fs.readFile(file[, options], callback)
// 在读取文件的时候，如果传递了编码，那么回调函数中的 data默认就会转换为 字符串
fs.readFile('./hello.txt', 'utf8', function (err, data) {
  if (err) {
    throw err;
  }
  // data 参数的数据类型是一个 Buffer 对象，里面保存的就是一个一个的字节（理解为字节数组）
  // 把 buffer 对象转换为字符串, 调用 toString() 方法
  console.log(data);
  // 5242880
  // 1024 * 1024 * 5
});







// -------------------异步操作 ---------------------------

// console.log('111');

// setTimeout(function () {
//   console.log('222');
// }, 1000);


// console.log('333');

// setTimeout(function () {
//   console.log('444');
// }, 1000);


// console.log('555');



// console.log('111');

// setTimeout(function () {
//   console.log('222');
// }, 10);


// console.log('333');

// setTimeout(function () {
//   console.log('444');
// }, 0);


// console.log('555');