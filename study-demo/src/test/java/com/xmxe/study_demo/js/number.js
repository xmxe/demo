// ------------数字 Number-------

/**
 * 数字千分位分割
 * @param {*} num 
 */
function commafy(num) {
  return num.toString().indexOf(".") !== -1
    ? num.toLocaleString()
    : num.toString().replace(/(\d)(?=(?:\d{3})+$)/g, "$1,");
}
commafy(1000)

/**
 * 生成随机数
 * @param {*} min 
 * @param {*} max 
 */

function randomNum(min, max) {
  switch (arguments.length) {
    case 1:
      return parseInt(Math.random() * min + 1, 10);
    case 2:
      return parseInt(Math.random() * (max - min + 1) + min, 10);
    default:
      return 0;
  }
}
randomNum(1,10)