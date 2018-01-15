题目很多记不全，这里列举几个记住的吧

1 什么是XSS攻击？

XSS攻击：跨站脚本攻击(Cross Site Scripting)，为不和层叠样式表(Cascading Style Sheets, CSS)的缩写混淆。故将跨站脚本攻击缩写为XSS。XSS是一种经常出现在web应用中的计算机安全漏洞，它允许恶意web用户将代码植入到提供给其它用户使用的页面中。比如这些代码包括HTML代码和客户端脚本。攻击者利用XSS漏洞旁路掉访问控制——例如同源策略(same origin policy)。这种类型的漏洞由于被骇客用来编写危害性更大的phishing攻击而变得广为人知。对于跨站脚本攻击，黑客界共识是：跨站脚本攻击是新型的“缓冲区溢出攻击“，而JavaScript是新型的“ShellCode”。



2 -XX:ThreadStackSize 表示什么意思？



3 http1.0 与1.1的区别

翻了下HTTP1.1的协议标准RFC2616，下面是看到的一些它跟HTTP1.0的差别。



Persistent Connection(持久连接)


在HTTP1.0中，每对Request/Response都使用一个新的连接。


HTTP 1.1则支持Persistent Connection, 并且默认使用persistent connection.




Host 域



HTTP1.1在Request消息头里头多了一个Host域，比如：



       GET /pub/WWW/TheProject.html HTTP/1.1

       Host: www.w3.org




   HTTP1.0则没有这个域。

   可能HTTP1.0的时候认为，建立TCP连接的时候已经指定了IP地址，这个IP地址上只有一个host。



date/time stamp (日期时间戳)


(接收方向)

无论是HTTP1.0还是HTTP1.1，都要能解析下面三种date/time stamp：



      Sun, 06 Nov 1994 08:49:37 GMT  ; RFC 822, updated by RFC 1123

      Sunday, 06-Nov-94 08:49:37 GMT ; RFC 850, obsoleted by RFC 1036

      Sun Nov  6 08:49:37 1994       ; ANSI C's asctime() format



(发送方向)
    HTTP1.0要求不能生成第三种asctime格式的date/time stamp；

    HTTP1.1则要求只生成RFC 1123(第一种)格式的date/time stamp。







Transfer Codings



HTTP1.1支持chunked transfer，所以可以有Transfer-Encoding头部域:

Transfer-Encoding: chunked



   HTTP1.0则没有。





Quality Values



HTTP1.1多了个qvalue域：



       qvalue         = ( "0" [ "." 0*3DIGIT ] )

                      | ( "1" [ "." 0*3("0") ] )





Entity Tags



用于Cache。





Range 和 Content-Range



HTTP1.1支持传送内容的一部分。比方说，当客户端已经有内容的一部分，为了节省带宽，可以只向服务器请求一部分。





100 (Continue) Status



100 (Continue) 状态代码的使用，允许客户端在发request消息body之前先用request header试探一下server，看server要不要接收request body，再决定要不要发request body。

客户端在Request头部中包含

Expect: 100-continue

Server看到之后呢如果回100 (Continue) 这个状态代码，客户端就继续发request body。



这个是HTTP1.1才有的。





Request method



HTTP1.1增加了OPTIONS, PUT, DELETE, TRACE, CONNECT这些Request方法.



       Method         = "OPTIONS"                ; Section 9.2

                      | "GET"                    ; Section 9.3

                      | "HEAD"                   ; Section 9.4

                      | "POST"                   ; Section 9.5

                      | "PUT"                    ; Section 9.6

                      | "DELETE"                 ; Section 9.7

                      | "TRACE"                  ; Section 9.8

                      | "CONNECT"                ; Section 9.9

                      | extension-method

       extension-method = token





Status code



  HTTP1.1 增加的新的status code：



(HTTP1.0没有定义任何具体的1xx status code, HTTP1.1有2个)

100 Continue

101 Switching Protocols



203 Non-Authoritative Information

205 Reset Content

206 Partial Content



302 Found (在HTTP1.0中有个 302 Moved Temporarily)

303 See Other

305 Use Proxy

307 Temporary Redirect



405 Method Not Allowed

406 Not Acceptable

407 Proxy Authentication Required

