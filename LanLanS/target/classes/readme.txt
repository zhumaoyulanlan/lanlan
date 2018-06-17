此版本的lanlan工程基于spring

有以下特点
1.java的非web开发及少.本项目人力有限. 1.0版本中将依赖javax(javaEE)的组件与只依赖javajdk的组件分成两个jar包
在此版本中,将只打包成一个jar包
2.移除了1.0版本中的sqlParameter泛型 value 直接使用object,使用起来更简洁.
3.移除了1.0版本中的ReflectMapper(改名为LanLanMoldelMapper)类中的模型类名转化成表名方法(ModelToTableName).
	原因如下:
		1.大部分情况通过注解关联数据库对应表名已经足够,同一程序中既使用ModelToTableName方法,
		又使用@tableName注解会使软件显得混乱
		2.我们在使用自动生成项目中,在生成model.java文件时,非常容易添加@tableName注解,
		这让ModelToTableName显得无用
		3.考虑后期添加数据库视图对应的model类时,单一的 ModelToTableName 会变得力不从心.
	ps:新版中当model类中未添加@tableName注解,LanLanMoldelMapper初始化时,将直接报错
4.增加了注入功能,和spring一样,对于不应为空的字段属性,推荐使用有参构造方法注入来强制保证初始化完成就有值
当有多个构造方法时,请使用[@Resource]注解标示提供调用的构造方法 ,  
 没有在构造函数中初始化且需要注入的属性, 必须在[字段]上添加[@Resource]注解


未完全实现部分
1. CommentUtile.sqlTypetoJavaType 仍然有部分java.sql.type未找到对应类
2.

