# springboot + seata + dubbo + zookeeper + mybatis + mysql
# demo-seata
> 分布式事务已经整合完成

# 项目结构
> 本项目分为三层
* web层
* 业务层
* 基础服务层

> web层为：fescar-dubbo-web  
> 业务层为：fescar-dubbo-business  
> 基础服务层： fescar-dubbo-account、fescar-dubbo-order、fescar-dubbo-storage  

> 服务调用方式为：web层调用业务层，业务层调用基础服务层。

# 数据库
```sql
CREATE TABLE `BUSINESS_ACCOUNT` (
  `accountId` varchar(32) NOT NULL COMMENT '主键uuid',
  `amount` decimal(18,6) DEFAULT NULL COMMENT '金额',
  `accountName` varchar(32) DEFAULT NULL COMMENT '账户名称',
  `logicDel` char(1) DEFAULT NULL COMMENT '逻辑删除 Y:删除 N:未删除',
  `remark` varchar(240) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`accountId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8

CREATE TABLE `BUSINESS_ORDER` (
  `orderId` varchar(32) NOT NULL COMMENT '主键uuid',
  `orderNo` varchar(32) DEFAULT NULL COMMENT '订单号',
  `orderDetail` varchar(240) DEFAULT NULL COMMENT '订单详情',
  `createTime` varchar(24) DEFAULT NULL COMMENT '创建时间',
  `logicDel` char(1) DEFAULT NULL COMMENT '逻辑删除 Y:删除 N:未删除',
  `remark` varchar(240) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`orderId`),
  UNIQUE KEY `orderNo` (`orderNo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8

CREATE TABLE `BUSINESS_STORAGE` (
  `storageId` varchar(32) NOT NULL COMMENT '主键uuid',
  `storageName` varchar(32) DEFAULT NULL COMMENT '仓储名称',
  `storageCount` int(11) DEFAULT NULL COMMENT '数量',
  `logicDel` char(1) DEFAULT NULL COMMENT '逻辑删除 Y:删除 N:未删除',
  `remark` varchar(240) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`storageId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8

#这个表是seata在at模式下需要的表
CREATE TABLE `undo_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `branch_id` bigint(20) NOT NULL,
  `xid` varchar(100) NOT NULL,
  `rollback_info` longblob NOT NULL,
  `log_status` int(11) NOT NULL,
  `log_created` datetime NOT NULL,
  `log_modified` datetime NOT NULL,
  `ext` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_unionkey` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=171 DEFAULT CHARSET=utf8
```

# 整合过程
## dubbo
> 我用的dubbo版本比较高，因为这个项目已经给Apache了，所以我没有使用带有阿里相关东西的包，带有阿里标记的我觉得以后估计都不会维护了。

> 启动类加上注解@EnableDubbo。

```java
@EnableDubbo
@SpringBootApplication
@MapperScan("com.sly.fescar.account.mapper")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
```

> 配置dubbo

```yml
dubbo:
  application:
    id: fescar-dubbo-account
    name: fescar-dubbo-account
    qos-enable: false
  protocol:
    id: dubbo
    name: dubbo
    port: 20882
  registry:
    id: fescar-dubbo-account
    address: zookeeper://127.0.0.1:2181
    protocol: zookeeper
  scan:
    base-packages:
    - com.sly.fescar.account.service
```

> 引入依赖

```xml
<!-- dubbo spring boot starter -->
<dependency>
	<groupId>org.apache.dubbo</groupId>
	<artifactId>dubbo-spring-boot-starter</artifactId>
	<version>2.7.1</version>
</dependency>
<!-- dubbo -->
<dependency>
	<groupId>org.apache.dubbo</groupId>
	<artifactId>dubbo</artifactId>
	<exclusions>
		<exclusion>
			<groupId>org.springframework</groupId>
			<artifactId>spring</artifactId>
		</exclusion>
		<exclusion>
			<groupId>javax.servlet</groupId>
			<artifactId>servlet-api</artifactId>
		</exclusion>
		<exclusion>
			<groupId>log4j</groupId>
			<artifactId>log4j</artifactId>
		</exclusion>
	</exclusions>
	<version>2.7.1</version>
</dependency>
<!-- Zookeeper dependencies -->
<dependency>
	<groupId>org.apache.dubbo</groupId>
	<artifactId>dubbo-dependencies-zookeeper</artifactId>
	<version>2.7.1</version>
	<exclusions>
		<exclusion>
			<groupId>org.slf4j</groupId>
			<artifactId>slf4j-log4j12</artifactId>
		</exclusion>
	</exclusions>
	<type>pom</type>
</dependency>
```

> 暴露服务，注意这里的@Service是org.apache.dubbo.config.annotation.Service，不要搞错了。

```java
@Service
public class AccountServiceImpl implements AccountService {
	
	@Autowired
	private AccountMapper accountMapper;

	/**
	 * 新增
	 * 
	 * @param account
	 * @return
	 * @author sly
	 * @time 2019年6月11日
	 */
	@Override
	public Map<String, Object> insert(Account account) {
		System.out.println("全局事务id:" + RootContext.getXID());
		
		accountMapper.insert(account);
		Map<String, Object> result = new HashMap<>(16);
		result.put("status", 200);
		result.put("message", "新增成功！");
		return result;
	}

}
```

> 消费服务其实和使用@Resource，@Autowired差不多，这是使用的是@Reference，org.apache.dubbo.config.annotation.Reference。

```java
@Reference
private StorageService storageService;
@Reference
private OrderService orderService;
@Reference
private AccountService accountService;
```

## seata
> 首先你需要启动seata-service  
> [下载地址](https://github.com/seata/seata/releases)

> 引入依赖

```xml
<!-- seata -->
<dependency>
	<groupId>io.seata</groupId>
	<artifactId>seata-all</artifactId>
	<version>0.6.1</version>
</dependency>
```

> 配置数据源和全局事务，如果没有数据源例如这个项目的业务层，那么只用配置全局事务即可。

```java
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.alibaba.druid.pool.DruidDataSource;

import io.seata.rm.datasource.DataSourceProxy;
import io.seata.spring.annotation.GlobalTransactionScanner;

/**
 * seata配置
 * 
 * @author sly
 * @time 2019年6月11日
 */
@Configuration
public class SeataAutoConfig {
	@Autowired
	private DataSourceProperties dataSourceProperties;

	/**
	 * druid数据源
	 * 
	 * @return
	 * @author sly
	 * @time 2019年6月11日
	 */
	@Bean
	@Primary
	public DruidDataSource druidDataSource() {
		DruidDataSource druidDataSource = new DruidDataSource();
		druidDataSource.setUrl(dataSourceProperties.getUrl());
		druidDataSource.setUsername(dataSourceProperties.getUsername());
		druidDataSource.setPassword(dataSourceProperties.getPassword());
		druidDataSource.setDriverClassName(dataSourceProperties.getDriverClassName());
		druidDataSource.setInitialSize(0);
		druidDataSource.setMaxActive(180);
		druidDataSource.setMaxWait(60000);
		druidDataSource.setMinIdle(0);
		// druidDataSource.setValidationQuery("Select 1 from DUAL");
		druidDataSource.setTestOnBorrow(false);
		druidDataSource.setTestOnReturn(false);
		druidDataSource.setTestWhileIdle(true);
		druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
		druidDataSource.setMinEvictableIdleTimeMillis(25200000);
		druidDataSource.setRemoveAbandoned(true);
		druidDataSource.setRemoveAbandonedTimeout(1800);
		druidDataSource.setLogAbandoned(true);
		return druidDataSource;
	}

	/**
	 * 代理数据源
	 * 
	 * @param druidDataSource
	 * @return
	 * @author sly
	 * @time 2019年6月11日
	 */
	@Bean
	public DataSourceProxy dataSourceProxy(DruidDataSource druidDataSource) {
		return new DataSourceProxy(druidDataSource);
	}

	/**
	 * 初始化mybatis sqlSessionFactory
	 * 
	 * @param dataSourceProxy
	 * @return
	 * @throws Exception
	 * @author sly
	 * @time 2019年6月11日
	 */
	@Bean
	public SqlSessionFactory sqlSessionFactory(DataSourceProxy dataSourceProxy) throws Exception {
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(dataSourceProxy);
		factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
		factoryBean.setTypeAliasesPackage("com.sly.fescar.common.model");
		factoryBean.setTransactionFactory(new JdbcTransactionFactory());
		return factoryBean.getObject();
	}

	/**
	 * 初始化全局事务扫描
	 * 
	 * @return
	 * @author sly
	 * @time 2019年6月11日
	 */
	@Bean
	public GlobalTransactionScanner globalTransactionScanner() {
		return new GlobalTransactionScanner("fescar-dubbo-account", "my_test_tx_group");
	}
}
```

> 引入seata配置，它们在本项目的registry.conf和file.conf里。

> registry.conf

```conf
registry {
  # file nacos
  type = "file"

  nacos {
    serverAddr = "localhost"
    namespace = "public"
    cluster = "default"
  }
  file {
    name = "file.conf"
  }
}

config {
  # file、nacos 、apollo、zk、consul
  type = "file"

  file {
    name = "file.conf"
  }
}
```

> file.conf

```conf
transport {
  # tcp udt unix-domain-socket
  type = "TCP"
  #NIO NATIVE
  server = "NIO"
  #enable heartbeat
  heartbeat = true
  #thread factory for netty
  thread-factory {
    boss-thread-prefix = "NettyBoss"
    worker-thread-prefix = "NettyServerNIOWorker"
    server-executor-thread-prefix = "NettyServerBizHandler"
    share-boss-worker = false
    client-selector-thread-prefix = "NettyClientSelector"
    client-selector-thread-size = 1
    client-worker-thread-prefix = "NettyClientWorkerThread"
    # netty boss thread size,will not be used for UDT
    boss-thread-size = 1
    #auto default pin or 8
    worker-thread-size = 8
  }
}

service {
  #vgroup->rgroup
  vgroup_mapping.my_test_tx_group = "default"
  #only support single node
  default.grouplist = "127.0.0.1:8091"
  #degrade current not support
  enableDegrade = false
  #disable
  disable = false
}

client {
  async.commit.buffer.limit = 10000
  lock {
    retry.internal = 10
    retry.times = 30
  }
}
## transaction log store
store {
  ## store mode: file、db
  mode = "file"

  ## file store
  file {
    dir = "file_store/data"

    # branch session size , if exceeded first try compress lockkey, still exceeded throws exceptions
    max-branch-session-size = 16384
    # globe session size , if exceeded throws exceptions
    max-global-session-size = 512
    # file buffer size , if exceeded allocate new buffer
    file-write-buffer-cache-size = 16384
    # when recover batch read size
    session.reload.read_size = 100
  }

  ## database store
  db {
    driver_class = ""
    url = ""
    user = ""
    password = ""
  }
}
```

> 使用事务，加上@GlobalTransactional注解在业务层的方法上即可，下面代码中的方法调用了三个不同工程的服务进行插入操作。经过故意抛出异常操作发现确实可以回滚，可以说整合基本完成。

```java
@GlobalTransactional
@Override
public Map<String, Object> purchase(String accountId, String orderId, String storageId) {
	try {
		System.out.println("accountId:" + accountId);
		System.out.println("orderId:" + orderId);
		System.out.println("storageId:" + storageId);
		
		Storage storage = new Storage();
		storage.setStorageId(CommonUtils.genUUID());
		storage.setStorageName("name");
		storage.setStorageCount(20);
		storage.setRemark("备注");
		storage.setLogicDel("N");
		Order order = new Order();
		order.setOrderId(CommonUtils.genUUID());
		order.setOrderNo("NO" + System.currentTimeMillis());
		order.setOrderDetail("详情");
		order.setCreateTime(DateUtils.formateTime(new Date()));
		order.setRemark("备注");
		order.setLogicDel("N");
		Account account = new Account();
		account.setAccountId(CommonUtils.genUUID());
		account.setAccountName("name");
		account.setAmount(new BigDecimal("100.5"));
		account.setLogicDel("N");
		account.setRemark("备注");
		
		storageService.insert(storage);
		orderService.insert(order);
		accountService.insert(account);
		Map<String, Object> result = new HashMap<>(16);
		result.put("status", 200);
		result.put("message", "购买成功！");
		return  result;
	} catch (Exception e) {
		LOGGER.error(ExceptionUtils.getStackTrace(e));
		throw new RuntimeException(e);
	}
	
}
```

# 小结
> 当前这个工程只是一个测试工程，所处的场景及环境非常简单。如果是生产场景我就不知道是否会起作用了，毕竟seata这个项目今年才开源给公众，目前还在开发中，查看GitHub的issue依然有很多的地方不完善。所有慎用该技术。但是我很看好该技术，毕竟对业务0侵入是非常诱人的。









