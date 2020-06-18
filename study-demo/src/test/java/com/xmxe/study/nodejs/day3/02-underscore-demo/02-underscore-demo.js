

// demo1:
// var _ = require('underscore');

// // 演示：
// var names = ['张三', '香香', '小黄'];
// var ages = [18, 19, 20];
// var genders = ['男', '女', '女'];


// // 压缩
// var result = _.zip(names, ages, genders);
// console.log(result);



// // 解压
// result = _.unzip(result);
// console.log(result);





// demo2:
var _ = require('underscore');


// 声明了一段代码模板代码的 HTML 文档
var html = '<h2><%= name %></h2>';

// template() 函数的返回依然是一个函数
var fn = _.template(html);
// 调用 template() 返回的这个函数 fn
// fn 接收一个数据对象，并用该数据对象，将 html 中的模板内容替换，生成最终的 HTML 代码。
html = fn({name: '达达'});
console.log(html);



console.log(fn.toString());


// // fn 函数的源代码
// function (data) {
//       return render.call(this, data, _);
// }