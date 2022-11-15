// 1. Replace All
var example = "potato potato";
console.log(example.replace(/pot/, "tom"));// "tomato potato"
console.log(example.replace(/pot/g, "tom"));// "tomato tomato"
// 2. 提取唯一值 我们可以使用Set对象和Spread运算符，创建一个剔除重复值的新数组。
var entries = [1, 2, 2, 3, 4, 5, 6, 6, 7, 7, 8, 4, 2, 1]
var unique_entries = [...new Set(entries)];
console.log(unique_entries);// [1, 2, 3, 4, 5, 6, 7, 8]
// 3. 将数字转换为字符串
var converted_number = 5 + "";
console.log(converted_number);// 5
console.log(typeof converted_number);// string
// 4. 将字符串转换为数字 请注意这里的用法，因为它只适用于“字符串数字”。
var the_string = "123";
console.log(+the_string);// 123
the_string = "hello";
console.log(+the_string);// NaN
// 5.随机排列数组中的元素
var my_list = [1, 2, 3, 4, 5, 6, 7, 8, 9];
console.log(my_list.sort(function() {
    return Math.random() - 0.5
}));// [4, 8, 2, 9, 1, 3, 6, 5, 7]
// 6. 展平多维数组
var entries = [1, [2, 5], [6, 7], 9];
var flat_entries = [].concat(...entries);// [1, 2, 5, 6, 7, 9]
// 7. 短路条件
// available && addToCart()代替
// if (available) {
//     addToCart();
// }
// 8. 动态属性名称
const dynamic = 'flavour';
var item = {
    name: 'Coke',
    [dynamic]: 'Cherry'
}
console.log(item);// { name: "Coke", flavour: "Cherry" }
// 9. 使用length调整大小 / 清空数组
// 如果我们要调整数组的大小：
var entries = [1, 2, 3, 4, 5, 6, 7];
console.log(entries.length);// 7
entries.length = 4;
console.log(entries.length);// 4
console.log(entries);// [1, 2, 3, 4]
// 如果我们要清空数组：
var entries = [1, 2, 3, 4, 5, 6, 7];
console.log(entries.length);// 7
entries.length = 0;
console.log(entries.length);// 0
console.log(entries);// []