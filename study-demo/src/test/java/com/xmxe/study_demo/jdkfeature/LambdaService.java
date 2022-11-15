package com.xmxe.study_demo.jdkfeature;
/**只有一个接口函数需要被实现的接口类型，我们叫它"函数式接口",
 * 为了避免后来的人在这个接口中增加接口函数导致其有多个接口函数需要被实现，变成"非函数接口”,我们可以在这个上面加上一个声明@FunctionalInterface,
 * 这样别人就无法在里面添加新的接口函数了
 * 
 * Java8中一个极其强悍的新接口 https://mp.weixin.qq.com/s/6_2QPQq22AU4UBKS4e7rjw
 **/
@FunctionalInterface
public interface LambdaService {
	int lambdaTest(int a,int b);
}
