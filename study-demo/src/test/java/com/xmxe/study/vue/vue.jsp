<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="privilege" uri="/WEB-INF/privilege.tld"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<base href="<%=basePath%>">
<title>vue</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script src="js/vue.min.js"></script>
</head>
<body>
	<!-- 1 -->
	<div id="one">{{ message }}</div>
	<!-- 2 -->
	<div id="two">
  		<button-counter></button-counter>
	</div>
	<!-- 3 -->
	<div id="three">
		<input v-my-directive />
	</div>
	<!-- 4 -->
	<div id="four"><!-- fileter(a,b)有参数时filtername与括号之间不能有空格,否则过滤器无效  反例:fileter (a,b)无效-->
		<p>{{message | filterA |filterB(3 ,5)  }}</p>
	</div>
	<script type="text/javascript">
	//1 helloword
	var one = new Vue({
		  el: '#one',
		  data: {
		    message: 'Hello Vue!'
		  }
		});
	
	//2 组件  定义一个名为 button-counter 的新组件(组件名强烈建议多个单词组合)
	Vue.component('button-counter', {
	  data: function () {
	    return {
	      count: 0
	    }
	  },
	  template: '<button v-on:click="count++">You clicked me {{ count }} times.</button>'
	});
	new Vue({ el: '#two' });
	
	// 3指令
	Vue.directive('my-directive',{	 
		  inserted:function(el){
			  el.focus();//页面加载自动获得焦点
		  }
	});
	new Vue({ el: '#three' });
	
	//4过滤器
	Vue.filter('filterA',function(data){
		return data + 4;
	});
	Vue.filter('filterB',function(data, begin, xing){
		//{{ message | filterB('arg1', arg2) }}
		//这里，filterB 被定义为接收三个参数的过滤器函数。其中 message 的值作为第一个参数，普通字符串 'arg1' 作为第二个参数，表达式 arg2 的值作为第三个参数。
		return data + begin + xing;
	});
	new Vue({
		el:'#four',
		data: {
			message:10
		}		
	});
	</script>	
</body>
</html>