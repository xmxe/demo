// 根据用户不同请求，做出不同响应（响应 现有的 HTML 文件）

// 加载 http 模块
var http = require('http');
// 加载 fs 模块
var fs = require('fs');
// 加载 path 模块
var path = require('path');


// 创建 http 服务，并启动该服务
http.createServer(function (req, res) {

  // 通过 req.url 获取用户请求的路径，根据不同的请求路径服务器做出不同的响应
  if (req.url === '/' || req.url === '/index') {
    // 读取 index.html 文件
    fs.readFile(path.join(__dirname, 'htmls', 'index.html'),  function (err, data) {
      if (err) {
        throw err;
      }

      // 把读取到的 index.html 中的内容直接发送给浏览器
      res.end(data);
    });
  } else if (req.url === '/login') {
    // 读取 index.html 文件
    fs.readFile(path.join(__dirname, 'htmls', 'login.html'),  function (err, data) {
      if (err) {
        throw err;
      }

      // 把读取到的 index.html 中的内容直接发送给浏览器
      res.end(data);
    });
  } else if (req.url === '/list') {
   // 读取 index.html 文件
    fs.readFile(path.join(__dirname, 'htmls', 'list.html'),  function (err, data) {
      if (err) {
        throw err;
      }

      // 把读取到的 index.html 中的内容直接发送给浏览器
      res.end(data);
    });
  } else if (req.url === '/register') {
    // 读取 index.html 文件
    fs.readFile(path.join(__dirname, 'htmls', 'register.html'),  function (err, data) {
      if (err) {
        throw err;
      }

      // 把读取到的 index.html 中的内容直接发送给浏览器
      res.end(data);
    });
  } else if (req.url === '/images/index.png') {
    // 表示用户要请求 images 下的 index.png 图片
    fs.readFile(path.join(__dirname, 'images', 'index.png'),  function (err, data) {
      if (err) {
        throw err;
      }

      res.setHeader('Content-Type', 'image/png');
      // 把读取到的 index.html 中的内容直接发送给浏览器
      res.end(data);
    });

  } else if (req.url === '/css/index.css') {
    fs.readFile(path.join(__dirname, 'css', 'index.css'),  function (err, data) {
      if (err) {
        throw err;
      }

      res.setHeader('Content-Type', 'text/css');
      // 把读取到的 index.html 中的内容直接发送给浏览器
      res.end(data);
    });
  } else {
    // 读取 index.html 文件
    fs.readFile(path.join(__dirname, 'htmls', '404.html'),  function (err, data) {
      if (err) {
        throw err;
      }

      // 把读取到的 index.html 中的内容直接发送给浏览器
      res.end(data);
    });
  }
  



}).listen(9090, function () {
  console.log('http://localhost:9090');
});