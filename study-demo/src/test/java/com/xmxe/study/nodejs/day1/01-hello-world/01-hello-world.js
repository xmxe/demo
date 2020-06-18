


/*
  fdjsaklfjfdskfjdskfl;afdsafsf
  fdsafdsaf
  fdsafasf
  fdjskalfdas
  fdjsklafdsafa
  fdjsklafdsfdsafdjsaklfds
  fdsfdjskalfds
*/




// ---- 加法计算器 ----
// var m = 10;
// var n = 100;


// function add(x, y) {
//   return x + y;
// }


// var result = add(m, n);

// console.log('计算的结果是：' + result);


// console.log('hello world!');


// console.log('a');
// console.log('b');

// process.stdout.write('a\n');

// // 通过 process.stdout.write() 可以进行不带换行的输出
// process.stdout.write('b');





// --- 输出一个三角形 ----

// 循环有多少行
for (var i = 0; i < 10; i++) {
  // 每行要输出多少个 *
  for (var j = 0; j <= i; j++) {
    // 每行输出的 * 不能换行
    process.stdout.write('* ');
  }

  // 当每行的所有 * 都输出完毕后，需要输出一个 换行
  console.log();
}

// *
// **
// ***
// ****



// process 模块在使用的时候无需通过 require() 函数来加载该模块，可以直接使用。
// fs 模块，在使用的时候，必须通过 require() 函数来加载该模块， 方可使用。 var fs = require('fs');
// 原因：process 模块是全局的模块，而 fs 模块不是全局模块。全局模块可以直接使用，而非全局模块需要先通过 require('') 加载该模块。



