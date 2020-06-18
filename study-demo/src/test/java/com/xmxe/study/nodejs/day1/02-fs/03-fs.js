
// 读写文件中的路径问题
// var fs = require('fs');

// // 此处的 ./ 相对路径，相对的是执行 node 命令的路径
// // 而不是相对于正在执行的这个 js 文件来查找 hello.txt
// fs.readFile('./hello.txt', 'utf8', function (err, data) {
//   if (err) {
//     throw err;
//   }

//   console.log(data);
// });





// 解决在文件读取中 ./ 相对路径的问题
// 解决： __dirname、__filename
// __dirname: 表示，当前正在执行的 js 文件所在的目录
// __filename: 表示，当前正在执行的 js 文件的完整路径

// console.log(__dirname);
// console.log(__filename);



var fs = require('fs');
// 加载 path 模块
var path = require('path');


// var filename = __dirname + '\\' + 'hello.txt';
var filename = path.join(__dirname, 'hello.txt');
console.log(filename);

// /fdsa/f/ds/afd/saf/saf/dsf/dsa/ds/sa

// 此处的 ./ 相对路径，相对的是执行 node 命令的路径
// 而不是相对于正在执行的这个 js 文件来查找 hello.txt
fs.readFile(filename, 'utf8', function (err, data) {
  if (err) {
    throw err;
  }

  console.log(data);
});