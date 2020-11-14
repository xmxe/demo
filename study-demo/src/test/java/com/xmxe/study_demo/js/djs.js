function showTime(a) {
  var b = {
    id: "showtime", //canvasid
    x: 60, //中心点坐标 X轴;
    y: 60, //中心点坐标 Y轴;
    radius: 60, //圆的半径
    angle: 0, //角度 无需设置
    linewidth: 6, //线的宽度
    backround: "#d65554", //倒计时背景色
    color: "#e4e4e4", //填充色
    day: 0,
    time: 0,
    minute: 0,
    seconds: 0,
  }; //若参数有更新则合并
  if (a) {
    b = $.extend(b, a);
  }
  this.total = 0;
  this.id = b.id;
  this.x = b.x;
  this.y = b.y;
  this.radius = b.radius;
  this.angle = b.angle;
  this.linewidth = b.linewidth;
  this.backround = b.backround;
  this.color = b.color;
  this.time = b.time;
  this.day = b.day;
  this.minute = b.minute;
  this.seconds = b.seconds;

  var canvas = document.getElementById(this.id);
  if (canvas == null) {
    return false;
  }
  var ctx = canvas.getContext("2d");

  this.creatText = function () {
    ctx.fillStyle = "#000";
    ctx.font = "13px Arial";
    ctx.fillText("剩余时间", 32, 38);
    ctx.fillStyle = "#333";
    if (Number(this.day) > 0) {
      ctx.font = "22px Arial";
      ctx.fillStyle = "#000";
      ctx.fillText(getStr(this.day), 13, 75);
      ctx.font = "13px Arial";
      ctx.fillStyle = "#333";
      ctx.fillText("天", 38, 75);
      ctx.font = "22px Arial";
      ctx.fillStyle = "#000";
      ctx.fillText(getStr(this.time), 58, 75);
      ctx.font = "14px Arial";
      ctx.fillStyle = "#333";
      ctx.fillText("小时", 82, 75);
    } else if (Number(this.time) > 0) {
      ctx.font = "22px Arial";
      ctx.fillStyle = "#000";
      ctx.fillText(getStr(this.time), 11, 75);
      ctx.font = "13px Arial";
      ctx.fillStyle = "#333";
      ctx.fillText("小时", 33, 75);
      ctx.font = "22px Arial";
      ctx.fillStyle = "#000";
      ctx.fillText(getStr(this.minute), 61, 75);
      ctx.font = "13px Arial";
      ctx.fillStyle = "#333";
      ctx.fillText("分钟", 84, 75);
    } else if (Number(this.minute) > 0) {
      ctx.font = "22px Arial";
      ctx.fillStyle = "#000";
      ctx.fillText(getStr(this.minute), 13, 75);
      ctx.font = "13px Arial";
      ctx.fillStyle = "#333";
      ctx.fillText("分钟", 35, 75);
      ctx.font = "22px Arial";
      ctx.fillStyle = "#000";
      ctx.fillText(getStr(this.seconds), 65, 75);
      ctx.font = "13px Arial";
      ctx.fillStyle = "#333";
      ctx.fillText("秒", 90, 75);
    } else if (Number(this.seconds) > 0) {
      ctx.font = "22px Arial";
      ctx.fillStyle = "#000";
      ctx.fillText(getStr(this.seconds), 40, 75);
      ctx.font = "13px Arial";
      ctx.fillStyle = "#333";
      ctx.fillText("秒", 65, 75);
    } else {
      ctx.font = "22px Arial";
      ctx.fillStyle = "#000";
      ctx.fillText(getStr("00:00"), 31, 75);
    }
    function getStr(num) {
      return num.toString().length < 2 ? "0" + num : num;
    }
  },
    showTime.prototype.creatEle = function () {
      var _w = canvas.getAttribute("width");
      var _h = canvas.getAttribute("height");
      ctx.clearRect(0, 0, _w, _h); //清楚canva绘制区域
      ctx.save();

      ctx.restore();
      ctx.save();
      ctx.translate(this.x, this.y);
      ctx.rotate(-Math.PI / 2);
      if (this.angle == 360) {
        ctx.fillStyle = this.color;
      } else {
        ctx.fillStyle = this.backround;
      }

      ctx.beginPath();
      ctx.arc(0, 0, this.radius - 0.5, (Math.PI / 180) * 0, Math.PI * 2, true);
      ctx.lineTo(0, 0);
      ctx.closePath();
      ctx.fill();
      ctx.restore();
      ctx.save();

      ctx.translate(this.x, this.y);
      ctx.rotate(-Math.PI / 2);
      ctx.fillStyle = this.color;
      ctx.beginPath();

      ctx.arc(
        0,
        0,
        this.radius,
        (Math.PI / 180) * this.angle,
        Math.PI * 2,
        true
      );
      ctx.lineTo(0, 0);
      ctx.closePath();
      ctx.fill();
      ctx.restore();
      ctx.save();

      ctx.beginPath();
      var linew = this.radius - this.linewidth;
      ctx.arc(this.x, this.y, linew, 0, 2 * Math.PI, true);
      ctx.closePath();
      ctx.fillStyle = "white";
      ctx.fill();
      ctx.restore();
      this.creatText();
    };
  this.creatEle();
}

