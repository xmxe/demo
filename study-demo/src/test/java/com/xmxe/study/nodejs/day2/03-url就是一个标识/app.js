

// 加载 http 模块
var http = require('http');
var path = require('path');
var fs = require('fs');
var mime = require('mime');


// 创建服务
http.createServer(function (req, res) {

  if (req.url === '/index.do' || req.url === '/index.html') {
    // 读取 index.html 并返回

    fs.readFile(path.join(__dirname, 'index.html'), function (err, data) {
      if (err) {
        throw err;
      }
      res.end(data);
    });
  } else if (req.url === '/haha.xxx') {
     fs.readFile(path.join(__dirname, '150.jpg'), function (err, data) {
      if (err) {
        throw err;
      }
      res.setHeader('Content-Type', 'image/jpeg');
      res.end(data);
    });
  }
  
}).listen(9090, function () {
  console.log('http://localhost:9090');
});