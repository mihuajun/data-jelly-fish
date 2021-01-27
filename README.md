### Data JellyFish 译文（数据水母）数据调度中心

#### 用途

与第三方的数据同步
内部系统之间的数据同步

#### 特性
1. 数据100%传输，不丢失任何一条数据
2. 实时性高，相比定时任务每5分钟，半天，一天，而言，在秒级实现同步
3. 对接成本低，增量同步时提供一个增量查询接口，全量同步时提供一个全量查询接口
4. 无中心化的分布式任务，实现任务分片能力，达到并发处理，实现快速调度的目的
5. 每一条数据都有同步的成功或失败记录，历史可查
6. 自定义重试策略，固定时长，指数级重试
7. 完善的监控信息，有多少同步了，有多少未同步，

#### 原理

以系统A同步数据到系统B为例，A系统提供一个http接口，实现数据增量或全量的抓取，”Data JellyFish 数据高度中心“ 简称 "DJ" 
DJ 启动生产者任务线 程T1调用A系统的http接口，将数据存储在自己的中间表中，同时，DJ启动消费者任务线程T2调用B系统提供的另一个接受数据的HTTP接口，来完成数据调度

#### 架构图
![输入图片说明](https://images.gitee.com/uploads/images/2021/0127/210151_af2b4cdf_5139840.png "屏幕截图.png")

#### 传统数据同步方案比较
1. RCP, http协议直接访问第三方，内存中重试三次后，消息丢失
2. 定时任务数据同步，延迟高，无分片并发能力
3. MQ,Kafka等，研发对接（保证数据准确传输的）成本高，无流水记录，无法直接回塑
4. 每次数据对接都需要重复开发，不具备可用性