var countdown = function (startTime, lastTime, nowTime, step) {
  this.startTime = startTime; //服务器开始时间
  this.lastTime = lastTime; //服务器到期时间
  this.nowTime = nowTime; //服务器当前时间
  this.alltime = this.lastTime - this.startTime; //总时间段
  this.step = step * 1000; //执行的阶段时间，一般是1秒
};
countdown.prototype = {
  atTime: function (a, b) {
    //参数说明：a:到期回调方法，b:倒计时回调方法
    var that = this;
    //var timeold = parseFloat(Number(that.lastTime) - Number(that.startTime));
    var timeold = that.lastTime - that.nowTime;
    var msPerDay = 24 * 60 * 60 * 1000;
    var e_daysold = timeold / msPerDay;
    var daysold = Math.floor(e_daysold); //天
    var e_hrsold = (e_daysold - daysold) * 24;
    var hrsold = Math.floor(e_hrsold); //小时
    var e_minsold = (e_hrsold - hrsold) * 60;
    var minsold = Math.floor((e_hrsold - hrsold) * 60); //分钟
    var seconds = Math.round((e_minsold - minsold) * 60); //秒
    var msSeconds =
      Math.ceil(
        Math.round(((e_minsold - minsold) * 60 - seconds) * 1000) / 100
      ) * 10;
    var totaltime = that.lastTime - that.nowTime;
    var timeangle = 360 - totaltime / (that.alltime / 360);
    if (msSeconds == 100) {
      msSeconds = 99;
    }
    if (that.nowTime > that.lastTime) {
      arguments[0]();
    } else {
      arguments[1](
        that.getStr(daysold),
        that.getStr(hrsold),
        that.getStr(minsold),
        that.getStr(seconds),
        that.getStr(msSeconds),
        Math.floor(timeangle)
      );
      that.nowTime = parseInt(that.nowTime) + that.step;
      window.setTimeout(function () {
        that.atTime(a, b);
      }, that.step);
    }
  },
  getStr: function (num) {
    return num.toString().length < 2 ? "0" + num : num;
  },
};

$(function () {
  var startTime = 1437765600000; //开始时间
  var lastTime = 1437766880000; //结束时间
  var nowTime = 1437766850000; //服务器当前时间
  var showtime = new countdown(startTime, lastTime, nowTime, 1);
  showtime.atTime(
    function () {},
    function () {
      var one = new showTime({
        id: "showtime",
        day: arguments[0],
        time: arguments[1],
        minute: arguments[2],
        seconds: arguments[3],
        angle: arguments[5],
      });
      one.creatEle();
    }
  );
});
