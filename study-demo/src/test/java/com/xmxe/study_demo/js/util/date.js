// ---------日期 Date------------

/**
 * 获取当前时间戳⬇
 */ 
// 方案一：精确到秒
console.log(Date.parse(new Date())) 
// 方案二：精确到毫秒
console.log(Date.now()) 
// 方案三：精确到毫秒
console.log(+new Date()) 
// 方案四：精确到毫秒
console.log(new Date().getTime()) 
// 方案五：精确到毫秒
console.log((new Date()).valueOf())

/**
 * js字符串转时间戳⬇
 * mytime是待转换时间字符串，格式：'2018-9-12 9:11:23'
 * 为了兼容IOS，需先将字符串转换为'2018/9/11 9:11:23'
 */
let mytime = '2018-9-12 9:11:23';
let dateTmp = mytime.replace(/-/g, "/");
console.log(new Date(dateTmp).getTime());
console.log(Date.parse(dateTmp));

/**
 * 时间戳转字符串
 */ 
var dateFormat = function (timestamp) {
  //先将时间戳转为Date对象，然后才能使用Date的方法
  var time = new Date(timestamp);
  var year = time.getFullYear(),
    month = time.getMonth() + 1, //月份是从0开始的
    day = time.getDate(),
    hour = time.getHours(),
    minute = time.getMinutes(),
    second = time.getSeconds();
  //add0()方法在后面定义
  return (
    year +
    "-" +
    this.add0(month) +
    "-" +
    this.add0(day) +
    "" +
    this.add0(hour) +
    ":" +
    this.add0(minute) +
    ":" +
    this.add0(second)
  );
};
var add0 = function (m) {
  return m < 10 ? "0" + m : m;
};
/**
 * 格式化字符串
 */
Date.prototype.format = function (fmt) { //author: meizz 
  var o = {
      "M+": this.getMonth() + 1, //月份 
      "d+": this.getDate(), //日 
      "h+": this.getHours(), //小时 
      "m+": this.getMinutes(), //分 
      "s+": this.getSeconds(), //秒 
      "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
      "S": this.getMilliseconds() //毫秒 
  };
  if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
  for (var k in o)
  if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
  return fmt;
}
console.log(new Date().format('yyyy-MM-dd hh:mm:ss'));
/**
 * JavaScript Date对象(https://www.w3school.com.cn/js/jsref_obj_date.asp)
 */