408 Request Timeout

409 Conflict

410 Gone

411 Length Required

412 Precondition Failed

413 Request Entity Too Large

414 Request-URI Too Long

415 Unsupported Media Type

416 Requested Range Not Satisfiable

417 Expectation Failed



504 Gateway Timeout

505 HTTP Version Not Supported







Content Negotiation



    HTTP1.1增加了Content Negotiation，分为Server-driven Negotiation，Agent-driven Negotiation和Transparent Negotiation三种。





Cache (缓存)



HTTP1.1(RFC2616)详细展开地描述了Cache机制，详见13节。



4 jvm结构（年轻代，年老代，持久代）及 eden与survivor的比例

      eden ：survivor=8:1

面试官：那你先讲讲它的内存模型吧

我：Java堆，Java栈，程序计数器，方法区，1.7的永久代，1.8的metaspace....
（噼里啪啦概念讲一通，简短描述下每个内存区的用途，能想到的都讲出来，不要保留，不要等面试官问 “还有吗？”）

面试官：好，一般Java堆是如何实现的？
我：在HotSpot虚拟机实现中，Java堆分成了新生代和老年代，我当时看的是1.7的实现，所有还有永久代，
新生代中又分为了eden区和survivor区，survivor区又分成了S0和S1，或则是from和to，
（这个时候，我要求纸和笔，因为我觉得这个话题可以聊蛮长时间，又是我比较熟悉的...一边画图，一边描述），
其中eden，from和to的内存大小默认是8:1:1（各种细节都要说出来...），此时，我已经在纸上画出了新生代和老年代代表的区域

面试官：恩，给我讲讲对象在内存中的初始化过程？

我：（千万不要只说，新对象在Java堆进行内存分配并初始化，或是在eden区进行内存分配并初始化）
要初始化一个对象，首先要加载该对象所对应的class文件，该文件的数据会被加载到永久代，
并创建一个底层的instanceKlass对象代表该class，再为将要初始化的对象分配内存空间，
优先在线程私有内存空间中分配大小，如果空间不足，再到eden中进行内存分配...^&&*%

5 sql分析主要看哪些参数？

sql_trace跟踪得到的执行计划:
Call              Count             CPU Time                 Elapsed Time                Disk              Query            Current               Rows
------- ------ -------- ------------ ---------- ---------- ---------- ----------
Parse              1                0.000                                  0.000                            0                    0                      0                          0
Execute          1                0.000                                  0.000                            0                    0                      0                          0
Fetch            481              0.047                                  0.059                            0                 1424                  0                        4800
------- ------ -------- ------------ ---------- ---------- ---------- ----------
Total             483              0.047                                  0.059                            0                  1424                  0                       4800



解释：
Parse： 这步将SQL语句转换成执行计划，包括检查是否有正确的授权和所需要用到的表、列以及其他引用到的对象是否存在。
Execute： 这步是真正的由Oracle来执行语句。对于insert、update、delete操作，这步会修改数据，对于select操作,这步就只是确定选择的记录。
Fetch： 返回查询语句中所获得的记录，这步只有select语句会被执行。
COUNT: 这个语句被parse、execute、fetch的次数。

CPU： 这个语句对于所有的parse、execute、fetch所消耗的cpu的时间，以秒为单位。
ELAPSED： 这个语句所有消耗在parse、execute、fetch的总的时间。
DISK： 从磁盘上的数据文件中物理读取的块的数量。一般来说更想知道的是正在从缓存中读取的数据而不是从磁盘上读取的数据。
QUERY： 在一致性读模式下，所有parse、execute、fetch所获得的buffer的数量。 一致性模式的buffer是用于给一个长时间运行的事务提供一个一致性读的快照，缓存实际上在头部存储了状态。
CURRENT： 在current模式下所获得的buffer的数量。一般在current模式下执行insert、update、delete操作都会获取buffer。在current模式下如果在高速缓存区发现有新的缓存足够给当前的事务使用，则这些buffer都会被读入了缓存区中。
ROWS: 所有SQL语句返回的记录数目，但是不包括子查询中返回的记录数目。对于select语句，返回记录是在fetch这步，
对于insert、update、delete操作，返回记录则是在execute这步。