// 根据用户的不同请求，服务器做出不同的响应

// 1. 加载 http 模块
var http = require('http');

// 2. 创建 http 服务
http.createServer(function (req, res) {
  
  // // 获取用户请求的路径 req.url
  // console.log(req.url);

  // // 结束响应
  // res.end(); 


  res.setHeader('Content-Type', 'text/plain; charset=utf-8');
  // 通过 req.url 获取用户请求的路径，根据不同的请求路径服务器做出不同的响应
  if (req.url === '/' || req.url === '/index') {
    // res.write('hello index');
    // res.end();
    res.end('Hello Index');
  } else if (req.url === '/login') {
    res.end('Hello login');
  } else if (req.url === '/list') {
    res.end('Hello List');
  } else if (req.url === '/register') {
    res.end('Hello Register');
  } else {
    res.end('404, not Found。客户端错误！');
  }

}).listen(8080, function () {
  console.log('http://localhost:8080');
});