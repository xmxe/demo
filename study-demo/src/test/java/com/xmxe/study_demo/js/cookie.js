// -------cookie-------
/**
 *
 * @param {*} key
 * @param {*} value
 * @param {*} expiredays 过期时间
 */
function setCookie(key, value, expiredays) {
  var exdate = new Date();
  exdate.setDate(exdate.getDate() + expiredays);
  document.cookie =
    key +
    "=" +
    escape(value) +
    (expiredays == null ? "" : ";expires=" + exdate.toGMTString());
}
/**
 *
 * @param {*} name cookie key
 */
function delCookie(name) {
  var exp = new Date();
  exp.setTime(exp.getTime() - 1);
  var cval = getCookie(name);
  if (cval != null) {
    document.cookie = name + "=" + cval + ";expires=" + exp.toGMTString();
  }
}
/**
 *
 * @param {*} name cookie key
 */
function getCookie(name) {
  var arr,
    reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
  if ((arr = document.cookie.match(reg))) {
    return arr[2];
  } else {
    return null;
  }
}
// 清空
// 有时候我们想清空，但是又无法获取到所有的cookie。
// 这个时候我们可以了利用写满，然后再清空的办法。
